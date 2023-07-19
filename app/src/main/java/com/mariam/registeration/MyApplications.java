package com.mariam.registeration;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import com.google.android.material.tabs.TabLayout;
import com.mariam.registeration.services.HandyAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MyApplications extends AppCompatActivity {
    private TabLayout tabLayout;
    private ListView listView;
    private AppAdapter adapter;
    private String userId = "11111111111111"; // Replace with the actual user ID
    private HandyAPI my_api = new HandyAPI();


    // App class declaration
    public class App {
        private String title;
        private String content;
        private int iconResId;
        private int rejectedIconResId;
        private String date;
        private int numOfApplications;
        private int price;

        public App(String title, String content, int iconResId, int rejectedIconResId, String date, int numOfApplications, int price) {
            this.title = title;
            this.content = content;
            this.iconResId = iconResId;
            this.rejectedIconResId = rejectedIconResId;
            this.date = date;
            this.numOfApplications = numOfApplications;
            this.price = price;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public int getIconResId() {
            return iconResId;
        }

        public int getRejectedIconResId() {
            return rejectedIconResId;
        }

        public String getDate() {
            return date;
        }

        public int getNumOfApplications() {
            return numOfApplications;
        }

        public int getPrice() {
            return price;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applications);

        // Find the TabLayout by its ID
        tabLayout = findViewById(R.id.tabLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                if (position == 0) {
                    // My Requests tab selected
                    Intent intent = new Intent(MyApplications.this, MyRequests.class);

                    // Apply custom transition animations
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(MyApplications.this,
                            android.R.anim.fade_in, android.R.anim.fade_out);
                    startActivity(intent, options.toBundle());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Do nothing
            }
        });

        TabLayout.Tab myApplicationsTab = tabLayout.getTabAt(1);
        if (myApplicationsTab != null) {
            myApplicationsTab.select();
        }

        // Initialize the ListView
        listView = findViewById(R.id.listView);

        // Create and set the adapter
        adapter = new AppAdapter(this, new ArrayList<>());
        listView.setAdapter(adapter);

        // Retrieve the applied posts
        RetrieveAppliedPostsTask task = new RetrieveAppliedPostsTask();
        task.execute();
    }

    private class AppAdapter extends ArrayAdapter<App> {
        private LayoutInflater inflater;

        public AppAdapter(Context context, List<App> apps) {
            super(context, 0, apps);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder viewHolder;

            if (view == null) {
                view = inflater.inflate(R.layout.apps_list_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.iconImageView = view.findViewById(R.id.userImage);
                viewHolder.titleTextView = view.findViewById(R.id.title);
                viewHolder.descTextView = view.findViewById(R.id.desc);
                viewHolder.dateTextView = view.findViewById(R.id.date);
                viewHolder.numOfApplicationsTextView = view.findViewById(R.id.numofapplications);
                viewHolder.priceTextView = view.findViewById(R.id.price);
                viewHolder.rejectedIconImageView = view.findViewById(R.id.rejectedIcon);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            App app = getItem(position);

            if (app != null) {
                viewHolder.iconImageView.setImageResource(app.getIconResId());
                viewHolder.titleTextView.setText(app.getTitle());
                viewHolder.descTextView.setText(app.getContent());
                viewHolder.dateTextView.setText(app.getDate());
                viewHolder.numOfApplicationsTextView.setText(String.valueOf(app.getNumOfApplications()) +" Apps");
                viewHolder.priceTextView.setText(String.valueOf(app.getPrice()) +" EGP");


                // Set rejectedIcon visibility based on the rejectedIconResId
                if (app.getRejectedIconResId() != 0) {
                    viewHolder.rejectedIconImageView.setVisibility(View.VISIBLE);
                    viewHolder.rejectedIconImageView.setImageResource(app.getRejectedIconResId());
                } else {
                    viewHolder.rejectedIconImageView.setVisibility(View.GONE);
                }
            }

            return view;
        }

        private class ViewHolder {
            ImageView iconImageView;
            TextView titleTextView;
            TextView descTextView;
            TextView dateTextView;
            TextView numOfApplicationsTextView;
            TextView priceTextView;
            ImageView rejectedIconImageView;
        }
    }

    private class RetrieveAppliedPostsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String result = "";

            try {
                URL url = new URL("http://"+ my_api.API_LINK+ "/appliedPosts/" + userId);
                //URL url = new URL("http://10.40.39.215/appliedPosts/" + userId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                Log.e("TAG", "the url is" +url);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    reader.close();
                    result = stringBuilder.toString();
                } else {
                    result = "HTTP response code: " + responseCode;
                }

                connection.disconnect();
            } catch (Exception e) {
                Log.e("TAG", "Error retrieving applied posts", e);
                result = e.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // Process the retrieved data and update the adapter
            try {
                JSONArray postsArray = new JSONArray(result);
                List<App> appsList = new ArrayList<>();

                for (int i = 0; i < postsArray.length(); i++) {
                    JSONObject postObject = postsArray.getJSONObject(i);
                    String title = postObject.getString("title");
                    String content = postObject.getString("content");
                    String acceptedStatus = postObject.getString("accepted_status");
                    String rawServiceDate = postObject.getString("service_date");
                    String date = rawServiceDate.split("T")[0];
                    int numOfApplications = postObject.getInt("num_applications");
                    int price = postObject.getInt("initial_price");
                    Log.e("TAG", "The json is "+ postObject);

                    // Determine the rejectedIconResId based on the acceptedStatus
                    int rejectedIconResId = 0;
                    if (acceptedStatus.equals("R")) {
                        rejectedIconResId = R.drawable.rejected;
                    } else if (acceptedStatus.equals("P")) {
                        rejectedIconResId = R.drawable.pending;
                    } else if (acceptedStatus.equals("A")) {
                        rejectedIconResId = R.drawable.accepted;
                    }

                    // Create a new App object and add it to the list
                    appsList.add(new App(title, content, R.drawable.person, rejectedIconResId, date, numOfApplications, price));
                }

                // Clear the adapter and add the retrieved apps to it
                adapter.clear();
                adapter.addAll(appsList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
