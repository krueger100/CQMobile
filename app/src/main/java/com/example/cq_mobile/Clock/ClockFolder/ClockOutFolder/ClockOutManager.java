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
import com.example.cq_mobile.R;

public class ClockOutManager {

    private Context context;
    ProgressBar progressBar;
    public ClockOutManager(Context context, ProgressBar progressBar) {
        this.context = context;
        this.progressBar = progressBar;
    }

    public void setupClockOutButton(TextView clockOutBtn) {
        SpannableString spannable = new SpannableString("  Clock out");
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.outline_timer_24);

        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable); // Ensure tinting works
            DrawableCompat.setTint(drawable, ContextCompat.getColor(context, R.color.white));

            int drawableSize = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
            drawable.setBounds(0, 0, drawableSize, drawableSize);

            ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
            spannable.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        spannable.setSpan(new android.text.style.RelativeSizeSpan(1.2f), 2, spannable.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE); // Increase text size

        clockOutBtn.setText(spannable);
        clockOutBtn.setGravity(Gravity.CENTER);
        clockOutBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);


        clockOutBtn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);

            int jobScheduleId = -1;
            int taskId = 823;
            int ticketMessageId = 208;
            String accessToken = "11440|SCCvtwapd6CIsge4KwhlMhmmLnFCTnLN6JEoRagl";
            int userId = 3;

            ClockOUTApiManager.clockOUT(jobScheduleId, taskId, ticketMessageId, progressBar, accessToken, userId, new ClockOUTApiManager.ApiCallback() {
                @Override
                public void onSuccess() {
                    Log.d("ClockOutManager", "Clock Out Successful");

                    new Handler(Looper.getMainLooper()).post(() -> {
                        progressBar.setVisibility(View.GONE);

                        // Clear SharedPreferences after successful clock out
                        SharedPreferences sharedPreferences = context.getSharedPreferences("ClockPrefs", Context.MODE_PRIVATE);
                        sharedPreferences.edit().clear().apply();

                        // Navigate to ClockActivity
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
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(context, "Failed to clock out: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            });


        });


    }
}
