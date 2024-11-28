package com.example.cq_mobile.WebManagerFolder;

import android.content.Context;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.File;

import android.content.Context;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebViewCacheManager {

    // Method to enable caching in WebView
    public static void enableCaching(WebView webView, Context context) {
        WebSettings webSettings = webView.getSettings();

        // Enable JavaScript
        webSettings.setJavaScriptEnabled(true);

        // Enable DOM Storage (localStorage for HTML5 web applications)
        webSettings.setDomStorageEnabled(true);

        // Enable database storage for offline use
        webSettings.setDatabaseEnabled(true);

        // Set cache mode (use cache if available)
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);  // It will use cached resources if available
        webView.getSettings().setDatabaseEnabled(true);
    }

    // Method to clear cache (if needed)
    public static void clearCache(WebView webView) {
        // Clear WebView cache
        webView.clearCache(true);
        webView.clearHistory();
    }

    // Optional: Clear the entire app cache if required
    public static void clearAppCache(Context context) {
        File cacheDir = context.getCacheDir();
        deleteDirectory(cacheDir);
    }

    // Helper method to delete all files in a directory (to clear the cache)
    private static void deleteDirectory(File dir) {
        if (dir != null && dir.isDirectory()) {
            for (File child : dir.listFiles()) {
                if (child.isDirectory()) {
                    deleteDirectory(child);
                } else {
                    child.delete();
                }
            }
        }
    }
}
