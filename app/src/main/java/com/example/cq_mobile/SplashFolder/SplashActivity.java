package com.example.cq_mobile.SplashFolder;

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
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.LoginFolder.Login;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;

public class SplashActivity extends AppCompatActivity {

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences ClockIN = getSharedPreferences("ClockPrefs", MODE_PRIVATE);
        boolean isClockedIn = ClockIN.getBoolean("ClockInSuccess", false);
        Log.d("SplashActivity", "isClockedIn: " + isClockedIn);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
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
}

/*


Login Checker
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        Log.d("SplashActivity", "isLoggedIn: " + isLoggedIn);


ClockIN Checker
      SharedPreferences ClockIN = getSharedPreferences("ClockPrefs", MODE_PRIVATE);
        boolean isClockedIn = ClockIN.getBoolean("ClockInSuccess", false);
        if (isClockedIn) {
            Log.d("SplashActivity", "Navigate to Login");
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent;
                if (isLoggedIn) {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, Login.class);
                }
                startActivity(intent);
                finish();
            }, 500);

        } else {
            Log.d("SplashActivity", "User is not clocked in.");
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent;
                if (isLoggedIn) {
                    intent = new Intent(SplashActivity.this, ClockActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, Login.class);
                }
                startActivity(intent);
                finish();
            }, 500);

        }



 */