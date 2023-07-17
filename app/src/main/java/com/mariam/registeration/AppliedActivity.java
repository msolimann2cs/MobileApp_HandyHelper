package com.mariam.registeration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AppliedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applied);
        Button viewApps = (Button) findViewById(R.id.ViewApps);
        Button goHome = (Button) findViewById(R.id.returnHome);
        viewApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppliedActivity.this, MyApplications.class);
                startActivity(intent);
            }
        });

        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppliedActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}