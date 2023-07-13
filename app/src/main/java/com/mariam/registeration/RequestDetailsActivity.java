package com.mariam.registeration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.mariam.registeration.R.id;
import com.mariam.registeration.R.layout;

public class RequestDetailsActivity extends AppCompatActivity {

    TextView backBtn;
    TextView invalidText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layout.activity_request_details);
        Bundle bundle = this.getIntent().getExtras();
        ImageView image = (ImageView)this.findViewById(id.userImage);
        TextView tvTitle = (TextView)this.findViewById(id.RequestTitle);
        TextView tvDesc = (TextView)this.findViewById(id.RequestDesc);
        TextView tvDate = (TextView)this.findViewById(id.RequestDate);
        TextView tvTime = (TextView)this.findViewById(id.RequestTime);
        this.backBtn = (TextView)this.findViewById(id.back);
        Button applyBtn = (Button) findViewById(id.applyBtn);
        WebView mywebview = (WebView) findViewById(id.webViewLocation);
        EditText bidText = (EditText) findViewById(id.bidText);
        invalidText = (TextView) findViewById(id.invalidText);
        mywebview.getSettings().setJavaScriptEnabled(true);
        this.backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if (bundle != null) {
            image.setImageResource(bundle.getInt("userImage"));
            tvTitle.setText(bundle.getString("reqName"));
            tvDesc.setText(bundle.getString("reqDesc"));
            tvDate.setText(bundle.getString("reqDate"));
            mywebview.loadUrl("https://www.google.com/maps/@"+bundle.getDouble("lat")+","+bundle.getDouble("lon"));

            tvTime.setText(Float.toString(bundle.getFloat("reqTime")));
        }

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int bidPrice = Integer.parseInt(bidText.getText().toString());
                // make post request
                boolean applied = false;
                if(bidPrice > 10) {
                    applied = true;
                    Intent intent = new Intent(RequestDetailsActivity.this, AppliedActivity.class);
                    startActivity(intent);
                }
                if(!applied){
                    invalidText.setVisibility(view.VISIBLE);
                }
            }
        });




    }
}