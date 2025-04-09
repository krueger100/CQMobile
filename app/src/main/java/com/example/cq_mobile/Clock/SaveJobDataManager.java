package com.example.cq_mobile.Clock;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SaveJobDataManager {

    private static final String TAG = "SaveJobDataManager";
    private static final String PREF_NAME = "JobDataPrefs";
    private static final String JOB_DATA_KEY = "job_data_key";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    public SaveJobDataManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
    }

    // Save list of job details to SharedPreferences
    public void saveJobData(List<JobDetails> jobDetailsList) {
        try {
            // Serialize the list of JobDetails to JSON format
            String json = gson.toJson(jobDetailsList);
            editor.putString(JOB_DATA_KEY, json);
            editor.apply();
            Log.d(TAG, "Job data saved successfully.");
        } catch (Exception e) {
            Log.e(TAG, "Error saving job data: " + e.getMessage());
        }
    }

    // Retrieve list of job details from SharedPreferences
    public List<JobDetails> getJobData() {
        try {
            String json = sharedPreferences.getString(JOB_DATA_KEY, null);
            if (json != null) {
                Type listType = new TypeToken<List<JobDetails>>() {}.getType();
                return gson.fromJson(json, listType);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving job data: " + e.getMessage());
        }
        return null;
    }

    // Clear all saved job data
    public void clearJobData() {
        try {
            editor.remove(JOB_DATA_KEY);
            editor.apply();
            Log.d(TAG, "Job data cleared successfully.");
        } catch (Exception e) {
            Log.e(TAG, "Error clearing job data: " + e.getMessage());
        }
    }
}

/*
        List<JobDetails> savedJobDetailsList = saveJobDataManager.getJobData();
        if (savedJobDetailsList != null && !savedJobDetailsList.isEmpty()) {
            for (JobDetails jobDetails : savedJobDetailsList) {
                Log.d("SavedJobData", "ID: " + jobDetails.getId());
                Log.d("SavedJobData", "Job ID: " + jobDetails.getJobId());
                Log.d("SavedJobData", "Job Started: " + jobDetails.getStartDate());


            }
        }

 */