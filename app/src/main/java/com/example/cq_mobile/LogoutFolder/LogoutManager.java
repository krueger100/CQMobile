package com.example.cq_mobile.LogoutFolder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.LoginFolder.Login;

import android.util.Log;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LogoutManager {

    private static final String USER_PREFS = "UserPrefs";


    public static void logoutUser(Context context) {
        // First, clear user-related data from SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
        sharedPrefManager.clearUserId();
        sharedPrefManager.clearUserName();
        sharedPrefManager.clearAddress();
        sharedPrefManager.clearAvatarUrl();
        sharedPrefManager.clearEmail();
        sharedPrefManager.clearPassword();
        sharedPrefManager.clearCoordinates();
        sharedPrefManager.clearStartJob();
        sharedPrefManager.clearStartJobMessage();
        SharedPreferences clockPrefs= context.getSharedPreferences("ClockPrefs", Context.MODE_PRIVATE);
        clockPrefs.edit().clear().apply();

        Log.d("SharedPrefManager", "All user data has been cleared.");

        String url = "https://cqbms.app/logout"; ///"https://aws.customquoter.co.uk/api/m/logout";
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create("", MediaType.get("text/plain"));
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

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
