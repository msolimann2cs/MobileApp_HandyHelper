package com.mariam.registeration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ToggleButton;

public class setup_interest extends AppCompatActivity {

    User user;
    private Button buttonBack,buttonnext;
    private int checked=0;
    private ToggleButton[] interests_button = new ToggleButton[5];
    private CheckBox notify;
    private int[] interests_id =
            {
                    R.id.button1,
                    R.id.button2,
                    R.id.button3,
                    R.id.button4,
                    R.id.button5
            };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_interest);

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
        notify = (CheckBox) findViewById(R.id.checkBox);
        for (int i = 0; i< 5; i++) {
            interests_button[i] = (ToggleButton) findViewById(interests_id[i]);
            int finalI = i;
            interests_button[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (interests_button[finalI].isChecked()){
                        checked++;
                    }
                    else checked--;

                    if (checked==0)
                    {
                        buttonnext.setBackgroundResource(R.drawable.deactivated_button);
                        buttonnext.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.grey));
                        buttonnext.setText("Maybe Later");
                    }
                    else {
                        buttonnext.setBackgroundResource(R.drawable.button_style_1_selected);
                        buttonnext.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                        buttonnext.setText("Next");
                    }
                }
            });
        }
        buttonnext = findViewById(R.id.next);
        buttonnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonnext.getText().toString().equals("Next")) {
                        String interest ="";
                        for (ToggleButton button: interests_button)
                        {
                            if (button.isChecked())
                            {
                                if (interest.isEmpty())
                                {
                                    interest += button.getText().toString();
                                }
                                else
                                {
                                    interest += ", "+button.getText().toString();
                                }
                            }
                        }
                        Log.i("interest", interest);
                        user.setInterest(interest);
                        if (notify.isChecked())
                        {
                            Log.i("notify", "true");
                            user.setNotify(1);
                        }
                }
                Intent i = new Intent(setup_interest.this, description.class);
                i.putExtra("user_data", user);
                setup_interest.this.startActivity(i);
            }
        });

        buttonBack = findViewById(R.id.back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(setup_interest.this, Setup_img.class);
                i.putExtra("user_data", user);
                setup_interest.this.startActivity(i);
            }
        });
    }
}