// DatabaseManager.java
package com.mariam.registeration.services;

import android.os.AsyncTask;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DatabaseManager {
    private HandyAPI my_api = new HandyAPI();
    public void updateDescription(String username, String description, View.OnClickListener callback) {
        new UpdateDescriptionTask((DatabaseCallback) callback).execute(username, description);
    }

    private class UpdateDescriptionTask extends AsyncTask<String, Void, String> {
        private final DatabaseCallback callback;

        public UpdateDescriptionTask(DatabaseCallback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String rawDescription = params[1];
            String apiUrl = "http://"+my_api.API_LINK+"/users/" + username + "/description";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                String description = new JSONObject().put("description", rawDescription).toString();
                String requestBody = description;

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(requestBody.getBytes());
                outputStream.flush();
                outputStream.close();

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
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (callback != null) {
                if (result.startsWith("Error")) {
                    callback.onDataFetchError(result);
                } else {
                    callback.onDataFetched(result);
                }
            }
        }
    }

    private class GetUserDetailsTask extends AsyncTask<String, Void, String> {
        private final DatabaseCallback callback;

        public GetUserDetailsTask(DatabaseCallback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(String... params) {
            String nationalId = params[0];
            String apiUrl = "http://"+my_api.API_LINK+"/users/" + nationalId + "/details";

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
            if (callback != null) {
                if (result.startsWith("Error")) {
                    callback.onDataFetchError(result);
                } else {
                    callback.onDataFetched(result);
                }
            }
        }
    }

    public void getUserDetails(String nationalId, DatabaseCallback callback) {
        new GetUserDetailsTask(callback).execute(nationalId);
    }

}
