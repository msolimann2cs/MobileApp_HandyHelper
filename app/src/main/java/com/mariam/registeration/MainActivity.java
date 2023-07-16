package com.mariam.registeration;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
//login or signup
    private Button Signup;
    private TextView Login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Signup = (Button) findViewById(R.id.signup);
        Signup.setOnClickListener(this);
        Login = (TextView) findViewById(R.id.signin);
        Login.setOnClickListener(this);

//        Intent i = new Intent(this,register_done.class);
//        this.startActivity(i);

    }

    @Override
    public void onClick(View view) {
//        Button pressed = (Button) view;

        if (view instanceof Button)
        {
            Intent i = new Intent(this,SignUp.class);
            this.startActivity(i);
        }
        else if (view instanceof TextView)
        {
            Intent i = new Intent(this,Login.class);
            this.startActivity(i);
        }
    }
}