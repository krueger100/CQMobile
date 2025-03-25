package com.example.cq_mobile.Clock.StartAndStopJobsFolder.jobEventCheckerFolder;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.cq_mobile.Clock.StartAndStopJobsFolder.StartJobAPIManager;
import com.example.cq_mobile.Clock.StartAndStopJobsFolder.StartJobResponse;
import com.example.cq_mobile.HelperManagers.UKDateTime;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.google.gson.Gson;

public class JobEventChecker {
    private final String email;
    private final String password;
    private final String accessToken;
    private final int userId;
    private final int taskId;
    private final View progressCircular;
    private final TextView startJobButton;
    private final JobEventCallback callback;
    private static final String TAG = "JobEventChecker";

    public JobEventChecker(String email, String password, String accessToken, int userId, int taskId,
                           View progressCircular, TextView startJobButton, JobEventCallback callback) {
        this.email = email;
        this.password = password;
        this.accessToken = accessToken;
        this.userId = userId;
        this.taskId = taskId;
        this.progressCircular = progressCircular;
        this.startJobButton = startJobButton;
        this.callback = callback;
    }

    public void checkStartedJob(Double latitude, Double longitude) {
        AccessTokenRequest request = new AccessTokenRequest(email, password);
        StartJobAPIManager startJobAPIManager = new StartJobAPIManager();
        Gson gson = new Gson();

        progressCircular.setVisibility(View.VISIBLE);
        Log.d(TAG, "Access Token: " + accessToken);
        Log.d(TAG, "userId: " + userId);
        Log.d(TAG, "jobId: " + taskId);
        Log.d(TAG, "email: " + email);
        Log.d(TAG, "latOut: " + latitude);
        Log.d(TAG, "longOut: " + longitude);

        startJobAPIManager.startJobWithToken(userId, String.valueOf(taskId), latitude, longitude, request, new StartJobAPIManager.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d(TAG, "Job started successfully. Response: " + response);
                StartJobResponse startJobResponse = gson.fromJson(response, StartJobResponse.class);

                if (startJobResponse == null) {
                    Log.e(TAG, "Response parsing failed.");

                    if (startJobButton.getContext() instanceof Activity) {
                        ((Activity) startJobButton.getContext()).runOnUiThread(() -> {
                            progressCircular.setVisibility(View.GONE);
                        });
                    }

                    callback.onFailure("Response parsing failed.");
                    return;
                }

                boolean success = startJobResponse.isSuccess();
                String serverMessage = startJobResponse.getMessage();

                if (startJobButton.getContext() instanceof Activity) {
                    ((Activity) startJobButton.getContext()).runOnUiThread(() -> {
                        progressCircular.setVisibility(View.GONE);
                        startJobButton.setText(serverMessage);
                    });
                }

                callback.onSuccess(serverMessage);
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Failed to start job: " + error);

                if (startJobButton.getContext() instanceof Activity) {
                    ((Activity) startJobButton.getContext()).runOnUiThread(() -> {
                        progressCircular.setVisibility(View.GONE);
                    });
                }

                callback.onFailure(error);
            }
        });
    }


}

/*
    private void checkStartedJob(Double latitude, Double longitude) {
        int tasK_id = Integer.parseInt(taskId);
        JobEventChecker jobEventChecker = new JobEventChecker(email, password, accessToken, userId, tasK_id, progress_circular, start_job,
                new JobEventCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Log.d("JobEvent", "Job Response: " + response);
                    }
                    @Override
                    public void onFailure(String error) {
                        Log.e("JobEvent", "Job start failed: " + error);
                        // Handle UI errors
                    }
                }
        );

        jobEventChecker.checkStartedJob(latitude, longitude);

    }

 */

