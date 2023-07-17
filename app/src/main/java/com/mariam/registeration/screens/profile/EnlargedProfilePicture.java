package com.mariam.registeration.screens.profile;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;
import com.mariam.registeration.R;

public class EnlargedProfilePicture extends AppCompatActivity {
    private GestureDetector gestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enlarged_profile_pic);

        // Change status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.black));
        }

        ShapeableImageView fullscreenImageView = findViewById(R.id.fullscreen_image);
        int imageResId = getIntent().getIntExtra("imageResId", 0);
        fullscreenImageView.setImageResource(imageResId);



        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
                // Calculate the difference in Y coordinates
                float deltaY = event2.getY() - event1.getY();

                // Check if the swipe is in an upward direction
                if (deltaY < 0) {
                    onBackPressed();
                }

                return true;
            }
        });

        TextView back_button = findViewById(R.id.text_back);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_UP) {
//            float deltaY = event.getRawY() - event.getY();
//            if (deltaY < 0) {
//                onBackPressed();
//            }
//        }
//        return super.onTouchEvent(event);
//    }


}
