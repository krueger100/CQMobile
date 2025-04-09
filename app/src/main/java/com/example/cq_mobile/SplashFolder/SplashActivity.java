package com.example.cq_mobile.SplashFolder;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;

import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.cq_mobile.HelperManagers.FadeIn_N_Out_AnimManager;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.ServerDataReconnect;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.LoginFolder.Login;
import com.example.cq_mobile.R;

public class SplashActivity extends AppCompatActivity {

    ProgressBar progressBar;
    ServerDataReconnect serverDataReconnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        serverDataReconnect = new ServerDataReconnect(getApplicationContext());

        SharedPreferences ClockIN = getSharedPreferences("ClockPrefs", MODE_PRIVATE);
        boolean isClockedIn = ClockIN.getBoolean("ClockInSuccess", false);
        Log.d("SplashActivity", "isClockedIn: " + isClockedIn);

       SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
        boolean isLoggedIn = sharedPrefManager.getIsLoggedIn();
        Log.d("SplashActivity", "isLoggedIn: " + isLoggedIn);

        if (!isTaskRoot()) {
            finish();
            return;
        }


        ImageView logo1 = findViewById(R.id.logo1);
        ImageView logo2 = findViewById(R.id.logo2);
        progressBar = findViewById(R.id.progressBar);

        FadeIn_N_Out_AnimManager.applyLoopingFadeInOutAnimation(logo1, logo2, 2000, 550);

        SplashManager splashManager = new SplashManager(this, progressBar);
        splashManager.checkCreadentials(isClockedIn, isLoggedIn);


    }

    @Override
    protected void onResume() {
        super.onResume();
        serverDataReconnect.reconnectAsync(success -> runOnUiThread(() -> {
            if (success) {
                Log.d("SplashActivity", "Reconnected successfully.");
                Log.d("SplashActivity", "Reconnected successfully");

            } else {
                Log.e("SplashActivity", "Reconnection failed. Redirecting to login.");
                new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Intent intent = new Intent(SplashActivity.this, Login.class);
                    startActivity(intent);
                    finish();
                }, 500);
            }
        }));

    }
}
