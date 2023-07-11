package com.mariam.registeration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.BoringLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mariam.registeration.R.drawable;
import com.mariam.registeration.R.id;
import com.mariam.registeration.R.layout;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    TextView filterBtn;
    ArrayList<Request> reqs;
    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        int[] images = new int[]{R.drawable.a, R.drawable.a, R.drawable.a, R.drawable.a, R.drawable.a};
        String[] descs = new String[]{"Walk my dog for 20 minutes hjklh hjkl hlk; hl;kjkl; kl; hjkl;hhiohiopph upu huioguiogo guio guiog uio ij kl hjlh oih uj lhuiohhuiohuioghuio hio huioh uiohuioh uiohu iohuiohui", "Change 4 light bulbs", "Change 4 light bulbs", "Mow my loan", "Deliver a 4kg package"};
        String[] titles = new String[]{"Pet Care", "Installation", "Installation", "Gardening", "Transportation"};
        String[] dates = new String[]{"2023-07-11", "2023-07-10", "2023-07-05", "2023-06-11", "2023-07-09",};
        //30.023008, 31.518187     30.034054, 31.450164      30.071717365740923, 31.369465196372992   30.08602403142341, 31.27112588129936   30.05438645981498, 31.00419195220193
        double[] locationLat = new double[]{30.023008, 30.034054, 30.071717365740923, 30.08602403142341,  30.05438645981498};
        double[] locationLon = new double[]{31.518187, 31.450164, 31.369465196372992, 31.27112588129936,  31.00419195220193};
        float[] prices = new float[]{100.0F, 200.0F, 300.0F, 100.0F, 200.0F};
        reqs = new ArrayList();

        for (int i = 0; i < 5; ++i) {
            Request req = new Request(titles[i], descs[i], dates[i], locationLat[i], locationLon[i],2.0F, prices[i], R.drawable.a);
            reqs.add(req);
        }


        //current Location
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            askPermission();
        }


        getLastLocation();




        Bundle bundle = this.getIntent().getExtras();
        boolean cats[] = new boolean[5];
        float minprice, maxprice, mindate, maxdate, mindis, maxdis;

        if (bundle != null) {
            cats = bundle.getBooleanArray("catagories");
            for (int i = 0; i < reqs.size(); i++) {
                if (cats[0] == false && reqs.get(i).title == "Gardening") {
                    reqs.remove(reqs.get(i));
                    i--;
                }

            }
            for (int i = 0; i < reqs.size(); i++) {

                if (cats[1] == false && reqs.get(i).title == "Car Care") {
                    reqs.remove(reqs.get(i));
                    i--;
                }

            }
            for (int i = 0; i < reqs.size(); i++) {


                if (cats[2] == false && reqs.get(i).title == "Installation") {
                    reqs.remove(reqs.get(i));
                    i--;

                }


            }
            for (int i = 0; i < reqs.size(); i++) {


                if (cats[3] == false && reqs.get(i).title == "Transportation") {
                    reqs.remove(reqs.get(i));
                    i--;
                }


            }
            for (int i = 0; i < reqs.size(); i++) {

                if (cats[4] == false && reqs.get(i).title == "Pet Care") {
                    reqs.remove(reqs.get(i));
                    i--;
                }
            }
            minprice = bundle.getFloat("priceMin");
            maxprice = bundle.getFloat("priceMax");

            Log.i("price", "min "+minprice + " max " + maxprice);

            mindate = bundle.getFloat("dateMin");
            maxdate = bundle.getFloat("dateMax");
            mindis = bundle.getFloat("disMin");
            maxdis = bundle.getFloat("disMax");

            for (int i = 0; i < reqs.size(); i++) {
                if (reqs.get(i).price > maxprice || reqs.get(i).price < minprice) {
                    reqs.remove(i);
                    i--;
                }


//
//                if (reqs.get(i).distance() > maxdis || reqs.get(i).distance() < mindis) {
//                    reqs.remove(i);
//                    i--;
//                }
            }

            for (int i = 0; i < reqs.size(); i++) {
                if (reqs.get(i).dateNum > maxdate || reqs.get(i).dateNum < mindate) {
                    reqs.remove(i);
                    i--;
                }
            }


        }


        RequestAdaptor adaptor = new RequestAdaptor(this, reqs);
        ListView lv = (ListView) this.findViewById(id.listView);
        this.filterBtn = (TextView) this.findViewById(id.filterButton);
        lv.setAdapter(adaptor);
        LinearLayout ll2 = (LinearLayout) this.findViewById(id.linearLayout2);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Request clickedReq = (Request) reqs.get(i);
                Intent intent = new Intent(HomeActivity.this, RequestDetailsActivity.class);
                intent.putExtra("reqName", clickedReq.title);
                intent.putExtra("reqDesc", clickedReq.description);
                intent.putExtra("reqDate", clickedReq.date);
                intent.putExtra("reqLocation", clickedReq.distance());
                intent.putExtra("reqTime", clickedReq.time);
                intent.putExtra("reqPrice", clickedReq.price);
                intent.putExtra("userImage", clickedReq.image);
                HomeActivity.this.startActivity(intent);
            }
        });
        this.filterBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, FilterActivity.class);
                HomeActivity.this.startActivity(intent);
            }
        });
    }

    private void getLastLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.i("hello", "hi");

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                List<Address> address;

                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());
                        try {
                            address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            Log.i("location", "Location lat " + address.get(0).getLatitude() + " Location long "+ address.get(0).getLongitude());
                        } catch (Exception e) {

                        }
                        for (Request req : reqs) {
                            req.setCurrentLocations(address.get(0).getLatitude(), address.get(0).getLongitude());
                        }
                    }
                }


            });
        }
        else{
            askPermission();
        }
    }

    private void askPermission(){
        ActivityCompat.requestPermissions(HomeActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }else{
                Toast.makeText(this, "Required Location", Toast.LENGTH_SHORT).show();
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

