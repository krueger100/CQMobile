package com.example.cq_mobile.Clock.TimeSheetFolder;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.cq_mobile.Clock.ApiTimeSheetCallback;

import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.HelperManagers.UKDateTime;
import com.example.cq_mobile.ui.home.UpdateJobsFolder.UpdateAPIFolder.TimeSheetColleagueAPI;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StopJobApiTimeSheetManager {
    private static final String TAG = "StopJobApiTimeSheetManager";
    private static final String BASE_URL = "https://cqbms.app";
    private static final String API_ENDPOINT = "/api/m/jobs/work-status/stop/%d";
    private static final String API_KEY = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";

    public interface ApiTSCallback {
        void onSuccess(String message);
        void onFailure(String error);
    }
    public static void stopJobWithTimesheet(String accessToken, int userId, ProgressBar progressBar, Context context, ApiTSCallback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

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
                .url(BASE_URL + String.format(API_ENDPOINT, userId))
                .put(body)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("x-api-key", API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();

        progressBar.post(() -> progressBar.setVisibility(View.VISIBLE));

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {


                String responseBody = response.body().string();
                Log.d(TAG, "API Response: " + responseBody);

                if (response.isSuccessful()) {
                    try {
                        StopJobTimeSheetResponse stopJobResponse = new Gson().fromJson(responseBody ,StopJobTimeSheetResponse.class);

                        if (stopJobResponse != null && stopJobResponse.success) {
                            String message = stopJobResponse.data.message;
                            callback.onSuccess(message);

                            SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
                            Double userStartLat = sharedPrefManager.getUserStartJobLatitude();
                            Double userStartLon = sharedPrefManager.getUserStartJobLongitude();
                            Double StartLat = sharedPrefManager.getStartJobLatitude();
                            Double StartLon = sharedPrefManager.getUserStartJobLongitude();
                            String ukDate = UKDateTime.getCurrentUKDate();
                            String ukTime = UKDateTime.getCurrentUKTime();
                            String jobId = sharedPrefManager.getStartJobID();
                            int taskId = sharedPrefManager.getTaskId();
                            String startedDate = sharedPrefManager.getKeyStartDate();
                            String stopDate = sharedPrefManager.getKeyStopDate();
                            sharedPrefManager.clearStartJob();
// ✅
                            new Handler(Looper.getMainLooper()).post(() -> {
                                if (accessToken != null && !accessToken.isEmpty()) {
                                    new Thread(() -> {
                                        TimeSheetColleagueAPI.sendTimeSheetColleagueData(accessToken, userId, userStartLat, userStartLon, ukDate, startedDate, stopDate,StartLat,StartLon,
                                                progressBar,jobId,taskId,context, new ApiTimeSheetCallback() {
                                                    @Override
                                                    public void onSuccess(String message) {
                                                        Log.d("StopJobApiTimeSheetManager", " -> sendTimeSheetColleagueData: <-" + message);
                                                        new Handler(Looper.getMainLooper()).post(() -> {
                                                            SharedPrefManager.getInstance(context).clearJobTrackingData();
                                                        });

                                                    }

                                                    @Override
                                                    public void onFailure(String error) {
                                                        Log.e("StopJobApiTimeSheetManager", "Failed to send TimeSheet Data: " + error);
                                                        new Handler(Looper.getMainLooper()).post(() ->
                                                                Toast.makeText(context, "Unable to Create Time Sheet", Toast.LENGTH_SHORT).show()
                                                        );
                                                    }
                                                }
                                        );
                                    }).start();
                                } else {
                                    Log.d("StopJobApiTimeSheetManager", "Access token is missing!");
                                    Toast.makeText(context, "Unable to Create Time Sheet", Toast.LENGTH_SHORT).show();
                                }
                            });




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

    public static class StopJobTimeSheetResponse {
        boolean success;
        String error_code;
        String message;
        Data data;

        public static class Data {
            String status;
            String message;
            List<Integer> shifts;
            String url;
        }
    }

}
