package com.example.cq_mobile.HelperManagers;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class CloseKeyboardManager {

    // Method to close the keyboard
    public static void closeKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}
/*
How to Use It in an Activity
CloseKeyboardManager.closeKeyboard(this);



How to Use It in a Fragment
CloseKeyboardManager.closeKeyboard(getActivity());

 */