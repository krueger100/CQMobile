package com.example.cq_mobile.Clock.ClockFolder.ClockInAPIFolder;

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

import okhttp3.Response;
import okhttp3.Callback;

import org.json.JSONException;
import org.json.JSONObject;

public class StartJobApiManager {
    private static final String TAG = "StartJobApiManager";
    private static final String BASE_URL = "https://aws.customquoter.co.uk";
    private static final String API_ENDPOINT = "/api/v1/start-working/3";
    private static final String API_KEY = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";
        String accessToken;
    public interface ApiCallback {
        void onSuccess(String message);
        void onFailure(String error);
    }

    public static void startJob(String accessToken, ProgressBar progressBar, ApiCallback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // Adjust as needed
                .readTimeout(30, TimeUnit.SECONDS) // Adjust as needed
                .build();

        // JSON body (from the image - corrected)
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("job", 4); // Correct key from the image
            jsonBody.put("custom_job", ""); // From image
            jsonBody.put("lat_out", 14.6705792); // From image (Optional - include if available)
            jsonBody.put("long_out", 120.6321152); // From image (Optional - include if available)
            //Removed other unnecessary parameters
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON body", e);
            callback.onFailure("Error creating JSON body: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));

        Log.d(TAG, "Request JSON: " + jsonBody.toString());


        Request request = new Request.Builder()
                .url(BASE_URL + API_ENDPOINT)
                .put(body) // Correct HTTP method: PUT (from the image)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("x-api-key", API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();

        Log.d(TAG, "AccessToken : " + jsonBody.toString());

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
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String message = jsonResponse.optString("message");
                        callback.onSuccess(message);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error", e);
                        callback.onFailure("JSON parsing error: " + e.getMessage());
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
}
/*

Example Usage in an Activity


                StartJobApiManager.startJob(accessToken,progressBar, new StartJobApiManager.ApiCallback() {
                    @Override
                    public void onSuccess(String message) {
                        new Handler(Looper.getMainLooper()).post(() ->
                              //  Toast.makeText(ClockActivity.this, message, Toast.LENGTH_SHORT).show());
                        Log.d("ClockActivity", "StartJobApiManager  " + message));


                    }

                    @Override
                    public void onFailure(String error) {
                        new Handler(Looper.getMainLooper()).post(() ->
                            //    Toast.makeText(ClockActivity.this, "Failed: " + error, Toast.LENGTH_LONG).show());
                        Log.d("ClockActivity", "StartJobApiManager  " + error));

                    }
                });


Example Usage in a Fragment
int jobScheduleId = 4; // Replace with actual ID
ProgressBar progressBar = getView().findViewById(R.id.progressBar); // Ensure you're getting the correct view

StartJobApiManager.startJob(jobScheduleId, progressBar, new StartJobApiManager.ApiCallback() {
    @Override
    public void onSuccess() {
        Toast.makeText(getContext(), "Job started successfully!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(String error) {
        Toast.makeText(getContext(), "Failed to start job: " + error, Toast.LENGTH_LONG).show();
    }
});


 */