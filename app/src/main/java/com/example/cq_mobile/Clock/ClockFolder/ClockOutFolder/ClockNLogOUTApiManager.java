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



public class ClockNLogOUTApiManager {

    private static final String TAG = "ClockNLogOUTApiManager";

    public interface ApiCLCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public static void clockOuTwithLogOut(int jobScheduleId, int taskId, int ticketMessageId, ProgressBar progressBar, String accessToken, int userId, ApiCLCallback callback) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new ApiClockNLogOUTTask(jobScheduleId, taskId, ticketMessageId, progressBar, accessToken, userId, callback));
    }

    private static class ApiClockNLogOUTTask implements Runnable {

        private final int jobScheduleId;
        private final int taskId;
        private final int ticketMessageId;
        private final ProgressBar progressBar;
        private final String accessToken;
        private final ApiCLCallback callback;
        int userId;

        public ApiClockNLogOUTTask(int jobScheduleId, int taskId, int ticketMessageId, ProgressBar progressBar, String accessToken, int userId, ApiCLCallback callback) {
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

            Log.w(TAG, "Retrieved user ID: " + userId);
            String baseUrl =  "https://cqbms.app";// "https://aws.customquoter.co.uk";
            String endpoint = String.format("/api/m/start-working/timed_out/%d", userId);
            String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";
            String jsonBody = String.format("{\"user_id\": %d, \"status\": \"stop\"}", userId);
            Log.w(TAG, "jsonBody: " + jsonBody);

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
                    .addHeader("User-Agent", "PostmanRuntime/7.43.0")
                    .addHeader("Accept-Encoding", "gzip, deflate, br")
                    .addHeader("Connection", "keep-alive")
                    .put(body)
                    .build();

            Log.w("ApiClockNLogOUTTask", "Request URL: " + request.url());
            Log.w("ApiClockNLogOUTTask", "Request Method: " + request.method());
            Log.w("ApiClockNLogOUTTask", "Request Headers: " + request.headers());
            Log.w("ApiClockNLogOUTTask", "Request Body: " + jsonBody.trim());

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
                    String responseBody = response.body() != null ? response.body().string() : null;
                    Log.d("ApiClockNLogOUTTask", "Response Code: " + response.code());
                    Log.d("ApiClockNLogOUTTask", "Response Message: " + response.message());
                    Log.d("ApiClockNLogOUTTask", "Response Headers: " + response.headers());
                    Log.d("ApiClockNLogOUTTask", "Response Body: " + responseBody);

                    progressBar.post(() -> progressBar.setVisibility(View.GONE));

                    if (response.isSuccessful() && responseBody != null) {
                        try {
                            Gson gson = new Gson();
                            ClockOutApiResponse apiResponse = gson.fromJson(responseBody, ClockOutApiResponse.class);

                            if (apiResponse != null && apiResponse.success) {
                                Log.i("ApiClockNLogOUTTask", "Clock-out successful: " + apiResponse.message);
                                callback.onSuccess();
                            } else {
                                String errorMessage = apiResponse != null ? apiResponse.message : "Unknown error";
                                Log.e("ApiClockNLogOUTTask", "Clock-out failed: " + errorMessage);
                                callback.onFailure("Failed to clock out: " + errorMessage);
                            }
                        } catch (JsonSyntaxException e) {
                            Log.e("ApiClockNLogOUTTask", "JSON Parsing Error: " + e.getMessage(), e);
                            callback.onFailure("Failed to parse response");
                        }
                    } else {
                        callback.onFailure("Request failed with status: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                    Log.e("ApiClockNLogOUTTask", "Request Failed: " + e.getMessage(), e);
                    progressBar.post(() -> progressBar.setVisibility(View.GONE));
                    callback.onFailure("Error clocking out: " + e.getMessage());
                }
            });
        }
    }
}