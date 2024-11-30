package com.example.cq_mobile.Clock.ClockFolder;

import android.os.Handler;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DigitalClockManager {

    private TextView digitalClock;
    private Handler handler;
    private Runnable updateClockRunnable;

    public DigitalClockManager(TextView digitalClock) {
        this.digitalClock = digitalClock;
        this.handler = new Handler();
        this.updateClockRunnable = new Runnable() {
            @Override
            public void run() {
                updateDigitalClock();
                handler.postDelayed(this, 1000); // Update every second
            }
        };
    }

    // Method to start updating the digital clock
    public void startClock() {
        handler.post(updateClockRunnable);
    }

    // Method to stop updating the digital clock
    public void stopClock() {
        handler.removeCallbacks(updateClockRunnable);
    }

    // Method to update the digital clock with the formatted time
    private void updateDigitalClock() {
        // Get the current time in "hh:mm a" format (12-hour with AM/PM)
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        digitalClock.setText(currentTime); // Set the current time in the TextView
    }
}
