package com.example.cq_mobile.ui.home.UpdateJobsFolder.UpdateAPIFolder;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import com.example.cq_mobile.Clock.ApiTimeSheetCallback;
import com.example.cq_mobile.Clock.ClockFolder.ClockInAPIFolder.TimerManager;

import com.example.cq_mobile.Clock.TimeSheetFolder.ApiTSCallback;
import com.example.cq_mobile.Clock.TimeSheetFolder.StopJobApi;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;

import java.util.concurrent.TimeUnit;

public class TimeSheetAPI {
    private static final String BASE_URL_SEND_JOB_DATA = "https://cqbms.app/api/m/time-sheet/store/";
    private static final String API_KEY = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    public static void sendTimeSheetData(String accessToken, int userId,
      double userLatitude, double userLongitude, double latitude, double longitude, int jobId,
       ProgressBar progressBar, Context context, ApiTimeSheetCallback callback) {

        if (accessToken == null || accessToken.isEmpty()) {
            Log.e("TimeSheetAPI", "Access token is required.");
            callback.onFailure("Access token is missing");
            return;
        }
        new Handler(Looper.getMainLooper()).post(() -> {
        }); // <-

        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
        String startDate = sharedPrefManager.getKeyDate();
        String stopTime = "00:00";
        String startTime = "00:00";

        Log.d("TimeSheetAPI", "startDate  " + startDate);
        Log.d("TimeSheetAPI", "startTime " + startTime);
        Log.d("TimeSheetAPI", "stopTime  " + stopTime);
        Log.d("TimeSheetAPI", "Sending TimeSheet Data...");
        Log.d("TimeSheetAPI", "jobId" + jobId);

        try {
            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("user", userId);
            jsonPayload.put("lat", userLatitude);
            jsonPayload.put("long", userLongitude);
            jsonPayload.put("job", jobId);
            jsonPayload.put("date", startDate);
            jsonPayload.put("start", startTime);
            jsonPayload.put("end", stopTime);
            jsonPayload.put("remarks", JSONObject.NULL);
            jsonPayload.put("lat_out", latitude);
            jsonPayload.put("long_out", longitude);
            jsonPayload.put("custom_job", 1);

            String jsonString = jsonPayload.toString();


            sendJobData(jobId, jsonString, accessToken, userId, progressBar, context, startTime, new ApiTimeSheetCallback() {
                @Override
                public void onSuccess(String message) {
                    callback.onSuccess("TimeSheet Sent Successfully");

                    StopJobApi.stopJobWithTimesheet(userId, progressBar, context, new ApiTSCallback() {
                        @Override
                        public void onSuccess(String message) {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                Log.w("StopJobApi", "message" + "\n -> " + message);


                                progressBar.post(() -> progressBar.setVisibility(View.GONE));
                                TimerManager timerManager = TimerManager.getInstance(context);
                                timerManager.resetTimer(context);
                            });
                        }

                        @Override
                        public void onFailure(String error) {
                            new Handler(Looper.getMainLooper()).post(() ->
                                    Log.e("TimeSheetAPI", "Failed to stop job: " + error));
                        }
                    });

                }

                @Override
                public void onFailure(String error) {
                    callback.onFailure("Failed to send time sheet data: " + error);
                }
            });

        } catch (Exception e) {
            Log.e("TimeSheetAPI", "JSON Exception: " + e.getMessage());
            callback.onFailure("Failed to create JSON payload");
        }


}

    public static void sendJobData(int jobId, String jsonPayload, String accessToken, int userId, ProgressBar progressBar,
                                   Context context, String startTime, ApiTimeSheetCallback callback) {
        String url = BASE_URL_SEND_JOB_DATA + jobId;

        Log.d("TimeSheetAPI", "Sending Job Data to URL: " + url);
        Log.d("TimeSheetAPI", "Payload: " + jsonPayload);

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
                Log.e("TimeSheetAPI", "Request failed: " + e.getMessage());
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("TimeSheetAPI", "Response Code: " + response.code());
                Log.d("TimeSheetAPI", "Response Body: " + responseBody);

                if (!response.isSuccessful()) {

                    Log.w("TimeSheetAPI " , "Calling: StopJobApiManagerWithoutLogout");


                    callback.onFailure("Request failed with code: " + response.code() + " - " + responseBody);

                } else {
                    callback.onSuccess(responseBody);
                }

            }
        });
    }
}

/*

           String startedDate = sharedPrefManager.getKeyStartDate();
                        String stopDate = sharedPrefManager.getKeyStopDate();


                        new Handler(Looper.getMainLooper()).post(() -> {
                            if (accessToken != null && !accessToken.isEmpty()) {
                                new Thread(() -> {
                                    TimeSheetAPI.sendTimeSheetData(accessToken, userId, userLatitude, userLongitude, latitude, longitude,
                                            Integer.parseInt(jobId), ukDate, startedDate, stopDate,
                                            new ApiTimeSheetCallback() {
                                                @Override
                                                public void onSuccess(String serverMessage) {
                                                    Log.d("TimeSheetManager", "sendTimeSheetData: " + serverMessage);
                                                    new Handler(Looper.getMainLooper()).post(() -> {
                                                        Log.w("StartJob", "TimeSheetManager: sendTimeSheetData -- >> " + serverMessage);

                                                        runOnUiThread(() -> {
                                                            progress_circular.setVisibility(View.GONE);
                                                            start_job.setText(serverMessage + "\n" + serverAdditionalMessage);
                                                            sharedPrefManager.saveStartedJobMessage(serverMessage);
                                                            showAlertDialog(NewBuild.this, success, serverMessage, accessToken, userId, progress_circular, startJob, jobId, taskId
                                                            );
                                                        });
                                                    });
                                                }

                                                @Override
                                                public void onFailure(String error) {
                                                    Log.e("ClockOutManager", "Failed to send TimeSheet Data: " + error);
                                                    new Handler(Looper.getMainLooper()).post(() ->
                                                            Toast.makeText(NewBuild.this, "Unable to Create Time Sheet", Toast.LENGTH_SHORT).show()
                                                    );
                                                }
                                            }
                                    );
                                }).start();
                            } else {
                                Log.d("ClockOutManager", "Access token is missing!");
                                Toast.makeText(NewBuild.this, "Unable to Create Time Sheet", Toast.LENGTH_SHORT).show();
                            }
                        });


 */

/*

        curl -X POST "https://cqbms.app/api/m/time-sheet/store/9199" \
        -H "Authorization: Bearer 6319|ybatMkdUZv9WsvwYLDG5cUnERWxMPxhekJRSsspK" \
        -H "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2" \
        -H "Accept: application/json" \
        -H "Content-Type: application/json" \
        -d '{
        "user": 278,
                "lat": 15.1486464,
                "long": 120.6059008,
                "job": 9199,
                "date": "2025-02-04",
                "start": "11:30",
                "end": "11:30",
                "remarks": null,
                "lat_out": 15.1486464,
                "long_out": 120.6059008,
                "custom_job": 1





 */
