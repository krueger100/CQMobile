package com.example.cq_mobile.WebManagerFolder;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class JavaScriptInterface {
    private Context context;

    public JavaScriptInterface(Context context) {
        this.context = context;
    }

    @android.webkit.JavascriptInterface
    public void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @android.webkit.JavascriptInterface
    public void logMessage(String message) {
        Log.d("JavaScriptInterface", message);
    }
}
