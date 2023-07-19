package com.mariam.registeration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.mariam.registeration.services.HandyAPI;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreatePost extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "CreatePost";
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    private EditText locationEditText;
    private EditText dateEditText;
    private EditText timeEditText;
    private EditText compensationEditText;
    private EditText descriptionEditText;
    private boolean isPlaceSelected;
    private double locationLat;
    private double locationLon;
    private HandyAPI my_api = new HandyAPI();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        // Check for internet connectivity
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No internet connection available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize the Places SDK
        Places.initialize(getApplicationContext(), "AIzaSyAAUKNLUrCJGc3UijGGKO6wz3VIloVlbRU");

        // Retrieve the EditText views from the layout
        locationEditText = findViewById(R.id.textBox);
        dateEditText = findViewById(R.id.dateEditText);
        timeEditText = findViewById(R.id.timeEditText);
        compensationEditText = findViewById(R.id.compensationTextBox);
        descriptionEditText = findViewById(R.id.descriptionTextBox);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                isPlaceSelected = true;
                LatLng latLng = place.getLatLng();
                if (latLng != null) {
                    locationLat = latLng.latitude;
                    locationLon = latLng.longitude;
                }
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
                isPlaceSelected = false;
            }
        });

        // Set up the click listener for the Confirm button
        Button confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = locationEditText.getText().toString();
                String date = dateEditText.getText().toString();
                String time = timeEditText.getText().toString();
                String compensation = compensationEditText.getText().toString();
                String description = descriptionEditText.getText().toString();

                if (validateInput(location, date, time, compensation, description)) {
                    // Format the date to "YYYY-MM-DD" format
                    String formattedDate = formatDate(date);

                    // Proceed with posting the request
                    new CreatePostTask().execute(location, formattedDate, time, compensation, description);
                } else {
                    Toast.makeText(CreatePost.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set up the click listener for the date picker icon
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Set up the click listener for the time picker icon
        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        View backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button press
                goback();
            }
        });
    }

    private void goback() {
        // Start a new activity or perform any other action you desire
        Intent intent = new Intent(CreatePost.this, PostService.class);
        startActivity(intent);
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, this, hourOfDay, minute, false);
        timePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String selectedDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());
        dateEditText.setText(selectedDate);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
        timeEditText.setText(selectedTime);
    }

    private boolean isNetworkAvailable() {
        // Check network connectivity
        return true; // Replace with your actual implementation
    }

    private boolean validateInput(String location, String date, String time, String compensation, String description) {
        boolean isValid = true;
        if (!isPlaceSelected) {
            isValid = false;
            Toast.makeText(CreatePost.this, "Please select a location", Toast.LENGTH_SHORT).show();
        }
        if (location.isEmpty()) {
            locationEditText.setError("Location is required");
            isValid = false;
        }

        if (date.isEmpty()) {
            dateEditText.setError("Date is required");
            isValid = false;
        }

        if (time.isEmpty()) {
            timeEditText.setError("Time is required");
            isValid = false;
        }

        if (compensation.isEmpty()) {
            compensationEditText.setError("Compensation is required");
            isValid = false;
        } else if (!compensation.matches("\\d+")) {
            compensationEditText.setError("Compensation must be a number");
            isValid = false;
        }

        if (description.isEmpty()) {
            descriptionEditText.setError("Description is required");
            isValid = false;
        }

        return isValid;
    }

    private String formatDate(String date) {
        try {
            DateFormat inputDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            Date inputDate = inputDateFormat.parse(date);

            DateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return outputDateFormat.format(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    private class CreatePostTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            String location = params[0];
            String date = params[1];
            String time = params[2];
            String compensation = params[3];
            String description = params[4];

            try {
                URL url = new URL("http://"+my_api.API_LINK + "/createPost");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                String nationalID = getNationalIDFromSharedPreferences(); // Retrieve the national ID

                JSONObject requestBody = new JSONObject();
                requestBody.put("national_id", nationalID);
                requestBody.put("title", location);
                requestBody.put("location_lat", locationLat);
                requestBody.put("location_lon", locationLon);
                requestBody.put("date", date);
                requestBody.put("time", time);
                requestBody.put("compensation", compensation);
                requestBody.put("description", description);

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(requestBody.toString());
                writer.flush();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return responseCode;
                } else {
                    return -1;  // Request failed
                }
            } catch (Exception e) {
                Log.e(TAG, "Error creating post: " + e.getMessage());
                return -1;  // Request failed
            }
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Post created successfully
                Toast.makeText(CreatePost.this, "Post created successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CreatePost.this, PostConfirmation.class);
                startActivity(intent);
            } else {
                // Handle error
                Toast.makeText(CreatePost.this, "Failed to create post", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private String getNationalIDFromSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return preferences.getString("Nat_ID", "");
    }
}
