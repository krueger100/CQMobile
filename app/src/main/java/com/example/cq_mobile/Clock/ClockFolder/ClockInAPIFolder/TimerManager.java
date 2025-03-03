package com.example.cq_mobile.Clock.ClockFolder.ClockInAPIFolder;

import android.os.Handler;
import android.os.Looper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class TimerManager {
    private static final String TAG = "TimerManager";
    private static TimerManager instance;
    private static final String PREFS_NAME = "TimerPrefs";
    private static final String KEY_SAVED_TIME = "saved_time";
    private static final String KEY_LAST_TIMESTAMP = "last_timestamp";

    private int seconds = 0;
    private boolean running = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private TimerListener listener;

    public interface TimerListener {
        void onTimerUpdate(String time);
    }

    private TimerManager() {}

    public static synchronized TimerManager getInstance(Context context) {
        if (instance == null) {
            instance = new TimerManager();
            instance.restoreSavedTime(context); // Load saved time on first instance
        }
        return instance;
    }

    public void startTimer() {
        if (!running) {
            running = true;
            handler.post(runnable);
            Log.d(TAG, "Timer started.");
        }
    }

    public void stopTimer() {
        running = false;
        Log.d(TAG, "Timer stopped.");
    }

    public void resetTimer() {
        seconds = 0;
        if (listener != null) {
            listener.onTimerUpdate(formatTime(seconds));
        }
        Log.d(TAG, "Timer reset to 00:00:00.");
    }

    public void setListener(TimerListener listener) {
        this.listener = listener;
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (running) {
                seconds += 60;
                if (listener != null) {
                    listener.onTimerUpdate(formatTime(seconds));
                }
                handler.postDelayed(this, 30000);
            }
        }
    };

    public void saveTimeState(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_SAVED_TIME, seconds);
        editor.putLong(KEY_LAST_TIMESTAMP, System.currentTimeMillis()); // Save current timestamp
        editor.apply();
        Log.d(TAG, "Timer state saved: " + formatTime(seconds));
    }

    public void restoreSavedTime(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int savedTime = prefs.getInt(KEY_SAVED_TIME, 0);
        long lastTimestamp = prefs.getLong(KEY_LAST_TIMESTAMP, 0);
        long currentTime = System.currentTimeMillis();

        if (lastTimestamp > 0) {
            long elapsedSeconds = (currentTime - lastTimestamp) / 1000; // Convert ms to sec
            savedTime += elapsedSeconds; // Add elapsed time
        }

        this.seconds = savedTime;
        if (listener != null) {
            listener.onTimerUpdate(formatTime(seconds));
        }
        Log.d(TAG, "Restored timer with elapsed time: " + formatTime(seconds));
    }

    private String formatTime(int totalSeconds) {
        int hrs = totalSeconds / 3600;
        int mins = (totalSeconds % 3600) / 60;
        int secs = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hrs, mins, secs);
    }
}
