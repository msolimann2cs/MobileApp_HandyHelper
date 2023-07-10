package com.mariam.registeration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class Register_success extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_success);
        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user_data");
        if (user != null) {
            // Do something with the User object
            Log.d("SecondActivity", "Received user: "
                    + user.getUsername() + ", " + user.getPass() +
                    ", " + user.getEmail() + ", " + user.getGender()
                    + ", " + user.getNat_ID()+ ", " + user.getB_day()
                    + "/" + user.getB_month() + "/" + user.getB_year());
        }
    }
}