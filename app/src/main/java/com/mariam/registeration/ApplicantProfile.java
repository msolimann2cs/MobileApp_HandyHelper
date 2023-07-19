package com.mariam.registeration;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mariam.registeration.screens.profile.ProfileSettings;
import com.mariam.registeration.services.HandyAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApplicantProfile extends AppCompatActivity {
    private static HandyAPI my_api = new HandyAPI();
    private static final String API_BASE_URL = "http://" +my_api.API_LINK; // Replace with your API base URL
    private static final String USER_ENDPOINT = "/user/";

    private ImageButton backButton;
    private ImageView userImage;
    private TextView userName;
    private TextView occupation;
    private TextView userDescription;
    private TextView gardeningButton;
    private TextView petcareButton;
    private TextView carcareButton;
    private TextView installationButton;
    private TextView transportationButton;
    private TextView ratingTextView; // Add the TextView for displaying the rating

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        backButton = findViewById(R.id.backButton);
        userImage = findViewById(R.id.userImage);
        userName = findViewById(R.id.userName);
        occupation = findViewById(R.id.occupation);
        userDescription = findViewById(R.id.userDescription);
        gardeningButton = findViewById(R.id.gardeningButton);
        petcareButton = findViewById(R.id.petcareButton);
        carcareButton = findViewById(R.id.carcareButton);
        installationButton = findViewById(R.id.installationButton);
        transportationButton = findViewById(R.id.transportationButton);
        ratingTextView = findViewById(R.id.userRating); // Initialize the TextView for rating

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String userId = "11111111111111"; // Replace with the actual user ID
        String url = API_BASE_URL + USER_ENDPOINT + userId;
        new RetrieveUserDetailsTask().execute(url);

        ImageView icon1 = findViewById(R.id.icon1);
        ImageView icon3 = findViewById(R.id.icon3);


        icon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle icon1 click here
                Intent intent = new Intent(ApplicantProfile.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        icon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle icon1 click here
                Intent intent = new Intent(ApplicantProfile.this, ProfileSettings.class);
                startActivity(intent);
            }
        });
    }

    private void populateUserDetails(JSONObject userDetails) {
        try {
            String name = userDetails.getString("name");
            String description = userDetails.getString("description");
            String interests = userDetails.getString("interests");
            float rating = (float) userDetails.getDouble("rating"); // Retrieve the rating value

            userName.setText(name);
            userDescription.setText(description);
            ratingTextView.setText(String.valueOf(rating)); // Set the rating value to the TextView
            Log.e("TAG", "interests are "+interests);

            if (interests.contains("Gardening")) {
                gardeningButton.setVisibility(View.VISIBLE);
            }
            if (interests.contains("Pet")) {
                petcareButton.setVisibility(View.VISIBLE);
            }
            if (interests.contains("Car")) {
                carcareButton.setVisibility(View.VISIBLE);
            }
            if (interests.contains("Installation")) {
                installationButton.setVisibility(View.VISIBLE);
            }
            if (interests.contains("Transportation")) {
                transportationButton.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            Log.e("ApplicantProfile", "Error parsing user details: " + e.getMessage());
        }
    }

    private class RetrieveUserDetailsTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... urls) {
            String url = urls[0];
            JSONObject userDetails = null;

            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    userDetails = new JSONObject(response.toString());
                } else {
                    Log.e("ApplicantProfile", "HTTP response code: " + responseCode);
                }

                connection.disconnect();
            } catch (IOException | JSONException e) {
                Log.e("ApplicantProfile", "Error retrieving user details: " + e.getMessage());
            }

            return userDetails;
        }

        @Override
        protected void onPostExecute(JSONObject userDetails) {
            if (userDetails != null) {
                populateUserDetails(userDetails);
            }
        }
    }
}



