package com.example.cq_mobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;

import android.webkit.CookieManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.cq_mobile.HelperManagers.FadeIn_N_Out_AnimManager;
import com.example.cq_mobile.HelperManagers.FullscreenManager;

public class SplashActivity extends AppCompatActivity {
    private WebView webView;
    private boolean isRedirected = false; // Flag to prevent multiple redirects
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FullscreenManager.enableFullScreen(getWindow());
        ImageView logo1 = findViewById(R.id.logo1);
        ImageView logo2 = findViewById(R.id.logo2);
        progressBar = findViewById(R.id.progressBar);
        FadeIn_N_Out_AnimManager.applyLoopingFadeInOutAnimation(logo1, logo2, 2000, 550);

        // Initialize WebView
        webView = new WebView(this);
        hideWebView();

        // Enable cookies
        CookieManager.getInstance().setAcceptCookie(true);

        // Load login page
        webView.loadUrl("https://aws.customquoter.co.uk/login");


        setupWebView();
    }

    private void hideWebView() {
        webView.setVisibility(View.GONE);
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(true);
        webView.setInitialScale(100);

        // Handle WebView navigation
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // Inject login credentials when on login page
                if (url.contains("/login") && !isRedirected) {
                    injectLoginScript();
                    isRedirected = true;
                }

                // Once logged in, proceed to the dashboard page
                if (url.contains("/dashboard")) {
                    navigateToMainActivity();
                }
            }
        });
    }

    private void injectLoginScript() {
        String js = "document.getElementById('login-email').value='info@cq-business-management-software.com';" +
                "document.getElementById('login-password').value='123456';" +
                "document.querySelector('button[type=button]').click();";
        webView.evaluateJavascript(js, null);
    }

    private void navigateToMainActivity() {
        // Store login state in SharedPreferences
        SharedPreferences preferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("is_logged_in", true); // Mark as logged in
        editor.apply();

        // Proceed to MainActivity after login is successful
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close SplashActivity to prevent returning to it
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