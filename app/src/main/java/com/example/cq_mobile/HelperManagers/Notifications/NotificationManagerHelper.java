package com.example.cq_mobile.HelperManagers.Notifications;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.cq_mobile.HelperManagers.Notifications.ChatNotif_folder.ChatReceiver;
import com.example.cq_mobile.R;

public class NotificationManagerHelper {

    private static final String NOTIFICATION_CHANNEL_ID = "chat_notifications";
    private static NotificationManagerHelper instance;
    private final Context context;
    private final NotificationManager notificationManager;

    private NotificationManagerHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    public static synchronized NotificationManagerHelper getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationManagerHelper(context);
        }
        return instance;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Chat Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for chat updates");
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showNotification(String title, String content, String avatarUrl, String time, String date, String channelUrl) {
        String fullContent = content + "\n📅 " + date + " 🕒 " + time;

        Intent intent = new Intent(context, ChatReceiver.class);
        intent.putExtra("channel_url", channelUrl);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        int notificationId = (title + time + date).hashCode();

        // Initial notification setup
        @SuppressLint("NotificationTrampoline") NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.nav_chat)
                .setContentTitle(title)
                .setContentText("Tap to view details")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(fullContent))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Load avatar image asynchronously
        Glide.with(context)
                .asBitmap()
                .load(avatarUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        @SuppressLint("NotificationTrampoline") NotificationCompat.Builder updatedBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                                .setSmallIcon(R.drawable.nav_chat)
                                .setContentTitle(title)
                                .setContentText("Tap to view details")
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(fullContent))
                                .setLargeIcon(resource)  // Set the avatar as large icon
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                .setPriority(NotificationCompat.PRIORITY_HIGH);

                        notificationManager.notify(notificationId, updatedBuilder.build());
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {}

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {}
                });
    }

    public void showNotification(String title, String content, Bitmap avatarBitmap, String time, String date, String channelUrl) {
        String fullContent = content + "\n📅 " + date + " 🕒 " + time;

        Intent intent = new Intent(context, ChatReceiver.class);
        intent.putExtra("channel_url", channelUrl);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        int notificationId = (title + time + date).hashCode();

        // Initial notification setup
        @SuppressLint("NotificationTrampoline") NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.nav_chat)
                .setContentTitle(title)
                .setContentText("Tap to view details")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(fullContent))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Use the passed Bitmap directly
        @SuppressLint("NotificationTrampoline") NotificationCompat.Builder updatedBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.nav_chat)
                .setContentTitle(title)
                .setContentText("Tap to view details")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(fullContent))
                .setLargeIcon(avatarBitmap)  // Use the passed Bitmap as large icon
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify(notificationId, updatedBuilder.build());
    }



}

/*
notif Auth
ya29.a0AXeO80S1pEG5mqFEZkmltCiL3yDf6s-1AXrIzpPpP64o_mG93856aWHja2HFSsrDZ7hUfEsC-6scXbyjLP91-TgtWZE102yJmYQUadTp0a8UhE5HSVjMknywn9fyW-CNbkpEdIxbUoqYXxZ58ae1Dh03n7vkxg_TM8ZRps8caCgYKAbcSARMSFQHGX2Mi4wxhPivRkc79eVhnTgGKaA0175
 */


//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://aws.customquoter.co.uk/tasks?task=" + channelUrl));
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
