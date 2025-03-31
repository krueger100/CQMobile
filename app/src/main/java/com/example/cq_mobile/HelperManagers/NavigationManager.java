package com.example.cq_mobile.HelperManagers;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.cq_mobile.Clock.ClockFolder.ClockOutFolder.ClockOutManager;
import com.example.cq_mobile.HelperManagers.Animation.TransitionAnimationManager;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.LogoutFolder.LogoutManager;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.MoreInFragment.AboutActivity;
import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.NewBuild;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class NavigationManager {
    private AppCompatActivity activity;
    private BottomNavigationView navView;
    private NavigationView navViewDrawer;
    private DrawerLayout drawerLayout;
    private BackPressManager backPressManager;
    String accessToken;
    int jobId;
    int taskId;
    int userId;
    String startDate;
    ProgressBar progressBar;
    TextView clockoutBtn;
    public NavigationManager(AppCompatActivity activity, TextView clockoutBtn, BottomNavigationView navView, NavigationView navViewDrawer, DrawerLayout drawerLayout, ProgressBar progressBar,
                             String accessToken, int jobId, int taskId, String startDate, int userId) {
        this.activity = activity;
        this.navView = navView;
        this.navViewDrawer = navViewDrawer;
        this.drawerLayout = drawerLayout;
        this.backPressManager = new BackPressManager(activity);
        this.progressBar = progressBar;
        this.accessToken = accessToken;
        this.jobId = jobId;
        this.taskId = taskId;
        this.startDate = startDate;
        this.userId = userId;
        this.clockoutBtn = clockoutBtn;
    }

    public void setupNavigation() {
        // Set up the AppBarConfiguration for top-level destinations
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_map, R.id.navigation_more,
                R.id.navigation_ticket, R.id.navigation_chat) // Bottom navigation IDs
                .setOpenableLayout(drawerLayout) // Enable drawer swipe gesture
                .build();

        // Initialize NavController
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main);

        // Setup Navigation with Bottom Navigation view
        NavigationUI.setupWithNavController(navView, navController);

        // Setup Navigation with Drawer View
        NavigationUI.setupWithNavController(navViewDrawer, navController);

        // Handle Bottom Navigation clicks
        navView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            // Clear only the Home Fragment if navigating away from it
            if (navController.getCurrentDestination() != null &&
                    navController.getCurrentDestination().getId() == R.id.navigation_home &&
                    id != R.id.navigation_home) {
                navController.popBackStack(R.id.navigation_home, true);
            }

            // Navigate to the selected fragment
            if (id == R.id.navigation_home) {
                navController.navigate(R.id.navigation_home);
                return true;
            } else if (id == R.id.navigation_map) {
                navController.navigate(R.id.navigation_map);
                return true;
            } else if (id == R.id.navigation_more) {
                navController.navigate(R.id.navigation_more);
                return true;
            } else if (id == R.id.navigation_ticket) {
                navController.navigate(R.id.navigation_ticket);
                return true;
            } else if (id == R.id.navigation_chat) {
                navController.navigate(R.id.navigation_chat);
                return true;
            }
            return false;
        });

        navViewDrawer.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_item_one) {
                navController.navigate(R.id.navigation_more);
            } else if (id == R.id.nav_item_two) {
                Intent intent2 = new Intent(activity, AboutActivity.class);
                intent2.putExtra("job_id", jobId);
                activity.startActivity(intent2);
           //     activity.finish();

            } else if (id == R.id.nav_item_three) {
                // Handle logout using LogoutManager
                SharedPrefManager sharedPrefManager = new SharedPrefManager(activity);
                ClockOutManager clockOutManager = new ClockOutManager(activity, progressBar, jobId, taskId, userId, startDate, sharedPrefManager);
                clockOutManager.AutoClockOutWithoutLogout(accessToken, jobId);

            } else {
                return false;
            }

            // Close the drawer after item selection
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Set a custom color for the app bar if needed (optional)
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1C2244")));
        }

        // Enable swipe to open drawer (this is the default behavior of DrawerLayout)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    // Method for handling back press to navigate to ClockActivity
    public void handleBackPress() {
        backPressManager.handleBackPress(MainActivity.class);
    }

    // Method for handling navigation up behavior
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main);
        return NavigationUI.navigateUp(navController, drawerLayout) || activity.onSupportNavigateUp();
    }


}

