package com.mariam.registeration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mariam.registeration.R.id;
import com.mariam.registeration.R.layout;

public class RequestDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextView backBtn;
    TextView invalidText;
    private GoogleMap myMap;
    SupportMapFragment supportMapFragment;

    double lat,lon;
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
        EditText bidText = (EditText) findViewById(id.bidText);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(id.googlemap);
        invalidText = (TextView) findViewById(id.invalidText);
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
            lat = bundle.getDouble("lat");
            lon= bundle.getDouble("lon");

            tvTime.setText(Float.toString(bundle.getFloat("reqTime")));
        }

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean applied = false;
                if (!(bidText.getText().toString().matches(""))) {
                    int bidPrice = Integer.parseInt(bidText.getText().toString());
                    // make post request

                    if (bidPrice > 10) {
                        applied = true;
                        Intent intent = new Intent(RequestDetailsActivity.this, AppliedActivity.class);
                        startActivity(intent);
                    }

                }
                if (!applied) {
                    invalidText.setVisibility(view.VISIBLE);
                }
            }
        });

        supportMapFragment.getMapAsync(RequestDetailsActivity.this);



    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        LatLng location = new LatLng(lat,lon);
        myMap.addMarker(new MarkerOptions().position(location).title("Service"));
        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
    }


}