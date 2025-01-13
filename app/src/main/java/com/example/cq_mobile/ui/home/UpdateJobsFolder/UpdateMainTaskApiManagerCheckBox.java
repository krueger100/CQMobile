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

public class UpdateMainTaskApiManagerCheckBox {
    private static final String TAG = "UpdateMainTaskApiManagerCheckBox";

    public static void updateMainTaskApiManager(String accessToken, String jobScheduleId, String taskId, String checklistItemId, boolean isChecked_subTask) {
        // Use ExecutorService to run the task in a background thread
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new ApiUpdateTaskMain(accessToken, jobScheduleId, taskId, checklistItemId, isChecked_subTask));
    }

    private static class ApiUpdateTaskMain implements Runnable {

        private String jobScheduleId;
        private String taskId;
        private String checklistItemId;
        private String accessToken;
        boolean isChecked_mainTask;

        public ApiUpdateTaskMain(String accessToken, String jobScheduleId, String taskId, String checklistItemId, boolean isChecked_mainTask) {
            this.accessToken = accessToken;
            this.jobScheduleId = jobScheduleId;
            this.taskId = taskId;
            this.checklistItemId = checklistItemId; // Track checklist item ID
            this.isChecked_mainTask = isChecked_mainTask;
        }

        @Override
        public void run() {
            // Updated base URL and endpoint with the new format
            String baseUrl = "https://aws.customquoter.co.uk";
            String endpoint = "/api/m/jobs/schedules/" + jobScheduleId + "/tasks/" + taskId;
            String token = accessToken;
            String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";
            String url = baseUrl + endpoint;

            // Construct the JSON body to include the checklist with the correct item and status
            String jsonBody = String.format(
                    "{\"checklist\": [{\"id\": \"%s\", \"checked\": %d}]}",
                    checklistItemId, isChecked_mainTask ? 1 : 0
            );

            // Create OkHttpClient instance
            OkHttpClient client = new OkHttpClient();

            // Create request body with the JSON data
            RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));

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
                    Log.d(TAG, "MAINTASK updated successfully. Response: " + response.body().string());
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
