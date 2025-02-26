package com.example.cq_mobile.HelperManagers.Notifications.ShowNotifFolder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cq_mobile.NotificationData.APIResponceFolder.FilterNotificationManager;
import com.example.cq_mobile.NotificationData.APIResponceFolder.FilteredNotificationResponse;
import com.example.cq_mobile.NotificationData.ShowNotificationActivity;
import com.example.cq_mobile.R;

import java.util.ArrayList;
import java.util.List;

public class ShowNotificationManager {

    private static boolean isPaused = false;

    public static void NotifFilter(Context context, String accessToken, String userId, boolean isNotificationDisplayed, SharedPreferences sharedPreferences) {
        FilterNotificationManager.fetchApiDataFilterGroupNotification(context, accessToken, userId, 1, 10, new FilterNotificationManager.ApiResponseCallback() {
            @Override
            public void onDataFetched(List<FilteredNotificationResponse.NotificationData> data) {
                if (isPaused) return;

                Log.d("FilterNotification", "Data fetched successfully: USER " + data);

                List<String> titles = new ArrayList<>();
                List<String> avatars = new ArrayList<>();

                for (FilteredNotificationResponse.NotificationData notification : data) {
                    Log.d("NotificationGROUP", "Title: " + notification.getTitle());
                    Log.d("NotificationGROUP", "Avatar URL: " + notification.getAvatar());
                    titles.add(notification.getTitle());
                    avatars.add(notification.getAvatar());
                }

                if (isNotificationDisplayed) {
                    Log.d("SharedPreferencesNotif", "TRUE");
                    new Handler(Looper.getMainLooper()).post(() -> showNotification(context, titles, avatars));
                } else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("notification_displayed", false);
                    editor.apply();
                    Log.d("SharedPreferencesNotif", "FALSE");
                }
            }

            @Override
            public void onError(String error) {
                Log.e("FilterNotification", "Error fetching data: " + error);
            }
        });
    }

    private static void showNotification(Context context, List<String> titles, List<String> avatars) {
        if (isPaused) return;

        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(() -> {
            ImageView notificationIndicator = ((Activity) context).findViewById(R.id.Notification_indication_on);
            TextView notificationCount = ((Activity) context).findViewById(R.id.notification_count);
            ProgressBar progressBar = ((Activity) context).findViewById(R.id.notification_progress_bar);
            ImageView header_Notification = ((Activity) context).findViewById(R.id.header_Notification);

            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(titles.size());
                progressBar.setProgress(titles.size());
                progressBar.setVisibility(View.GONE);
            }

            if (notificationIndicator != null) {
                notificationIndicator.setVisibility(View.VISIBLE);
                notificationCount.setVisibility(View.VISIBLE);
                notificationCount.setText(String.valueOf(titles.size()));
                Log.d("ShowNotificationManager", "Notification indicator set to VISIBLE");
                Log.d("ShowNotificationManager", "Showing " + titles.size() + " notifications");
            } else {
                if (notificationIndicator != null) notificationIndicator.setVisibility(View.GONE);
                if (notificationCount != null) notificationCount.setVisibility(View.GONE);
                Log.e("ShowNotificationManager", "Notification indicator not found");
            }

            if (header_Notification != null) {
                header_Notification.setOnClickListener(v -> RetrieveStoredNotificationData(context, titles, avatars));
            } else {
                Log.e("ShowNotificationManager", "Header notification not found");
            }
        });
    }

    private static void RetrieveStoredNotificationData(Context context, List<String> titles, List<String> avatars) {
        Intent intent = new Intent(context, ShowNotificationActivity.class);
        intent.putStringArrayListExtra("teamNames", new ArrayList<>(titles));
        intent.putStringArrayListExtra("teamAvatars", new ArrayList<>(avatars));
        context.startActivity(intent);
    }

    public static void pauseNotifications() {
        isPaused = true;
    }

    public static void resumeNotifications() {
        isPaused = false;
    }
}
