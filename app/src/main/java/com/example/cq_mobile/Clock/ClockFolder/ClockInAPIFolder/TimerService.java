package com.example.cq_mobile.Clock.ClockFolder.ClockInAPIFolder;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.cq_mobile.R;
public class TimerService extends Service {

    public static final String CHANNEL_ID = "TimerServiceChannel";
    public static final String TIMER_UPDATE_ACTION = "com.example.cq_mobile.TIMER_UPDATE";

    private Handler handler = new Handler(Looper.getMainLooper());
    private int seconds = 0;
    private boolean running = false;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        startForegroundService();
    }

    @SuppressLint("ForegroundServiceType")
    private void startForegroundService() {
        createNotificationChannel();

        // Initial Notification Setup
        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Clocked In")
                .setContentText("00:00:00")  // Initial time
                .setSmallIcon(R.drawable.android12splash_orange)
                .setOnlyAlertOnce(true)  // Prevents sound/vibration on updates
                .setOngoing(true);       // Makes the notification persistent

        startForeground(1, notificationBuilder.build());
        startTimer();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Timer Service Channel",
                    NotificationManager.IMPORTANCE_LOW // Low importance to avoid sound
            );
            notificationManager.createNotificationChannel(serviceChannel);
        }
    }

    private void startTimer() {
        running = true;
        handler.post(timerRunnable);
    }

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (running) {
                seconds++;
                int hrs = seconds / 3600;
                int mins = (seconds % 3600) / 60;
                int secs = seconds % 60;
                String time = String.format("%02d:%02d:%02d", hrs, mins, secs);

                // 🔄 Update Notification with New Time
                notificationBuilder.setContentText(time);
                notificationManager.notify(1, notificationBuilder.build());

                // Broadcast timer update to UI
                Intent intent = new Intent(TIMER_UPDATE_ACTION);
                intent.putExtra("time", time);
                sendBroadcast(intent);

                handler.postDelayed(this, 1000); // Repeat every second
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
        handler.removeCallbacks(timerRunnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
