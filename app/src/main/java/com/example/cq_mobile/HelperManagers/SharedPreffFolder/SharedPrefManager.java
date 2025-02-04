package com.example.cq_mobile.HelperManagers.SharedPreffFolder;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String PREF_NAME = "UserPreferences";
    private static final String KEY_ACCESS_TOKEN = "access_token"; // Corrected the key
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_AVATAR = "avatarUrl";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Save user data
    public void saveUserData(String accessToken, String userId, String firstName, String lastName, String email, String avatarUrl,String password) {
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_FIRST_NAME, firstName);
        editor.putString(KEY_LAST_NAME, lastName);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_AVATAR, avatarUrl);
        editor.apply();
    }

    // Retrieve user data
    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public String getFirstName() {
        return sharedPreferences.getString(KEY_FIRST_NAME, null);
    }

    public String getLastName() {
        return sharedPreferences.getString(KEY_LAST_NAME, null);
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }
    public String getPassword() {
        return sharedPreferences.getString(KEY_PASSWORD, null);
    }


    public String getAvatarUrl() {
        return sharedPreferences.getString(KEY_AVATAR, null);
    }

    // Clear user data
    public void clearUserData() {
        editor.clear();
        editor.apply();
    }
}

/*
Retrieve User Data When Needed
     SharedPrefManager sharedPrefManager = new SharedPrefManager(getContext());
        String accessToken = sharedPrefManager.getAccessToken();
        String userId = sharedPrefManager.getUserId();
        String firstName = sharedPrefManager.getFirstName();
        String lastName = sharedPrefManager.getLastName();
        String email = sharedPrefManager.getEmail();
       String password = sharedPrefManager.getPassword();



        Log.d("ToDoFragmentSharedPreff", "Retrieved User Data: ");
        Log.d("ToDoFragmentSharedPreff", "Access Token: " + accessToken);
        Log.d("ToDoFragmentSharedPreff", "User ID: " + userId);
        Log.d("ToDoFragmentSharedPreff", "First Name: " + firstName);
        Log.d("ToDoFragmentSharedPreff", "Last Name: " + lastName);
        Log.d("ToDoFragmentSharedPreff", "Email: " + email);



clear user data (e.g., on logout), call:
SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
String accessToken = sharedPrefManager.getAccessToken();
Log.d("AccessToken", "Access Token: " + accessToken);

clear all
     SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
        sharedPrefManager.clearUserData();
        Log.d("SharedPrefManager", "All user data has been cleared from SharedPreferences.");

 */