package com.example.cq_mobile.ui.myJob.myJobFolder;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.cq_mobile.R;
import com.example.cq_mobile.WebManagerFolder.HideNavigationBarManager;
import com.example.cq_mobile.WebManagerFolder.WebViewCacheManager;

public class ToDoFragment extends Fragment {

    private WebView webView;
    private ProgressBar progressBar;  // Optional: If you want to show a loading spinner

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);

        // Initialize WebView
        webView = view.findViewById(R.id.webView);
        webView.setVisibility(View.INVISIBLE);
        progressBar = view.findViewById(R.id.progressBar);  // Add a ProgressBar in your layout

        // Ensure cookies are enabled
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true); // Allow third-party cookies for session persistence

        // Check if the user is logged in by reading the session from SharedPreferences
        SharedPreferences preferences = getActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("is_logged_in", false);

        // Enable caching for the WebView
        WebViewCacheManager.enableCaching(webView, getContext());

        if (isLoggedIn) {
            // If logged in, load the job listings page
            webView.loadUrl("https://aws.customquoter.co.uk/job-listings");
        } else {
            // If not logged in, load the login page or handle appropriately
            webView.loadUrl("https://cqbms.app/login");
        }

        setupWebView();

        return view;
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(true);
        webView.setInitialScale(250);

        // Handle WebView navigation and show a progress bar while the page is loading
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // Optionally, show a ProgressBar when the page starts loading
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);  // Show the progress bar
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Hide the ProgressBar once the page has finished loading
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);  // Hide the progress bar
                    webView.setVisibility(View.VISIBLE);

                }

                // Hide any unwanted navigation bars or elements, if necessary
                HideNavigationBarManager.hideNavigationBar(webView);

                // Log success or perform other actions when page is loaded successfully
                if (url.equals("https://aws.customquoter.co.uk/job-listings")) {
                    // Page loaded successfully (for example, a job listings page)
                    Log.d("ToDoFragment", "Job listings page loaded successfully");
                } else {
                    // Handle cases where the URL doesn't match or if an error occurs
                    Log.d("ToDoFragment", "Page loaded: " + url);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                // Handle error page loading if necessary
                Log.e("ToDoFragment", "Error loading page: " + error.getDescription());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clear the reference to WebView to prevent memory leaks
   //    webView = null;
    }
}
