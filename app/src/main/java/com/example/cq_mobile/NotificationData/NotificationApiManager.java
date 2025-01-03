package com.example.cq_mobile.NotificationData;

import android.util.Log;

import com.example.cq_mobile.R;
import com.google.gson.Gson;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.List;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;

public class NotificationApiManager {

    // API Key
    private static final String API_KEY = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";
    private static final String BASE_URL = "https://aws.customquoter.co.uk";
    private static final String TAG = "NotificationApiManager";
    private static final String NOTIFICATION_CHANNEL_ID = "default_channel";

    public interface ApiResponseCallback {
        void onDataFetched(List<NotificationResponse.NotificationData> data);
        void onError(String error);
    }

    // Method to fetch paginated data
    public static void fetchApiDataPaginated(Context context, String accessToken, int page, int pageSize, ApiResponseCallback callback) {
        // Check for notification permissions
        if (!checkNotificationPermission(context)) {
            Log.d(TAG, "Notification permission not granted.");
            return;
        }

        String endpoint = "/api/v1/jobschedule";
        String url = String.format("%s%s?page=%d&per_page=%d&e=map&id=4&group_id=user_4&type=user&status[0]=0&status[1]=1&status[2]=2",
                BASE_URL, endpoint, page, pageSize);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("x-api-key", API_KEY)
                .addHeader("token", accessToken)
                .addHeader("job_schedule_id", "5659")
                .addHeader("task_id", "772")
                .addHeader("job_schedule_ids", "5659")
                .addHeader("note_id", "124")
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

                    displayNotification(context, "API Response", jsonResponse);

                    Gson gson = new Gson();
                    NotificationResponse notificationResponse = gson.fromJson(jsonResponse, NotificationResponse.class);

                    if (notificationResponse != null && notificationResponse.getData() != null && !notificationResponse.getData().isEmpty()) {
                        callback.onDataFetched(notificationResponse.getData());
                    } else {
                        callback.onError("No notifications found.");
                    }
                } else {
                    String errorResponse = response.body() != null ? response.body().string() : "No error body";
                    Log.e(TAG, "Request Failed: " + response.code() + " - " + errorResponse);
                    callback.onError("Request Failed: " + response.code() + " - " + errorResponse);
                }
            }
        });
    }

    // Method to display the notification
    private static void displayNotification(Context context, String title, String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Default Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.android12splash_orange)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(1, notification);
    }

    // Method to check notification permission
    private static boolean checkNotificationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }
}
