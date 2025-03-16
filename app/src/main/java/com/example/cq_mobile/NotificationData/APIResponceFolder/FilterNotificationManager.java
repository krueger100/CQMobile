package com.example.cq_mobile.NotificationData.APIResponceFolder;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.cq_mobile.NotificationData.NotificationResponse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import android.Manifest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// FilterNotificationManager.java
public class FilterNotificationManager {

    private static final String API_KEY = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";
    private static final String BASE_URL = "https://cqbms.app";//"https://aws.customquoter.co.uk";
    private static final String TAG = "FilterNotificationManager";

    public interface ApiResponseCallback {
        void onDataFetched(List<FilteredNotificationResponse.NotificationData> data);
        void onError(String error);
    }

    public static void fetchApiDataFilterUserNotification(Context context, String accessToken, String userId, int page, int pageSize, ApiResponseCallback callback) {
        if (!checkNotificationPermission(context)) {
            Log.d(TAG, "Notification permission not granted.");
            return;
        }

        String group_id = null;
        if (userId != null) {
            group_id = "user_" + userId;  // Dynamically set the group_id based on userId
            Log.d("FilterNotificationManager_group_id", group_id);
        }

        String endpoint = "/api/v1/jobschedule";
        String url = String.format("%s%s?page=%d&per_page=%d&e=map&id=%s&user_id=%s&type=user&status[0]=0&status[1]=1&status[2]=2",
                BASE_URL, endpoint, page, pageSize, userId, group_id);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("x-api-key", API_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    Log.d(TAG, "Full API Response: " + jsonResponse);

                    Gson gson = new Gson();
                    FilteredNotificationResponse filteredNotificationResponse = gson.fromJson(jsonResponse, FilteredNotificationResponse.class);
                    NotificationResponse notificationResponse = gson.fromJson(jsonResponse, NotificationResponse.class);

                    String avatarUrl = notificationResponse.getAvatar();
                    Log.d(TAG, "Avatar URL: " + avatarUrl);

                    if (filteredNotificationResponse.getData() != null) {
                        List<FilteredNotificationResponse.NotificationData> notificationDataList = filteredNotificationResponse.getData();
                        for (FilteredNotificationResponse.NotificationData notification : notificationDataList) {
                            notification.setAvatar(avatarUrl); // Add avatar URL to each notification
                        }

                        for (FilteredNotificationResponse.NotificationData notification : notificationDataList) {
                            FilteredNotificationResponse.JobCategory jobCategory = notification.getJob_category();
                            if (jobCategory != null) { // Important null check!
                                String jobCategoryName = jobCategory.getName();
                                // Use jobCategoryName as needed
                                Log.d("Job Category Name", jobCategoryName);
                            } else {
                                Log.d("Job Category", "Job Category is null for this notification.");
                            }
                        }

                        callback.onDataFetched(notificationDataList);
                    } else {
                        callback.onError("No data found in the response.");
                    }
                } else {
                    String errorResponse = response.body() != null ? response.body().string() : "No error body";
                    Log.e(TAG, "Request Failed: " + response.code() + " - " + errorResponse);
                    callback.onError("Request Failed: " + response.code() + " - " + errorResponse);
                }
            }
        });
    }

    public static void fetchApiDataFilterGroupNotification(Context context, String accessToken, String userId, int page, int pageSize, ApiResponseCallback callback) {
        if (!checkNotificationPermission(context)) {
            Log.d(TAG, "Notification permission not granted.");
            return;
        }

        String group_id = null;
        if (userId != null) {
            group_id = "user_" + userId;
            Log.d(TAG, group_id);
        }
        String endpoint = "/api/v1/jobschedule";
        String url = String.format("%s%s?page=%d&per_page=%d&e=map&id=%s&group_id=%s&type=group&status[0]=0&status[1]=1&status[2]=2",
                BASE_URL, endpoint, page, pageSize, userId, group_id);  // 0 - pending, 1 - complete, 2 - skipped

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("x-api-key", API_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    Log.d(TAG, "Full API Response: " + jsonResponse);

                    try {
                        Gson gson = new Gson();
                        NotificationResponse notificationResponse = gson.fromJson(jsonResponse, NotificationResponse.class);

                        // Prepare data for callback
                        List<FilteredNotificationResponse.NotificationData> notificationDataList = new ArrayList<>();
                        for (NotificationResponse.Team team : notificationResponse.getTeammembersdata()) {
                            FilteredNotificationResponse.NotificationData data = new FilteredNotificationResponse.NotificationData();
                            data.setAvatar(team.getAvatar());
                            data.setTitle(team.getName());
                            data.setDescription("Color: " + team.getColor());
                            notificationDataList.add(data);


                        }

                        callback.onDataFetched(notificationDataList);

                    } catch (JsonSyntaxException e) {
                        Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                        callback.onError("Parsing error: " + e.getMessage());
                    }
                } else {
                    String errorResponse = response.body() != null ? response.body().string() : "No error body";
                    Log.e(TAG, "Request Failed: " + response.code() + " - " + errorResponse);
                    callback.onError("Request Failed: " + response.code() + " - " + errorResponse);
                }
            }
        });
    }

    public static boolean checkNotificationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

}

