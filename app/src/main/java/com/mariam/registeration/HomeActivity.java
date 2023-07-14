package com.mariam.registeration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.BoringLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements LocationListener {
    TextView filterBtn;
    ArrayList<Request> reqs;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationManager locationManager;
    private final static int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        int[] images = new int[]{R.drawable.a, R.drawable.a, R.drawable.a, R.drawable.a, R.drawable.a};
//        String[] descs = new String[]{"Walk my dog for 20 minutes hjklh hjkl hlk; hl;kjkl; kl; hjkl;hhiohiopph upu huioguiogo guio guiog uio ij kl hjlh oih uj lhuiohhuiohuioghuio hio huioh uiohuioh uiohu iohuiohui", "Change 4 light bulbs", "Change 4 light bulbs", "Mow my loan", "Deliver a 4kg package"};
//        String[] titles = new String[]{"Pet Care", "Installation", "Installation", "Gardening", "Transportation"};
//        String[] dates = new String[]{"2023-07-11", "2023-07-10", "2023-07-05", "2023-06-11", "2023-07-09",};
//
//        //30.023008, 31.518187     30.034054, 31.450164      30.071717365740923, 31.369465196372992   30.08602403142341, 31.27112588129936   30.05438645981498, 31.00419195220193
//        double[] locationLat = new double[]{30.023008, 30.034054, 30.071717365740923, 30.08602403142341, 30.05438645981498};
//        double[] locationLon = new double[]{31.518187, 31.450164, 31.369465196372992, 31.27112588129936, 31.00419195220193};
//        int[] prices = new int[]{100, 200, 300, 100, 200};

        if (ContextCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }


        getLastLocation();



//        for (int i = 0; i < 5; ++i) {
//            Request req = new Request(titles[i], descs[i], dates[i], locationLat[i], locationLon[i], 2.0F, prices[i], R.drawable.a);
//            reqs.add(req);
//        }


        //current Location

        reqs = new ArrayList();
        getAllRequests getReqs = new getAllRequests();

        getReqs.execute("http:\\\\127.0.0.1:5001\\allRequests");




        TextView profile_button = findViewById(R.id.navProfile);
        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfileMain.class);
                startActivity(intent);
            }
        });
    }

    private void getLastLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                return;
            }
            Log.i("HIIIIIIIII", "HIIIIIIIIIIII");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, HomeActivity.this);
        }
        catch(Exception e){
            e.printStackTrace();
        }


    }
//
//                for (Request req : reqs) {
//                        req.setCurrentLocations(address.get(0).getLatitude(), address.get(0).getLongitude());



    @Override
    public void onLocationChanged(@NonNull Location location) {
        try{
            Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            for (Request req : reqs) {
                req.setCurrentLocations(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
            }
            Log.i("LOCATIONNNNNNNNNN", "Lat "+addresses.get(0).getLatitude());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }


    public class getAllRequests extends AsyncTask<String, Integer, String> {
        private static final String API_URL = "http://"+"192.168.100.8:5001/"+"allRequests";
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;


        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            StringBuilder stringBuilder = new StringBuilder();

            try {
                URL url = new URL(API_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                // Read the response
                InputStream inputStream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return stringBuilder.toString();


    }

        @Override
        protected void onPostExecute(String s) {
            try {
                Log.i("NETTTTT", s);
                JSONArray jsonarr = new JSONArray(s);
                int arrSize = jsonarr.length();
                for(int i =0; i<arrSize; i++){
                    JSONObject jsonReq = jsonarr.getJSONObject(i);
                    int id = jsonReq.getInt("id");
                    int cat = jsonReq.getInt("category");
                    String title = jsonReq.getString("title");
                    String desc = jsonReq.getString("description");
                    double locationLat = jsonReq.getDouble("locationLat");
                    double locationLon = jsonReq.getDouble("locationLong");
                    String date = jsonReq.getString("servicedate").substring(0,10);
                    String time = jsonReq.getString("servicetime");
                    int initialPrice = jsonReq.getInt("initialPrice");
                    String userId = jsonReq.getString("userId");
                    Request req = new Request(id,cat,title,desc, date, time,locationLat, locationLon, initialPrice, userId);
                    reqs.add(req);
                }

                display();



            }catch (Exception e){
                Log.i("ERTOOOTOTOTOO", s);

            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        protected void display(){


            Bundle bundle = HomeActivity.this.getIntent().getExtras();
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

                Log.i("price", "min " + minprice + " max " + maxprice);

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


            RequestAdaptor adaptor = new RequestAdaptor(HomeActivity.this, reqs);
            ListView lv = (ListView) HomeActivity.this.findViewById(id.listView);
            HomeActivity.this.filterBtn = (TextView) HomeActivity.this.findViewById(id.filterButton);
            lv.setAdapter(adaptor);
            LinearLayout ll2 = (LinearLayout) HomeActivity.this.findViewById(id.linearLayout2);
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
                    intent.putExtra("userImage", R.drawable.a);
                    intent.putExtra("lat", clickedReq.locationLat);
                    intent.putExtra("lon", clickedReq.locationLong);
                    HomeActivity.this.startActivity(intent);
                }
            });
            HomeActivity.this.filterBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent intent = new Intent(HomeActivity.this, FilterActivity.class);
                    HomeActivity.this.startActivity(intent);
                }
            });
        }
    }
}

