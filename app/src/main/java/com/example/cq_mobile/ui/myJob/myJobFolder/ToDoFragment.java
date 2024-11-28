package com.example.cq_mobile.ui.myJob.myJobFolder;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
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
import com.example.cq_mobile.URLdata.GetURL;
import com.example.cq_mobile.WebManagerFolder.HideNavigationBarManager;
import com.example.cq_mobile.WebManagerFolder.WebViewCacheManager;

public class ToDoFragment extends Fragment {

    private WebView webView;
    private ProgressBar progressBar; // Optional ProgressBar for loading indication

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);

        // Initialize WebView and ProgressBar
        webView = view.findViewById(R.id.webView);
        webView.setVisibility(View.INVISIBLE);
        progressBar = view.findViewById(R.id.progressBar);

        // Set up WebView and enable cookies
        setupCookieManager();
        setupWebView();

        // Check login status
        SharedPreferences preferences = requireActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("is_logged_in", false);

        // Handle caching and load appropriate URL
        WebViewCacheManager.enableCaching(webView, getContext());
        if (isLoggedIn) {
            loadJobListingsPage();
        } else {
            loadLoginPage();
        }

        return view;
    }

    private void setupCookieManager() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true); // Allow third-party cookies
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(true);
        webView.setInitialScale(250);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        webView.setVisibility(View.VISIBLE);
                    }, 1000);
                }

                HideNavigationBarManager.hideNavigationBar(webView);

                // Log the HTML content
                logHtmlContent();

                // Log or handle specific URL cases
                Log.d("JavaScriptInterface ", "Page loaded: " + url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e("ToDoFragment", "Error loading page: " + error.getDescription());
            }
        });
    }



    private void logHtmlContent() {
        webView.evaluateJavascript(
                "(function() { return document.documentElement.outerHTML; })();",
                new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String html) {
                        // Log the HTML content of the current page
                        Log.d("JavaScriptInterface ","WebViewHTML  " + html);
                    }
                });
    }


    private void loadJobListingsPage() {
        webView.loadUrl("https://aws.customquoter.co.uk/job-listings");

        // Utilize GetURL manager class for advanced operations
        GetURL getURL = new GetURL(webView);

        // Log the current URL and HTML content
        String currentUrl = getURL.getCurrentUrl();
        Log.d("CurrentURL", currentUrl);

        getURL.fetchHtmlContent();
    }

    private void loadLoginPage() {
        webView.loadUrl("https://cqbms.app/login");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    //    webView = null; // Clear WebView reference to prevent memory leaks

    }
}


/*
      private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(true);
        webView.setInitialScale(250);

        // Add the JavaScript interface
        webView.addJavascriptInterface(new JavaScriptInterface(requireContext()), "Android");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (progressBar != null) {
                webView.evaluateJavascript(
                            "javascript:(function() {" +
                                    "   function sendMessageToAndroid() {" +
                                    "       Android.showToast('Hello from JavaScript!');" +
                                    "   }" +
                                    "   function logMessageToAndroid() {" +
                                    "       Android.logMessage('This is a message from JavaScript.');" +
                                    "   }" +
                                    "   document.addEventListener('DOMContentLoaded', function() {" +
                                    "       sendMessageToAndroid();" +
                                    "       logMessageToAndroid();" +
                                    "   });" +
                                    "})()",
                            null
                    );


                        progressBar.setVisibility(View.VISIBLE);



                    }


                    // Log or handle specific URL cases
                    Log.d("ToDoFragment", "Page loaded: " + url);

       }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (progressBar != null) {
                    HideNavigationBarManager.hideNavigationBar(webView);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        webView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }, 1000);
                }

                HideNavigationBarManager.hideNavigationBar(webView);

                // Log the HTML content
                logHtmlContent();

                // Log or handle specific URL cases
                Log.d("ToDoFragment", "Page loaded: " + url);

                // Example: Call a JavaScript function after the page is loaded
                webView.evaluateJavascript("javascript:window.Android.logMessage('Page Loaded')", null);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e("ToDoFragment", "Error loading page: " + error.getDescription());
            }
        });
    }

 */