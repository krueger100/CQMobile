package com.example.cq_mobile.ui.home.UpdateJobsFolder.UpdateAPIFolder;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.example.cq_mobile.Clock.ApiTimeSheetCallback;
import com.example.cq_mobile.Clock.ClockFolder.ClockInAPIFolder.TimerManager;

import com.example.cq_mobile.Clock.TimeSheetFolder.ApiTSCallback;
import com.example.cq_mobile.Clock.TimeSheetFolder.StopJobApi;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.LoginFolder.AuthManager;
import com.example.cq_mobile.MainActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import org.json.JSONObject;
import java.util.concurrent.TimeUnit;
public class TimeSheetColleagueAPI {
    private static final String BASE_URL_SEND_COLLEAGUE_DATA = "https://cqbms.app/api/m/time-sheet/store/colleague/";
    private static final String API_KEY = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    public static void sendTimeSheetColleagueData(
            double userLatitude,
            double userLongitude,
            double latitude,
            double longitude,
            int jobId,
            int taskId,
            ProgressBar progressBar,
            Context context,
            ApiTimeSheetCallback callback) {


        AuthManager authManager = AuthManager.getInstance(context);
        String accessToken = authManager.getToken();
        int userId = authManager.getUserId();

        if (accessToken == null || accessToken.isEmpty()) {
            Log.e("TimeSheetColleagueAPI", "Access token is required.");
            callback.onFailure("Access token is missing");
            return;
        }

        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
        String Date = sharedPrefManager.getKeyDate();
        String stopDate = sharedPrefManager.getKeyStopDate();
        String startDate = sharedPrefManager.getKeyStartDate();
        Log.d("TimeSheetColleagueAPI", "Date  "  +Date );
        Log.d("TimeSheetColleagueAPI", "Start time  "  +startDate );
        Log.d("TimeSheetColleagueAPI", "StopDate time  "  +stopDate );
        Log.d("TimeSheetColleagueAPI", "Sending TimeSheet Colleague Data...");

        JSONObject payload = new JSONObject();
        try {
            payload.put("user", userId);
            payload.put("lat", 0.0);
            payload.put("long", 0.0);
            payload.put("date", "2025-04-01");
            payload.put("start", "18:57");
            payload.put("end", "19:10");
            payload.put("clocked_out", 1);
            payload.put("remarks", JSONObject.NULL);
            payload.put("lat_out", 0.0);
            payload.put("long_out", 0.0);
            payload.put("manual", 1);

            String jsonString = payload.toString();

            sendJobColleagueData(jobId, jsonString, accessToken, userId, taskId, progressBar, context, new ApiTimeSheetCallback() {
                @Override
                public void onSuccess(String message) {
                    callback.onSuccess("TimeSheet Sent Successfully");

                    StopJobApi.stopJobWithTimesheetColleague( userId, progressBar, context, new ApiTSCallback() {
                        @Override
                        public void onSuccess(String message) {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                Log.w("TimeSheetColleagueAPI", "StopJob Api With Logout" + "\n -> " +
                                        "\n" + BASE_URL_SEND_COLLEAGUE_DATA + "\t" +
                                        " -> " + "https://cqbms.app/api/m/jobs/work-status/stop/" + "\t" +
                                        " -> " + "Response:  -->> " + "\t" + message);




                                progressBar.post(() -> progressBar.setVisibility(View.GONE));
                                TimerManager timerManager = TimerManager.getInstance(context);
                                timerManager.resetTimer(context);
                            });
                        }

                        @Override
                        public void onFailure(String error) {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                Log.e("TimeSheetColleagueAPI", "Failed to stop job: " + error);
                            });
                        }
                    });
                }

                @Override
                public void onFailure(String error) {
                    callback.onFailure("Failed to send timesheet data: " + error);
                }
            });

        } catch (Exception e) {
            Log.e("TimeSheetColleagueAPI", "JSON Exception: " + e.getMessage());
            callback.onFailure("Failed to create JSON payload");
        }
    }

    public static void sendJobColleagueData(
            int jobId,
            String jsonPayload,
            String accessToken,
            int userId,
            int taskId,
            ProgressBar progressBar,
            Context context,
            ApiTimeSheetCallback callback) {

        String url = BASE_URL_SEND_COLLEAGUE_DATA + userId;


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
                callback.onFailure("Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("TimeSheetColleagueAPI", "Response Code: " + response.code());
                Log.d("TimeSheetColleagueAPI", "Response Body: " + responseBody);

                if (!response.isSuccessful()) {
                    Log.w("TimeSheetColleagueAPI", "Request failed with code: " + response.code());
                    callback.onFailure("Request failed with code: " + response.code() + " - " + responseBody);
                } else {
                    callback.onSuccess(responseBody);
                }
            }
        });
    }
}


/*



        @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        // ✅
                        StopJobApi.stopJobWithTimesheetColleague(accessToken, userId, progressBar, context, new ApiTSCallback() {
                            @Override
                            public void onSuccess(String message) {
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    Log.w("TimeSheetColleagueAPI", "StopJob Api With Logout" + "\n -> " +
                                            "\n" + BASE_URL + "\t" +
                                            " -> " + "https://cqbms.app/api/m/jobs/work-status/stop/" + "\t" +
                                            " -> " + "Response:  -->> " + "\t" + message);

                                    progressBar.post(() -> progressBar.setVisibility(View.GONE));
                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                        Intent intent = new Intent(context, MainActivity.class);
                                        context.startActivity(intent);
                                    }, 2000);

                                    TimerManager timerManager = TimerManager.getInstance(context, startedDate);
                                    timerManager.resetTimer(context);
                                });
                            }

                            @Override
                            public void onFailure(String error) {
                                new Handler(Looper.getMainLooper()).post(() ->
                                        Log.e("TimeSheetColleagueAPI", "Failed to stop job: " + error));
                            }
                        });

                    }, 3000);

                } else {
                    ResponseBody responseBody = response.body();
                    String responseText = (responseBody != null) ? responseBody.string() : "No response body";


                }
            }

 */


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