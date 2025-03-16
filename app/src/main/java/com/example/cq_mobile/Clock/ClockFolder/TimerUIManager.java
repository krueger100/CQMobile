package com.example.cq_mobile.Clock.ClockFolder;


import android.app.Activity;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cq_mobile.Clock.ClockFolder.ClockInAPIFolder.TimerManager;
import com.example.cq_mobile.Clock.ClockFolder.ClockOutFolder.ClockOutManager;
import com.example.cq_mobile.Clock.StartAndStopJobsFolder.StopJobApiManager;
import com.example.cq_mobile.HelperManagers.Animation.TransitionAnimationManager;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.LogoutFolder.LogoutManager;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.Looper;
import android.util.Log;

import androidx.annotation.DrawableRes;

public class TimerUIManager implements TimerManager.TimerListener {
    private static final String TAG = "TimerUIManager";

    private final View rootView;
    private final TextView timerTextView,jobTitle;
    private final FloatingActionButton fab;
    private final ImageButton clockOutController;
    private final ProgressBar progressBarTimer;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private boolean isHidden = false;
    private final TimerManager timerManager;
    String startDate;
    public TimerUIManager(View rootView, String startDate) {
        Log.d(TAG, "Initializing TimerUIManager...");

        this.rootView = rootView;
        this.timerTextView = rootView.findViewById(R.id.timer_text);
        this.fab = rootView.findViewById(R.id.fab_timer);
        this.clockOutController = rootView.findViewById(R.id.clockOutController);
        this.progressBarTimer = rootView.findViewById(R.id.progress_bar_timer);
        this.startDate = startDate;
        this.jobTitle = rootView.findViewById(R.id.job_title);
        timerManager = TimerManager.getInstance(rootView.getContext(), startDate);
        timerManager.setListener(this);

        restoreTimerState(startDate);
        setupListeners();
    }

    private void setupListeners() {
        Log.d(TAG, "Setting up listeners...");
        clockOutController.setOnClickListener(v -> {
            Log.d(TAG, "Clock out button clicked.");
            toggleVisibilityWithAnimation();

            @DrawableRes int drawableRes = isHidden
                    ? R.drawable.baseline_arrow_back_ios_24
                    : R.drawable.baseline_arrow_forward_ios_24;

            clockOutController.animate().alpha(0f).setDuration(150)
                    .withEndAction(() -> {
                        clockOutController.setImageResource(drawableRes);
                        clockOutController.animate()
                                .alpha(1f)
                                .setDuration(150) // Fade in
                                .start();
                    })
                    .start();

        });


        fab.setOnClickListener(v -> {
            Context context = rootView.getContext();
            if (context instanceof MainActivity) {
                MainActivity activity = (MainActivity) context;
                activity.getTimerManager().resetTimer(context);

                SharedPrefManager sharedPrefManager = new SharedPrefManager(fab.getContext());
                String accessToken = sharedPrefManager.getAccessToken();
                int userID = sharedPrefManager.getUserId();
                int savedJobId = sharedPrefManager.getJobId();
                int savedTaskId = sharedPrefManager.getTaskId();
                String startTime = sharedPrefManager.getKeyStartDate();

                if (accessToken == null || accessToken.isEmpty()) {
                    Toast.makeText(context, "Error: Access token missing", Toast.LENGTH_SHORT).show();
                    return;
                }
                StopJobApiManager.stopJob(accessToken, userID, progressBarTimer, new StopJobApiManager.ApiCallback() {
                    @Override
                    public void onSuccess(String message) {
                        new Handler(Looper.getMainLooper()).post(() -> {

                            ClockOutManager clockOutManager = new ClockOutManager(context, progressBarTimer, savedJobId, savedTaskId, userID, startDate);
                            clockOutManager.AutoClockOutandLogout(accessToken, savedJobId, savedTaskId, startTime);
                            Toast.makeText(context, "Timer Stopped", Toast.LENGTH_SHORT).show();

                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e("StartJob", "Failed to stop job: " + error);
                        new Handler(Looper.getMainLooper()).post(() ->
                                Toast.makeText(context, "Failed to stop job: " + error, Toast.LENGTH_SHORT).show()
                        );
                    }
                });

            }
        });
    }

    private void toggleVisibilityWithAnimation() {
        Log.d(TAG, "Toggling UI visibility. isHidden: " + isHidden);

        if (!isHidden) {
            TransitionAnimationManager.slideOutToRight(timerTextView, 120);
            TransitionAnimationManager.slideOutToRight(jobTitle, 130);
            TransitionAnimationManager.slideOutToRight(fab, 150);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                timerTextView.setVisibility(View.GONE);
                jobTitle.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
                Log.d(TAG, "UI components hidden.");
            }, 150);
        } else {
            TransitionAnimationManager.slideInFromRight(timerTextView, 150);
            TransitionAnimationManager.slideInFromRight(jobTitle, 130);
            TransitionAnimationManager.slideInFromRight(fab, 120);
            timerTextView.setVisibility(View.VISIBLE);
            jobTitle.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
            Log.d(TAG, "UI components visible.");
        }

        isHidden = !isHidden;
    }

    private void restoreTimerState(String startDate) {
        Log.d(TAG, "Restoring timer state...");
        Log.d(TAG, "startDate  "  + startDate);
        timerManager.restoreSavedTime(rootView.getContext());


    }

    @Override
    public void onTimerUpdate(String time) {
        Log.d(TAG, "Timer updated: " + time);
        if (rootView.getContext() instanceof Activity) {
            ((Activity) rootView.getContext()).runOnUiThread(() -> {
                timerTextView.setText(time);
                uiHandler.postDelayed(() -> {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> progressBarTimer.setVisibility(View.GONE), 1000);
                    Log.d(TAG, "Progress bar hidden after update.");
                }, 2000);
            });
        }
    }




    public void cleanup() {
        Log.d(TAG, "Cleaning up TimerUIManager...");
        timerManager.saveTimeState(rootView.getContext());
        timerManager.setListener(null);
    }
}

