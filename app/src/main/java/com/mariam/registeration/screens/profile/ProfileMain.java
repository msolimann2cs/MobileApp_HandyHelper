package com.mariam.registeration.screens.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;

import com.mariam.registeration.HomeActivity;
import com.mariam.registeration.R;
import com.mariam.registeration.User;
import com.mariam.registeration.services.HandyAPI;
import com.mariam.registeration.services.UserSession;
import com.mariam.registeration.services.db_manager2;

import org.json.JSONException;
import org.json.JSONObject;


public class ProfileMain extends AppCompatActivity {
    private static HandyAPI my_api = new HandyAPI();
    LinearLayout parentLayout;
    boolean category_edit = false;
    boolean location_edit = false;
    ShapeableImageView Homebtn;

    String image;
    String rating;
    List<String> interests = new ArrayList<>();
    String description;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();


// Now you can use the userList in Activity B

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_main);

        LinearLayout parentLayout = findViewById(R.id.parent_layout);
//        User current_user = new User("Mariam", "mariam3@gmail.com",
//                "mariam", "Female", 2000, 10,
//                10, "11111111111111");

//        Intent intent = getIntent();
//        User current_user = (User) intent.getSerializableExtra("current_user");
        User current_user = UserSession.getInstance().getLoggedUser();

        // ------------------------------
        // Replace "YOUR_NATIONAL_ID" with the actual national ID you want to retrieve details for
        String username = current_user.getUsername();
        LinearLayout category_tab = findViewById(R.id.categories_tab);
//        GetUserDetailsTask task = new GetUserDetailsTask();
//        task.execute("mariam");
        LinearLayout cat1 = findViewById(R.id.cat1);
        LinearLayout cat2 = findViewById(R.id.cat2);
        LinearLayout cat3 = findViewById(R.id.cat3);
        Future<String> userDetailsFuture = executorService.submit(new GetUserDetailsTask(username));
        try {
            String result = userDetailsFuture.get();

            // Handle the result here
            // Parse the JSON response and extract the specific columns
            // This code should remain the same as before
            JSONObject jsonResult = new JSONObject(result);
            image = jsonResult.getString("image");
            rating = jsonResult.getString("rating");
            String interests_string = jsonResult.getString("interests");
            String[] itemsArray = interests_string.split(", ");
            interests.addAll(Arrays.asList(itemsArray));
            description = jsonResult.getString("description");
//            System.out.println(interests);

            // Update the UI with the retrieved user details
            TextView ratingField = findViewById(R.id.rating);
            ratingField.setText(rating);

            TextView aboutMeDescTextView = findViewById(R.id.about_me_desc);
            aboutMeDescTextView.setText(description);

            for(int i = 0; i < interests.size(); i++){
                addTextViewToCategory(interests.get(i), cat1, cat2, cat3);
            }



        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        // ------------------------------

        LinearLayout Homebtn =  findViewById(R.id.homeBtn);

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
//        LinearLayout categoriesLayout1 = findViewById(R.id.categories);
//        LinearLayout categoriesLayout2 = findViewById(R.id.categories_line2);

        // TextView locationTextView = findViewById(R.id.location);
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
                    category_tab.setBackground(borderDrawable);
//                    categoriesLayout1.setBackground(borderDrawable);
//                    categoriesLayout2.setBackground(borderDrawable);
                    // locationTextView.setBackground(borderDrawable);
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
                    category_tab.setBackground(null);
//                    categoriesLayout1.setBackground(null);
//                    categoriesLayout2.setBackground(null);
                    // locationTextView.setBackground(null);
                    aboutMeDescEditTextRef.set(null);
                    category_edit = false;
                    location_edit = false;

                    editProfileButton.setText("Edit Profile");
                }
            }
        });


        category_tab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (category_edit)
                    showCategory();
            }
        });
//        LinearLayout category_button1 = findViewById(R.id.categories);
//        LinearLayout category_button2 = findViewById(R.id.categories_line2);
//        // TextView location_button = findViewById(R.id.location);
//        category_button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (category_edit)
//                    showCategory();
//            }
//        });
//        category_button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (category_edit)
//                    showCategory();
//            }
//        });

//        location_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (location_edit)
//                    showLocation();
//            }
//        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Shutdown the executor service when the activity is destroyed
        executorService.shutdown();
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
        final boolean[] interestsChanged = {false};
        int interests_size = interests.size();
        LinearLayout cat1 = findViewById(R.id.cat1);
        LinearLayout cat2 = findViewById(R.id.cat2);
        LinearLayout cat3 = findViewById(R.id.cat3);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.activity_category_edit, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        System.out.println(interests);
        LinearLayout pet_care = bottomSheetDialog.findViewById(R.id.pet_care);
        LinearLayout installtion = bottomSheetDialog.findViewById(R.id.installation);
        LinearLayout gardening = bottomSheetDialog.findViewById(R.id.gardening);
        LinearLayout transportation = bottomSheetDialog.findViewById(R.id.trasportation);
        LinearLayout cars = bottomSheetDialog.findViewById(R.id.cars);

        if(interests.contains("Petcare")){
            int drawableResourceId = R.drawable.button_style2_selected;
            Drawable drawable = ContextCompat.getDrawable(this, drawableResourceId);
            pet_care.setBackground(drawable);
        }else{
            int drawableResourceId = R.drawable.button_style2;
            Drawable drawable = ContextCompat.getDrawable(this, drawableResourceId);
            pet_care.setBackground(drawable);
        }
        if(interests.contains("Installation")){
            int drawableResourceId = R.drawable.button_style3_selected;
            Drawable drawable = ContextCompat.getDrawable(this, drawableResourceId);
            installtion.setBackground(drawable);
        }
        else{
            int drawableResourceId = R.drawable.button_style3;
            Drawable drawable = ContextCompat.getDrawable(this, drawableResourceId);
            installtion.setBackground(drawable);
        }
        if(interests.contains("Gardening")){
            int drawableResourceId = R.drawable.button_style3_selected;
            Drawable drawable = ContextCompat.getDrawable(this, drawableResourceId);
            gardening.setBackground(drawable);
        }
        else{
            int drawableResourceId = R.drawable.button_style3;
            Drawable drawable = ContextCompat.getDrawable(this, drawableResourceId);
            gardening.setBackground(drawable);
        }
        if(interests.contains("Transportation")){
            int drawableResourceId = R.drawable.button_style3_selected;
            Drawable drawable = ContextCompat.getDrawable(this, drawableResourceId);
            transportation.setBackground(drawable);
        }else{
            int drawableResourceId = R.drawable.button_style3;
            Drawable drawable = ContextCompat.getDrawable(this, drawableResourceId);
            transportation.setBackground(drawable);
        }
        if(interests.contains("Cars")){
            int drawableResourceId = R.drawable.button_style4_selected;
            Drawable drawable = ContextCompat.getDrawable(this, drawableResourceId);
            cars.setBackground(drawable);
        }else{
            int drawableResourceId = R.drawable.button_style4;
            Drawable drawable = ContextCompat.getDrawable(this, drawableResourceId);
            cars.setBackground(drawable);
        }
        boolean isEmptyInterests = interests.isEmpty();
        boolean containsNullValuesOrEmptyStrings = interests.stream()
                .allMatch(value -> value == null || value.isEmpty());

        if (isEmptyInterests || containsNullValuesOrEmptyStrings) {
            // Clear the interests list
            interests.clear();
            interests_size = 0;
        }


        int finalInterests_size = interests_size;
        pet_care.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selected = R.drawable.button_style2_selected;
                Drawable selectedDrawable = ContextCompat.getDrawable(v.getContext(), selected);
                Drawable currentDrawable = v.getBackground();

                if (currentDrawable.getConstantState().equals(selectedDrawable.getConstantState())) {
                    int unselected = R.drawable.button_style2;
                    Drawable unselectedDrawable = ContextCompat.getDrawable(v.getContext(), unselected);
                    v.setBackground(unselectedDrawable);

                    // Find the index of "Petcare" in the interests list
                    int petcareIndex = interests.indexOf("Petcare");
                    if (petcareIndex != -1) {
                        interests.remove(petcareIndex);

                        // Function to remove a TextView from a LinearLayout if it exists
                        removeTextViewIfExists(cat1, "Petcare");
                        removeTextViewIfExists(cat2, "Petcare");
                        removeTextViewIfExists(cat3, "Petcare");
                    }
                }
                else{
                    v.setBackground(selectedDrawable);
                    interests.add("Petcare");
                    addTextViewToCategory("Petcare", cat1, cat2, cat3);
                }

                interestsChanged[0] = true;
//                UpdateInterestsTask task = new UpdateInterestsTask();
//                task.execute(UserSession.getInstance().getLoggedUser().getUsername(), result);
            }
        });
        installtion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selected = R.drawable.button_style3_selected;
                Drawable selectedDrawable = ContextCompat.getDrawable(v.getContext(), selected);
                Drawable currentDrawable = v.getBackground();

                if (currentDrawable.getConstantState().equals(selectedDrawable.getConstantState())) {
                    int unselected = R.drawable.button_style2;
                    Drawable unselectedDrawable = ContextCompat.getDrawable(v.getContext(), unselected);
                    v.setBackground(unselectedDrawable);

                    // Find the index of "Petcare" in the interests list
                    int petcareIndex = interests.indexOf("Installation");
                    if (petcareIndex != -1) {
                        interests.remove(petcareIndex);

                        // Function to remove a TextView from a LinearLayout if it exists
                        removeTextViewIfExists(cat1, "Installation");
                        removeTextViewIfExists(cat2, "Installation");
                        removeTextViewIfExists(cat3, "Installation");
                    }
                }
                else{
                    v.setBackground(selectedDrawable);
                    interests.add("Installation");
                    addTextViewToCategory("Installation", cat1, cat2, cat3);
                }
                interestsChanged[0] = true;
            }
        });
        gardening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selected = R.drawable.button_style3_selected;
                Drawable selectedDrawable = ContextCompat.getDrawable(v.getContext(), selected);
                Drawable currentDrawable = v.getBackground();

                if (currentDrawable.getConstantState().equals(selectedDrawable.getConstantState())) {
                    int unselected = R.drawable.button_style2;
                    Drawable unselectedDrawable = ContextCompat.getDrawable(v.getContext(), unselected);
                    v.setBackground(unselectedDrawable);

                    // Find the index of "Petcare" in the interests list
                    int petcareIndex = interests.indexOf("Gardening");
                    if (petcareIndex != -1) {
                        interests.remove(petcareIndex);

                        // Function to remove a TextView from a LinearLayout if it exists
                        removeTextViewIfExists(cat1, "Gardening");
                        removeTextViewIfExists(cat2, "Gardening");
                        removeTextViewIfExists(cat3, "Gardening");
                    }
                }
                else{
                    v.setBackground(selectedDrawable);
                    interests.add("Gardening");
                    addTextViewToCategory("Gardening", cat1, cat2, cat3);
                }
                interestsChanged[0] = true;
            }
        });
        transportation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selected = R.drawable.button_style3_selected;
                Drawable selectedDrawable = ContextCompat.getDrawable(v.getContext(), selected);
                Drawable currentDrawable = v.getBackground();

                if (currentDrawable.getConstantState().equals(selectedDrawable.getConstantState())) {
                    int unselected = R.drawable.button_style2;
                    Drawable unselectedDrawable = ContextCompat.getDrawable(v.getContext(), unselected);
                    v.setBackground(unselectedDrawable);

                    // Find the index of "Petcare" in the interests list
                    int petcareIndex = interests.indexOf("Transportation");
                    if (petcareIndex != -1) {
                        interests.remove(petcareIndex);

                        // Function to remove a TextView from a LinearLayout if it exists
                        removeTextViewIfExists(cat1, "Transportation");
                        removeTextViewIfExists(cat2, "Transportation");
                        removeTextViewIfExists(cat3, "Transportation");
                    }
                }
                else{
                    v.setBackground(selectedDrawable);
                    interests.add("Transportation");
                    addTextViewToCategory("Transportation", cat1, cat2, cat3);
                }
                interestsChanged[0] = true;
            }
        });
        cars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selected = R.drawable.button_style4_selected;
                Drawable selectedDrawable = ContextCompat.getDrawable(v.getContext(), selected);
                Drawable currentDrawable = v.getBackground();

                if (currentDrawable.getConstantState().equals(selectedDrawable.getConstantState())) {
                    int unselected = R.drawable.button_style2;
                    Drawable unselectedDrawable = ContextCompat.getDrawable(v.getContext(), unselected);
                    v.setBackground(unselectedDrawable);

                    // Find the index of "Petcare" in the interests list
                    int petcareIndex = interests.indexOf("Cars");
                    if (petcareIndex != -1) {
                        interests.remove(petcareIndex);

                        // Function to remove a TextView from a LinearLayout if it exists
                        removeTextViewIfExists(cat1, "Cars");
                        removeTextViewIfExists(cat2, "Cars");
                        removeTextViewIfExists(cat3, "Cars");
                    }
                }
                else{
                    v.setBackground(selectedDrawable);
                    interests.add("Cars");
                    addTextViewToCategory("Cars", cat1, cat2, cat3);
                }
                interestsChanged[0] = true;
            }
        });


//        System.out.println(result);

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // Check if interests have been changed

//                int childCount = category_tab.getChildCount();


// Check if the interests list is empty or contains only null values or empty strings


                if (interestsChanged[0]) {
                    // Update the database
                    StringBuilder stringBuilder = new StringBuilder();

                    for (int i = 0; i < interests.size(); i++) {
                        stringBuilder.append(interests.get(i));
                        if (i < interests.size() - 1) {
                            stringBuilder.append(", ");
                        }
                    }

                    String result = stringBuilder.toString();

                    UpdateInterestsTask task = new UpdateInterestsTask();
                    task.execute(UserSession.getInstance().getLoggedUser().getUsername(), result);
                }


            }
        });


        bottomSheetDialog.show();



    }


    private class UpdateDescriptionTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String rawDescription = params[1];
            String apiUrl = "http://"+my_api.API_LINK+"/users/" + username + "/description";

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

//    private class GetUserDetailsTask extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... params) {
//            String username = params[0];
//            System.out.println(username);
//            String apiUrl = "http://"+my_api.API_LINK+"/users/" + username + "/details";
//
//            try {
//                URL url = new URL(apiUrl);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("GET");
//                connection.setRequestProperty("Content-Type", "application/json");
//
//                int responseCode = connection.getResponseCode();
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    InputStream inputStream = connection.getInputStream();
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                    StringBuilder response = new StringBuilder();
//                    String line;
//                    while ((line = bufferedReader.readLine()) != null) {
//                        response.append(line);
//                    }
//                    bufferedReader.close();
//                    inputStream.close();
//
//                    return response.toString();
//                } else {
//                    return "Error: " + responseCode;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                return "Error: " + e.getMessage();
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            // Handle the result here
//            // Parse the JSON response and extract the specific columns
//            try {
//                JSONObject jsonResult = new JSONObject(result);
//                image = jsonResult.getString("image");
//                rating = jsonResult.getString("rating");
//                interests = jsonResult.getString("interests");
//                description = jsonResult.getString("description");
//
//                TextView rating_field = findViewById(R.id.rating);
//                rating_field.setText(rating);
//
//                TextView aboutMeDescTextView = findViewById(R.id.about_me_desc);
//                aboutMeDescTextView.setText(description);
//
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//                // Handle JSON parsing error
//            }
//        }
//    }

    // --------- new old
//public class GetUserDetailsTask extends AsyncTask<String, Void, String> {
//    private static final HandyAPI my_api = new HandyAPI();
//    private static final String API_URL_BASE = "http://" + my_api.API_LINK + "/users/%s/details";
//
//    @Override
//    protected String doInBackground(String... params) {
//        String username = params[0];
//        String apiUrl = String.format(API_URL_BASE, username);
//
//        HttpURLConnection urlConnection = null;
//        BufferedReader reader = null;
//        StringBuilder stringBuilder = new StringBuilder();
//
//        try {
//            URL url = new URL(apiUrl);
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestMethod("GET");
//
//            // Read the response
//            InputStream inputStream = urlConnection.getInputStream();
//            reader = new BufferedReader(new InputStreamReader(inputStream));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                stringBuilder.append(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (urlConnection != null) {
//                urlConnection.disconnect();
//            }
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return stringBuilder.toString();
//    }
//
//    @Override
//    protected void onPostExecute(String result) {
//        // Handle the result here
//        // Parse the JSON response and extract the specific columns
//        try {
//            JSONObject jsonResult = new JSONObject(result);
//            String image = jsonResult.getString("image");
//            String rating = jsonResult.getString("rating");
//            String interests = jsonResult.getString("interests");
//            String description = jsonResult.getString("description");
//
//            // Update the UI with the retrieved user details
//            TextView ratingField = findViewById(R.id.rating);
//            ratingField.setText(rating);
//
//            TextView aboutMeDescTextView = findViewById(R.id.about_me_desc);
//            aboutMeDescTextView.setText(description);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            // Handle JSON parsing error
//        }
//    }
//}


    private class GetUserDetailsTask implements Callable<String> {
        private HandyAPI my_api = new HandyAPI();
        private String API_URL_BASE = "http://" + my_api.API_LINK + "/users/%s/details";
        private String username;

        GetUserDetailsTask(String username) {
            this.username = username;
        }

        @Override
        public String call() throws Exception {
            String apiUrl = String.format(API_URL_BASE, username);

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
    }

    private TextView createTextView(String text) {
        Context context = this;

        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textView.setTextColor(Color.BLACK);

        int drawableResourceId = R.drawable.button_style1_mod;
        Drawable drawable = ContextCompat.getDrawable(this, drawableResourceId);
        textView.setBackground(drawable);

        // Add padding to the TextView
        int padding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                20,
                context.getResources().getDisplayMetrics()
        );
        textView.setPadding(padding, 5, padding, 5);

        // Set line spacing
        textView.setLineSpacing(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                4,
                context.getResources().getDisplayMetrics()
        ), 1.0f);

        // Set gravity
        textView.setGravity(Gravity.START | Gravity.TOP);

        // Set text size
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        // Set text color
        textView.setTextColor(ContextCompat.getColor(context, R.color.black));

        // Add layout parameters to the TextView
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(10, 0, 0, 0);
        textView.setLayoutParams(params);

        return textView;
    }

    private class UpdateInterestsTask {
        private static final String BASE_URL = "http://" + my_api.API_LINK; // Replace with your server's base URL

        public void execute(String username, String interests) {

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> future = executor.submit(() -> performUpdate(username, interests));

            try {
                String result = future.get(); // This blocks until the task is completed

                // Handle the result here
                if (result.equals("success")) {
                    // Interests updated successfully
                } else {
                    // API call failed, handle the error
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                // Handle exceptions here
            } finally {
                executor.shutdown(); // Shutdown the executor
            }
        }

        private String performUpdate(String username, String interests) {
            String apiUrl = BASE_URL + "/update_interests";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", username);
                jsonObject.put("interests", interests);
                String requestBody = jsonObject.toString();

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
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }
    }

    private void removeTextViewIfExists(LinearLayout layout, String text) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                if (textView.getText().toString().equals(text)) {
                    layout.removeView(textView);
                    break; // Stop searching after removing the first occurrence
                }
            }
        }
    }

    private void addTextViewToCategory(String text, LinearLayout cat1, LinearLayout cat2, LinearLayout cat3) {
        int cat1ChildCount = cat1.getChildCount();
        int cat2ChildCount = cat2.getChildCount();
        int cat3ChildCount = cat3.getChildCount();

        if (cat1ChildCount < 2) {
            TextView textView = createTextView(text);
            cat1.addView(textView);
        } else if (cat2ChildCount < 2) {
            TextView textView = createTextView(text);
            cat2.addView(textView);
        } else if (cat3ChildCount < 2) {
            TextView textView = createTextView(text);
            cat3.addView(textView);
        } else {
            // All categories have two elements, so we'll add to cat1 as the first priority
            TextView textView = createTextView(text);
            cat1.addView(textView);
        }
    }






}
