package com.example.cq_mobile.Clock.ClockFolder;


import android.app.Activity;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cq_mobile.Clock.ClockFolder.ClockOutFolder.ClockOutManager;
import com.example.cq_mobile.HelperManagers.Animation.TransitionAnimationManager;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.Looper;
import android.util.Log;

import androidx.annotation.DrawableRes;

public class TimerUIManager {
    private static final String TAG = "TimerUIManager";

    private final View rootView;
    private final TextView timerTextView, jobTitle;
    private final FloatingActionButton fab;
    private final ImageButton clockOutController;
    private final ProgressBar progressBarTimer;
    private final ProgressBar progressBar;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private boolean isHidden = false;
    private final TimerFunctionManager timerFunctionManager; // Use TimerFunctionManager
    private final SharedPrefManager sharedPrefManager;
    private final ClockOutManager clockOutManager;
    private final String startDate;
    private final int jobId, taskId, userId;
    TextView clockoutBtn;
    LinearLayout timerLayout;
    public TimerUIManager(View rootView, String startDate, int jobId, int taskId, int userId, ProgressBar progressBar,
                          ClockOutManager clockOutManager, TimerFunctionManager timerFunctionManager, TextView clockoutBtn,
                          LinearLayout timerLayout ) {
        Log.d(TAG, "Initializing TimerUIManager...");

        this.rootView = rootView;
        this.timerTextView = rootView.findViewById(R.id.timer_text);
        this.fab = rootView.findViewById(R.id.fab_timer);
        this.clockOutController = rootView.findViewById(R.id.clockOutController);
        this.progressBarTimer = rootView.findViewById(R.id.progress_bar_timer);
        this.progressBar = progressBar;
        this.startDate = startDate;
        this.jobId = jobId;
        this.taskId = taskId;
        this.userId = userId;
        this.jobTitle = rootView.findViewById(R.id.job_title);  //job_title_message
        this.sharedPrefManager = new SharedPrefManager(fab.getContext());
        this.clockOutManager = clockOutManager;
        this.timerFunctionManager = timerFunctionManager;
        this.clockoutBtn = clockoutBtn;
        this.timerLayout = timerLayout;

        restoreTimerState(startDate);




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
                                .setDuration(150)
                                .start();
                    })
                    .start();

        });


        fab.setOnClickListener(v -> {
            Context context = rootView.getContext();
            if (context instanceof MainActivity) {
                MainActivity activity = (MainActivity) context;
                // .getTimerManager().resetTimer(context);

                String accessToken = sharedPrefManager.getAccessToken();
                int userID = sharedPrefManager.getUserId();
                int savedJobId = sharedPrefManager.getJobId();
                int savedTaskId = sharedPrefManager.getTaskId();
                String startTime = sharedPrefManager.getKeyStartDate();
                String jobTitle = sharedPrefManager.getStartJob();
                if (accessToken == null || accessToken.isEmpty()) {
                    Toast.makeText(context, "Error: Access token missing", Toast.LENGTH_SHORT).show();
                    return;
                }


                Log.w("JobTitle", "jobTitle  ->> " + jobTitle);
                if (jobTitle != null && !jobTitle.trim().isEmpty()) {
                    clockOutManager.setupClockOutButtonFab(fab, accessToken, jobId);
                    Log.w("JobTitle", "MAIN_ACTIVITY  <<-- " + jobTitle);
                    Log.w(TAG, "setupClockOutButton  PRESSED<<-- ");
                } else {
                    clockOutManager.setupStopJobWithTimeSheetFab(fab, accessToken, jobId,taskId,sharedPrefManager,progressBar);
                    Log.w("JobTitle", "MAIN_ACTIVITY  <<-- " + jobTitle);
                    Log.w(TAG, "setupStopJobWithTimeSheet  PRESSED<<-- ");
                }

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
        Log.d(TAG, "startDate  " + startDate);
        timerFunctionManager.resumeTimerAfterReopen(rootView.getContext());
    }

    public void updateTimerUI(String time) {
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



}
