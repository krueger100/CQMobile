package com.example.cq_mobile.HelperManagers.Notifications;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().isEmpty()) return;

        Map<String, String> data = remoteMessage.getData();
        String title = data.get("sender");
        String content = data.get("text");
        String avatarUrl = data.get("avatar");
        String time = data.get("time");
        String date = data.get("date");
        String channelUrl = data.get("channel");


        Log.d(TAG, "Received message from senderToken: "+title+" - "+content);

        // Use the NotificationManagerHelper to show the notification
        NotificationManagerHelper.getInstance(getApplicationContext())
                .showNotification(title, content, avatarUrl, time, date, channelUrl);
    }
}
