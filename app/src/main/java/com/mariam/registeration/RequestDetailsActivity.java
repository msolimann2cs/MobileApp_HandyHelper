package com.mariam.registeration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.mariam.registeration.services.HandyAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RequestDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextView backBtn;
    TextView invalidText;
    private GoogleMap myMap;
    SupportMapFragment supportMapFragment;
int p_id;

    double lat,lon;
    HandyAPI API = new HandyAPI();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layout.activity_request_details);
        Bundle bundle = this.getIntent().getExtras();
        LinearLayout applyLayout = (LinearLayout) this.findViewById(id.applyLayout);
        LinearLayout appliedLayout = (LinearLayout) this.findViewById(id.appliedLayout);
        ImageView image = (ImageView)this.findViewById(id.userImage);
        TextView tvTitle = (TextView)this.findViewById(id.RequestTitle);
        TextView tvDesc = (TextView)this.findViewById(id.RequestDesc);
        TextView tvDate = (TextView)this.findViewById(id.RequestDate);
        TextView tvTime = (TextView)this.findViewById(id.RequestTime);
        TextView statusText = (TextView) this.findViewById(id.statusText);
        TextView userText = (TextView) this.findViewById(id.userName);
        TextView ageText = (TextView) this.findViewById(id.userAge);
        TextView genderText = (TextView) this.findViewById(id.userGender);
        this.backBtn = (TextView)this.findViewById(id.back);
        Button applyBtn = (Button) findViewById(id.applyBtn);
        EditText bidText = (EditText) findViewById(id.bidText);
        TextView applicStatus = (TextView) findViewById(id.applicStatus);
        LinearLayout finsishedLayout = (LinearLayout) findViewById(id.finishedLayout);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(id.googlemap);
        invalidText = (TextView) findViewById(id.invalidText);
        Button startBtn = (Button) findViewById(id.startBtn);
        Button closeBtn = (Button) findViewById(id.closeBtn);
        Button finishBtn = (Button) findViewById(id.finishBtn);

        final List<String> dateTime = new ArrayList<String>();
        dateTime.add("");
        dateTime.add("");

        SharedPreferences sharedPref = getSharedPreferences("application", Context.MODE_PRIVATE);
        String national_id = sharedPref.getString("national_id", "15874962853465");
        final ArrayList<String> nat_id = new ArrayList<String>();
        nat_id.add(national_id);
        final ArrayList<Integer> post_id = new ArrayList<Integer>();
        post_id.add(0);
        final ArrayList<String> user_id = new ArrayList<String>();
        user_id.add("");


        this.backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onBackPressed();
            }
        });
        String reqDate = "";
        if (bundle != null) {
            dateTime.set(0,bundle.getString("reqTime"));
            dateTime.set(0,bundle.getString("reqTime"));
            user_id.set(0, bundle.getString("userId"));
            dateTime.set(1,bundle.getString("reqDate"));
            post_id.set(0,bundle.getInt("reqId"));
            image.setImageResource(bundle.getInt("userImage"));
            tvTitle.setText(bundle.getString("reqName"));
            tvDesc.setText(bundle.getString("reqDesc"));
            reqDate = bundle.getString("reqDate");
            tvDate.setText(reqDate);
            tvTime.setText(dateTime.get(0));
            lat = bundle.getDouble("lat");
            lon= bundle.getDouble("lon");

            
        }
updateApps updateapps = new updateApps(post_id.get(0), new updateApps.OnRequestCompletedListener() {
    @Override
    public void onRequestCompleted(String response) {

    }
});

        updateapps.execute();
        getUser userget = new getUser(user_id.get(0), new getUser.OnRequestCompletedListener() {
            @Override
            public void onRequestCompleted(String response) {
                try {
                    JSONObject jsonUser = new JSONObject(response);
                    String dobs = jsonUser.getString("date_of_birth").substring(0, 10);
                    LocalDate dob = LocalDate.parse(dobs);
                    LocalDate currDate = LocalDate.now();
                    String userName = jsonUser.getString("username");
                    int years = Period.between(dob, currDate).getYears();
                    String disYears = years + " Years old";
                    String gender;
                    if (jsonUser.getString("gender").equals("F")) {
                        gender = "Female";
                    } else {
                        gender = "Male";
                    }
                    userText.setText(userName);
                    ageText.setText(disYears);
                    genderText.setText(gender);
                }catch (JSONException e){

                }
            }
        });
        userget.execute();
        getPost apply = new getPost(national_id, post_id.get(0), new getPost.OnRequestCompletedListener() {
            @Override
            public void onRequestCompleted(String response) {
                if(!response.equals("0")){
                    applyLayout.setVisibility(View.INVISIBLE);
                    appliedLayout.setVisibility(View.VISIBLE);
                    try {
                        JSONObject application = new JSONObject(response);
                        String stat = application.getString("accepted_status");
                        Log.i("RESSSSSSSSSSSSSSSSSSSSSSSs", response);
                        if(stat.equals("P")){
                            Drawable pending = getResources().getDrawable(R.drawable.pending_48px);
                            statusText.setText("Your Application is Still Pending");
                            applicStatus.setBackground(pending);
                        } else if (stat.equals("R")) {
                            Drawable Rejected = getResources().getDrawable(R.drawable.error_48px);
                            statusText.setText("Your Application is Rejected!");
                            applicStatus.setBackground(Rejected);
                        }
                        else{
                            Drawable Accepted = getResources().getDrawable(R.drawable.check_circle_48px);
                            statusText.setText("Your Application is Accepted and will start at " + dateTime.get(0) + " "+dateTime.get(1));
                            applicStatus.setBackground(Accepted);
                            getFullPost fullPost = new getFullPost(post_id.get(0), new getFullPost.OnRequestCompletedListener(){
                                @Override
                                public void onRequestCompleted(String response){
                                    LocalDate currDate = LocalDate.now();

                                    try {
                                        JSONObject post = new JSONObject(response);
                                        String state = post.getString("state");
                                        String date = post.getString("service_date").substring(0,10);
                                        String time = post.getString("service_time");
                                        Log.i("222222222222222222222222", response);
                                        if (state.equals("n") && currDate.compareTo(LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)  )>= 0 && LocalTime.now().isAfter(LocalTime.parse(time))) {
                                            appliedLayout.setVisibility(View.INVISIBLE);
                                            startBtn.setVisibility(View.VISIBLE);
                                        } else if (state.equals("s")) {
                                            appliedLayout.setVisibility(View.INVISIBLE);
                                            startBtn.setVisibility(View.INVISIBLE);
                                            finishBtn.setVisibility(View.VISIBLE);
                                        }
                                        else if (state.equals("f")){
                                            appliedLayout.setVisibility(View.INVISIBLE);
                                            finishBtn.setVisibility(View.INVISIBLE);
                                            finsishedLayout.setVisibility(View.VISIBLE);
                                        }
                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                            });

                            fullPost.execute();
                        }
                    }catch (JSONException e){

                    }

                }
                else{
                    applyLayout.setVisibility(View.VISIBLE);
                    appliedLayout.setVisibility(View.INVISIBLE);

                    applyBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            boolean applied = false;
                            if (!(bidText.getText().toString().matches(""))) {
                                int bidPrice = Integer.parseInt(bidText.getText().toString());
                                // make post request

                                if (bidPrice > 10) {
                                    applied = true;
                                    applyToPost app = new applyToPost(national_id, post_id.get(0), Integer.parseInt(bidText.getText().toString()), new applyToPost.OnRequestCompletedListener() {
                                        @Override
                                        public void onRequestCompleted(String response) {
                                            if(response.equals("1")) {
                                                Intent intent = new Intent(RequestDetailsActivity.this, AppliedActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }else{
                                                invalidText.setVisibility(view.VISIBLE);
                                            }
                                        }
                                    });
                                    app.execute();

                                }

                            }
                            if (!applied) {
                                invalidText.setVisibility(view.VISIBLE);
                            }
                        }
                    });



                }
            }
        });

        apply.execute();
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePostState update = new updatePostState(post_id.get(0),"s", new updatePostState.OnRequestCompletedListener(){
                    @Override
                    public void onRequestCompleted(String response){
                        finish();
                        startActivity(getIntent());
                    }

                });
                update.execute();
            }
        });

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePostState update = new updatePostState(post_id.get(0),"f", new updatePostState.OnRequestCompletedListener(){
                    @Override
                    public void onRequestCompleted(String response){
                        finish();
                        startActivity(getIntent());
                    }

                });
                update.execute();
            }
        });


        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RequestDetailsActivity.this, HomeActivity.class);
                startActivity(intent);
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

    public class getPost extends AsyncTask<String, Integer, String>{
        String API_URL = "http://"+API.API_LINK+"/"+"apply";
        private String nationalId;
        private int postId;
        private OnRequestCompletedListener listener;

        public getPost(String nationalId, int postId, OnRequestCompletedListener listener) {
            this.nationalId = nationalId;
            this.postId = postId;
            this.listener = listener;
        }
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            StringBuilder stringBuilder = new StringBuilder();


            try {
                URL url = new URL(API_URL+"?national_id="+ nationalId +"&post_id=" + postId);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");


                // Read the response
                InputStream inputStream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } catch (IOException  e) {
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
            Log.i("RESSSSSS", s);
            super.onPostExecute(s);

            if (listener != null) {
                listener.onRequestCompleted(s);
            }
        }

        public interface OnRequestCompletedListener {
            void onRequestCompleted(String response);
        }
    }


    public class applyToPost extends AsyncTask<String, Integer, String>{
        String API_URL = "http://"+API.API_LINK+"/"+"apply";
        private String nationalId;
        private int postId;
        private int price;
        private OnRequestCompletedListener listener;

        public applyToPost(String nationalId, int postId, int price,  OnRequestCompletedListener listener) {
            this.nationalId = nationalId;
            this.postId = postId;
            this.price = price;
            this.listener = listener;
        }
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            StringBuilder stringBuilder = new StringBuilder();


            try {
                URL url = new URL(API_URL+"?national_id="+ nationalId +"&post_id=" + postId);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");

                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                // Create JSON object with national_id and post_id
                JSONObject jsonParams = new JSONObject();
                jsonParams.put("national_id", nationalId);
                jsonParams.put("post_id", postId);
                jsonParams.put("apply_price", price);


                // Write JSON data to request body
                OutputStream outputStream = urlConnection.getOutputStream();
                outputStream.write(jsonParams.toString().getBytes("UTF-8"));
                outputStream.close();


                // Read the response
                InputStream inputStream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } catch (IOException | JSONException e) {
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
            Log.i("RESSSSSS", s);
            super.onPostExecute(s);

            if (listener != null) {
                listener.onRequestCompleted(s);
            }
        }

        public interface OnRequestCompletedListener {
            void onRequestCompleted(String response);
        }
    }

    public class getFullPost extends AsyncTask<String, Integer, String>{
        String API_URL = "http://"+API.API_LINK+"/"+"post";
        private int postId;
        private OnRequestCompletedListener listener;

        public getFullPost(int postId, OnRequestCompletedListener listener) {

            this.postId = postId;

            this.listener = listener;
        }
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            StringBuilder stringBuilder = new StringBuilder();


            try {
                URL url = new URL(API_URL+"?post_id=" + postId);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");



                // Read the response
                InputStream inputStream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } catch (IOException  e) {
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
            Log.i("RESSSSSS", s);
            super.onPostExecute(s);

            if (listener != null) {
                listener.onRequestCompleted(s);
            }
        }

        public interface OnRequestCompletedListener {
            void onRequestCompleted(String response);
        }
    }


    public class updatePostState extends AsyncTask<String, Integer, String>{
        String API_URL = "http://"+API.API_LINK+"/"+"updatepoststatus";
        private int postId;
        private String status;
        private OnRequestCompletedListener listener;

        public updatePostState(int postId, String status, OnRequestCompletedListener listener) {

            this.postId = postId;
            this.status = status;

            this.listener = listener;
        }
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            StringBuilder stringBuilder = new StringBuilder();


            try {
                URL url = new URL(API_URL+"?post_id=" + postId+"&status="+status);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");



                // Read the response
                InputStream inputStream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } catch (IOException  e) {
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
            Log.i("RESSSSSS", s);
            super.onPostExecute(s);

            if (listener != null) {
                listener.onRequestCompleted(s);
            }
        }

        public interface OnRequestCompletedListener {
            void onRequestCompleted(String response);
        }
    }

    public class getUser extends AsyncTask<String, Integer, String>{
        String API_URL = "http://"+API.API_LINK+"/"+"users/";
        private String nationalId;
        private int postId;
        private OnRequestCompletedListener listener;

        public getUser(String nationalId,  OnRequestCompletedListener listener) {
            this.nationalId = nationalId;

            this.listener = listener;
        }
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            StringBuilder stringBuilder = new StringBuilder();


            try {
                URL url = new URL(API_URL+ nationalId);
                Log.i("userssss", API_URL+ nationalId);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");



                // Read the response
                InputStream inputStream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } catch (IOException  e) {
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
            Log.i("RESSSSSS", s);
            super.onPostExecute(s);

            if (listener != null) {
                listener.onRequestCompleted(s);
            }
        }

        public interface OnRequestCompletedListener {
            void onRequestCompleted(String response);
        }
    }



    public class updateApps extends AsyncTask<String, Integer, String>{
        String API_URL = "http://"+API.API_LINK+"/"+"updateallapps?post_id=";

        private int postId;
        private OnRequestCompletedListener listener;

        public updateApps(int postId,  OnRequestCompletedListener listener) {
            this.postId = postId;

            this.listener = listener;
        }
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            StringBuilder stringBuilder = new StringBuilder();


            try {
                URL url = new URL(API_URL+ postId);
                Log.i("userssss", API_URL+ postId);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");



                // Read the response
                InputStream inputStream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } catch (IOException  e) {
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
            Log.i("RESSSSSS", s);
            super.onPostExecute(s);

            if (listener != null) {
                listener.onRequestCompleted(s);
            }
        }

        public interface OnRequestCompletedListener {
            void onRequestCompleted(String response);
        }
    }


}