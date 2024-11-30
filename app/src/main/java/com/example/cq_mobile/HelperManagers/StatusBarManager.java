package com.example.cq_mobile.HelperManagers;
import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.View;
import android.graphics.Color;

public class StatusBarManager {

    // Method to change the status bar color and text/icons color
    public static void setStatusBarLight(Activity activity) {
        Window window = activity.getWindow();

        // Set the status bar background color to white
        window.setStatusBarColor(Color.WHITE);

        // For devices running Android 11 (API 30) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = window.getInsetsController();
            if (insetsController != null) {
                // Set the status bar text/icons to black
                insetsController.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                );
            }
        }
        // For devices below Android 11 (API 30)
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Set the status bar text/icons to black
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    // Method to restore the default status bar appearance (optional)
    public static void setStatusBarDefault(Activity activity) {
        Window window = activity.getWindow();

        // Set the status bar to default color (usually dark)
        window.setStatusBarColor(Color.TRANSPARENT); // or choose the desired default color

        // Reset system UI visibility to default
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = window.getInsetsController();
            if (insetsController != null) {
                insetsController.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
            }
        } else {
            // Reset to default UI visibility
            window.getDecorView().setSystemUiVisibility(0);
        }
    }
}
