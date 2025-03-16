package com.example.cq_mobile.Clock.StartAndStopJobsFolder.jobEventCheckerFolder;

public interface JobEventCallback {
    void onSuccess(String response);
    void onFailure(String error);
}
