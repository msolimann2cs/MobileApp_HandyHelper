package com.mariam.registeration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import android.Manifest;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Setup_img extends AppCompatActivity {

    private ImageView imageView;
    private Button btnCamera, btnGallery, btnnext;
    private Bitmap bitmap;
    private byte[] imageBytes;

    private static final int CAMERA_REQUEST_CODE = 100;
//    private static final int GALLERY_REQUEST_CODE = 101;
    private static final int SELECT_PICTURE = 200;
    private static final int REQUEST_PERMISSION_CODE = 102;
    private BottomSheetDialog bottomSheetDialog;
    Button showMenuButton;
    LinearLayout layout_Main;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_img);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user_data");
        if (user != null) {
            // Do something with the User object
            Log.d("SecondActivity", "Received user: "
                    + user.getUsername() + ", " + user.getPass() +
                    ", " + user.getEmail() + ", " + user.getGender()
                    + ", " + user.getNat_ID()+ ", " + user.getDate_of_birth()
                    + "," + user.getPhone());
        }

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_menu_img);
        bottomSheetDialog.setOnCancelListener(dialogInterface -> {
            layout_Main.setAlpha((float) 1);
            if (imageBytes != null) {
                showMenuButton.setText("Choose another");
                btnnext.setBackgroundResource(R.drawable.button_style_1_selected);
                btnnext.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                btnnext.setText("Next");
            }

        });
//        bottomSheetDialog.setOnShowListener(dialogInterface -> {
//
//        });
        layout_Main = (LinearLayout) findViewById(R.id.background_view);
//        layout_Main.setAlpha((float) 1);

        showMenuButton = findViewById(R.id.choose_img);
        showMenuButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                layout_Main.setAlpha((float) 0.7);
                bottomSheetDialog.show();
            }
        });

        imageView = findViewById(R.id.imageView);

        btnnext = findViewById(R.id.next);
        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageBytes != null) {
                    user.setImageBytes(imageBytes);
                }
                Intent i = new Intent(Setup_img.this,setup_interest.class);
                i.putExtra("user_data", user);
                Setup_img.this.startActivity(i);
            }
        });
        btnCamera = bottomSheetDialog.findViewById(R.id.btnCamera);
        btnGallery = bottomSheetDialog.findViewById(R.id.btnGallery);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    takePictureFromCamera();
                } else {
                    requestPermission();
                }
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    choosePictureFromGallery();
                } else {
                    requestPermission();
                }
            }
        });
    }

    // Check if the required permissions have been granted
    private boolean checkPermission() {
        int cameraPermission = ContextCompat.checkSelfPermission(Setup_img.this, Manifest.permission.CAMERA);
        int storagePermission = ContextCompat.checkSelfPermission(Setup_img.this, Manifest.permission.READ_EXTERNAL_STORAGE);

        return cameraPermission == PackageManager.PERMISSION_GRANTED && storagePermission == PackageManager.PERMISSION_GRANTED;
    }

    // Request the required permissions
    private void requestPermission() {
        ActivityCompat.requestPermissions(Setup_img.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePictureFromCamera();
            } else {
                Toast.makeText(Setup_img.this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Launch the camera app to take a picture
    private void takePictureFromCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }

    // Launch the gallery app to choose a picture
    private void choosePictureFromGallery() {

        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);

    }

    // Handle the result of the camera or gallery app
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:
                    bitmap = (Bitmap) data.getExtras().get("data");
                    imageView.setImageBitmap(bitmap);
                    break;
                case SELECT_PICTURE :
                    // Get the url of the image from data
                    Uri selectedImageUri = data.getData();
                    if (null != selectedImageUri) {
                        // update the preview image in the layout
                        imageView.setImageURI(selectedImageUri);
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            }

            if (bitmap != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                imageBytes = bos.toByteArray();
            }
        }
    }
}