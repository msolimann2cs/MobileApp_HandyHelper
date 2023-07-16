package com.mariam.registeration;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserApiService {
    private static final String BASE_URL = "http://10.39.1.39:3000";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private OkHttpClient client;

    public UserApiService() {
        client = new OkHttpClient();
    }

    public void updateUserDescription(String userId, String description) {
        try {
            // Create the JSON request body
            String requestBody = "{\"description\":\"" + description + "\"}";

            // Create the HTTP PUT request
            Request request = new Request.Builder()
                    .url(BASE_URL + "/users/" + userId + "/description")
                    .put(RequestBody.create(JSON, requestBody))
                    .build();

            // Send the request asynchronously
            client.newCall(request).enqueue(new okhttp3.Callback() {

                @Override
                public void onResponse(okhttp3.Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // Handle successful response
                        // You can parse the response body if needed
                        String responseBody = response.body().string();
                        System.out.println("Update successful: " + responseBody);
                    } else {
                        // Handle error response
                        System.out.println("Update failed: " + response.code() + " " + response.message());
                    }
                }

                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    // Handle network error
                    System.out.println("Update failed: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            // Handle exception
            System.out.println("Update failed: " + e.getMessage());
        }
    }
}
