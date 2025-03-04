package com.example.cq_mobile.Clock.ClockFolder.ClockInAPIFolder;

import android.os.Handler;
import android.os.Looper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimerManager {
    private static TimerManager instance;
    private static final String PREFS_NAME = "TimerPrefs";
    private static final String KEY_SAVED_TIME = "saved_time";
    private static final String KEY_LAST_TIMESTAMP = "last_timestamp";
    private static final String TAG = "TimerManager";

    private boolean running = false;
    private long startTimeMillis = 0;
    private int seconds = 0;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private TimerListener listener;
    private SharedPrefManager sharedPrefManager;
    public interface TimerListener {
        void onTimerUpdate(String time);
    }

    private TimerManager() {}

    public static synchronized TimerManager getInstance(Context context, String startDate) {
        if (instance == null) {
            instance = new TimerManager();
            instance.sharedPrefManager = new SharedPrefManager(context);
            instance.sharedPrefManager.saveClockinStartDate(startDate);
        }
        return instance;
    }

    public void startTimer() {
        if (!running) {
            running = true;
            startTimeMillis = System.currentTimeMillis() - (seconds * 1000L);
            handler.post(runnable);
            Log.d(TAG, "Timer started.");

        }
    }

    public void stopTimer(Context context) {
        running = false;

        Log.d(TAG, "Timer stopped.");
        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
        String stopTime = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date());
        sharedPrefManager.saveClockinStopDate(stopTime);
        Log.d(TAG, "Stop time saved: " + stopTime);

    }

    public void resetTimer(Context context) {
        stopTimer(context);
        seconds = 0;
        startTimeMillis = 0;

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_SAVED_TIME);
        editor.remove(KEY_LAST_TIMESTAMP);
        editor.apply();

        if (listener != null) {
            listener.onTimerUpdate(formatTime(seconds));
        }
        Log.d(TAG, "Timer reset and data wiped.");
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (running) {
                long currentTimeMillis = System.currentTimeMillis();
                seconds = (int) ((currentTimeMillis - startTimeMillis) / 1000);

                if (listener != null) {
                    listener.onTimerUpdate(formatTime(seconds));
                }
                handler.postDelayed(this, 1000); // Update every 1 second
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
        int savedTime = prefs.getInt(KEY_SAVED_TIME, -1); // Default to -1 to check existence
        long lastTimestamp = prefs.getLong(KEY_LAST_TIMESTAMP, 0);
        long currentTime = System.currentTimeMillis();

        if (savedTime == -1 || lastTimestamp == 0) {
            // If no saved time exists, reset to 00:00:00
            this.seconds = 0;
            Log.w(TAG, "No saved time found. Starting from 00:00:00.");
        } else if (savedTime == 0) {
            // If saved time is explicitly 00:00:00, also reset
            this.seconds = 0;
            Log.w(TAG, "Saved time is 00:00:00. Starting fresh.");
        } else {
            // Calculate elapsed time and resume
            long elapsedSeconds = (currentTime - lastTimestamp) / 1000; // Convert ms to sec
            this.seconds = savedTime + (int) elapsedSeconds;
            Log.d(TAG, "Restored timer with elapsed time: " + formatTime(seconds));
        }

        if (listener != null) {
            listener.onTimerUpdate(formatTime(seconds));
        }
    }

    // 🚀 NEW METHOD TO RESUME TIMER AFTER APP REOPEN
    public void resumeTimerAfterReopen(Context context) {
        restoreSavedTime(context); // Get saved time + elapsed time
        startTimer(); // Resume the timer
        Log.d(TAG, "Timer resumed from saved state: " + formatTime(seconds));
    }

    // 🔹 Helper method to format time properly
    private String formatTime(int totalSeconds) {
        int hrs = totalSeconds / 3600;
        int mins = (totalSeconds % 3600) / 60;
        int secs = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hrs, mins, secs);
    }

    public void setListener(TimerListener listener) {
        this.listener = listener;
    }
}
