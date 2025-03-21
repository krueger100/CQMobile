package com.example.cq_mobile.ui.home.HomeFolder.TaskFolder.FilesINTaskFolder;

import android.app.Activity;


public class FileInnerTaskManager {
    private final Activity activity;
    private final String accessToken;
    private final String jobId;
    private final int taskId;
    private final String apiKey;


    public FileInnerTaskManager(Activity activity, String accessToken, String jobId, int taskId, String apiKey) {
        this.activity = activity;
        this.accessToken = accessToken;
        this.jobId = jobId;
        this.taskId = taskId;
        this.apiKey = apiKey;

    }




}