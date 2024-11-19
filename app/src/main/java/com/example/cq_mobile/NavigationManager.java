package com.example.cq_mobile;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.cq_mobile.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;


public class NavigationManager {
    private AppCompatActivity activity;
    private BottomNavigationView navView;
    private NavigationView navViewDrawer;
    private DrawerLayout drawerLayout;

    public NavigationManager(AppCompatActivity activity, BottomNavigationView navView, NavigationView navViewDrawer, DrawerLayout drawerLayout) {
        this.activity = activity;
        this.navView = navView;
        this.navViewDrawer = navViewDrawer;
        this.drawerLayout = drawerLayout;
    }

    public void setupNavigation() {
        // Set up the AppBarConfiguration for top-level destinations
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_map, R.id.navigation_my_jobs,
                R.id.navigation_ticket, R.id.navigation_chat) // New fragment IDs
                .setOpenableLayout(drawerLayout)
                .build();

        // Initialize NavController
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(activity, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        NavigationUI.setupWithNavController(navViewDrawer, navController);

        // Change the app bar title based on the selected fragment
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_home) {
                activity.setTitle("Home");
            } else if (destination.getId() == R.id.navigation_map) {
                activity.setTitle("Map");
            } else if (destination.getId() == R.id.navigation_my_jobs) {
                activity.setTitle("MyJobs");
            } else if (destination.getId() == R.id.navigation_ticket) {
                activity.setTitle("Tickets");
            } else if (destination.getId() == R.id.navigation_chat) {
                activity.setTitle("Chat");
            }
        });

        // Set a custom color for the app bar
        activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF000000")));
    }

    // Method for handling navigation up behavior
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main);
        return NavigationUI.navigateUp(navController, drawerLayout) || activity.onSupportNavigateUp();
    }
}
