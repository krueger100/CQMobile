package com.example.cq_mobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;

import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.cq_mobile.Clock.ClockActivity;
import com.example.cq_mobile.HelperManagers.FadeIn_N_Out_AnimManager;
import com.example.cq_mobile.HelperManagers.FullscreenManager;
import com.example.cq_mobile.LoginFolder.Login;

public class SplashActivity extends AppCompatActivity {

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences ClockIN = getSharedPreferences("ClockPrefs", MODE_PRIVATE);
        boolean isClockedIn = ClockIN.getBoolean("ClockInSuccess", false);


        if (!isTaskRoot()) {
            finish();
            return;
        }
        // Find views
        ImageView logo1 = findViewById(R.id.logo1);
        ImageView logo2 = findViewById(R.id.logo2);
        progressBar = findViewById(R.id.progressBar);

        // Retrieve the SharedPreferences data
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false); // default is false if not set
        Log.d("SplashActivity", "isLoggedIn: " + isLoggedIn);

        // Apply animation
        FadeIn_N_Out_AnimManager.applyLoopingFadeInOutAnimation(logo1, logo2, 2000, 550);



        if (isClockedIn) {
            Log.d("ClockActivity", "User has successfully clocked in before.");

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent;
                if (isLoggedIn) {
                    // Navigate to ClockActivity if user is logged in
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    // Navigate to Login activity if user is not logged in or if isLoggedIn is false
                    intent = new Intent(SplashActivity.this, Login.class);
                }
                startActivity(intent);
                finish();
            }, 500);

        } else {
            Log.d("ClockActivity", "User is not clocked in.");
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent;
                if (isLoggedIn) {
                    // Navigate to ClockActivity if user is logged in
                    intent = new Intent(SplashActivity.this, ClockActivity.class);
                } else {
                    // Navigate to Login activity if user is not logged in or if isLoggedIn is false
                    intent = new Intent(SplashActivity.this, Login.class);
                }
                startActivity(intent);
                finish(); // Close the splash activity
            }, 500); // Delay for 500 milliseconds

        }



    }
}


