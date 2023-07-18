package com.mariam.registeration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

public class description extends AppCompatActivity {

    private User user;
    private EditText descrip;
    private TextView count_text;
    private View backgroundView;
    private Button buttonBack,buttonnext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

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
        count_text = (TextView) findViewById(R.id.count_words);
        descrip = (EditText) findViewById(R.id.description_edittext);
        InputFilter[] inputFilters = new InputFilter[1];
        inputFilters[0] = new InputFilter.LengthFilter(500);
        descrip.setFilters(inputFilters);
        descrip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do something before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String temp = s.length()+"/500 words";
                count_text.setText(temp);
                if (count==0)
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

            @Override
            public void afterTextChanged(Editable s) {
                // Do something after text changes

            }
        });
        buttonnext = findViewById(R.id.next);
        buttonnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonnext.getText().toString().equals("Next")) {
                    String desc = descrip.getText().toString();
                    Log.i("desc", desc);
                    user.setDescription(desc);
                }
                Intent i = new Intent(description.this, register_done.class);
                i.putExtra("user_data", user);
                description.this.startActivity(i);
            }
        });

        buttonBack = findViewById(R.id.back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(description.this, setup_interest.class);
                i.putExtra("user_data", user);
                description.this.startActivity(i);
            }
        });
        backgroundView = findViewById(R.id.background_view);
        backgroundView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                hideKeyboard();
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