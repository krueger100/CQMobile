package com.example.cq_mobile.HelperManagers;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.cq_mobile.Clock.ClockActivity;
import com.example.cq_mobile.LogoutFolder.LogoutManager;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
public class NavigationManager {
    private AppCompatActivity activity;
    private BottomNavigationView navView;
    private NavigationView navViewDrawer;
    private DrawerLayout drawerLayout;
    private BackPressManager backPressManager;

    public NavigationManager(AppCompatActivity activity, BottomNavigationView navView, NavigationView navViewDrawer, DrawerLayout drawerLayout) {
        this.activity = activity;
        this.navView = navView;
        this.navViewDrawer = navViewDrawer;
        this.drawerLayout = drawerLayout;
        this.backPressManager = new BackPressManager(activity);
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

        // Handle Drawer Navigation clicks
        navViewDrawer.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_item_one) {
                Toast.makeText(activity, "Account clicked", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_item_two) {
                Toast.makeText(activity, "About clicked", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_item_three) {
                // Handle logout using LogoutManager
                LogoutManager.logoutUser(activity);
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

