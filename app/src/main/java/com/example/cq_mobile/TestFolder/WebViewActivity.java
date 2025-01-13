package com.example.cq_mobile.TestFolder;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cq_mobile.R;

import java.net.MalformedURLException;
import java.net.URL;

public class WebViewActivity extends AppCompatActivity {

    private FrameLayout webViewContainer;
    private URL url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        // Get the URL from the intent as a String
        Intent intent = getIntent();
        String urlString = intent.getStringExtra("url");

        if (urlString != null) {
            try {
                // Convert the String to a URL
                url = new URL(urlString);  // Convert String to URL
                if (!isValidUrl(url)) {
                    Log.e("WebViewActivity", "URL format is invalid.");
                    return;
                }

                if (!isNetworkAvailable()) {
                    Log.e("WebViewActivity", "No network available.");
                    return;
                }

                // Open the URL in the browser
                openUrlInBrowser(url);

            } catch (MalformedURLException e) {
                Log.e("WebViewActivity", "Invalid URL format", e);
            }
        } else {
            Log.e("WebViewActivity", "URL is null or empty.");
        }
    }

    // Open the URL in a browser
    private void openUrlInBrowser(URL url) {
        Log.d("WebViewActivity", "Opening URL in browser: " + url.toString());

        // Create an Intent to view the URL
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
        startActivity(browserIntent);
        finish();  // Finish the activity once the browser is opened
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // No need to destroy WebView, as it's no longer used
    }

    // Check if the URL is valid (can be opened)
    private boolean isValidUrl(URL url) {
        return url != null && url.toString().startsWith("http");
    }

    // Check for network connectivity
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
