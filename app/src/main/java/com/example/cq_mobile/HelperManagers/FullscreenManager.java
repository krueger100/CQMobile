package com.example.cq_mobile.HelperManagers;

import android.os.Build;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;


public class FullscreenManager {

    public static void enableFullScreen(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Ensure the window object is valid
            if (window != null) {
                window.setDecorFitsSystemWindows(false);
                WindowInsetsController controller = window.getInsetsController();
                if (controller != null) {
                    controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                    controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_DEFAULT);
                }
            }
        }
    }
}
/*
   // Use FullscreenManager to enable full-screen mode
        FullscreenManager.enableFullScreen(getWindow());

        setContentView(R.layout.activity_main);
 */