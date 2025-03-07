package com.example.cq_mobile.ui.home.UpdateJobsFolder.UpdateAPIFolder;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import okhttp3.*;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class UpdateJobApiManager {
    private static final String TAG = "UpdateJobApiManager";

    public static void updateJobStatus(String jobScheduleId, String taskId, String status, String accessToken, ProgressBar progressbar, TextView progress_text) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new ApiUpdateJobTask(jobScheduleId, taskId, status, accessToken, progressbar, progress_text));
    }

    private static class ApiUpdateJobTask implements Runnable {
        private final String jobScheduleId;
        private final String taskId;
        private final String status;
        private final String accessToken;
        private final ProgressBar progressbar;
        private final TextView progress_text;
        private final Handler handler = new Handler(Looper.getMainLooper());

        public ApiUpdateJobTask(String jobScheduleId, String taskId, String status, String accessToken, ProgressBar progressbar, TextView progress_text) {
            this.jobScheduleId = jobScheduleId;
            this.taskId = taskId;
            this.status = status;
            this.accessToken = accessToken;
            this.progressbar = progressbar;
            this.progress_text = progress_text;
        }

        @Override
        public void run() {
            updateProgress(0);
            setVisibility(View.VISIBLE);

            String baseUrl = "https://aws.customquoter.co.uk";
            String endpoint = "/api/m/jobs/schedules/" + jobScheduleId;
            String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";
            String url = baseUrl + endpoint;

            String jsonBody = String.format("{\"status\": \"%s\", \"task_id\": \"%s\"}", status, taskId);
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), jsonBody);

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("x-api-key", apiKey)
                    .patch(body)
                    .build();

            updateProgress(50);

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Job status updated successfully. Response: " + response.body().string());
                    updateProgress(100);
                } else {
                    Log.d(TAG, "Request Failed: " + response.code() + " - " + response.message());
                    Log.d(TAG, "Error Body: " + response.body().string());
                    updateProgress(0);
                }
            } catch (IOException e) {
                Log.d(TAG, "Error: " + e.getMessage());
                updateProgress(0);
            }

            setVisibility(View.GONE);
        }

        private void updateProgress(int progress) {
            handler.post(() -> {
                if (progressbar != null && progress_text != null) {
                    progressbar.setProgress(progress);
                    progress_text.setText(progress + "%");
                }
            });
        }

        private void setVisibility(int visibility) {
            handler.post(() -> {
                if (progressbar != null && progress_text != null) {
                    progressbar.setVisibility(visibility);
                    progress_text.setVisibility(visibility);
                }
            });
        }
    }
}
