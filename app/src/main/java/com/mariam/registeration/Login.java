package com.mariam.registeration;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mariam.registeration.services.HandyAPI;
import com.mariam.registeration.services.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Blob;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Login extends AppCompatActivity {
    private HandyAPI my_api = new HandyAPI();
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;
    private TextView mSignUpTextView;
    private TextView mForgetPasswordTextView;
    private View backgroundView;
    private TextView errorMessageTextView;
    Context context;

    ArrayList<User> Users = new ArrayList<User>();
    Boolean logged = false;
    String responseData="";
    User temp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;

        Users.add(new User("Mariam", "mariam@gmail.com",
                "mariam", "Female", 2000, 10,
                10, "10101010101010"));

        backgroundView = findViewById(R.id.background_view);
        backgroundView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });

        mEmailEditText = findViewById(R.id.editTextTextEmailAddress);
        mPasswordEditText = findViewById(R.id.editTextTextPassword);
        mLoginButton = findViewById(R.id.Login);
        mSignUpTextView = findViewById(R.id.SignUp);
        mForgetPasswordTextView = findViewById(R.id.forgetpass);

        errorMessageTextView = findViewById(R.id.error_message);
        errorMessageTextView.setVisibility(View.GONE);


        // Set click listeners for buttons
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve email and password entered
                String email = mEmailEditText.getText().toString().trim();
                String password = mPasswordEditText.getText().toString().trim();

                // Perform login action
                if (TextUtils.isEmpty(email)) {
                    mEmailEditText.setError("Please enter your email");
                } else if (TextUtils.isEmpty(password)) {
                    mPasswordEditText.setError("Please enter your password");
                } else {

                    final String API_URL = "http://"+my_api.API_LINK+"/login";

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                URL url = new URL(API_URL+ "?email=" + mEmailEditText.getText().toString()
                                        + "&pass=" + mPasswordEditText.getText().toString());
//
                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                conn.setRequestMethod("GET"); // Replace with the HTTP method you want to use
//                                conn.setRequestProperty("Content-Type", "application/json"); // Replace with the content type you expect to send/receive
//                                conn.setDoOutput(true);
                                System.out.println(url);
                                int responseCode = conn.getResponseCode();
                                System.out.println(responseCode);
                                if (responseCode == HttpURLConnection.HTTP_OK) { // Replace with the expected response code
                                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                    String inputLine;
                                    StringBuffer response = new StringBuffer();
                                    while ((inputLine = in.readLine()) != null) {
                                        response.append(inputLine);
                                        System.out.println(response);
                                    }
                                    in.close();

                                    responseData = response.toString();
                                    System.out.println(responseData);
                                    if (!responseData.isEmpty())
                                    {
                                        Log.i("found","found");
                                        JSONObject obj = new JSONObject(responseData);
                                        temp = new User(
                                                obj.getString("username"),
                                                obj.getString("email"),
                                                obj.getString("pass"),
                                                obj.getString("gender"),
                                            0,0,0, obj.getString("national_id"));
                                        temp.setPhone( obj.getString("phone_number"));
                                        temp.setInterest( obj.getString("interests"));
                                        temp.setNotify( obj.getInt("notify"));
                                        temp.setDescription( obj.getString("description"));
                                        temp.setImageBytes(obj.getString("image").getBytes());
//                                        temp.setImageBytes(obj.getString("image"));
                                        Login.this.logged=true;
                                        Login.this.update_move(temp);
                                    }
                                } else {
                                    Login.this.logged=false;
                                    System.out.println("Not logged");
//                                    Login.this.notlogged();
                                    String errorResponse = "HTTP error code: " + responseCode;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }).start();
//                    if(!logged)
//                    {
//                        Login.this.errorMessageTextView.setVisibility(View.VISIBLE);
//                        Login.this.mEmailEditText.setBackgroundResource(R.drawable.red_roundedrec);
//                        Login.this.mPasswordEditText.setBackgroundResource(R.drawable.red_roundedrec);
//                    }
                }
            }
        });

        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SignUp.class);
                context.startActivity(i);
            }
        });

        mForgetPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform forget password action
            }
        });

    }
    public void notlogged() {
        System.out.println("Not logged");
        errorMessageTextView.setVisibility(View.VISIBLE);
        mEmailEditText.setBackgroundResource(R.drawable.red_roundedrec);
        mPasswordEditText.setBackgroundResource(R.drawable.red_roundedrec);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    private void update_move(User temp)
    {
        UserSession.getInstance().setLoggedUser(temp);
        System.out.println(temp.getNat_ID());
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", temp.getUsername());
        editor.putString("email", temp.getEmail());
        editor.putString("pass", temp.getPass());
        editor.putString("gender", temp.getGender());
        editor.putString("birth", temp.getDate_of_birth());
        editor.putString("Nat_ID", temp.getNat_ID());
        editor.putString("phone", temp.getPhone());
        editor.putString("interest", temp.getInterest());
        editor.putInt("notify", temp.getNotify());
        editor.putString("description", temp.getDescription());
        editor.putString("photo", Base64.encodeToString(temp.getImageBytes(), Base64.DEFAULT));
        editor.apply();
        Intent i = new Intent(Login.this, HomeActivity.class);
        i.putExtra("Uniqid","From_Login");
        Login.this.startActivity(i);
    }
}