package com.example.cq_mobile.Clock.ClockFolder.ClockInAPIFolder;

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

public class ClockINApiManager {

    private static final String TAG = "ClockINApiManager";

    public interface ApiCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public static void clockIN(int jobScheduleId, int taskId, int ticketMessageId, ProgressBar progressBar, String accessToken, ApiCallback callback) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new ApiClockINTask(jobScheduleId, taskId, ticketMessageId, progressBar, accessToken, callback));
    }

    private static class ApiClockINTask implements Runnable {

        private final int jobScheduleId;
        private final int taskId;
        private final int ticketMessageId;
        private final ProgressBar progressBar;
        private final String accessToken;
        private final ApiCallback callback;

        public ApiClockINTask(int jobScheduleId, int taskId, int ticketMessageId, ProgressBar progressBar, String accessToken, ApiCallback callback) {
            this.jobScheduleId = jobScheduleId;
            this.taskId = taskId;
            this.ticketMessageId = ticketMessageId;
            this.progressBar = progressBar;
            this.accessToken = accessToken;
            this.callback = callback;
        }

        @Override
        public void run() {
            String baseUrl = "https://aws.customquoter.co.uk";
            String endpoint = "/api/v1/start-working/timedIn/3";
            String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";

            String jsonBody = String.format("{\"job_schedule_id\": %d, \"task_id\": %d, \"ticket_message_id\": %d}", jobScheduleId, taskId, ticketMessageId);

            postStartClockIn(baseUrl, endpoint, accessToken, apiKey, jsonBody);
        }

        private void postStartClockIn(String baseUrl, String endpoint, String accessToken, String apiKey, String jsonBody) {
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
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    String responseBody = response.body() != null ? response.body().string() : null;
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Work started successfully. Response: " + responseBody);
                        progressBar.post(() -> progressBar.setVisibility(View.GONE));
                        callback.onSuccess();
                    } else {
                        Log.e(TAG, "Request Failed: " + response.code() + " - " + response.message());
                        Log.e(TAG, "Error Body: " + responseBody);
                        progressBar.post(() -> progressBar.setVisibility(View.GONE));
                        callback.onFailure("Failed to start work: " + responseBody);
                    }
                }

                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e(TAG, "Error starting work: " + e.getMessage(), e);
                    progressBar.post(() -> progressBar.setVisibility(View.GONE));
                    callback.onFailure("Error starting work: " + e.getMessage());
                }
            });
        }
    }
}


/*
curl -X POST "https://aws.customquoter.co.uk/api/v1/start-working/timedIn/4" \
-H "Authorization: Bearer 7324|ydfJPOHwE5TymiX5vzSKOfbUglApfgw3sI9Y6bcG" \
-H "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2" \
-H "Accept: application/json" \
-H "Content-Type: application/json" \
-H "User-Agent: PostmanRuntime/7.43.0" \
-H "Accept-Encoding: gzip, deflate, br" \
-H "Connection: keep-alive" \
-d '{
  "job_schedule_id": 5682,
  "task_id": 774,
  "job_schedule_ids": [5682],
  "note_id": 125,
  "ticket_category_id": 1,
  "ticket_id": 178,
  "checklist_id": 497,
  "ticket_message_id": 767
}'


 */