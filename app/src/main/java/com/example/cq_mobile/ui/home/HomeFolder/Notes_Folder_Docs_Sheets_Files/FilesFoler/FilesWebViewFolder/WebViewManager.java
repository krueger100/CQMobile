package com.example.cq_mobile.ui.home.HomeFolder.Notes_Folder_Docs_Sheets_Files.FilesFoler.FilesWebViewFolder;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.graphics.Bitmap;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;

public class WebViewManager {

    private Context context;
    private WebView webView;

    public WebViewManager(Context context) {
        this.context = context;
        this.webView = new WebView(context);
        this.webView.getSettings().setJavaScriptEnabled(true); // Enable JavaScript
        this.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d("WebViewManager", "Page loading started: " + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("WebViewManager", "Page loading finished: " + url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, android.webkit.WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e("WebViewManager", "Error loading page: " + error.getDescription());
            }
        });
        this.webView.setWebChromeClient(new WebChromeClient());
    }

    public void loadUrl(String url) {
        if (webView != null) {
            webView.loadUrl(url);
            Log.d("WebViewManager", "Loading URL: " + url);  // Log URL to verify
        } else {
            Log.e("WebViewManager", "WebView is null");
        }
    }

    public WebView getWebView() {
        return webView;
    }

    public void clearCache() {
        if (webView != null) {
            webView.clearCache(true);
        }
    }

    public void setUpWebViewDialog(ViewGroup parentLayout) {
        if (parentLayout != null) {
            parentLayout.removeAllViews();
            parentLayout.addView(webView);

            // Ensure WebView has a proper layout
            ViewGroup.LayoutParams params = webView.getLayoutParams();
            if (params == null) {
                params = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
                webView.setLayoutParams(params);
            }
        } else {
            Log.e("WebViewManager", "parentLayout is null, unable to remove views.");
        }
    }

    public void destroyWebView() {
        if (webView != null) {
            webView.destroy();
        }
    }
}
