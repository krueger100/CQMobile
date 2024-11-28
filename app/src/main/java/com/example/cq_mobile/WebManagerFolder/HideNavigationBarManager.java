package com.example.cq_mobile.WebManagerFolder;

import android.webkit.WebView;

public class HideNavigationBarManager {

    // Method to hide the navigation bar by using JavaScript
    public static void hideNavigationBar(WebView webView) {
        String js = "document.querySelector('.navbar').style.display='none';"+
                 "document.querySelector('.top-menu-content').style.display='none';";
        webView.evaluateJavascript(js, null);
    }
}



