package com.example.cq_mobile.ui.home.UpdateJobsFolder.UpdateAPIFolder;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.example.cq_mobile.Clock.ApiTimeSheetCallback;
import com.example.cq_mobile.Clock.ClockFolder.ClockInAPIFolder.TimerManager;
import com.example.cq_mobile.Clock.ClockFolder.ClockOutFolder.ClockNLogOUTApiManager;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import java.util.concurrent.TimeUnit;



/*
public class TimeSheetColleagueAPI {
    private static final String BASE_URL = "https://cqbms.app/api/m/time-sheet/store/colleague/";
    private static final String API_KEY = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    public static void sendTimeSheetColleagueData(String accessToken, int userId, double startLat, double startLon,
                                                  String ukDate, String startTime, String endTime,
                                                  ApiTimeSheetCallback callback) {
        if (accessToken == null || accessToken.isEmpty()) {
            Log.e("TimeSheetColleagueAPI", "Access token is required.");
            callback.onFailure("Access token is missing");
            return;
        }

        Log.d("TimeSheetColleagueAPI", "Sending TimeSheet Colleague Data...");

        try {
            // Create JSON payload
            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("user", userId);
            jsonPayload.put("lat", startLat);
            jsonPayload.put("long", startLon);
            jsonPayload.put("date", ukDate);
            jsonPayload.put("start", startTime);
            jsonPayload.put("end", endTime);
            jsonPayload.put("clocked_out", 1);
            jsonPayload.put("remarks", JSONObject.NULL);
            jsonPayload.put("lat_out", startLat);
            jsonPayload.put("long_out", startLon);

            // Convert JSON to string
            String jsonString = jsonPayload.toString();

            // Send colleague job data
            sendJobColleagueData(userId, jsonString, accessToken, callback);

        } catch (Exception e) {
            Log.e("TimeSheetColleagueAPI", "JSON Exception: " + e.getMessage());
            callback.onFailure("Failed to create JSON payload");
        }
    }

    public static void sendJobColleagueData(int userId, String jsonPayload, String accessToken, ApiTimeSheetCallback callback) {
        String url = BASE_URL + userId;

        Log.d("TimeSheetColleagueAPI", "Sending Job Colleague Data to URL: " + url);
        Log.d("TimeSheetColleagueAPI", "Payload: " + jsonPayload);

        RequestBody body = RequestBody.create(jsonPayload, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("x-api-key", API_KEY)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TimeSheetColleagueAPI", "Request failed: " + e.getMessage());
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure("Request failed with code: " + response.code());
                } else {
                    String responseBody = response.body().string();
                    Log.d("TimeSheetColleagueAPI", "Response: " + responseBody);

                    callback.onSuccess(responseBody);
                }
            }
        });
    }
}

 */

public class TimeSheetColleagueAPI {
    private static final String BASE_URL = "https://cqbms.app/api/m/time-sheet/store/colleague/";
    private static final String API_KEY = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    public static void sendTimeSheetColleagueData(String accessToken, int userId, Double userStartLat, Double userStartLon, String ukDate,
                                                  String startedDate, String stopDate, Double startLat, Double startLon, ProgressBar progressBar, String jobId, int taskId, Context context, ApiTimeSheetCallback apiTimeSheetCallback) {
        if (accessToken == null || accessToken.isEmpty()) {
            Log.e("TimeSheetColleagueAPI", "Access token is required.");
            return;
        }

        Log.d("TimeSheetColleagueAPI", "Sending TimeSheet Colleague Data...");

        try {
            // Create JSON payload
            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("user", userId);
            jsonPayload.put("lat", userStartLat);
            jsonPayload.put("long", userStartLon);
            jsonPayload.put("date", ukDate);
            jsonPayload.put("start", startedDate);
            jsonPayload.put("end", stopDate);
            jsonPayload.put("clocked_out", 1);
            jsonPayload.put("remarks", JSONObject.NULL);
            jsonPayload.put("lat_out", startLat);
            jsonPayload.put("long_out", startLon);

            // Convert JSON to string
            String jsonString = jsonPayload.toString();

            // Send colleague job data
            sendJobColleagueData(userId, jsonString, accessToken, progressBar, context, Integer.parseInt(jobId), taskId, startedDate);

        } catch (Exception e) {
            Log.e("TimeSheetColleagueAPI", "JSON Exception: " + e.getMessage());
        }
    }

    public static void sendJobColleagueData(int userId, String jsonPayload, String accessToken,
                                            ProgressBar progressBar, Context context,
                                            int jobId, int taskId, String startedDate) {
        String url = BASE_URL + userId;

        Log.d("TimeSheetColleagueAPI", "Sending Job Colleague Data to URL: " + url);
        Log.d("TimeSheetColleagueAPI", "Payload: " + jsonPayload);

        RequestBody body = RequestBody.create(jsonPayload, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("x-api-key", API_KEY)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TimeSheetColleagueAPI", "Request failed: " + e.getMessage());
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(context, "Failed to send timesheet data: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    new Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(context, "Request failed: " + response.code(), Toast.LENGTH_SHORT).show()
                    );
                } else {
                    String responseBody = response.body().string();
                    Log.d("TimeSheetColleagueAPI", "Response: " + responseBody);
                    int ticketMessageId = 0;
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        ClockNLogOUTApiManager.clockOuTwithLogOut(jobId, taskId, ticketMessageId, progressBar, accessToken, userId, new ClockNLogOUTApiManager.ApiCLCallback() {
                            @Override
                            public void onSuccess() {
                                Log.d("ClockOutManager", "setupClockOutWithTimeSheet: ClockOut and Logout");
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (progressBar != null) progressBar.setVisibility(View.GONE);

                                    TimerManager timerManager = TimerManager.getInstance(context, startedDate);
                                    timerManager.resetTimer(context);
                                });
                            }

                            @Override
                            public void onFailure(String error) {
                                Log.e("ClockOutManager", "Clock Out Failed: " + error);
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (progressBar != null) progressBar.setVisibility(View.GONE);

                                });
                            }
                        });
                    }, 3000);

                }
            }
        });
    }
}


/*

  curl -X POST "https://cqbms.app/api/m/time-sheet/store/colleague/278" \
        -H "Authorization: Bearer 6319|ybatMkdUZv9WsvwYLDG5cUnERWxMPxhekJRSsspK" \
        -H "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2" \
     -H "Accept: application/json" \
     -H "Content-Type: application/json" \
     -d '{
           "user": 278,
           "lat": 15.1453696,
           "long": 120.5960704,
           "date": "2025-02-05",
           "start": "13:38",
           "end": "13:38",
           "clocked_out": 1,
           "remarks": null,
           "lat_out": 15.1453696,
           "long_out": 120.5960704
         }'

 */