package com.example.cq_mobile.ui.home.HomeFolder.JobsFolder;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.cq_mobile.LoginFolder.AuthManager;
import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.SubTasks.SubTask;
import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.SubTasks.SubTaskResponse;
import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.TaskMainFolder.Taskmain;
import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.TaskMainFolder.TaskmainResponse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewBuildApiManager {

    public interface ApiResponseCallback<T> {
        void onDataFetched(List<T> data);
        void onError(String error);
    }

    public static void fetchNewBuiltApiData(Context context, String jobId, ApiResponseCallback<Taskmain> callback) {
        String baseUrl = "https://cqbms.app";
        String endpoint = String.format("/api/m/jobs/schedules/%s", jobId);
        String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";
        String url = String.format("%s%s?page=1&per_page=100&status=todo", baseUrl, endpoint);
        String TAG = "NewBuildApiManager";

        // Get the token from AuthManager
        String token = AuthManager.getInstance(context).getToken();

        if (token == null) {
            // Handle case when token is null
            callback.onError("Token is missing.");
            return;
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("x-api-key", apiKey)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Use the main thread to update the UI after the failure
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    Log.d(TAG, String.format("API_RESPONSE: %s", jsonResponse));

                    Gson gson = new Gson();
                    TaskmainResponse taskmainResponse = gson.fromJson(jsonResponse, TaskmainResponse.class);

                    if (taskmainResponse != null && taskmainResponse.getData() != null) {
                        List<Taskmain> taskmainList = new ArrayList<>();
                        taskmainList.add(taskmainResponse.getData());

                        // Run on the main thread to update the UI
                        new Handler(Looper.getMainLooper()).post(() -> callback.onDataFetched(taskmainList));
                    } else {
                        new Handler(Looper.getMainLooper()).post(() -> callback.onError("No data found."));
                    }
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError("Request Failed: " + response.code()));
                }
            }
        });
    }

    public static void fetchSecondaryApiData(Context context,String jobId, int page, int pageSize, ApiResponseCallback<SubTask> callback) {
        String baseUrl = "https://cqbms.app";
        String endpoint = String.format("/api/m/jobs/schedules/%s/tasks", jobId);
        String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";

        String url = baseUrl + endpoint + "?page=" + page + "&per_page=" + pageSize;
        Log.d("ApiRequest", "Request URL: " + url); // Log the request URL

        // Get the token from AuthManager
        String token = AuthManager.getInstance(context).getToken();

        if (token == null) {
            // Handle case when token is null
            callback.onError("Token is missing.");
            return;
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("x-api-key", apiKey)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ApiRequest", "Request failed: " + e.getMessage()); // Log the error
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("ApiRequest", "Response code: " + response.code()); // Log the response code
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    Log.d("ApiRequest", "Response body: " + jsonResponse); // Log the response body

                    try {
                        Gson gson = new Gson();
                        SubTaskResponse secondaryResponse = gson.fromJson(jsonResponse, SubTaskResponse.class);

                        if (secondaryResponse != null && secondaryResponse.getData() != null) {
                            Log.d("ApiRequest", "Data fetched successfully."); // Log success
                            // Run on the main thread to update the UI
                            new Handler(Looper.getMainLooper()).post(() -> callback.onDataFetched(secondaryResponse.getData()));

                            // Check if there is more data (pagination logic)
                            if (secondaryResponse.getMeta() != null) {
                                int currentPage = secondaryResponse.getMeta().getCurrentPage();
                                int lastPage = secondaryResponse.getMeta().getLastPage();

                                if (currentPage < lastPage) {
                                    // More pages exist, request the next page
                                    Log.d("ApiRequest", "More pages available. Current page: " + currentPage);
                                    fetchSecondaryApiData(context,jobId, currentPage + 1, pageSize, callback); // Recursive call for the next page
                                } else {
                                    Log.d("ApiRequest", "All data loaded.");
                                }
                            } else {
                                Log.e("ApiRequest", "No meta data found."); // Log if no meta data
                                new Handler(Looper.getMainLooper()).post(() -> callback.onError("No meta data found."));
                            }

                        } else {
                            Log.e("ApiRequest", "No secondary data found."); // Log if no data found
                            new Handler(Looper.getMainLooper()).post(() -> callback.onError("No secondary data found."));
                        }
                    } catch (JsonSyntaxException e) {
                        Log.e("ApiRequest", "JSON Parsing Error: " + e.getMessage()); // Log JSON parsing error
                        new Handler(Looper.getMainLooper()).post(() -> callback.onError("JSON Parsing Error: " + e.getMessage()));
                    }
                } else {
                    String errorResponse = response.body().string();
                    Log.e("ApiRequest", "Request failed: " + response.code() + ", " + errorResponse); // Log request failure
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError("Request Failed: " + response.code() + ", " + errorResponse));
                }
            }
        });
    }
}
