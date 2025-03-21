package com.example.cq_mobile.Clock.ClockFolder.ClockOutFolder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.cq_mobile.Clock.ClockActivity;
import com.example.cq_mobile.Clock.ClockFolder.ClockInAPIFolder.TimerManager;
import com.example.cq_mobile.Clock.StartAndStopJobsFolder.StopJobApiManager;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.LoginFolder.Login;
import com.example.cq_mobile.R;


public class ClockOutManager {
    private Context context;
    private ProgressBar progressBar;
    private int savedJobId;
    private int savedTaskId;
    int userID;
    String startDate;
    private static final String USER_PREFS = "UserPrefs";

    public ClockOutManager(Context context, ProgressBar progressBar, int savedJobId, int savedTaskId, int userID, String startDate) {
        this.context = context;
        this.progressBar = progressBar;
        this.savedJobId = savedJobId; // ✅ Assigned values
        this.savedTaskId = savedTaskId;
        this.userID = userID;
        this.startDate = startDate;
    }

    public void setupClockOutButton(TextView clockOutBtn, String accessToken, int jobId) {
        SpannableString spannable = new SpannableString("  Clock out");
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.outline_timer_24);
        Log.w("ClockOutManager", "<<<< AccessToken >>>> "+"\n" +" -->>  "+ accessToken + "\n" + "savedJobId - " + savedJobId + " jobId - " + savedTaskId );
        Log.w("ClockOutManager", " <<<< USERID >>>> " + userID );
        Log.w("ClockOutManager", " <<<<ClockOut and Logout >>>> " + userID );

        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, ContextCompat.getColor(context, R.color.white));

            int drawableSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
            drawable.setBounds(0, 0, drawableSize, drawableSize);

            ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
            spannable.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        }

        spannable.setSpan(new android.text.style.RelativeSizeSpan(1.2f), 2, spannable.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        clockOutBtn.setText(spannable);
        clockOutBtn.setGravity(Gravity.CENTER);
        clockOutBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

     //   clockOutBtn.setOnClickListener(v -> AutoClockOutandLogout(accessToken, jobId, savedTaskId,startDate));

        clockOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StopJobApiManager.stopJob(accessToken, userID, progressBar, new StopJobApiManager.ApiCallback() {
                    @Override
                    public void onSuccess(String message) {
                        new Handler(Looper.getMainLooper()).post(() -> {

                            AutoClockOutandLogout(accessToken, jobId, savedTaskId,startDate);
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


    ///ClockOut With Logout
    public void AutoClockOutandLogout(String accessToken, int jobId, int savedTaskId, String startDate) {
        initiateClockOutAndLogOut(accessToken, jobId, this.savedTaskId, this.startDate);
    }
    private void initiateClockOutAndLogOut(String accessToken, int jobId, int taskId, String startDate) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        int ticketMessageId = 0;
        ClockNLogOUTApiManager.clockOuTwithLogOut(jobId, taskId, ticketMessageId, progressBar, accessToken, userID, new ClockNLogOUTApiManager.ApiCallback() {
            @Override
            public void onSuccess() {
                Log.d("ClockOutManager", "Clock/Log Out Successful");
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    clearClockPrefsWithLogout(ClockOutManager.this.startDate);

                    // ✅ Navigate to Login
                    Intent intent = new Intent(context, Login.class);
                    intent.putExtra("key", "value");
                    context.startActivity(intent);

                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                Log.e("ClockOutManager", "Clock Out Failed: Clock/Log Out  " + error);
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    Toast.makeText(context, "Failed to clock out: Clock/Log Out  " + error, Toast.LENGTH_LONG).show();
                });
            }
        });

    }
    private void clearClockPrefsWithLogout(String startDate) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ClockPrefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        Log.w("ClockOutManager", "<<<< TimerManager >>>> "+"\n" +" -->>  "+ startDate );
        TimerManager timerManager = TimerManager.getInstance(context, startDate);
        timerManager.resetTimer(context);

        SharedPreferences USER_PREFS_Preferences = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = USER_PREFS_Preferences.edit();
        editor.clear();
        editor.apply();

        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
        sharedPrefManager.clearEmail();
        sharedPrefManager.clearPassword();
        sharedPrefManager.clearStartJob();
        sharedPrefManager.clearStartJobMessage();
    }

    ///ClockOut Without Logout
    public void AutoClockOutWithoutLogout(String accessToken, int jobId) {
        initiateClockOutWithoutLogout(accessToken, jobId, savedTaskId,startDate);
    }
    private void initiateClockOutWithoutLogout(String accessToken, int jobId, int taskId, String startDate) {

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        int ticketMessageId = 0;
        ClockOUTApiManager.clockOUT(jobId, taskId, ticketMessageId, progressBar, accessToken, userID, new ClockOUTApiManager.ApiCallback() {
            @Override
            public void onSuccess() {
                Log.d("ClockOutManager", "Clock Out Successful");
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);

                    // ✅ Reusable method for clearing clock-in data
                    clearClockPrefs(ClockOutManager.this.startDate);

                    // ✅ Navigate to ClockActivity
                    Intent intent = new Intent(context, ClockActivity.class);
                    intent.putExtra("key", "value");
                    context.startActivity(intent);

                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                Log.e("ClockOutManager", "Clock Out Failed: " + error);
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    Toast.makeText(context, "Failed to clock out: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });

    }
    private void clearClockPrefs(String startDate) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ClockPrefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        Log.w("ClockOutManager", "<<<< TimerManager >>>> "+"\n" +" -->>  "+ startDate );
        TimerManager timerManager = TimerManager.getInstance(context, startDate);
        timerManager.resetTimer(context);


    }
}
