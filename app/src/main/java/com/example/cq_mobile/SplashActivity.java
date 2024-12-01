package com.example.cq_mobile;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;

import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.cq_mobile.HelperManagers.FadeIn_N_Out_AnimManager;
import com.example.cq_mobile.HelperManagers.FullscreenManager;
import com.example.cq_mobile.LoginFolder.Login;

public class SplashActivity extends AppCompatActivity {

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Find views
        ImageView logo1 = findViewById(R.id.logo1);
        ImageView logo2 = findViewById(R.id.logo2);
        progressBar = findViewById(R.id.progressBar);

        // Apply animation
        FadeIn_N_Out_AnimManager.applyLoopingFadeInOutAnimation(logo1, logo2, 2000, 550);

        // Navigate to the Login activity after a delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, Login.class);
            startActivity(intent);
            finish(); // Close the splash activity
        }, 500);
    }
}




/*
  private void injectLoginScript() {
        // JavaScript to fill login form and submit
        String js = "document.getElementById('login-email').value='carloliverkrueger111@gmail.com';" +
                "document.getElementById('login-password').value='carloliverkrueger';" +
                "document.querySelector('button[type=button]').click();";
        webView.evaluateJavascript(js, null);
    }




                       // Redirect logic after login
                if ((url.contains("/dashboard") || url.contains("/jobschedule")) && !isRedirected) {
                    webView.loadUrl("https://cqbms.app/jobschedule/lists");
                    isRedirected = true; // Prevent further redirects
                }

 */