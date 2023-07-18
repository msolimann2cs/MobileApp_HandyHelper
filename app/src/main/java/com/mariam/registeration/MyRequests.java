package com.mariam.registeration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.mariam.registeration.screens.profile.ProfileMain;
import com.mariam.registeration.screens.profile.ProfileSettings;
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

class CustomItem {
    private String title;
    private String description;
    private int price;
    private String date;
    private int numApplications;
    private int postId;
    private boolean hasAcceptedApplicant;

    public CustomItem(String title, String description, int price, String date, int numApplications, int postId, boolean hasAcceptedApplicant) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.date = date;
        this.numApplications = numApplications;
        this.postId = postId;
        this.hasAcceptedApplicant = hasAcceptedApplicant;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public String getDate() {
        return date;
    }

    public int getNumApplications() {
        return numApplications;
    }

    public boolean hasAcceptedApplicant() {
        return hasAcceptedApplicant;
    }

    @Override
    public String toString() {
        return title; // or any other desired format
    }

    public int getPostId() {
        return postId;
    }
}

public class MyRequests extends AppCompatActivity {

    private ListView listView;
    private List<CustomItem> itemList;
    private CustomArrayAdapter adapter;
    private TabLayout tabLayout;
    private FloatingActionButton fabCreatePost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        listView = findViewById(R.id.listView);
        tabLayout = findViewById(R.id.tabLayout);

        // Initialize the itemList
        itemList = new ArrayList<>();

        // Set the adapter with the custom ArrayAdapter
        adapter = new CustomArrayAdapter(this, itemList);
        listView.setAdapter(adapter);

        fabCreatePost = findViewById(R.id.fabCreatePost);

        // Set a click listener for the FloatingActionButton
        fabCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyRequests.this, PostService.class);
                startActivity(intent);
            }
        });

        ImageView icon1 = findViewById(R.id.icon1);
        ImageView icon3 = findViewById(R.id.icon3);


        icon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle icon1 click here
                Intent intent = new Intent(MyRequests.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        icon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle icon1 click here
                Intent intent = new Intent(MyRequests.this, ProfileSettings.class);
                startActivity(intent);
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                if (position == 1) {
                    // My Applications tab selected
                    Intent intent = new Intent(MyRequests.this, MyApplications.class);

                    // Apply custom transition animations
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(MyRequests.this,
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item
                CustomItem selectedItem = itemList.get(position);

                if (selectedItem.hasAcceptedApplicant()) {
                    // Redirect to the Accepted page
                    Intent intent = new Intent(MyRequests.this, Accepted.class);
                    intent.putExtra("post_id", selectedItem.getPostId());
                    startActivity(intent);
                } else {
                    // Open the Applicants activity and pass the post ID
                    Intent intent = new Intent(MyRequests.this, Applicants.class);
                    intent.putExtra("post_id", selectedItem.getPostId());
                    startActivity(intent);
                }
            }
        });

        // Make the HTTP request to retrieve the combined data with accepted status
        new RetrieveCombinedDataWithAcceptedTask().execute();
    }

    private class CustomArrayAdapter extends ArrayAdapter<CustomItem> {

        public CustomArrayAdapter(Context context, List<CustomItem> items) {
            super(context, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.requests_list_item, parent, false);
            }

            CustomItem item = getItem(position);

            TextView titleText = convertView.findViewById(R.id.titleText);
            TextView descriptionText = convertView.findViewById(R.id.descriptionText);
            TextView priceText = convertView.findViewById(R.id.price);
            TextView dateText = convertView.findViewById(R.id.date);
            TextView applicationsText = convertView.findViewById(R.id.numofapplications);

            // Set the values for the TextView elements
            titleText.setText(item.getTitle());
            descriptionText.setText(item.getDescription());
            priceText.setText(String.valueOf(item.getPrice()) + " EGP");
            dateText.setText(item.getDate());
            applicationsText.setText(item.getNumApplications() + " Apps");

            return convertView;
        }
    }

    private class RetrieveCombinedDataWithAcceptedTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String result = "";
            HandyAPI API = new HandyAPI();
            try {
                //String nationalID = getNationalIDFromSharedPreferences(); // Retrieve the national ID
                String nationalID = "11111111111111"; // Retrieve the national ID

                URL url = new URL("http://"+API.API_LINK+"/combinedData/" + nationalID);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                Log.e("TAG", "connection is " + connection);

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
                Log.e("TAG", "Error retrieving combined data with accepted status", e);
                result = e.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.startsWith("HTTP response code:")) {
                // Handle error
                Log.e("TAG", result);
            } else {
                // Parse the JSON response
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    Log.d("TAG", "JSON response: " + result);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String title = jsonObject.getString("title");
                        int initialPrice = jsonObject.getInt("initial_price");
                        String username = jsonObject.getString("username");
                        String itemDescription = jsonObject.getString("content");
                        String rawServiceDate = jsonObject.getString("service_date");
                        String serviceDate = rawServiceDate.split("T")[0];
                        int numApplications = jsonObject.getInt("num_applications");
                        int postId = jsonObject.getInt("id");
                        int intValue = jsonObject.getInt("has_accepted_applicant");
                        boolean hasAccepted = (intValue != 0);

                        CustomItem item = new CustomItem(title, itemDescription, initialPrice, serviceDate, numApplications, postId, hasAccepted);
                        itemList.add(item);
                    }
                    adapter.notifyDataSetChanged();

                    Log.d("TAG", "Parsed JSON response. Item count: " + itemList.size());
                } catch (JSONException e) {
                    Log.e("TAG", "Error parsing JSON response", e);
                }
            }
        }
    }

    private String getNationalIDFromSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return preferences.getString("Nat_ID", "");
    }
}
