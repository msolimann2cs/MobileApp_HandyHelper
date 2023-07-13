package com.mariam.registeration;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;


public class ProfileSettings extends AppCompatActivity{
    ShapeableImageView Homebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        Homebtn = (ShapeableImageView) findViewById(R.id.homeBtn);

        Homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileSettings.this, HomeActivity.class);
                startActivity(intent);
            }
        });

// Change status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.navy_blue));
        }
        TextView profile_button = findViewById(R.id.profile_tab);
        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(ProfileSettings.this, ProfileMain.class);
                startActivity(intent);
            }
        });
        TextView wallet_button = findViewById(R.id.wallet_tab);
        wallet_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(ProfileSettings.this, ProfileWallet.class);
                startActivity(intent);
            }
        });


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
                startActivity(intent);
            }
        });
        TextView customer_support_button = findViewById(R.id.customer_support);
        customer_support_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileSettings.this, CustomerSupportMain.class);
                startActivity(intent);
            }
        });

    }
}