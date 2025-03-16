package com.example.cq_mobile.HelperManagers.Notifications.ChatNotif_folder;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.cq_mobile.HelperManagers.Notifications.NotificationManagerHelper;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatDetails;
import com.example.cq_mobile.ui.chat.ChatNotif.ChatNotificationItem;
import com.example.cq_mobile.ui.chat.ChatNotif.ChatsNotificationsApiManager;
import com.example.cq_mobile.ui.chat.ChatNotif.NotificationAPIResponse;
import com.example.cq_mobile.ui.chat.sendMessageFolder.UpdateReadAPIManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GetChatNotifManager {
    private final Context context;

    public GetChatNotifManager(Context context) {
        this.context = context;
    }

    public void GetChatNotif(String accessToken) {
        if (accessToken == null || accessToken.isEmpty()) {
            Log.e("GetChatNotif", "Error: accessToken is null or empty. API request skipped.");
            return;
        }
        Log.w("GetChatNotif", "AccessToken --->>> " + accessToken);

        ChatsNotificationsApiManager.fetchChatNotifications(accessToken, new ChatsNotificationsApiManager.ApiCallback() {
            @Override
            public void onSuccess(NotificationAPIResponse response) {
                if (response == null || !response.isSuccess() || response.getData() == null || response.getData().getChat() == null) {
                    Log.e("GetChatNotif", "Invalid or null response received");
                    return;
                }

                List<ChatNotificationItem> notifications = response.getData().getChat().getData();
                if (notifications == null || notifications.isEmpty()) {
                    Log.w("GetChatNotif", "No new chat notifications.");
                    return;
                }

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String jsonResponse = gson.toJson(notifications);
                Log.d("GetChatNotif", "Chat Notification Response: \n" + jsonResponse);
                Log.d("GetChatNotif", "SIZE: " + notifications.size());


                Handler handler = new Handler(Looper.getMainLooper());
                int delay = 3000; // 1 second delay between notifications
                int[] count = {0};



                Set<Integer> uniqueChannels = new HashSet<>();  // Use a Set to store unique channels

                for (ChatNotificationItem notification : notifications) {
                    handler.postDelayed(() -> {
                        String sender = (notification.getSender() != null) ? notification.getSender() : "Unknown Sender";
                        String message = (notification.getText() != null) ? notification.getText() : "No message available";
                        String avatarUrl = notification.getAvatar();
                        String time = (notification.getTime() != null) ? notification.getTime() : "Unknown Time";
                        String date = (notification.getDate() != null) ? notification.getDate() : "Unknown Date";
                        int channelValue = notification.getChannel();
                        String channel = (channelValue > 0) ? String.valueOf(channelValue) : "Unknown Channel";
                        Log.w("GetChatNotif", "Channel: " + channel);

                        if (avatarUrl == null || avatarUrl.isEmpty()) {
                            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.emptyglide);
                            Bitmap bitmap;

                            if (drawable instanceof BitmapDrawable) {
                                bitmap = ((BitmapDrawable) drawable).getBitmap();
                            } else if (drawable != null) {
                                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                                Canvas canvas = new Canvas(bitmap);
                                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                                drawable.draw(canvas);
                            } else {
                                Log.e("GetChatNotif", "Drawable resource emptyglide not found. Using default bitmap.");
                                bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                            }
                        }

                        NotificationManagerHelper.getInstance(context).showNotification(sender, message, avatarUrl, time, date, channel);
                    }, count[0] * delay);
                    count[0]++;

                    uniqueChannels.add(notification.getChannel());
                }

                for (int channel : uniqueChannels) {
                    UpdateReadAPIManager.updateReadStatus(channel, accessToken, new UpdateReadAPIManager.ApiCallback() {
                        @Override
                        public void onSuccess() {
                            Log.d("ChatAdapter", "Total Unread Messages: Read status updated successfully for channel: " + channel);
                        }

                        @Override
                        public void onFailure(String error) {
                            Log.d("ChatAdapter", "Total Unread Messages: Read status update failed for channel: " + channel + " Error: " + error);
                        }
                    });
                }







            }

            @Override
            public void onFailure(String error) {
                Log.e("GetChatNotif", "API Request Failed: " + (error != null ? error : "Unknown error"));
            }
        });
    }


}




/*
                for (ChatNotificationItem notification : notifications) {
                    handler.postDelayed(() -> {
                        String sender = (notification.getSender() != null) ? notification.getSender() : "Unknown Sender";
                        String message = (notification.getText() != null) ? notification.getText() : "No message available";
                        String avatarUrl = notification.getAvatar();
                        String time = (notification.getTime() != null) ? notification.getTime() : "Unknown Time";
                        String date = (notification.getDate() != null) ? notification.getDate() : "Unknown Date";
                        int channelValue = notification.getChannel();
                        String channel = (channelValue > 0) ? String.valueOf(channelValue) : "Unknown Channel";
                        Log.w("GetChatNotif", "Channel:  " + channel);

                        if (avatarUrl == null || avatarUrl.isEmpty()) {
                            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.emptyglide);
                            Bitmap bitmap;

                            if (drawable instanceof BitmapDrawable) {
                                bitmap = ((BitmapDrawable) drawable).getBitmap();
                            } else if (drawable != null) {
                                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                                Canvas canvas = new Canvas(bitmap);
                                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                                drawable.draw(canvas);
                            } else {
                                Log.e("GetChatNotif", "Drawable resource emptyglide not found. Using default bitmap.");
                                bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                            }
                        }

                        NotificationManagerHelper.getInstance(context).showNotification(sender, message, avatarUrl, time, date, channel);



                    }, count[0] * delay);
                    count[0]++;




                }


 */