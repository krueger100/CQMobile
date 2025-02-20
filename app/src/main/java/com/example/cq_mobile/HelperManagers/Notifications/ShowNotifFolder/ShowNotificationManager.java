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

import com.example.cq_mobile.NotificationData.APIResponceFolder.FilterNotificationManager;
import com.example.cq_mobile.NotificationData.APIResponceFolder.FilteredNotificationResponse;
import com.example.cq_mobile.NotificationData.ShowNotificationActivity;
import com.example.cq_mobile.R;

import java.util.ArrayList;
import java.util.List;

public class ShowNotificationManager {

    public static void NotifFilter(Context context, String accessToken, String userId, boolean isNotificationDisplayed, SharedPreferences sharedPreferences) {
        FilterNotificationManager.fetchApiDataFilterGroupNotification(context, accessToken, userId, 1, 10, new FilterNotificationManager.ApiResponseCallback() {
            @Override
            public void onDataFetched(List<FilteredNotificationResponse.NotificationData> data) {
                // Handle the success response
                Log.d("FilterNotification", "Data fetched successfully: USER " + data);

                List<String> titles = new ArrayList<>();
                List<String> avatars = new ArrayList<>();

                // Iterate over the notification data to populate titles and avatars lists
                for (FilteredNotificationResponse.NotificationData notification : data) {
                    Log.d("NotificationGROUP", "Title: " + notification.getTitle());
                    Log.d("NotificationGROUP", "Avatar URL: " + notification.getAvatar());
                    titles.add(notification.getTitle());
                    avatars.add(notification.getAvatar());


                    if (isNotificationDisplayed) {
                        Log.d("SharedPreferencesNotif", "TRUE");
                        showNotification(context, titles, avatars);
                    } else {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("notification_displayed", false);
                        editor.apply();
                        Log.d("SharedPreferencesNotif", "FALSE");
                        new ArrayList<>(titles);
                        new ArrayList<>(avatars);
                        //      navigateToShowNotificationActivity(titles,avatars);
                    }

                }
            }

            @Override
            public void onError(String error) {
                // Handle the error
                Log.e("FilterNotification", "Error fetching data: " + error);
            }
        });
    }

    private static void showNotification(Context context, List<String> titles, List<String> avatars) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Find ImageView by ID
            ImageView notificationIndicator = ((Activity) context).findViewById(R.id.Notification_indication_on);
            ImageView header_Notification = ((Activity) context).findViewById(R.id.header_Notification);

            // Check if the ImageView is found
            if (notificationIndicator != null) {
                notificationIndicator.setVisibility(View.VISIBLE);
                Log.d("ShowNotificationManager", "Notification indicator set to VISIBLE because there are notifications");
            } else {
                Log.e("ShowNotificationManager", "Notification indicator not found in the layout");
            }

            // Set up click listener if header_Notification is found
            if (header_Notification != null) {
                header_Notification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RetrieveStoredNotificationData(context, titles, avatars);
                    }
                });
            } else {
                Log.e("ShowNotificationManager", "Header notification not found in the layout");
            }
        }, 1000);
    }

    private static void RetrieveStoredNotificationData(Context context, List<String> titles, List<String> avatars) {
        Intent intent = new Intent(context, ShowNotificationActivity.class);
        intent.putStringArrayListExtra("teamNames", new ArrayList<>(titles));
        intent.putStringArrayListExtra("teamAvatars", new ArrayList<>(avatars));
        context.startActivity(intent);
    }
}
