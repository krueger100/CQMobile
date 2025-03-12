package com.example.cq_mobile.ui.home.UpdateJobsFolder.UpdateAPIFolder;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    public static void updateSubTaskApiManager(String accessToken, String jobScheduleId, String taskId, String status, ProgressBar progressbar, TextView progress_text) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new ApiUpdateTaskSub(accessToken, jobScheduleId, taskId, status, progressbar, progress_text));
    }

    private static class ApiUpdateTaskSub implements Runnable {
        private final String jobScheduleId;
        private final String taskId;
        private final String status;
        private final String accessToken;
        private final ProgressBar progressbar;
        private final TextView progress_text;
        private final Handler handler = new Handler(Looper.getMainLooper());

        public ApiUpdateTaskSub(String accessToken, String jobScheduleId, String taskId, String status, ProgressBar progressbar, TextView progress_text) {
            this.accessToken = accessToken;
            this.jobScheduleId = jobScheduleId;
            this.taskId = taskId;
            this.status = status;
            this.progressbar = progressbar;
            this.progress_text = progress_text;
        }

        @Override
        public void run() {
            setVisibility(View.VISIBLE);
            updateProgress(0);

            String baseUrl = "https://cqbms.app";//"https://aws.customquoter.co.uk";
            String endpoint = "/api/m/jobs/schedules/" + jobScheduleId + "/tasks/" + taskId;
            String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";
            String url = baseUrl + endpoint;

            String jsonBody = String.format("{\"status\": \"%s\", \"task_id\": \"%s\"}", status, taskId);
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonBody);

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("x-api-key", apiKey)
                    .patch(body)
                    .build();

            updateProgress(50);

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    Log.d(TAG, "Job status updated successfully. Response: " + responseBody);
                    updateProgress(100);
                } else {
                    Log.d(TAG, "Request Failed: " + response.code() + " - " + response.message());
                    Log.d(TAG, "Error Body: " + responseBody);
                    updateProgress(0);
                }
            } catch (IOException e) {
                Log.d(TAG, "Error: " + e.getMessage());
                updateProgress(0);
            }

            handler.postDelayed(() -> setVisibility(View.GONE), 1000);
        }

        private void updateProgress(int progress) {
            handler.post(() -> {
                if (progressbar != null && progress_text != null) {
                    progressbar.setIndeterminate(false);
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
