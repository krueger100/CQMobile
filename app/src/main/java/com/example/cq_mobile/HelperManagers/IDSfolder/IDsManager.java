package com.example.cq_mobile.HelperManagers.IDSfolder;

import android.os.Handler;
import android.os.Looper;

import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.SubTasks.SubTask;
import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.SubTasks.SubTaskResponse;
import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.TaskMainFolder.Taskmain;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class IDsManager {

    public interface ApiResponseCallback<T> {
        void onDataFetched(List<T> data);
        void onError(String error);
    }

    public static void fetchJobIdPaginated(String accessToken, ApiResponseCallback<Taskmain> callback) {

        if (accessToken == null || accessToken.isEmpty()) {
            callback.onError("Access token is missing.");
            return;
        }

        String baseUrl =  "https://cqbms.app";//"https://aws.customquoter.co.uk";
        String endpoint = "/api/m/jobs/schedules/today";
        String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";
        String url = String.format("%s%s?page=1&per_page=100&status=todo", baseUrl, endpoint);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("x-api-key", apiKey)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError("Network Error: " + (e.getMessage() != null ? e.getMessage() : "Unknown error")));
                android.util.Log.e("IDsManager", "Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonResponse = response.body().string();
                        android.util.Log.d("IDsManager", "Response body: " + jsonResponse);

                        Gson gson = new Gson();
                        TaskIdResponse taskIdResponse = gson.fromJson(jsonResponse, TaskIdResponse.class);

                        if (taskIdResponse != null) {
                            if (taskIdResponse.getData() != null) {
                                android.util.Log.d("IDsManager", "Data size: " + taskIdResponse.getData().size());
                                if (!taskIdResponse.getData().isEmpty()) {
                                    new Handler(Looper.getMainLooper()).post(() -> callback.onDataFetched(taskIdResponse.getData()));
                                } else {
                                    new Handler(Looper.getMainLooper()).post(() -> callback.onError("No jobs scheduled for today."));
                                    android.util.Log.e("IDsManager", "No jobs scheduled for today.");
                                }
                            } else {
                                android.util.Log.e("IDsManager", "Data field is null.");
                                new Handler(Looper.getMainLooper()).post(() -> callback.onError("Data field is null in response."));
                            }
                        } else {
                            android.util.Log.e("IDsManager", "Response parsing returned null.");
                            new Handler(Looper.getMainLooper()).post(() -> callback.onError("Invalid API response format."));
                        }

                    } catch (JsonSyntaxException e) {
                        new Handler(Looper.getMainLooper()).post(() -> callback.onError("JSON Parsing Error: " + e.getMessage()));
                        android.util.Log.e("IDsManager", "JSON Parsing Error: " + e.getMessage());
                    }
                } else {
                    String errorMsg = response.body() != null ? response.body().string() : "No response body";
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError("Request Failed: " + response.code() + ", " + errorMsg));
                    android.util.Log.e("IDsManager", "Request failed: " + response.code() + ", " + errorMsg);
                }
            }
        });
    }

    // Fetch Task IDs
    public static void fetchTaskIdDataPaginated(String jobId, int page, int pageSize, String accessToken, ApiResponseCallback<SubTask> callback) {

        if (accessToken == null || accessToken.isEmpty()) {
            callback.onError("Access token is missing.");
            return;
        }

        if (jobId == null || jobId.isEmpty()) {
            callback.onError("Job ID is missing.");
            return;
        }

        String baseUrl ="https://cqbms.app";// "https://aws.customquoter.co.uk";
        String endpoint = String.format("/api/m/jobs/schedules/%s/tasks", jobId);
        String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";
        String url = baseUrl + endpoint + "?page=" + page + "&per_page=" + pageSize;

        android.util.Log.d("IDsManager", "Request URL: " + url);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("x-api-key", apiKey)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError("Network Error: " + (e.getMessage() != null ? e.getMessage() : "Unknown error")));
                android.util.Log.e("IDsManager", "Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                android.util.Log.d("IDsManager", "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    android.util.Log.d("IDsManager", "Response body: " + jsonResponse);

                    try {
                        Gson gson = new Gson();
                        SubTaskResponse secondaryResponse = gson.fromJson(jsonResponse, SubTaskResponse.class);

                        if (secondaryResponse != null && secondaryResponse.getData() != null && !secondaryResponse.getData().isEmpty()) {
                            new Handler(Looper.getMainLooper()).post(() -> callback.onDataFetched(secondaryResponse.getData()));
                            android.util.Log.d("IDsManager", "Data fetched successfully.");

                            if (secondaryResponse.getMeta() != null) {
                                int currentPage = secondaryResponse.getMeta().getCurrentPage();
                                int lastPage = secondaryResponse.getMeta().getLastPage();

                                if (currentPage < lastPage) {
                                    android.util.Log.d("IDsManager", "Fetching next page: " + (currentPage + 1));
                                    fetchTaskIdDataPaginated(jobId, currentPage + 1, pageSize, accessToken, callback);
                                } else {
                                    android.util.Log.d("IDsManager", "All data loaded.");
                                }
                            } else {
                                android.util.Log.e("IDsManager", "No meta data found.");
                                new Handler(Looper.getMainLooper()).post(() -> callback.onError("No meta data found in response."));
                            }
                        } else {
                            android.util.Log.e("IDsManager", "No sub-task data found.");
                            new Handler(Looper.getMainLooper()).post(() -> callback.onError("No sub-task data found."));
                        }
                    } catch (JsonSyntaxException e) {
                        new Handler(Looper.getMainLooper()).post(() -> callback.onError("JSON Parsing Error: " + e.getMessage()));
                        android.util.Log.e("IDsManager", "JSON Parsing Error: " + e.getMessage());
                    }
                } else {
                    String errorMsg = response.body() != null ? response.body().string() : "No response body";
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError("Request Failed: " + response.code() + ", " + errorMsg));
                    android.util.Log.e("IDsManager", "Request failed: " + response.code() + ", " + errorMsg);
                }
            }
        });
    }
}



/*

String jobId = "12345"; // Replace with your actual Job ID
String accessToken = "your_access_token"; // Replace with your actual access token

IDsManager.fetchJobIdPaginated(jobId, accessToken, new IDsManager.ApiResponseCallback<Taskmain>() {
    @Override
    public void onDataFetched(List<Taskmain> data) {
        // Handle the fetched Taskmain data
        for (Taskmain task : data) {
            Log.d("FetchJobId", "Task: " + task.toString());
        }
    }

    @Override
    public void onError(String error) {
        // Handle the error
        Log.e("FetchJobId", "Error: " + error);
    }
});
 */


/*
String jobId = "12345";
int page = 1;
int pageSize = 100;
String accessToken = "your_access_token";

IDsManager.fetchTaskIdDataPaginated(jobId, page, pageSize, accessToken, new IDsManager.ApiResponseCallback<SubTask>() {
    @Override
    public void onDataFetched(List<SubTask> data) {
        // Handle the fetched SubTask data
        for (SubTask subTask : data) {
            Log.d("FetchTaskId", "SubTask: " + subTask.toString());
        }
    }

    @Override
    public void onError(String error) {
        // Handle the error
        Log.e("FetchTaskId", "Error: " + error);
    }
});

 */