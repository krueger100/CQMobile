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
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.LoginFolder.Login;
import com.example.cq_mobile.R;


public class ClockOutManager {
    private Context context;
    private ProgressBar progressBar;
    private int savedJobId;
    private int savedTaskId;
    int userID;

    public ClockOutManager(Context context, ProgressBar progressBar, int savedJobId, int savedTaskId, int userID) {
        this.context = context;
        this.progressBar = progressBar;
        this.savedJobId = savedJobId; // ✅ Assigned values
        this.savedTaskId = savedTaskId;
        this.userID = userID;
    }

    public void setupClockOutButton(TextView clockOutBtn, String accessToken, int jobId) {
        SpannableString spannable = new SpannableString("  Clock out");
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.outline_timer_24);
        Log.w("ClockOutManager", "<<<< AccessToken >>>> "+"\n" +" -->>  "+ accessToken + "\n" + "savedJobId - " + savedJobId + " jobId - " + savedTaskId );
        Log.w("ClockOutManager", " <<<< USERID >>>> " + userID );


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

        clockOutBtn.setOnClickListener(v -> initiateClockOut(accessToken, jobId, savedTaskId));
    }

    public void AutoClockOut(String accessToken, int jobId) {
        initiateClockOutAndLogOut(accessToken, jobId, savedTaskId);
    }

    public void AutoClockOutWithoutLogout(String accessToken, int jobId) {
        initiateClockOut(accessToken, jobId, savedTaskId);
    }

    private void initiateClockOut(String accessToken, int jobId, int taskId) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        int ticketMessageId = 0;
        ClockOUTApiManager.clockOUT(jobId, taskId, ticketMessageId, progressBar, accessToken, userID, new ClockOUTApiManager.ApiCallback() {
            @Override
            public void onSuccess() {
                Log.d("ClockOutManager", "Clock Out Successful");
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);

                    // ✅ Reusable method for clearing clock-in data
                    clearClockPrefs();

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


    private void initiateClockOutAndLogOut(String accessToken, int jobId, int taskId) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        int ticketMessageId = 0;
        ClockNLogOUTApiManager.clockOuTwithLogOut(jobId, taskId, ticketMessageId, progressBar, accessToken, userID, new ClockNLogOUTApiManager.ApiCallback() {
            @Override
            public void onSuccess() {
                Log.d("ClockOutManager", "Clock Out Successful");
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    clearClockPrefs();

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
                Log.e("ClockOutManager", "Clock Out Failed: " + error);
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    Toast.makeText(context, "Failed to clock out: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });

    }


    // ✅ Extracted method to clear clock-in data
    private void clearClockPrefs() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ClockPrefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();


        /*
        SharedPreferences USER_PREFS_Preferences = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = USER_PREFS_Preferences.edit();
        editor.clear();
        editor.apply();

        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
        sharedPrefManager.clearEmail();
        sharedPrefManager.clearPassword();

         */
    }
}
