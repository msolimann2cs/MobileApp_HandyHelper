package com.mariam.registeration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Console;

public class verify_phone2 extends AppCompatActivity {

    private User user;
    private String code,verify;
    private EditText[] input_code;
    private Button buttonConfirm;
    private Button buttonBack;
    private TextView resendcode;
    private String phone;
    private TextView unmatched;
    private View backgroundView;
//    private int count=0;
    private int[] code_text ={
            R.id.editTextNumber1,
            R.id.editTextNumber2,
            R.id.editTextNumber3,
            R.id.editTextNumber4,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone2);

        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        user = (User) intent.getSerializableExtra("user_data");
        System.out.println("verify2");
        code = intent.getStringExtra("code");
        if (user != null) {
            // Do something with the User object
            Log.d("SecondActivity", "Received user: " + user.getPhone());
        }
        unmatched = (TextView) findViewById(R.id.unmatched);
        buttonConfirm = (Button) findViewById(R.id.confirm_button);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verify = "";
                for (int i = 0; i < 4; i++) {
                    verify += input_code[i].getText().toString();
                }
                if (verify.equals(code)) {
                    System.out.println("match");
                    unmatched.setVisibility(View.INVISIBLE);
                    Intent i = new Intent(verify_phone2.this,Setup_img.class);
                    user.setPhone(phone);
                    i.putExtra("user_data", user);
                    verify_phone2.this.startActivity(i);

                } else {
                    System.out.println(code);
                    System.out.println(verify);
                    System.out.println("unmatch");
                    unmatched.setVisibility(View.VISIBLE);
                    for (EditText edittext: input_code)
                    {
                        edittext.setBackgroundResource(R.drawable.whiterounded_rededges);
                    }
                }
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

        input_code = new EditText[4];
        for (int count = 0; count < 4; count++) {
            input_code[count] = (EditText) findViewById(code_text[count]);
        }

        for (int i = 0; i < 4; i++) {
            final int currentIndex = i;
            input_code[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Do something before text changes
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Move focus to the next EditText view after a single character is entered
                    if (s.length() == 1 && currentIndex < input_code.length - 1) {
                        input_code[currentIndex + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Do something after text changes
                    if (!allcode_entered()) {
                        buttonConfirm.setEnabled(false);
                        buttonConfirm.setBackgroundResource(R.drawable.deactivated_button);
                        buttonConfirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.grey));

                    } else {
                        buttonConfirm.setEnabled(true);
                        buttonConfirm.setBackgroundResource(R.drawable.button_style_1_selected);
                        buttonConfirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                    }
                }
            });

            input_code[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // Select all text in the EditText view when it gains focus
                    if (hasFocus) {
                        ((EditText) v).selectAll();
                    }
                }
            });

        }
        buttonBack = findViewById(R.id.back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(verify_phone2.this, verify_phone.class);
                verify_phone2.this.startActivity(i);
            }
        });

        resendcode = (TextView) findViewById(R.id.resend_code);
        resendcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSms();
            }
        });
    }
    private boolean allcode_entered()
    {
        for (EditText edittext: input_code)
        {
            if (edittext.getText().toString().isEmpty())
            {
                return false;
            }
        }
        return true;
    }
    private String generateVerificationCode() {
        // Generate a random 6-digit verification code
        int code = (int) (Math.random() * 9000) + 1000;
        return String.valueOf(code);
    }
    private void sendVerificationCode(String phone, String verificationCode) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, "Your verification code is: " + verificationCode, null, null);
    }
    private void sendSms() {
        String verificationCode = generateVerificationCode();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (telephonyManager != null && telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY) {
            sendVerificationCode(phone, verificationCode);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, "Your verification code is: " + verificationCode, null, null);
            System.out.println(verificationCode);
            code = verificationCode;
            Toast.makeText(this, "SMS sent", Toast.LENGTH_SHORT).show();
        } else {
            System.out.println("Not sent");
            Toast.makeText(this, "Cannot send SMS", Toast.LENGTH_SHORT).show();
        }
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
