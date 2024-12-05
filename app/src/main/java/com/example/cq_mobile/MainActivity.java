package com.example.cq_mobile;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cq_mobile.HelperManagers.NavigationManager;
import com.example.cq_mobile.HelperManagers.StatusBarManager;
import com.example.cq_mobile.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;

import androidx.drawerlayout.widget.DrawerLayout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationManager navigationManager;
    private static final String TAG = "MainActivity"; // Tag for logging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseApp.initializeApp(this);
        StatusBarManager.setStatusBarLight(this);



        // Initialize DrawerLayout and NavigationManager
        drawerLayout = binding.drawerLayout;
        navigationManager = new NavigationManager(this, binding.navView, binding.navViewDrawer, drawerLayout);

        // Set up navigation
        navigationManager.setupNavigation();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navigationManager.onSupportNavigateUp();
    }

   }
