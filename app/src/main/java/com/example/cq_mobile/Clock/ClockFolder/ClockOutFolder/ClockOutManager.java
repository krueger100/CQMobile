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
import android.widget.ProgressBar;
import android.widget.TextView;
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
                    TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics()); // Convert dp to px
            drawable.setBounds(0, 0, drawableSize, drawableSize);

            ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
            spannable.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        spannable.setSpan(new android.text.style.RelativeSizeSpan(1.2f), 2, spannable.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE); // Increase text size

        clockOutBtn.setText(spannable);
        clockOutBtn.setGravity(Gravity.CENTER);
        clockOutBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);


        // Clockout button listener
        clockOutBtn.setOnClickListener(v -> {
            progressBar.setVisibility(v.VISIBLE);

            if (context != null) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("ClockPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                Log.d("ClockActivity", "Clock Out Successful");

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    progressBar.setVisibility(v.GONE);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        Intent intent = new Intent(context, ClockActivity.class);
                        intent.putExtra("key", "value");
                        context.startActivity(intent);

                        if (context instanceof Activity) {
                            ((Activity) context).finish();
                        }
                    }, 1000);


                }, 2000);

            }
        });

    }
}
