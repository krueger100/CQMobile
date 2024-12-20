package com.example.cq_mobile.ui.home.UpdateJobsFolder;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class UpdateSubTaskApiManager {
    private static final String TAG = "UpdateSubTaskApiManager";

    public static void updateSubTaskApiManager(String accessToken, String jobScheduleId, String taskId, String status) {
        // Use ExecutorService to run the task in a background thread
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new ApiUpdateTaskSub(accessToken,jobScheduleId, taskId, status));
    }

    private static class ApiUpdateTaskSub implements Runnable {

        private String jobScheduleId;
        private String taskId;
        private String status;
        String accessToken;
        public ApiUpdateTaskSub(String accessToken, String jobScheduleId, String taskId, String status) {
            this.accessToken = accessToken;
            this.jobScheduleId = jobScheduleId;
            this.taskId = taskId;
            this.status = status;
        }

        @Override
        public void run() {

            // Updated base URL and endpoint with the new format
            String baseUrl = "https://aws.customquoter.co.uk";
            String endpoint = "/api/m/jobs/schedules/" + jobScheduleId + "/tasks/" + taskId;
            String token = accessToken;
            String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";
            String url = baseUrl + endpoint;

            // Update JSON body format to include task_id and status
            String jsonBody = String.format("{\"status\": \"%s\", \"task_id\": \"%s\"}", status, taskId);

            // Create OkHttpClient instance
            OkHttpClient client = new OkHttpClient();

            // Create request body with the JSON data
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), jsonBody);

            // Build the PATCH request
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("x-api-key", apiKey)
                    .patch(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    // Log the successful response
                    Log.d(TAG, "Job status updated successfully. Response: " + response.body().string());
                } else {
                    // Log the failure
                    Log.d(TAG, "Request Failed: " + response.code() + " - " + response.message());
                    Log.d(TAG, "Error Body: " + response.body().string());
                }
            } catch (IOException e) {
                Log.d(TAG, "Error: " + e.getMessage());
            }
        }
    }
}
