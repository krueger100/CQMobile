package com.example.cq_mobile.Clock.ClockFolder;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cq_mobile.Clock.ClockFolder.ClockInAPIFolder.TimerManager;
import com.example.cq_mobile.Clock.ClockFolder.ClockOutFolder.ClockOutManager;

public class TimerFunctionManager implements TimerManager.TimerListener {
    private static final String TAG = "TimerFunctionManager";
    private final TimerUIManager timerUIManager;
    private final TimerManager timerManager;
    private final View rootView;
    TextView clockoutBtn;
    public TimerFunctionManager(View rootView, String startDate, int jobId, int taskId, int userId, ProgressBar progressBar, ClockOutManager clockOutManager, TextView clockoutBtn,
                                LinearLayout timerLayout ) {
        this.rootView = rootView;
        this.clockoutBtn = clockoutBtn;
        this.timerManager = TimerManager.getInstance(rootView.getContext());
        this.timerUIManager = new TimerUIManager(rootView, startDate, jobId, taskId,
                userId, progressBar, clockOutManager, this, clockoutBtn , timerLayout);
        this.timerManager.setListener(this);
    }

    public void startTimer() {
        timerManager.startTimer(clockoutBtn.getContext());
    }

    public void stopTimer(Context context) {
        timerManager.stopTimer(context);
    }

    public void resetTimer(Context context) {
        timerManager.resetTimer(context);
    }

    public void resumeTimerAfterReopen(Context context) {
        timerManager.resumeTimerAfterReopen(context);
    }

    @Override
    public void onTimerUpdate(String time) {
        timerUIManager.updateTimerUI(time);
    }

    @Override
    public void onVisibilityChanged(boolean isVisible) {

    }

    public void cleanup() {
        Log.d(TAG, "Cleaning up TimerUIManager...");
        timerManager.saveTimeState(rootView.getContext());
        timerManager.setListener(null);
    }

       public TimerManager getTimerManager() {
        return timerManager;
    }
}


/*
If you have a fragment that needs to interact with the timer, you can do something like this:

MainActivity activity = (MainActivity) getActivity();
if (activity != null) {
    TimerFunctionManager timerManager = activity.getTimerFunctionManager();
    if (timerManager != null) {
        timerManager.startTimer(); // Start the timer from the fragment
    }
}


Button startButton = findViewById(R.id.startTimerButton);

startButton.setOnClickListener(v -> {
    if (timerFunctionManager != null) {
        timerFunctionManager.startTimer();
        Log.d("MainActivity", "Timer started from MainActivity");
    }
});

 */