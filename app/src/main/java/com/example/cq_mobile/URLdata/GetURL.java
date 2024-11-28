package com.example.cq_mobile.URLdata;

import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebView;

public class GetURL {

    private WebView webView;

    // Constructor to initialize with a WebView instance
    public GetURL(WebView webView) {
        this.webView = webView;
        initializeWebView();
    }

    // Method to initialize WebView settings
    private void initializeWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
    }

    // Method to get the current URL of the WebView
    public String getCurrentUrl() {
        return webView.getUrl();
    }

    // Method to fetch the HTML content of the WebView
    public void fetchHtmlContent() {
        webView.evaluateJavascript(
                "(function() { return document.documentElement.outerHTML; })();",
                new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String html) {
                        // Log the HTML content of the page
                        Log.d("HTML", html);
                    }
                });
    }
}
