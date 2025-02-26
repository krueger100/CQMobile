package com.example.cq_mobile.Clock.ClockFolder.ClockInAPIFolder;

import android.os.Handler;
import android.os.Looper;

public class TimerManager {
    private static TimerManager instance;

    private int seconds = 0;
    private boolean running = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private TimerListener listener;

    public interface TimerListener {
        void onTimerUpdate(String time);
    }

    private TimerManager() {
        // Private constructor for singleton
    }

    // Singleton instance getter
    public static synchronized TimerManager getInstance() {
        if (instance == null) {
            instance = new TimerManager();
        }
        return instance;
    }

    // Start the timer
    public void startTimer() {
        if (!running) {
            running = true;
            handler.post(runnable);
        }
    }

    // Stop the timer
    public void stopTimer() {
        running = false;
    }

    // Set listener
    public void setListener(TimerListener listener) {
        this.listener = listener;
    }

    // Runnable to update the timer every second
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (running) {
                seconds++;
                int hrs = seconds / 3600;
                int mins = (seconds % 3600) / 60;
                int secs = seconds % 60;
                String time = String.format("%02d:%02d:%02d", hrs, mins, secs);

                // Notify listener
                if (listener != null) {
                    listener.onTimerUpdate(time);
                }

                // Post next update
                handler.postDelayed(this, 1000);
            }
        }
    };
}
