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

import com.google.android.material.imageview.ShapeableImageView;
import com.mariam.registeration.screens.profile.ChangePassword;
import com.mariam.registeration.HomeActivity;
import com.mariam.registeration.MainActivity;
import com.mariam.registeration.R;
import com.mariam.registeration.User;
import com.mariam.registeration.services.DatabaseCallback;
import com.mariam.registeration.services.DatabaseManager;
import com.mariam.registeration.services.UserSession;

import org.json.JSONException;
import org.json.JSONObject;


public class ProfileSettings extends AppCompatActivity implements DatabaseCallback {
    ShapeableImageView Homebtn;
    private DatabaseManager database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

//        Intent intent = getIntent();
//        User current_user = (User) intent.getSerializableExtra("current_user");

        LinearLayout Homebtn = findViewById(R.id.homeBtn);

        Homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileSettings.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        database = new DatabaseManager();
        User logged_user = UserSession.getInstance().getLoggedUser();
        String nationalId = logged_user.getNat_ID();
        database.getUserDetails(nationalId, this);
        TextView name = findViewById(R.id.name);
        name.setText(logged_user.getUsername());

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
        TextView customer_support_button = findViewById(R.id.customer_support);
        customer_support_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileSettings.this, CustomerSupportMain.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onDataFetched(String result) {
        try {
            JSONObject jsonResult = new JSONObject(result);
            String image = jsonResult.getString("image");
            String rating = jsonResult.getString("rating");
            String interests = jsonResult.getString("interests");
            String description = jsonResult.getString("description");

            // Use the retrieved data as needed
            // Update UI or perform other operations with the data
            TextView rating_field = findViewById(R.id.rating);
            rating_field.setText(rating);

//            TextView aboutMeDescTextView = findViewById(R.id.about_me_desc);
//            aboutMeDescTextView.setText(description);
        } catch (JSONException e) {
            e.printStackTrace();
            // Handle JSON parsing error
        }
    }

    @Override
    public void onDataFetchError(String errorMessage) {

    }
}