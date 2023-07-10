package com.mariam.registeration;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SignUp extends AppCompatActivity implements View.OnClickListener  {
    private EditText birthdateEditText;
    private EditText username;
    private EditText email;
    private EditText password;
    private EditText con_password;
    private EditText ID;
    User user_data ;
    private final String[] genderOptions = {"Male", "Female"};
    private AutoCompleteTextView genderDropdown;
    private View backgroundView;
    int day=0, month=0,year=0;
    private Button register;
    private boolean Date_valid = false;
    private TextView Signin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        user_data = new User("","","","",
                0,0,0,"");

        backgroundView = findViewById(R.id.background_view);
        backgroundView.setOnClickListener(this);

        Signin = findViewById(R.id.Signin);
        Signin.setOnClickListener(this);

        register = (Button) findViewById(R.id.Reg);
        register.setOnClickListener(this);
        email = (EditText) findViewById(R.id.editTextTextEmailAddress);
        email.setOnClickListener(this);
        username = (EditText) findViewById(R.id.editTextUsername);
        username.setOnClickListener(this);
        password = (EditText) findViewById(R.id.editTextTextPassword);
        password.setOnClickListener(this);
        con_password = (EditText) findViewById(R.id.editTextConfPassword);
        con_password.setOnClickListener(this);
        ID = (EditText) findViewById(R.id.editTextNumber);
        ID.setOnClickListener(this);
//        new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                hideKeyboard();
//            }
//        });
        birthdateEditText = findViewById(R.id.birthdate_edittext);
        birthdateEditText.addTextChangedListener(new TextWatcher() {

            private boolean isFormatting;
            private int slashCount;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Check if the user is entering or removing a slash
                if (count > 0 && s.charAt(start) == '/' && !isFormatting) {
                    slashCount++;
                    isFormatting = true;
                } else if (count == 0 && slashCount > 0 && !isFormatting) {
                    slashCount--;
                    isFormatting = true;
                } else {
                    isFormatting = false;
                }
//                s = birthdateEditText.getText().toString();
                // Add slashes after the second and fourth digits
//                if (!isFormatting && s.length() == 1 && Integer.parseInt(s.toString())>1) {
//                    s = s.subSequence(0, s.length() - 1);
//                    birthdateEditText.setText(s);
//                    birthdateEditText.setSelection(birthdateEditText.getText().length());
//                }
//                else if (!isFormatting && s.length() == 2 && Integer.parseInt(s.toString())> 12) {
//                    s = s.subSequence(0, s.length() - 1);
//                    birthdateEditText.setText(s);
//                    birthdateEditText.setSelection(birthdateEditText.getText().length());
//                }
                if (!isFormatting && slashCount == 0 && s.length() == 2) {
                    birthdateEditText.setText(s + "/");
                    birthdateEditText.setSelection(birthdateEditText.getText().length());
                    slashCount++;
                } else if (!isFormatting && slashCount == 1 && s.length() == 5) {
                    birthdateEditText.setText(s + "/");
                    birthdateEditText.setSelection(birthdateEditText.getText().length());
                    slashCount++;
                } else if (!isFormatting && s.length() > 10) {
                    s = s.subSequence(0, s.length() - 1);
                    birthdateEditText.setText(s);
                    birthdateEditText.setSelection(birthdateEditText.getText().length());
                }

                Date_valid = true;
                String date = birthdateEditText.getText().toString();
                String[] pieces = date.split("/");
                if (pieces.length != 3)
                {
                    Date_valid = false;
                }else {
                    day = Integer.parseInt(pieces[0]);
                    month = Integer.parseInt(pieces[1]); // Add 1 because Calendar.MONTH is zero-based
                    year = Integer.parseInt(pieces[2]);
                }

                if ((month == 1 || month == 3 || month == 5 || month == 7 || month == 8
                        || month == 10 || month == 12) && day > 31) {
                    Date_valid = false;
                } else if ((month == 4 || month == 6 || month == 2 || month == 9 ||
                        month == 11) && day > 30) {
                    Date_valid = false;
                }
                if (year > 2007 || month < 1 || month > 12 || day < 1 || day > 31) {
                    Date_valid = false;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        genderDropdown = findViewById(R.id.gender_dropdown);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genderOptions);
        genderDropdown.setAdapter(adapter);
        genderDropdown.setOnClickListener(this);
        genderDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedGender = (String) parent.getItemAtPosition(position);
            }
        });
    }
    @Override
    public void onClick(View view) {
        if (view instanceof EditText)
        {
            hideKeyboard();
            EditText pressed = (EditText) view;
            if (pressed == genderDropdown)
            {
                genderDropdown.showDropDown();
            }
        } else if (view instanceof TextView)
        {
            TextView pressed = (TextView) view;
            if (pressed == register)
            {
                ////ensure all fields are filled

                if (TextUtils.isEmpty(username.getText().toString()))
                {
                    Log.i("notext","enter some text");
                    username.setError("Please fill");
                    username.requestFocus();
                }
                else if (email.getText().toString().isEmpty()|| !email.getText().toString().contains("@"))
                {
                    Log.i("notext","enter some text");
                    email.setError("Please enter valid email");
                    email.requestFocus();
                }
                else if (password.getText().toString().isEmpty())
                {
                    Log.i("notext","enter some text");
                    password.setError("Please enter valid Password");
                }
                else if (!password.getText().toString().equals(con_password.getText().toString()))
                {
                    con_password.setError("Passwords don't match");
                    con_password.requestFocus();
                }
                else if (genderDropdown.getText().toString().isEmpty())
                {
                    Log.i("notext","enter some text");
                    genderDropdown.setError("Please enter valid gender");
                }
                else if (birthdateEditText.getText().toString().isEmpty())
                {
                    birthdateEditText.setError("Please fill");
                }
                else if (!Date_valid)
                {
                    birthdateEditText.setError("Invalid Date");
                }
                else if (ID.getText().toString().isEmpty() || ID.getText().toString().length()!=14)
                {
                    ID.setError("ID not valid");
                }
                else{
                    user_data.setUsername(username.getText().toString());
                    user_data.setEmail(email.getText().toString());
                    user_data.setPass(password.getText().toString());
                    user_data.setGender(genderDropdown.getText().toString());
                    user_data.setB_day(day);
                    user_data.setB_month(month);
                    user_data.setB_year(year);
                    user_data.setNat_ID( ID.getText().toString());
                    Intent i = new Intent(this,Register_success.class);
                    i.putExtra("user_data", user_data);
                    this.startActivity(i);
                }
            }
            else{
                Intent i = new Intent(this,Login.class);
                this.startActivity(i);
            }


        }
        else
            hideKeyboard();
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