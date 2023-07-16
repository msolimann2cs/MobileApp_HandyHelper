package com.mariam.registeration;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
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
    int day=0, month=0,yearr=0;
    private Button register;
    private boolean Date_valid = false;
    private TextView Signin;
    EditText editTextDate;
    Calendar calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        user_data = new User("","","","",
                0,0,0,"");
        editTextDate = findViewById(R.id.editTextDate);
        calendar = Calendar.getInstance();

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
                    username.setError("Please fill");
                }
                else if (email.getText().toString().isEmpty()|| !email.getText().toString().contains("@")
                        || !email.getText().toString().contains("."))
                {
                    email.setError("Please enter valid email");
                }
                else if (password.getText().toString().isEmpty())
                {
                    password.setError("Please enter valid Password");
                }
                else if (!password.getText().toString().equals(con_password.getText().toString()))
                {
                    con_password.setError("Passwords don't match");
                }
                else if (genderDropdown.getText().toString().isEmpty())
                {
                    genderDropdown.setError("Please enter valid gender");
                }
                else if (Date_valid && editTextDate.getText().toString().isEmpty())
                {
                    editTextDate.setError("Invalid Date");
                }
                else if (ID.getText().toString().isEmpty() || ID.getText().toString().length()!=14)
                {
                    ID.setError("ID not valid");
                }
                else{
                    user_data.setUsername(username.getText().toString());
                    user_data.setEmail(email.getText().toString());
                    user_data.setPass(password.getText().toString());
                    if (genderDropdown.getText().toString().equals("Female"))
                        user_data.setGender("F");
                    else
                        user_data.setGender("M");
//                    user_data.setB_day(day);
//                    user_data.setB_month(month);
//                    user_data.setB_year(yearr);
                    user_data.setDate_of_birth(day,month,yearr);
                    user_data.setNat_ID( ID.getText().toString());
                    Intent i = new Intent(this,verify_phone.class);
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

    public void showDatePickerDialog(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(SignUp.this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    // Set the selected date in the input text field
                    editTextDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    day=dayOfMonth;
                    month =monthOfYear;
                    yearr=year;
                    Calendar calendar = Calendar.getInstance();
                    int year_now = calendar.get(Calendar.YEAR);
                    if ((year_now-yearr)<15)
                    {
                        editTextDate.setError("please enter valid date");
                        Date_valid = false;
                    }
                    else
                        Date_valid=true;

                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}