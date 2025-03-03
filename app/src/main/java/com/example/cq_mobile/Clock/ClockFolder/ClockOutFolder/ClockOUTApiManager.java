package com.example.cq_mobile.Clock.ClockFolder.ClockOutFolder;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.annotations.NotNull;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class ClockOUTApiManager {

    private static final String TAG = "ClockOUTApiManager";

    public interface ApiCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public static void clockOUT(int jobScheduleId, int taskId, int ticketMessageId, ProgressBar progressBar, String accessToken, int userId, ApiCallback callback) {
        Log.d(TAG, "clockOUT called with jobScheduleId: " + jobScheduleId + ", taskId: " + taskId + ", ticketMessageId: " + ticketMessageId + ", userId: " + userId);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new ApiClockOUTTask(jobScheduleId, taskId, ticketMessageId, progressBar, accessToken, userId, callback));
    }

    private static class ApiClockOUTTask implements Runnable {

        private final int jobScheduleId;
        private final int taskId;
        private final int ticketMessageId;
        private final ProgressBar progressBar;
        private final String accessToken;
        private final ApiCallback callback;
        int userId;

        public ApiClockOUTTask(int jobScheduleId, int taskId, int ticketMessageId, ProgressBar progressBar, String accessToken, int userId, ApiCallback callback) {
            this.jobScheduleId = jobScheduleId;
            this.taskId = taskId;
            this.ticketMessageId = ticketMessageId;
            this.progressBar = progressBar;
            this.accessToken = accessToken;
            this.userId = userId;
            this.callback = callback;
        }

        @Override
        public void run() {
            Log.d(TAG, "Starting ClockOUT API call for user: " + userId);

            String baseUrl = "https://aws.customquoter.co.uk";
            String endpoint = String.format("/api/m/start-working/timed_out/%d", userId);
            String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";
            String jsonBody = String.format("{\"user_id\": %d, \"status\": \"stop\"}", userId);

            Log.d(TAG, "Constructed API endpoint: " + baseUrl + endpoint);
            Log.d(TAG, "JSON Body: " + jsonBody);

            postStopClockOut(baseUrl, endpoint, accessToken, apiKey, jsonBody);
        }

        private void postStopClockOut(String baseUrl, String endpoint, String accessToken, String apiKey, String jsonBody) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            MediaType mediaType = MediaType.get("application/json");
            RequestBody body = RequestBody.create(jsonBody, mediaType);

            Request request = new Request.Builder()
                    .url(String.format(Locale.US, "%s%s", baseUrl, endpoint))
                    .addHeader("Authorization", String.format(Locale.US, "Bearer %s", accessToken))
                    .addHeader("x-api-key", apiKey)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .put(body)
                    .build();

            // Logging request details
            Log.d(TAG, "Sending request to: " + request.url());
            Log.d(TAG, "Request Method: " + request.method());
            Log.d(TAG, "Request Headers: " + request.headers());
            Log.d(TAG, "Request Body: " + jsonBody.trim());

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
                    String responseBody = response.body() != null ? response.body().string() : null;

                    Log.d(TAG, "Response Code: " + response.code());
                    Log.d(TAG, "Response Message: " + response.message());
                    Log.d(TAG, "Response Headers: " + response.headers());
                    Log.d(TAG, "Response Body: " + responseBody);

                    progressBar.post(() -> progressBar.setVisibility(View.GONE));

                    if (response.isSuccessful() && responseBody != null) {
                        try {
                            Gson gson = new Gson();
                            ClockOutApiResponse apiResponse = gson.fromJson(responseBody, ClockOutApiResponse.class);

                            if (apiResponse != null && apiResponse.success) {
                                Log.i(TAG, "Clock-out successful: " + apiResponse.message);
                                callback.onSuccess();
                            } else {
                                String errorMessage = apiResponse != null ? apiResponse.message : "Unknown error";
                                Log.e(TAG, "Clock-out failed: " + errorMessage);
                                callback.onFailure("Failed to clock out: " + errorMessage);
                            }
                        } catch (JsonSyntaxException e) {
                            Log.e(TAG, "JSON Parsing Error: " + e.getMessage(), e);
                            callback.onFailure("Failed to parse response");
                        }
                    } else {
                        Log.e(TAG, "Request failed with status: " + response.code());
                        callback.onFailure("Request failed with status: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                    Log.e(TAG, "Request Failed: " + e.getMessage(), e);
                    progressBar.post(() -> progressBar.setVisibility(View.GONE));
                    callback.onFailure("Error clocking out: " + e.getMessage());
                }
            });
        }
    }
}
