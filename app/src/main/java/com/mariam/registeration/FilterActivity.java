package com.mariam.registeration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.slider.RangeSlider;

import java.util.List;

public class FilterActivity extends AppCompatActivity {
    boolean cats[] = new boolean[5];
    Button catGardening;
    Button catPetCate;
    Button catCarCare;
    Button catInstallation;
    Button catTransportation;
    TextView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        cats[0] = false;
        cats[1] = false;
        cats[2] = false;
        cats[3] = false;
        cats[4] = false;

        catGardening = (Button) findViewById(R.id.gardeningButton);
        catPetCate = (Button) findViewById(R.id.petcareButton);
        catCarCare = (Button) findViewById(R.id.carcareButton);
        catInstallation = (Button) findViewById(R.id.installationButton);
        catTransportation = (Button) findViewById(R.id.transportationButton);
        backBtn = (TextView) findViewById(R.id.back);
        ColorStateList catagButtons = getResources().getColorStateList(R.color.colorstate);
        ColorStateList catagButtonsClicked = getResources().getColorStateList(R.color.colorstateclick);

        catGardening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cats[0] = !cats[0];
                if(cats[0] == false){
                    catGardening.setBackgroundTintList(catagButtons);
                }
                else{
                    catGardening.setBackgroundTintList(catagButtonsClicked);
                }

            }
        });

        catCarCare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cats[1] = !cats[1];
                if(cats[1] == false){
                    catCarCare.setBackgroundTintList(catagButtons);
                }
                else{
                    catCarCare.setBackgroundTintList(catagButtonsClicked);
                }
            }
        });

        catInstallation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cats[2] = !cats[2];

                if(cats[2] == false){
                    catInstallation.setBackgroundTintList(catagButtons);
                }
                else{
                    catInstallation.setBackgroundTintList(catagButtonsClicked);
                }
            }
        });

        catTransportation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cats[3] = !cats[3];
                if(cats[3] == false){
                    catTransportation.setBackgroundTintList(catagButtons);
                }
                else{
                    catTransportation.setBackgroundTintList(catagButtonsClicked);
                }
            }
        });


        catPetCate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cats[4] = !cats[4];
                if(cats[4] == false){
                    catPetCate.setBackgroundTintList(catagButtons);

                }
                else{
                    catPetCate.setBackgroundTintList(catagButtonsClicked);

                }

            }
        });



        RangeSlider priceSlider = (RangeSlider) findViewById(R.id.priceSlider);




        RangeSlider dateSlider = (RangeSlider) findViewById(R.id.dateSlider);


        RangeSlider disSlider = (RangeSlider) findViewById(R.id.distanceSlider);



        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FilterActivity.this, HomeActivity.class);
                List<Float> priceVals = priceSlider.getValues();
                List<Float> dateVals = dateSlider.getValues();
                List<Float> disValues = disSlider.getValues();
                intent.putExtra("Uniqid","From_Filter");
                intent.putExtra("catagories", cats);
                intent.putExtra("priceMin", priceVals.get(0));
                intent.putExtra("priceMax", priceVals.get(1));
                intent.putExtra("dateMin", dateVals.get(0));
                intent.putExtra("dateMax", dateVals.get(1));
                intent.putExtra("disMin", disValues.get(0));
                intent.putExtra("disMax", disValues.get(1));

                startActivity(intent);



            }
        });
    }


}
