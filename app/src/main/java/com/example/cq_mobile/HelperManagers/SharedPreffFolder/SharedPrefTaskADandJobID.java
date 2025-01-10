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
        SharedPrefTaskADandJobID sharedPrefManagerSave = new SharedPrefTaskADandJobID(TaskActivity.this);
        sharedPrefManagerSave.saveUserjobANDtaskID(jobId, String.valueOf(taskId));


 */

/*
// Initialize SharedPrefTaskADandJobID
SharedPrefTaskADandJobID sharedPrefTaskADandJobID = new SharedPrefTaskADandJobID(context);

// Retrieve saved job and task IDs
String jobId = sharedPrefTaskADandJobID.getJobId();
String taskId = sharedPrefTaskADandJobID.getTaskId();

// Use the retrieved values
if (jobId != null && taskId != null) {
    Log.d("SharedPref", "Job ID: " + jobId);
    Log.d("SharedPref", "Task ID: " + taskId);
} else {
    Log.d("SharedPref", "No job or task ID found");
}

 */