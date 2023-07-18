package com.mariam.registeration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mariam.registeration.R.id;
import com.mariam.registeration.services.HandyAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    private HandyAPI my_api = new HandyAPI();
    TextView filterBtn;
    ArrayList<Request> reqs;
    private static final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        reqs = new ArrayList<>();

        getAllRequests getReqs = new getAllRequests();
        ActivityCompat.requestPermissions(HomeActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);



        location loc = new location();
        loc.setCtx(this);
        loc.getLastLocation();
        Request req = new Request();
        req.currLat = loc.lat;
        req.currLon = loc.lon;

        getReqs.execute();

        Intent intent = getIntent();
        User current_user = (User) intent.getSerializableExtra("current_user");
        TextView profile_button = findViewById(R.id.navProfile);
        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfileMain.class);
                intent.putExtra("current_user", current_user);
                startActivity(intent);
            }
        });

        // Set click listener for navPost
        TextView navPost = findViewById(R.id.navPost);
        navPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to another page
                Intent intent = new Intent(HomeActivity.this, MyRequests.class);
                startActivity(intent);
            }
        });
    }

    public class getAllRequests extends AsyncTask<String, Integer, String> {

        private static final String API_URL = "http://"+"10.40.34.169:3000/"+"posts";

      
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

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
                for (int i = 0; i < arrSize; i++) {
                    JSONObject jsonReq = jsonarr.getJSONObject(i);
                    int id = jsonReq.getInt("id");
                    String cat = jsonReq.getString("category");
                    String title = jsonReq.getString("title");
                    String desc = jsonReq.getString("content");
                    double locationLat = jsonReq.getDouble("location_lat");
                    double locationLon = jsonReq.getDouble("location_lon");
                    String date = jsonReq.getString("service_date").substring(0, 10);
                    String time = jsonReq.getString("service_time");
                    int initialPrice = jsonReq.getInt("initial_price");
                    String userId = jsonReq.getString("national_id");
                    boolean locEnabled = true;
                    if (ActivityCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                        locEnabled = false;
                    }
                    Request req = new Request(id, cat, title, desc, date, time, locationLat, locationLon, initialPrice, userId, locEnabled);

                    reqs.add(req);
                }

                display();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        protected void display() {
            Bundle bundle = HomeActivity.this.getIntent().getExtras();
            boolean cats[] = new boolean[5];
            cats[0] = false;
            float minprice, maxprice, mindate, maxdate, mindis, maxdis;

            if (bundle != null&&bundle.getString("Uniqid").matches("From_Filter")) {

                cats = bundle.getBooleanArray("catagories");
                Log.i("Size", "s "+cats.length);
                for (int i = 0; i < reqs.size(); i++) {

                    if (cats[0] == false && reqs.get(i).cat.equals("Gardening") ) {
                        reqs.remove(reqs.get(i));

                        i--;
                    }
                }
                for (int i = 0; i < reqs.size(); i++) {
                    if (!cats[1] && reqs.get(i).cat.equals("Carcare")) {
                        reqs.remove(i);
                        i--;
                    }
                }
                for (int i = 0; i < reqs.size(); i++) {
                    if (!cats[2] && reqs.get(i).cat.equals("Installation")) {
                        reqs.remove(i);
                        i--;
                    }
                }
                for (int i = 0; i < reqs.size(); i++) {
                    if (!cats[3] && reqs.get(i).cat.equals("Transportation")) {
                        reqs.remove(i);
                        i--;
                    }
                }
                for (int i = 0; i < reqs.size(); i++) {
                    if (!cats[4] && reqs.get(i).cat.equals("Petcare")) {
                        reqs.remove(i);
                        i--;
                    }
                }
                minprice = bundle.getFloat("priceMin");
                maxprice = bundle.getFloat("priceMax");
                mindate = bundle.getFloat("dateMin");
                maxdate = bundle.getFloat("dateMax");
                mindis = bundle.getFloat("disMin");
                maxdis = bundle.getFloat("disMax");

                for (int i = 0; i < reqs.size(); i++) {
                    if (reqs.get(i).price > maxprice || reqs.get(i).price < minprice) {
                        reqs.remove(i);
                        i--;
                    }
                }
                if(reqs.get(0).locEnabled) {
                    for (int i = 0; i < reqs.size(); i++) {
                        if (reqs.get(i).distance() > maxdis || reqs.get(i).distance() < mindis) {
                            reqs.remove(i);
                            i--;
                        }
                    }
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
                    intent.putExtra("reqId", clickedReq.id);
                    intent.putExtra("userId", clickedReq.userId);


                    HomeActivity.this.startActivity(intent);
                }
            });
            HomeActivity.this.filterBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent intent = new Intent(HomeActivity.this, FilterActivity.class);
                    intent.putExtra("enabled", reqs.get(0).locEnabled);
                    HomeActivity.this.startActivity(intent);
                }
            });
        }
    }
}
