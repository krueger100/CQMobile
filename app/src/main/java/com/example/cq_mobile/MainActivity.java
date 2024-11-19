package com.example.cq_mobile;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cq_mobile.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;

import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationManager navigationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseApp.initializeApp(this);

        // Initialize DrawerLayout
        drawerLayout = binding.drawerLayout;
        // Create the NavigationManager instance
        navigationManager = new NavigationManager(this, binding.navView, binding.navViewDrawer, drawerLayout);

        // Set up the navigation using the NavigationManager
        navigationManager.setupNavigation();


    }

    @Override
    public boolean onSupportNavigateUp() {
        return navigationManager.onSupportNavigateUp();
    }
}
