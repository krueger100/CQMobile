package com.example.cq_mobile.HelperManagers.SharedPreffFolder;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerDataReconnect {
    private static final String LOGIN_URL = "https://cqbms.app/api/m/login";
    private static final String PREFS_NAME = "ServerDataPrefs";
    private static final String EMAIL_KEY = "email";
    private static final String PASSWORD_KEY = "password";
    private static final String API_KEY_HEADER = "x-api-key";
    private static final String API_KEY_VALUE = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";

    private final Context context;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ServerDataReconnect(Context context) {
        this.context = context;
    }

    // Save login data
    public void saveLoginData(String email, String password) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(EMAIL_KEY, email);
        editor.putString(PASSWORD_KEY, password);
        editor.apply();
        Log.d("ServerDataReconnect", "Login data saved.");
    }

    public void reconnectAsync(ReconnectCallback callback) {
        executor.execute(() -> {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String email = prefs.getString(EMAIL_KEY, null);
            String password = prefs.getString(PASSWORD_KEY, null);

            if (email == null || password == null) {
                Log.e("ServerDataReconnect", "No login data found. Reconnection failed.");
                callback.onReconnectResult(false);
                return;
            }

            try {
                URL url = new URL(LOGIN_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty(API_KEY_HEADER, API_KEY_VALUE);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                String payload = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
                OutputStream os = connection.getOutputStream();
                os.write(payload.getBytes());
                os.flush();
                os.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    Log.d("ServerDataReconnect", "Reconnection successful.");
                    callback.onReconnectResult(true);
                } else {
                    Log.e("ServerDataReconnect", "Reconnection failed with response code: " + responseCode);
                    callback.onReconnectResult(false);
                }

            } catch (Exception e) {
                Log.e("ServerDataReconnect", "Reconnection error: ", e);
                callback.onReconnectResult(false);
            }
        });
    }

    public void clearData() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();


        SharedPreferences saveClockInDATA = context.getSharedPreferences("ClockPrefs", Context.MODE_PRIVATE);
        saveClockInDATA.edit().clear().apply();

        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
        sharedPrefManager.clearUserId();
        sharedPrefManager.clearUserName();
        sharedPrefManager.clearAddress();
        sharedPrefManager.clearAvatarUrl();
        sharedPrefManager.clearEmail();
        sharedPrefManager.clearPassword();
        sharedPrefManager.clearCoordinates();
        sharedPrefManager.clearIsLoggedIn();
        sharedPrefManager.clearStartJob();
        sharedPrefManager.clearStartJobMessage();
        sharedPrefManager.clearJobTrackingData();
        sharedPrefManager.clearStartDate();
        sharedPrefManager.clearStopDate();


        Log.d("ServerDataReconnect", "Login data cleared.");
    }

    // Callback Interface
    public interface ReconnectCallback {
        void onReconnectResult(boolean success);
    }
}
