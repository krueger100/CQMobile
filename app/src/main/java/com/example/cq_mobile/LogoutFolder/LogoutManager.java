package com.example.cq_mobile.LogoutFolder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.cq_mobile.LoginFolder.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LogoutManager {

    private static final String USER_PREFS = "UserPrefs";


    public static void logoutUser(Context context) {
        // First, clear user-related data from SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Remove all keys from SharedPreferences
        editor.apply();

        // Make POST request to logout API
        String url = "https://aws.customquoter.co.uk/api/m/logout";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(okhttp3.RequestBody.create(null, new byte[0])) // Empty POST body
                .build();

        // Execute the request in a background thread
        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    Log.d("LogoutManager", "User logged out successfully.");

                    // Navigate the user back to the Login screen
                    Intent intent = new Intent(context, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the back stack
                    context.startActivity(intent);
                } else {
                    Log.e("LogoutManager", "Logout failed: " + response.message());
                }
            } catch (Exception e) {
                Log.e("LogoutManager", "Error during logout request", e);
            }
        }).start();
    }
}
