package com.mariam.registeration;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class Login extends AppCompatActivity {
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context= this;

        Users.add(new User("mariam","mariam@gmail.com",
                "mariam","Female",2000,10,
                10,"12345678912345"));

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
                    for(User user : Users)
                    {
                        if (user.getEmail().equals(mEmailEditText.getText().toString())
                        && user.getPass().equals(mPasswordEditText.getText().toString()))
                        {
                            logged = true;
                            Intent i = new Intent(context,HomeActivity.class);
                            i.putExtra("current_user", user);
                            context.startActivity(i);
                        }
                    }
                    if(!logged)
                    {
                        errorMessageTextView.setVisibility(View.VISIBLE);
                        mEmailEditText.setBackgroundResource(R.drawable.red_roundedrec);
                        mPasswordEditText.setBackgroundResource(R.drawable.red_roundedrec);
                    }


                }
            }
        });

        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,SignUp.class);
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
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}