package com.example.cq_mobile.Clock.TimeSheetFolder;
import okhttp3.*;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.cq_mobile.Clock.ApiTimeSheetCallback;


public class TimeSheetManager {
    private static final String API_KEY = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .build();

    public static void sendTimeSheetData(String accessToken, int userId, Double userLatitude, Double userLongitude,
                                         double latitude, double longitude, String jobId, String ukDate,String ukTime,
                                         String startedDate, String stopDate , ApiTimeSheetCallback callback) {
        if (accessToken == null || accessToken.isEmpty()) {
            Log.e("TimeSheetManager", " sendTimeSheetData: Access token is required.");
            callback.onFailure("Access token is missing");
            return;
        }

        Log.d("TimeSheetManager", "sendTimeSheetData:  Sending TimeSheet Data...");

        @SuppressLint("DefaultLocale") String jobDataPayload = String.format("""
        {
            "user": %d,
            "lat": %.6f,
            "long": %.6f,
            "job": %s,
            "date": "%s",
            "start": "%s",
            "end": "%s",
            "remarks": null,
            "lat_out": %.6f,
            "long_out": %.6f,
            "custom_job": 1
        }
    """, userId, userLatitude, userLongitude, jobId, ukDate, startedDate, stopDate, latitude, longitude);

        sendJobData(Integer.parseInt(jobId), jobDataPayload, accessToken, new ApiTimeSheetCallback() {
            @Override
            public void onSuccess(String message) {
                callback.onSuccess("TimeSheet from sendTimeSheetData: Sent Successfully");
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure("Failed to send time sheet data: " + error);
            }
        });
    }

    public static void sendJobData(int jobId, String jsonPayload, String accessToken, ApiTimeSheetCallback callback) {
        String url = "https://cqbms.app/api/m/time-sheet/store/" + jobId;

        Log.w("TimeSheetManager", "Sending Job Data to URL: sendTimeSheetData " + url);
        Log.w("TimeSheetManager", "Payload: sendTimeSheetData " + jsonPayload);

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
                Log.e("TimeSheetManager", "sendTimeSheetData Request failed:  " + e.getMessage());
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure("sendJobData Request failed: " + response.code());
                } else {
                    Log.w("TimeSheetManager", "sendJobData Response: " + response.body().string());
                    callback.onSuccess("Job data sent successfully");
                }
            }
        });
    }



    public static void sendTimeSheetColleagueData(String accessToken, int userId, String jobId,
                                                  Double userStartLat, Double userStartLon,
                                                  Double startLat, Double startLon,
                                                  String ukDate, String keyStartDate,
                                                  String keyStopDate, ApiTimeSheetCallback callback) {
        if (accessToken == null || accessToken.isEmpty()) {
            Log.e("TimeSheetManager", "Access token is required.");
            callback.onFailure("Access token is missing");
            return;
        }

        Log.d("TimeSheetManager", "Sending TimeSheet Colleague Data...");

        // Dynamically insert the correct values into the JSON payload
        @SuppressLint("DefaultLocale") String colleagueDataPayload = String.format("""
        {
            "user": %d,
            "lat": %.6f,
            "long": %.6f,
            "job": "%s",
            "date": "%s",
            "start": "%s",
            "end": "%s",
            "remarks": null,
            "lat_out": %.6f,
            "long_out": %.6f,
            "custom_job": 1
        }
    """, userId, userStartLat, userStartLon, jobId, ukDate, keyStartDate, keyStopDate, startLat, startLon);

        sendJobColleagueData(colleagueDataPayload, accessToken, new ApiTimeSheetCallback() {
            @Override
            public void onSuccess(String message) {
                callback.onSuccess("TimeSheet Colleague Data Sent Successfully");
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure("Failed to send colleague time sheet data: " + error);
            }
        });
    }

    public static void sendJobColleagueData(String jsonPayload, String accessToken, ApiTimeSheetCallback callback) {
        String url = "https://cqbms.app/api/m/time-sheet/store/colleague/";

        Log.d("TimeSheetManager", "Sending Job Colleague Data to URL: " + url);
        Log.d("TimeSheetManager", "Payload: " + jsonPayload);

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
                Log.e("TimeSheetManager", "sendJobColleagueData Request failed: " + e.getMessage());
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure("sendJobColleagueData Request failed: " + response.code());
                } else {
                    Log.d("TimeSheetManager", "sendJobColleagueData Response: " + response.body().string());
                    callback.onSuccess("Colleague data sent successfully");
                }
            }
        });
    }
}

/*
  new Handler(Looper.getMainLooper()).post(() -> {
                            if (accessToken != null && !accessToken.isEmpty()) {
                                new Thread(() -> {
                                    TimeSheetManager.sendTimeSheetColleagueData(accessToken, userID, new ApiTimeSheetCallback() {
                                        @Override
                                        public void onSuccess(String message) {
                                            new Handler(Looper.getMainLooper()).post(() -> {
                                                AutoClockOutandLogout(accessToken, jobId, savedTaskId, startDate);
                                                Toast.makeText(context, "Timer Stopped", Toast.LENGTH_SHORT).show();
                                            });
                                        }

                                        @Override
                                        public void onFailure(String error) {
                                            Log.e("ClockOutManager", "Failed to send TimeSheet Data: " + error);
                                            new Handler(Looper.getMainLooper()).post(() ->
                                                    Toast.makeText(context, "Unable to Create Time Sheet", Toast.LENGTH_SHORT).show()
                                            );
                                        }
                                    });
                                }).start();
                            } else {
                                Log.d("ClockOutManager", "Access token is missing!");
                                Toast.makeText(context, "Unable to Create Time Sheet", Toast.LENGTH_SHORT).show();
                            }
                        });


 */

    /*
    >>> Process of clock in, start a job, stop a job, and clock out
    A. CLocked In
        : api/m/start-working/timed_in/{user_id}
            Method: Post
            Payload: {
                "user_id": 379,//optiotn - if not set the authenticated user will be use.
                "lat_out": 15.1453696,
                "long_out": 120.5960704
            }

    B. Start Job:
        : api/m/jobs/work-status/start/{user_id}
            Method: Put
            Payload: {
                "e": "jobs",
                "status": "start",
                "job": 5803,
                "custom_job": null,
                "lat_out": 15.1486464,
                "long_out": 120.6059008
                "manual": 1
            }

    C. Stop Job:
        : api/m/time-sheet/store/{job_id}
            Method: Post
            Payload: {
                "user": 379,
                "lat": 15.1449853,
                "long": 120.5887029,
                "job": 2709,
                "date": 2025-03-21,
                "start": 09:28,
                "end": 09:33,
                "remarks": "",
                "lat_out": 15.1449853,
                "long_out": 120.5887029,
                "manual": 1
            }

        : api/m/jobs/work-status/stop/{user_id}
            Method: Put
            Payload: {
                "user_id": "jobs",
                "status": "stop",
            }

    D. Clock Out:
        : api/m/time-sheet/store/colleague/{user_id}
            Method: POST
            Payload: {
                "user": 379,
                "lat": 15.2073561,
                "long": 120.6534098,
                "date": "2025-03-21",
                "start": 09:51,
                "end": 09:53,
                "clocked_out": 1,
                "remarks": "",
                "lat_out": 15.1449853,
                "long_out": 120.5887029,
                "manual": 1
            }

        : api/m/start-working/timed_out/{user_id}
            Method: PUT
            Payload: {
                "user_id": 379,
                "status": "stop"
            }


     */
