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

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONException;
import org.json.JSONObject;


public class ChangePassword extends AppCompatActivity{

    private EditText editTextCurrentPassword;
    private EditText editTextNewPassword;
    private EditText editTextConfirmPassword;
    private Button buttonChangePassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.navy_blue));
        }

        Intent intent = getIntent();
        User current_user = (User) intent.getSerializableExtra("current_user");

        editTextCurrentPassword = findViewById(R.id.current_pwd);
        editTextNewPassword = findViewById(R.id.new_pwd);
        editTextConfirmPassword = findViewById(R.id.confirm_pwd);
        buttonChangePassword = findViewById(R.id.submit_btn);

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Retrieve entered passwords
                String currentPassword = editTextCurrentPassword.getText().toString();
                String newPassword = editTextNewPassword.getText().toString();
                String confirmPassword = editTextConfirmPassword.getText().toString();

                // Validate passwords
                if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(ChangePassword.this, "Please enter new password and confirm password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(ChangePassword.this, "New password and confirm password must match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Make network request to update the password
                String apiUrl = "http://192.168.1.5:3000/users/"; // Replace with your API URL
                String username = current_user.getUsername(); // Replace with the username
                ChangePasswordTask task = new ChangePasswordTask();
                task.execute(apiUrl, username, newPassword);
            }
        });
    }

    private class ChangePasswordTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            String apiUrl = params[0];
            String username = params[1];
            String newPassword = params[2];

            try {
                URL url = new URL(apiUrl + username + "/password");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                JSONObject requestBody = new JSONObject();
                requestBody.put("password", newPassword);

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(requestBody.toString().getBytes());
                outputStream.flush();
                outputStream.close();

                return connection.getResponseCode();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Password updated successfully
                Toast.makeText(ChangePassword.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Error updating password
                Toast.makeText(ChangePassword.this, "Error updating password. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }



    }