package com.mariam.registeration;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;


public class ProfileWallet extends AppCompatActivity{
    private ListView listView;
    ShapeableImageView Homebtn;

    private ArrayAdapter<LinearLayout> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_wallet);
        Homebtn = (ShapeableImageView) findViewById(R.id.homeBtn);

        Homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileWallet.this, HomeActivity.class);
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
                Intent intent = new Intent(ProfileWallet.this, ProfileMain.class);
                startActivity(intent);
            }
        });
        TextView settings_button = findViewById(R.id.settings_tab);
        settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(ProfileWallet.this, ProfileSettings.class);
                startActivity(intent);
            }
        });

        listView = findViewById(R.id.list_view);

        // Create an array or list of LinearLayouts to be displayed
        List<LinearLayout> linearLayouts = new ArrayList<>();

        // Add your LinearLayouts to the list
        linearLayouts.add(createLinearLayout());
        linearLayouts.add(createLinearLayout());
        linearLayouts.add(createLinearLayout());
        linearLayouts.add(createLinearLayout());
        linearLayouts.add(createLinearLayout());
        linearLayouts.add(createLinearLayout());
        linearLayouts.add(createLinearLayout());
        linearLayouts.add(createLinearLayout());
        linearLayouts.add(createLinearLayout());
        linearLayouts.add(createLinearLayout());
        // ...

        // Create the custom adapter with the list of LinearLayouts
        adapter = new LinearLayoutAdapter(this, linearLayouts);

        // Set the adapter for the ListView
        listView.setAdapter(adapter);



    }


    private LinearLayout createLinearLayout() {
        // Create a new LinearLayout programmatically
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Add your desired views and content to the LinearLayout
        // ...

        return linearLayout;
    }


}