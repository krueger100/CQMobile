package com.example.cq_mobile.HelperManagers.SharedPreffFolder;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefTaskADandJobID {
    private static final String PREF_NAME = "UserJobTaskID";
    private static final String TASK_ID = "task_id";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPrefTaskADandJobID(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Save user data
    public void saveUserjobANDtaskID(String taskId) {
        editor.putString(TASK_ID, taskId);
        editor.apply();
    }
    public String getTaskId() {
        return sharedPreferences.getString(TASK_ID, null);
    }

    public void clearJob_TasK_ID() {
        editor.clear();
        editor.apply();
    }
}


/*

       SharedPrefTaskADandJobID sharedPrefTaskADandJobID = new SharedPrefTaskADandJobID(this);
        taskId = sharedPrefTaskADandJobID.getTaskId();
        if (taskId != null) {
            Log.d(TAG, "Task ID: " + taskId);
            Log.d(TAG, "Job ID: " + jobId);

        } else {
            taskId = String.valueOf(sharedPrefManager.getTaskId());
            jobId = String.valueOf(sharedPrefManager.getJobId());
            Log.d(TAG, "Job ID -> SharedPrefManager  " + taskId);
            Log.d(TAG, "Task ID -> SharedPrefManager  " + jobId);
        }

 */