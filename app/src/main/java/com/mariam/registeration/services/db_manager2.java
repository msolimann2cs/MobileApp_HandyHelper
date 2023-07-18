package com.mariam.registeration.services;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;

import com.mariam.registeration.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class db_manager2 {

    public static class FetchUserDetailsTask extends AsyncTask<String, Void, String> {
        private HandyAPI my_api = new HandyAPI();
        private WeakReference<Activity> activityRef;

        public FetchUserDetailsTask(Activity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            System.out.println(username);
            String apiUrl = "http://"+my_api.API_LINK+"/users/" + username + "/details";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        response.append(line);
                    }
                    bufferedReader.close();
                    inputStream.close();

                    return response.toString();
                } else {
                    return "Error: " + responseCode;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Activity activity = activityRef.get();
            if (activity == null || activity.isFinishing()) {
                // Activity is no longer valid, do nothing
                return;
            }

            // Parse the JSON response and extract the specific columns
            try {
                JSONObject jsonResult = new JSONObject(result);
                String rating = jsonResult.getString("rating");
                String description = jsonResult.getString("description");

                // Update the UI elements using the activity reference
                TextView rating_field = activity.findViewById(R.id.rating);
                rating_field.setText(rating);

                TextView aboutMeDescTextView = activity.findViewById(R.id.about_me_desc);
                aboutMeDescTextView.setText(description);
            } catch (JSONException e) {
                e.printStackTrace();
                // Handle JSON parsing error
            }
        }
    }

}
