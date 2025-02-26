package com.example.cq_mobile.Clock.ClockFolder.ClockOutFolder;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.IOException;
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
            String baseUrl = "https://aws.customquoter.co.uk";
            String endpoint = String.format("/api/v1/start-working/timed_out/%d", userId);

            String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";

            String jsonBody = String.format("{\"job_schedule_id\": %d, \"task_id\": %d, \"ticket_message_id\": %d}", jobScheduleId, taskId, ticketMessageId);
            Log.w(TAG, "jsonBody: " + jsonBody);
            postStopClockOut(baseUrl, endpoint, accessToken, apiKey, jsonBody);
        }

        private void postStopClockOut(String baseUrl, String endpoint, String accessToken, String apiKey, String jsonBody) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonBody);

            Request request = new Request.Builder()
                    .url(baseUrl + endpoint)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("x-api-key", apiKey)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("User-Agent", "PostmanRuntime/7.43.0")
                    .addHeader("Accept-Encoding", "gzip, deflate, br")
                    .addHeader("Connection", "keep-alive")
                    .put(body)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    String responseBody = response.body() != null ? response.body().string() : null;
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Clocked out successfully. Response: " + responseBody);
                        progressBar.post(() -> progressBar.setVisibility(View.GONE));
                        callback.onSuccess();
                    } else {
                        Log.e(TAG, "Request Failed: " + response.code() + " - " + response.message());
                        Log.e(TAG, "Error Body: " + responseBody);
                        progressBar.post(() -> progressBar.setVisibility(View.GONE));
                        callback.onFailure("Failed to clock out: " + responseBody);
                    }
                }

                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e(TAG, "Error clocking out: " + e.getMessage(), e);
                    progressBar.post(() -> progressBar.setVisibility(View.GONE));
                    callback.onFailure("Error clocking out: " + e.getMessage());
                }
            });
        }
    }
}

