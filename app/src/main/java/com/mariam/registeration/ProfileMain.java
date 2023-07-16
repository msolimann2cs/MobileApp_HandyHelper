package com.mariam.registeration;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONException;
import org.json.JSONObject;


public class ProfileMain extends AppCompatActivity {
    LinearLayout parentLayout;
    boolean category_edit = false;
    boolean location_edit = false;
    ShapeableImageView Homebtn;
    private UserApiService userApiService;

    String image;
    String rating;
    String interests;
    String description;


// Now you can use the userList in Activity B

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_main);

        LinearLayout parentLayout = findViewById(R.id.parent_layout);

        Intent intent = getIntent();
        User current_user = (User) intent.getSerializableExtra("current_user");

        // ------------------------------
        // Replace "YOUR_NATIONAL_ID" with the actual national ID you want to retrieve details for
        String nationalId = current_user.getNat_ID();
        GetUserDetailsTask task = new GetUserDetailsTask();
        task.execute(nationalId);

        // ------------------------------

        Homebtn = (ShapeableImageView) findViewById(R.id.homeBtn);

        Homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileMain.this, HomeActivity.class);
                startActivity(intent);
            }
        });

// Change status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.navy_blue));
        }
        // ---------------------------------------------
        TextView name = findViewById(R.id.name);
        name.setText(current_user.getUsername());

//        TextView rating_field = findViewById(R.id.rating);
//        rating_field.setText(rating);

        // ---------------------------------------------




        // TextView wallet_button = findViewById(R.id.wallet_tab);
//        wallet_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//                Intent intent = new Intent(ProfileMain.this, ProfileWallet.class);
//                startActivity(intent);
//            }
//        });
        TextView settings_button = findViewById(R.id.settings_tab);
        settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(ProfileMain.this, ProfileSettings.class);
                intent.putExtra("current_user", current_user);
                startActivity(intent);
            }
        });

        ShapeableImageView profile_picture = findViewById(R.id.profile_picture);
        profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start FullscreenImageActivity and pass the image data
                Intent intent = new Intent(ProfileMain.this, EnlargedProfilePicture.class);
                intent.putExtra("imageResId", R.drawable.profile_photo_demo); // Pass the image resource ID or other image data
                startActivity(intent);
            }
        });


        Button editProfileButton = findViewById(R.id.edit_profile);
        TextView aboutMeDescTextView = findViewById(R.id.about_me_desc);
        AtomicReference<EditText> aboutMeDescEditTextRef = new AtomicReference<>(null);
        LinearLayout categoriesLayout1 = findViewById(R.id.categories);
        LinearLayout categoriesLayout2 = findViewById(R.id.categories_line2);
        TextView locationTextView = findViewById(R.id.location);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category_edit = true;
                location_edit = true;
                EditText aboutMeDescEditText = aboutMeDescEditTextRef.get();
                if (aboutMeDescEditText == null) {
                    // Change to Edit mode
                    aboutMeDescEditText = new EditText(ProfileMain.this);
                    aboutMeDescEditText.setId(R.id.about_me_desc);
                    aboutMeDescEditText.setLayoutParams(aboutMeDescTextView.getLayoutParams());
                    aboutMeDescEditText.setText(aboutMeDescTextView.getText());
                    aboutMeDescEditText.setSelection(aboutMeDescEditText.getText().length());
                    aboutMeDescEditText.setTextColor(ContextCompat.getColor(ProfileMain.this, android.R.color.black));

                    // Create a custom drawable for the border
                    Drawable borderDrawable = createBorderDrawable();

                    // Set the custom drawable as the background of the EditText
                    aboutMeDescEditText.setBackground(borderDrawable);
                    categoriesLayout1.setBackground(borderDrawable);
                    categoriesLayout2.setBackground(borderDrawable);
                    locationTextView.setBackground(borderDrawable);
                    aboutMeDescEditTextRef.set(aboutMeDescEditText);

                    ViewGroup parent = (ViewGroup) aboutMeDescTextView.getParent();
                    int index = parent.indexOfChild(aboutMeDescTextView);
                    parent.removeView(aboutMeDescTextView);
                    parent.addView(aboutMeDescEditText, index);


                    editProfileButton.setText("Done");
                } else {
                    // Change back to TextView mode
                    String updatedText = aboutMeDescEditText.getText().toString();
                    aboutMeDescTextView.setText(updatedText);
                    aboutMeDescTextView.setTextColor(ContextCompat.getColor(ProfileMain.this, android.R.color.black));
                    // userApiService = new UserApiService();
                    // userApiService.updateUserDescription(current_user.getNat_ID(), updatedText);
                    String username = current_user.getUsername(); // Replace with the actual username
                    String newDescription = updatedText;
                    UpdateDescriptionTask task = new UpdateDescriptionTask();
                    task.execute(username, newDescription);

                    ViewGroup parent = (ViewGroup) aboutMeDescEditText.getParent();
                    int index = parent.indexOfChild(aboutMeDescEditText);
                    parent.removeView(aboutMeDescEditText);
                    parent.addView(aboutMeDescTextView, index);
                    categoriesLayout1.setBackground(null);
                    categoriesLayout2.setBackground(null);
                    locationTextView.setBackground(null);
                    aboutMeDescEditTextRef.set(null);
                    category_edit = false;
                    location_edit = false;

                    editProfileButton.setText("Edit Profile");
                }
            }
        });


        LinearLayout category_button1 = findViewById(R.id.categories);
        LinearLayout category_button2 = findViewById(R.id.categories_line2);
        TextView location_button = findViewById(R.id.location);
        category_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (category_edit)
                    showCategory();
            }
        });
        category_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (category_edit)
                    showCategory();
            }
        });

        location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (location_edit)
                    showLocation();
            }
        });


    }

    // Create a custom drawable for the border
    private Drawable createBorderDrawable() {
        int strokeWidth = getResources().getDimensionPixelSize(R.dimen.border_stroke_width);
        int strokeColor = ContextCompat.getColor(this, R.color.off_white_darker);
        int cornerRadius = getResources().getDimensionPixelSize(R.dimen.border_corner_radius);

        GradientDrawable borderDrawable = new GradientDrawable();
        borderDrawable.setShape(GradientDrawable.RECTANGLE);
        borderDrawable.setStroke(strokeWidth, strokeColor);
        borderDrawable.setCornerRadius(cornerRadius);

        return borderDrawable;
    }

    private FrameLayout createOverlayLayout() {
        LinearLayout rootLayout = findViewById(R.id.parent_layout); // Replace `root_layout` with the ID of your root layout
        LayoutInflater inflater = LayoutInflater.from(this);
        FrameLayout overlayLayout = (FrameLayout) inflater.inflate(R.layout.activity_category_edit, rootLayout, false);

        overlayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle clicks on the overlay layout (e.g., to dismiss the overlay)
                rootLayout.removeView(overlayLayout); // Remove the overlay layout from the root layout
                // Additional handling if needed
            }
        });

        rootLayout.addView(overlayLayout);

        return overlayLayout;
    }

    private void showCategory() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.activity_category_edit, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void showLocation() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.activity_location_edit, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private class UpdateDescriptionTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String rawDescription = params[1];
            String apiUrl = "http://10.39.1.162:3000/users/" + username + "/description";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                String description = new JSONObject().put("description", rawDescription).toString();
                //String requestBody = "{\"description\":\"" + description + "\"}";
                String requestBody = description;
                Log.d("Request Payload", requestBody);

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(requestBody.getBytes());
                outputStream.flush();
                outputStream.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        response.append(line);
                    }
                    bufferedReader.close();
                    inputStream.close();

                    return response.toString();
                } else {
                    return "Error: " + responseCode;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Handle the result here
            //Toast.makeText(ProfileMain.this, result, Toast.LENGTH_SHORT).show();
        }

    }

    private class GetUserDetailsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String nationalId = params[0];
            String apiUrl = "http://10.39.1.162:3000/users/" + nationalId + "/details";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        response.append(line);
                    }
                    bufferedReader.close();
                    inputStream.close();

                    return response.toString();
                } else {
                    return "Error: " + responseCode;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Handle the result here
            // Parse the JSON response and extract the specific columns
            try {
                JSONObject jsonResult = new JSONObject(result);
                image = jsonResult.getString("image");
                rating = jsonResult.getString("rating");
                interests = jsonResult.getString("interests");
                description = jsonResult.getString("description");

                TextView rating_field = findViewById(R.id.rating);
                rating_field.setText(rating);

                TextView aboutMeDescTextView = findViewById(R.id.about_me_desc);
                aboutMeDescTextView.setText(description);


                // Use the retrieved data as needed
                // e.g., update UI elements with the retrieved values
            } catch (JSONException e) {
                e.printStackTrace();
                // Handle JSON parsing error
            }
        }
    }

}