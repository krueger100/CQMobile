package com.example.cq_mobile;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cq_mobile.HelperManagers.NavigationManager;
import com.example.cq_mobile.HelperManagers.StatusBarManager;
import com.example.cq_mobile.databinding.ActivityMainBinding;
import com.example.cq_mobile.ui.map.MapFragment;
import com.google.firebase.FirebaseApp;

import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationManager navigationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate layout using ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase and set status bar appearance
        FirebaseApp.initializeApp(this);
        StatusBarManager.setStatusBarLight(this);

        // Initialize NavigationManager with required parameters
        drawerLayout = binding.drawerLayout;
        navigationManager = new NavigationManager(
                this,
                binding.navView,
                binding.navViewDrawer,
                drawerLayout
        );

        // Register a back press callback
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (navigationManager != null) {
                    navigationManager.handleBackPress();
                } else {
                    finish();
                }
            }
        });

        navigationManager.setupNavigation();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navigationManager.onSupportNavigateUp();
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatusBarManager.setStatusBarDefault(this);
    }
}

