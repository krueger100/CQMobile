package com.example.cq_mobile.HelperManagers;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.cq_mobile.R;

    public class CategoryColorManager {

        /**
         * ||||   ////     ||||\\\     //\\\
         * ||||  ////      |||| \\\   ////\\\\   Krueger,Carl Oliver
         * |||| ////       |||| ////  ||||  \\\\  carloliverkrueger111@gmail.com
         * |||| \\\\       ||||////   ||||
         * ||||   \\\\     |||| \\\\   \\\\   ==////
         * ||||     \\\\   ||||    \\\\  \\\\  ////
         * ||||       \\\\ ||||     \\\\   \\\////
         */


        public static Drawable getCategoryBackground(Context context, String categoriesColors) {
            // Check for null or empty string
            if (categoriesColors == null || categoriesColors.trim().isEmpty()) {
                Log.w("CategoryColorManager", "Received null or empty color. Using default color.");
                return ContextCompat.getDrawable(context, R.drawable.button_red);
            }

            categoriesColors = categoriesColors.trim();

            try {
                int color = Color.parseColor(categoriesColors);

                int transparentColor = (0x33 << 24) | (color & 0x00FFFFFF);

                GradientDrawable drawable = new GradientDrawable();
                drawable.setColor(transparentColor); // Background with transparency
                drawable.setCornerRadius(55f);
                drawable.setStroke(2, color); // Stroke with original color
                drawable.setPadding(12, 12, 12, 12);

                // Convert 35dp to pixels for the height
                int heightInPixels = (int) (55 * context.getResources().getDisplayMetrics().density);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), heightInPixels);

                return drawable;
            } catch (IllegalArgumentException e) {
                Log.w("CategoryColorManager", "Invalid color format: " + categoriesColors, e);
                return ContextCompat.getDrawable(context, R.drawable.button_red);
            }
        }
    }
