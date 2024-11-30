package com.example.cq_mobile.LoginFolder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class LogoutManager {

    private static final String USER_PREFS = "UserPrefs";
    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String COOKIES = "cookies";
    private static final String CSRF_TOKEN = "csrfToken";

    public static void logoutUser(Context context) {
        // Clear user-related data from SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Remove all keys from SharedPreferences
        editor.apply();

        // Navigate the user back to the Login screen
        Intent intent = new Intent(context, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the back stack
        context.startActivity(intent);
    }
}
