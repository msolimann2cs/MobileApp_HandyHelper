package com.mariam.registeration;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.mariam.registeration.R.id;
import com.mariam.registeration.R.layout;

public class RequestDetailsActivity extends AppCompatActivity {

    TextView backBtn;

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
            tvTime.setText(Float.toString(bundle.getFloat("reqTime")));
        }

    }
}