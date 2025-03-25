package com.example.cq_mobile.Clock.StartAndStopJobsFolder;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.NewBuild;
import com.google.gson.Gson;

import okhttp3.Response;
import okhttp3.Callback;

import org.json.JSONException;
import org.json.JSONObject;

public class StopJobApiManager {
    private static final String TAG = "StopJobApiManager";
    private static final String BASE_URL = "https://cqbms.app";
    private static final String API_ENDPOINT = "/api/m/jobs/work-status/stop/%d";
    private static final String API_KEY = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";

    public interface ApiJSCallback {
        void onSuccess(String message);
        void onFailure(String error);
    }

    public static void stopJob(String accessToken, int userId, ProgressBar progressBar, Context context, ApiJSCallback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // Adjust as needed
                .readTimeout(30, TimeUnit.SECONDS) // Adjust as needed
                .build();

        // JSON body
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("status", "stop");
            jsonBody.put("user_id", userId);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON body", e);
            callback.onFailure("Error creating JSON body: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));

        Log.d(TAG, "Request JSON: " + jsonBody.toString());

        Request request = new Request.Builder()
                .url(BASE_URL + String.format(API_ENDPOINT, userId)) // Use the userId in the URL
                .put(body) // Correct HTTP method: PUT
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("x-api-key", API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();

        // Show progress bar (ensure it's on the UI thread)
        progressBar.post(() -> progressBar.setVisibility(View.VISIBLE));

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Hide progress bar (ensure it's on the UI thread)
                progressBar.post(() -> progressBar.setVisibility(View.GONE));

                String responseBody = response.body().string();
                Log.d(TAG, "API Response: " + responseBody);

                if (response.isSuccessful()) {
                    try {
                        // Process successful response (example)
                        StopJobResponse stopJobResponse = new Gson().fromJson(responseBody, StopJobResponse.class);
                        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
                        sharedPrefManager.clearStartJob();
                        if (stopJobResponse != null && stopJobResponse.success) {
                            String message = stopJobResponse.data.message;
                            callback.onSuccess(message);
                        } else {
                            callback.onFailure("Failed to stop the job.");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response", e);
                        callback.onFailure("Error parsing response: " + e.getMessage());
                    }
                } else {
                    // Handle error response
                    Log.e(TAG, "API Error: " + response.code() + " - " + response.message());
                    callback.onFailure("API Error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // Hide progress bar (ensure it's on the UI thread)
                progressBar.post(() -> progressBar.setVisibility(View.GONE));

                Log.e(TAG, "API call failed", e);
                callback.onFailure("API call failed: " + e.getMessage());

                // Show a Toast on the UI thread (example)
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(progressBar.getContext(), "API call failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }



    // StopJobResponse class
    public static class StopJobResponse {
        boolean success;
        String error_code;
        String message;
        Data data;

        public static class Data {
            String status;
            String event;
            String message;
            String job;
            boolean clockedout;
        }
    }
}

/*
// Inside an Activity or Fragment
public void stopJobExample() {
    String accessToken = "your_access_token_here"; // Replace with your actual access token
    int userId = 379; // Replace with the actual user ID
    ProgressBar progressBar = findViewById(R.id.progressBar); // Replace with your actual ProgressBar ID

    StopJobApiManager.stopJob(accessToken, userId, progressBar, new StopJobApiManager.ApiTimeSheetCallback() {
        @Override
        public void onSuccess(String message) {
            // Handle the success response here
            Toast.makeText(getApplicationContext(), "Success: " + message, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(String error) {
            // Handle the failure response here
            Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_LONG).show();
        }
    });
}


 */

