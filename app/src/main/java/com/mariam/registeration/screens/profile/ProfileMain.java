package com.mariam.registeration.screens.profile;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.AsyncTask;
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
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.mariam.registeration.HomeActivity;
import com.mariam.registeration.R;
import com.mariam.registeration.User;
import com.mariam.registeration.UserApiService;
import com.mariam.registeration.screens.profile.EnlargedProfilePicture;
import com.mariam.registeration.screens.profile.ProfileSettings;
import com.mariam.registeration.services.DatabaseCallback;
import com.mariam.registeration.services.DatabaseManager;
import com.mariam.registeration.services.HandyAPI;
import com.mariam.registeration.services.UserSession;

import org.json.JSONException;
import org.json.JSONObject;


public class ProfileMain extends AppCompatActivity implements DatabaseCallback {
    private HandyAPI my_api = new HandyAPI();
    private DatabaseManager database;
    LinearLayout parentLayout;
    boolean category_edit = false;
    boolean location_edit = false;
    ShapeableImageView Homebtn;
    private UserApiService userApiService;

    String image;
    String rating;
    String interests;
    String description;


// Now you can use the userList in Activity B

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_main);

        LinearLayout parentLayout = findViewById(R.id.parent_layout);

        User logged_user = UserSession.getInstance().getLoggedUser();
        database = new DatabaseManager();
        String nationalId = logged_user.getNat_ID();
        database.getUserDetails(nationalId, this);

        // ------------------------------

        LinearLayout Homebtn = findViewById(R.id.homeBtn);

        Homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileMain.this, HomeActivity.class);
                startActivity(intent);
            }
        });

// Change status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.navy_blue));
        }
        // ---------------------------------------------
        TextView name = findViewById(R.id.name);
        name.setText(logged_user.getUsername());

//        TextView rating_field = findViewById(R.id.rating);
//        rating_field.setText(rating);

        // ---------------------------------------------




        // TextView wallet_button = findViewById(R.id.wallet_tab);
//        wallet_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//                Intent intent = new Intent(ProfileMain.this, ProfileWallet.class);
//                startActivity(intent);
//            }
//        });
        TextView settings_button = findViewById(R.id.settings_tab);
        settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(ProfileMain.this, ProfileSettings.class);
                //intent.putExtra("current_user", current_user);
                startActivity(intent);
            }
        });

        ShapeableImageView profile_picture = findViewById(R.id.profile_picture);
        profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start FullscreenImageActivity and pass the image data
                Intent intent = new Intent(ProfileMain.this, EnlargedProfilePicture.class);
                intent.putExtra("imageResId", R.drawable.profile_photo_demo); // Pass the image resource ID or other image data
                startActivity(intent);
            }
        });


        Button editProfileButton = findViewById(R.id.edit_profile);
        TextView aboutMeDescTextView = findViewById(R.id.about_me_desc);
        AtomicReference<EditText> aboutMeDescEditTextRef = new AtomicReference<>(null);
        LinearLayout categoriesLayout1 = findViewById(R.id.categories);
        LinearLayout categoriesLayout2 = findViewById(R.id.categories_line2);
        // TextView locationTextView = findViewById(R.id.location);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category_edit = true;
                location_edit = true;
                EditText aboutMeDescEditText = aboutMeDescEditTextRef.get();
                if (aboutMeDescEditText == null) {
                    // Change to Edit mode
                    aboutMeDescEditText = new EditText(ProfileMain.this);
                    aboutMeDescEditText.setId(R.id.about_me_desc);
                    aboutMeDescEditText.setLayoutParams(aboutMeDescTextView.getLayoutParams());
                    aboutMeDescEditText.setText(aboutMeDescTextView.getText());
                    aboutMeDescEditText.setSelection(aboutMeDescEditText.getText().length());
                    aboutMeDescEditText.setTextColor(ContextCompat.getColor(ProfileMain.this, android.R.color.black));

                    // Create a custom drawable for the border
                    Drawable borderDrawable = createBorderDrawable();

                    // Set the custom drawable as the background of the EditText
                    aboutMeDescEditText.setBackground(borderDrawable);
                    categoriesLayout1.setBackground(borderDrawable);
                    categoriesLayout2.setBackground(borderDrawable);
                    // locationTextView.setBackground(borderDrawable);
                    aboutMeDescEditTextRef.set(aboutMeDescEditText);

                    ViewGroup parent = (ViewGroup) aboutMeDescTextView.getParent();
                    int index = parent.indexOfChild(aboutMeDescTextView);
                    parent.removeView(aboutMeDescTextView);
                    parent.addView(aboutMeDescEditText, index);


                    editProfileButton.setText("Done");
                } else {
                    // Change back to TextView mode
                    String updatedText = aboutMeDescEditText.getText().toString();
                    aboutMeDescTextView.setText(updatedText);
                    aboutMeDescTextView.setTextColor(ContextCompat.getColor(ProfileMain.this, android.R.color.black));
                    // userApiService = new UserApiService();
                    // userApiService.updateUserDescription(current_user.getNat_ID(), updatedText);
                    String username = logged_user.getUsername(); // Replace with the actual username
                    String newDescription = updatedText;
                    database.updateDescription(username, newDescription, this);

                    ViewGroup parent = (ViewGroup) aboutMeDescEditText.getParent();
                    int index = parent.indexOfChild(aboutMeDescEditText);
                    parent.removeView(aboutMeDescEditText);
                    parent.addView(aboutMeDescTextView, index);
                    categoriesLayout1.setBackground(null);
                    categoriesLayout2.setBackground(null);
                    // locationTextView.setBackground(null);
                    aboutMeDescEditTextRef.set(null);
                    category_edit = false;
                    location_edit = false;

                    editProfileButton.setText("Edit Profile");
                }
            }
        });


        LinearLayout category_button1 = findViewById(R.id.categories);
        LinearLayout category_button2 = findViewById(R.id.categories_line2);
        // TextView location_button = findViewById(R.id.location);
        category_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (category_edit)
                    showCategory();
            }
        });
        category_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (category_edit)
                    showCategory();
            }
        });

//        location_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (location_edit)
//                    showLocation();
//            }
//        });


    }

    // Create a custom drawable for the border
    private Drawable createBorderDrawable() {
        int strokeWidth = getResources().getDimensionPixelSize(R.dimen.border_stroke_width);
        int strokeColor = ContextCompat.getColor(this, R.color.off_white_darker);
        int cornerRadius = getResources().getDimensionPixelSize(R.dimen.border_corner_radius);

        GradientDrawable borderDrawable = new GradientDrawable();
        borderDrawable.setShape(GradientDrawable.RECTANGLE);
        borderDrawable.setStroke(strokeWidth, strokeColor);
        borderDrawable.setCornerRadius(cornerRadius);

        return borderDrawable;
    }

    private FrameLayout createOverlayLayout() {
        LinearLayout rootLayout = findViewById(R.id.parent_layout); // Replace `root_layout` with the ID of your root layout
        LayoutInflater inflater = LayoutInflater.from(this);
        FrameLayout overlayLayout = (FrameLayout) inflater.inflate(R.layout.activity_category_edit, rootLayout, false);

        overlayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle clicks on the overlay layout (e.g., to dismiss the overlay)
                rootLayout.removeView(overlayLayout); // Remove the overlay layout from the root layout
                // Additional handling if needed
            }
        });

        rootLayout.addView(overlayLayout);

        return overlayLayout;
    }

    private void showCategory() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.activity_category_edit, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void showLocation() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.activity_location_edit, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
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
            TextView rating_view = findViewById(R.id.rating);
            rating_view.setText(rating);
            TextView description_view = findViewById(R.id.descriptionText);
            description_view.setText(description);
        } catch (JSONException e) {
            e.printStackTrace();
            // Handle JSON parsing error
        }
    }

    @Override
    public void onDataFetchError(String errorMessage) {

    }
}