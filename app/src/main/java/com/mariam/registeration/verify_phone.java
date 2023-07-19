package com.mariam.registeration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.Manifest;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

public class verify_phone extends AppCompatActivity implements View.OnClickListener {
    EditText editTextPhone;
    Button buttonSend,buttonBack;
    String verificationCode;
    private View backgroundView;
    private String phone="";
    private User user;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    public static final String ACCOUNT_SID = "ACde827196220eeef6de10d018f919c2f6";
    public static final String AUTH_TOKEN = "4dda50444b6adf84557dfffe0ef49c20";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        Intent intent = getIntent();
        System.out.println("verify1");
        user = (User) intent.getSerializableExtra("user_data");
        backgroundView = findViewById(R.id.background_view);
        backgroundView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                hideKeyboard();
            }
        });
        buttonSend = findViewById(R.id.send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone = editTextPhone.getText().toString();
                if (phone.substring(0,2).equals("+2"))
                {
                    sendVerificationCode();
                }else
                    editTextPhone.setError("Please enter valid number (eg. +20**********");

            }
        });
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            String phone = editTextPhone.getText().toString();
            if (s.length()==13){
                System.out.println(s.toString().substring(2));
                verificationCode = generateVerificationCode();
                buttonSend.setEnabled(true);
                buttonSend.setBackgroundResource(R.drawable.button_style_1_selected);
                buttonSend.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            }
            else
            {
                buttonSend.setEnabled(false);
                buttonSend.setBackgroundResource(R.drawable.deactivated_button);
                buttonSend.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.grey));
            }
        }
            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });
        buttonBack = findViewById(R.id.back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(verify_phone.this,SignUp.class);
                verify_phone.this.startActivity(i);
            }
        });
    }

    private String generateVerificationCode() {
        // Generate a random 6-digit verification code
        int code = (int) (Math.random() * 9000) + 1000;
        return String.valueOf(code);
    }

    private void sendVerificationCode() {
//        SmsManager smsManager = SmsManager.getDefault();
//        smsManager.sendTextMessage(phone, null, "Your verification code is: " + verificationCode, null, null);
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        String verificationCode = generateVerificationCode();
//        Message message = Message.creator(
//                        new com.twilio.type.PhoneNumber(phone),
//                        new com.twilio.type.PhoneNumber("+18149628262"),
//                        "Your verification code is: " + verificationCode).create();
        System.out.println("sent");
//        System.out.println("Verification code sent to " + message.getTo() + ": " + message.getBody());
        Intent i = new Intent(verify_phone.this,verify_phone2.class);
        i.putExtra("user_data",user);
        i.putExtra("phone",phone);
        i.putExtra("code",verificationCode);
        System.out.println("move to 2");
        verify_phone.this.startActivity(i);
    }

    @Override
    public void onClick(View view) {

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