package com.example.cq_mobile.ui.home.UpdateJobsFolder.UpdateAPIFolder;

import android.util.Log;
import okhttp3.*;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//                                UpdateJobApiManager.updateJobStatus(jobId, "373", categories);
public class UpdateJobApiManager {

    private static final String TAG = "UpdateJobApiManager";

    public static void updateJobStatus(String jobScheduleId, String taskId, String status, String accessToken) {
        // Use ExecutorService to run the task in a background thread
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new ApiUpdateJobTask(jobScheduleId, taskId, status, accessToken));

    }

    private static class ApiUpdateJobTask implements Runnable {

        private String jobScheduleId;
        private String taskId;
        private String status;
        private String accessToken;

        public ApiUpdateJobTask(String jobScheduleId, String taskId, String status, String accessToken) {
            this.jobScheduleId = jobScheduleId;
            this.taskId = taskId;
            this.status = status;
            this.accessToken = accessToken; // Pass the accessToken here
        }

        @Override
        public void run() {

            String baseUrl = "https://aws.customquoter.co.uk";
            String endpoint = "/api/m/jobs/schedules/" + jobScheduleId;
            String token =accessToken;//"3805|2NzKCMW8T6zH7sA25uEhxX2BOi1nzsqvvI2CRao4";
            String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";
            String url = baseUrl + endpoint;
            String jsonBody = String.format("{\"status\": \"%s\", \"task_id\": \"%s\"}", status, taskId);
            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), jsonBody);

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
