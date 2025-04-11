package com.example.cq_mobile.Clock.ClockFolder.ClockInAPIFolder;
import android.os.Handler;
import android.os.Looper;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.cq_mobile.HelperManagers.DateAndTimeManager;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;

public class TimerManager {
    private static TimerManager instance;
    private static final String PREFS_NAME = "TimerPrefs";
    private static final String KEY_SAVED_TIME = "saved_time";
    private static final String KEY_LAST_TIMESTAMP = "last_timestamp";
    private static final String TAG = "TimerManager";

    private boolean running = false;
    long startTimeMillis = 0;
    private int seconds = 0;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private TimerListener listener;
    private SharedPrefManager sharedPrefManager;
    private boolean isTimerVisible = false;



    public interface TimerListener {
        void onTimerUpdate(String time);
        void onVisibilityChanged(boolean isVisible);
    }

    private TimerManager() {}



    public static synchronized TimerManager getInstance(Context context ) {
        if (instance == null) {
            instance = new TimerManager();
            instance.sharedPrefManager = new SharedPrefManager(context);
        }
        return instance;
    }

    public void startTimer(Context context) {
        if (!running) {
            running = true;
            startTimeMillis = System.currentTimeMillis() - (seconds * 1000L);
 //---> Device Time -->>
            handler.post(runnable);
            Log.d(TAG, "Timer started.");
        }
    }

    public void resetTimerBeforeStart() {
        // Reset the timer variables
        running = false;
        seconds = 0;
        startTimeMillis = 0;
        handler.removeCallbacks(runnable);

        Log.d(TAG, "Timer reset.");
    }


    public void stopTimer(Context context) {
        running = false;
        isTimerVisible = false;
        Log.d(TAG, "Timer stopped.");
        sharedPrefManager.clearStartJob();
        sharedPrefManager.clearStartJobMessage();

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
                handler.postDelayed(this, 1000);
            }
        }
    };


    public void saveTimeState(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_SAVED_TIME, seconds);
        editor.putLong(KEY_LAST_TIMESTAMP, System.currentTimeMillis());
        editor.apply();
        Log.d(TAG, "Timer state saved: " + formatTime(seconds));
    }

    public void restoreSavedTime(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int savedTime = prefs.getInt(KEY_SAVED_TIME, -1); // Default to -1 to check existence
        long lastTimestamp = prefs.getLong(KEY_LAST_TIMESTAMP, 0);
        long currentTime = System.currentTimeMillis();

        if (savedTime == -1 || lastTimestamp == 0) {
            this.seconds = 0;
            Log.w(TAG, "No saved time found. Starting from 00:00:00.");
        } else if (savedTime == 0) {
            this.seconds = 0;
            Log.w(TAG, "Saved time is 00:00:00. Starting fresh.");
        } else {
            long elapsedSeconds = (currentTime - lastTimestamp) / 1000;
            this.seconds = savedTime + (int) elapsedSeconds;
            Log.d(TAG, "Restored timer with elapsed time: " + formatTime(seconds));
        }

        if (listener != null) {
            listener.onTimerUpdate(formatTime(seconds));
        }
    }

    // 🚀
    public void resumeTimerAfterReopen(Context context) {
        restoreSavedTime(context);
        startTimer(context);
        Log.d(TAG, "Timer resumed from saved state: " + formatTime(seconds));
    }

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
