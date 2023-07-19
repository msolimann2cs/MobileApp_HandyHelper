package com.mariam.registeration.screens.profile;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.mariam.registeration.HomeActivity;
import com.mariam.registeration.MainActivity;
import com.mariam.registeration.MyRequests;
import com.mariam.registeration.R;
import com.mariam.registeration.User;
import com.mariam.registeration.services.DatabaseCallback;
import com.mariam.registeration.services.DatabaseManager;
import com.mariam.registeration.services.HandyAPI;
import com.mariam.registeration.services.UserSession;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class ProfileSettings extends AppCompatActivity {
    ShapeableImageView Homebtn;
    private DatabaseManager database;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

//        Intent intent = getIntent();
        User current_user = UserSession.getInstance().getLoggedUser();
        String username = current_user.getUsername();
//        User current_user = (User) intent.getSerializableExtra("current_user");
        Future<String> userDetailsFuture = executorService.submit(new ProfileSettings.GetUserDetailsTask(username));
        try {
            String result = userDetailsFuture.get();

            // Handle the result here
            // Parse the JSON response and extract the specific columns
            // This code should remain the same as before
            JSONObject jsonResult = new JSONObject(result);
            String rating = jsonResult.getString("rating");

//            System.out.println(interests);

            // Update the UI with the retrieved user details
            TextView ratingField = findViewById(R.id.rating);
            ratingField.setText(rating);




        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        LinearLayout Homebtn = findViewById(R.id.homeBtn);

        Homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileSettings.this, HomeActivity.class);
                finish();
                startActivity(intent);
            }
        });
        TextView name = findViewById(R.id.name);
        name.setText(current_user.getUsername());
        TextView navPost = findViewById(R.id.navPost);
        navPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to another page
                Intent intent = new Intent(ProfileSettings.this, MyRequests.class);
                finish();
                startActivity(intent);
            }
        });

// Change status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.navy_blue));
        }

        ShapeableImageView profile_picture = findViewById(R.id.profile_picture);
        profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start FullscreenImageActivity and pass the image data
                Intent intent = new Intent(ProfileSettings.this, EnlargedProfilePicture.class);
                intent.putExtra("imageResId", R.drawable.profile_photo_demo); // Pass the image resource ID or other image data
                startActivity(intent);
            }
        });

        TextView profile_button = findViewById(R.id.profile_tab);
        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showBottomSheet();
                finish();
                Intent intent = new Intent(ProfileSettings.this, ProfileMain.class);
                startActivity(intent);
            }
        });
//        TextView wallet_button = findViewById(R.id.wallet_tab);
//        wallet_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//                Intent intent = new Intent(ProfileSettings.this, ProfileWallet.class);
//                startActivity(intent);
//            }
//        });


        TextView logout_button = findViewById(R.id.log_out);
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileSettings.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


        TextView account_info_button = findViewById(R.id.account_info);
        account_info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileSettings.this, AccountInfo.class);
                startActivity(intent);
            }
        });
        TextView change_passowrd_button = findViewById(R.id.change_password);
        change_passowrd_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileSettings.this, ChangePassword.class);
                //intent.putExtra("current_user", current_user);
                startActivity(intent);
            }
        });
//        TextView customer_support_button = findViewById(R.id.customer_support);
//        customer_support_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ProfileSettings.this, CustomerSupportMain.class);
//                startActivity(intent);
//            }
//        });

    }

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

    private void showBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.activity_profile_main, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

}