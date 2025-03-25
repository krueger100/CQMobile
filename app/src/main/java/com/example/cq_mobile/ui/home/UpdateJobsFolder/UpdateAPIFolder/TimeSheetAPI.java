package com.example.cq_mobile.ui.home.UpdateJobsFolder.UpdateAPIFolder;

import android.util.Log;

import com.example.cq_mobile.Clock.ApiTimeSheetCallback;

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

    public static void sendTimeSheetData(
            String accessToken,
            int userId,
            double userLatitude,
            double userLongitude,
            double latitude,
            double longitude,
            int jobId,
            String ukDate,
            String startTime,
            String endTime,
            ApiTimeSheetCallback callback) {

        if (accessToken == null || accessToken.isEmpty()) {
            Log.e("TimeSheetAPI", "Access token is required.");
            callback.onFailure("Access token is missing");
            return;
        }

        Log.d("TimeSheetAPI", "Sending TimeSheet Data...");

        try {
            // Create JSON payload
            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("user", userId);
            jsonPayload.put("lat", userLatitude);
            jsonPayload.put("long", userLongitude);
            jsonPayload.put("job", jobId);  // Ensure job ID is correctly set
            jsonPayload.put("date", ukDate);
            jsonPayload.put("start", startTime);
            jsonPayload.put("end", endTime);
            jsonPayload.put("remarks", JSONObject.NULL);
            jsonPayload.put("lat_out", latitude);
            jsonPayload.put("long_out", longitude);
            jsonPayload.put("custom_job", 1);

            // Convert JSON to string
            String jsonString = jsonPayload.toString();

            // Send job data
            sendJobData(jobId, jsonString, accessToken, new ApiTimeSheetCallback() {
                @Override
                public void onSuccess(String message) {
                    callback.onSuccess("TimeSheet Sent Successfully");
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


    public static void sendJobData(int jobId, String jsonPayload, String accessToken, ApiTimeSheetCallback callback) {
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
                if (!response.isSuccessful()) {
                    callback.onFailure("Request failed with code: " + response.code());
                } else {
                    String responseBody = response.body().string();
                    Log.d("TimeSheetAPI", "Response: " + responseBody);
                    callback.onSuccess(responseBody);
                }
            }
        });
    }
}



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
