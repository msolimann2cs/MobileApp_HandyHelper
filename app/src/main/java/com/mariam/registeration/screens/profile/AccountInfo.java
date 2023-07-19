package com.mariam.registeration.screens.profile;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
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
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.mariam.registeration.R;
import com.mariam.registeration.User;
import com.mariam.registeration.services.HandyAPI;
import com.mariam.registeration.services.UserSession;

import org.json.JSONException;
import org.json.JSONObject;


public class AccountInfo extends AppCompatActivity {

    private HandyAPI my_api = new HandyAPI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.navy_blue));
        }

        TextView back_btn = findViewById(R.id.back_btn_info);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        User current_user = UserSession.getInstance().getLoggedUser();
        EditText email_edit = findViewById(R.id.editTextEmailProfile);
        email_edit.setText(current_user.getEmail());
        EditText username_edit = findViewById(R.id.editTextUsername);
        username_edit.setText(current_user.getUsername());
        EditText phone_edit = findViewById(R.id.editTextPhoneNumber);
        phone_edit.setText(current_user.getPhone());

        Button submit_btn = findViewById(R.id.submit_btn);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = current_user.getUsername();
                String email = String.valueOf(email_edit.getText());
                String newUsername = String.valueOf(username_edit.getText());
                String phoneNumber = String.valueOf(phone_edit.getText());

                ExecutorService executor = Executors.newSingleThreadExecutor();
                Future<String> future = executor.submit(() -> updateUserInfo(username, email, newUsername, phoneNumber));

                try {
                    String result = future.get(); // This blocks until the task is completed

                    // Handle the API response here
                    if (result.contains("success")) {
                        // User updated successfully
                        Toast.makeText(AccountInfo.this, "User data updated successfully", Toast.LENGTH_SHORT).show();
                        User temp = new User(
                               newUsername,
                                email,
                                current_user.getPass(),
                                current_user.getGender(),
                                0,0,0, current_user.getNat_ID());
                        temp.setPhone( phoneNumber);
                        temp.setInterest( current_user.getInterest());
                        temp.setNotify( current_user.getNotify());
                        temp.setDescription( current_user.getDescription());
                        UserSession.getInstance().setLoggedUser(temp);
                    } else {
                        Toast.makeText(AccountInfo.this, "Error updating user data. Please try again.", Toast.LENGTH_SHORT).show();
                        // Handle API call failure
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle exceptions here
                } finally {
                    executor.shutdown(); // Shutdown the executor
                }


            }
        });
    }


    private String updateUserInfo(String username, String email, String newUsername, String phoneNumber) {
        String apiUrl = "http://"+my_api.API_LINK+"/update_user"; // Replace with your server's API URL

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", username);
            jsonObject.put("email", email);
            jsonObject.put("newUsername", newUsername);
            jsonObject.put("phone_number", phoneNumber);
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
                String response1 = response.toString();
                Log.d("API Response", response1);


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