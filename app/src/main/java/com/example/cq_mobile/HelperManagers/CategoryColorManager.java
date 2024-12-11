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
        // Trim the input color string to avoid leading/trailing spaces
        categoriesColors = categoriesColors.trim();

        if (categoriesColors.isEmpty()) {
            // If the category color is empty, return a default drawable
            return ContextCompat.getDrawable(context, R.drawable.button_red);
        }

        try {
            // Use the categoriesColors without transparency for stroke
            int color = Color.parseColor(categoriesColors);

            // Create a drawable with the color for the background with 20% transparency
            int transparentColor = (0x33 << 24) | (color & 0x00FFFFFF);

            // Create a drawable with a stroke and transparent background
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(transparentColor); // Background with transparency
            drawable.setCornerRadius(55f);

            drawable.setStroke(2, color);  // 3dp stroke width and the original color

            drawable.setPadding(12, 12, 12, 12);

            // Convert 35dp to pixels for the height
            int heightInPixels = (int) (55 * context.getResources().getDisplayMetrics().density);

            // Set the height of the drawable by modifying its bounds
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), heightInPixels);

            return drawable;
        } catch (IllegalArgumentException e) {
            // If the color string is invalid, log the error and return a default drawable
            Log.e("CategoryColorManager", "Invalid color format: " + categoriesColors, e);
            return ContextCompat.getDrawable(context, R.drawable.button_red);
        }
    }
}