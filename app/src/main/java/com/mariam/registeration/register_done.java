package com.mariam.registeration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
public class register_done extends AppCompatActivity {

    private TextView greeting;
    private User user;
    private Button finish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_done);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user_data");
        if (user != null) {
            // Do something with the User object
            Log.d("SecondActivity", "Received user: "
                    + user.getUsername() + ", " + user.getPass() +
                    ", " + user.getEmail() + ", " + user.getGender()
                    + ", " + user.getNat_ID()+ ", " + user.getDate_of_birth()
                    + "," + user.getPhone()+ "," + user.getNotify());
        }
        greeting = (TextView) findViewById(R.id.hello);
        String name = "Hello, " ;
//        +user.getUsername();
        greeting.setText(name);

        finish = findViewById(R.id.finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("username", user.getUsername());
                editor.putString("email", user.getEmail());
                editor.putString("pass", user.getPass());
                editor.putString("gender", user.getGender());
                editor.putString("birth", user.getDate_of_birth());
                editor.putString("Nat_ID", user.getNat_ID());
                editor.putString("phone", user.getPhone());
                editor.putString("interest", user.getInterest());
                editor.putInt("notify", user.getNotify());
                editor.putString("description", user.getDescription());
                editor.apply();
                Log.i("done","done");
                final String API_URL = "http://192.168.1.8:3000/adduser";
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(API_URL);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("POST"); // Replace with the HTTP method you want to use
                            conn.setRequestProperty("Content-Type", "application/json"); // Replace with the content type you expect to send/receive
                            conn.setDoOutput(true);

                            ObjectMapper mapper = new ObjectMapper();
                            String jsonString = null;
                            try {
                                jsonString = mapper.writeValueAsString(user);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            System.out.println(jsonString);

                            OutputStream os = conn.getOutputStream();
                            os.write(jsonString.getBytes());
                            os.flush();
                            os.close();

                            int responseCode = conn.getResponseCode();
                            if (responseCode == HttpURLConnection.HTTP_OK) { // Replace with the expected response code
                                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                String inputLine;
                                StringBuffer response = new StringBuffer();
                                while ((inputLine = in.readLine()) != null) {
                                    response.append(inputLine);
                                }
                                in.close();

                                String responseData = response.toString(); // Replace this with whatever you want to do with the response data
                            } else {
                                String errorResponse = "HTTP error code: " + responseCode; // Replace this with whatever you want to do with the error response
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                Intent i = new Intent(register_done.this, HomeActivity.class);
                register_done.this.startActivity(i);
            }
        });
    }

}