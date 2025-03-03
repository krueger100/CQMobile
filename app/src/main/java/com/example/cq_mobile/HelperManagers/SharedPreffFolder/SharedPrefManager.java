package com.example.cq_mobile.HelperManagers.SharedPreffFolder;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.TaskMainFolder.Taskmain;
import com.google.android.gms.maps.model.LatLng;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPrefManager {
    private static final String PREF_NAME = "UserPreferences";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "user_name";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_AVATAR = "avatarUrl";
    private static final String NOTIFTOKEN = "notifToken";
    private static final String KEY_JOB_ID = "job_id";
    private static final String KEY_TASK_ID = "task_id";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";

    private static final String KEY_COORDINATES_LIST = "coordinates_list";

    private static final String KEY_START_DATE = "start_date";
    private static final String KEY_STOP_DATE = "sop_date";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Save methods
    public void saveNewNotificationToken(String token) {
        editor.putString(NOTIFTOKEN, token).apply();
        Log.w("SharedPrefManager", "NOTIFTOKEN   -->> Updated");
    }

    public void saveAccessToken(String accessToken) {
        editor.putString(KEY_ACCESS_TOKEN, accessToken).apply();
        Log.w("SharedPrefManager", "KEY_ACCESS_TOKEN   -->> Updated");
    }

    public void saveUserName(String username) {
        editor.putString(KEY_USERNAME, username).apply();
        Log.w("SharedPrefManager", "KEY_USERNAME   -->> Updated");
    }

    public void saveFirstName(String firstname) {
        editor.putString(KEY_FIRST_NAME, firstname).apply();
        Log.w("SharedPrefManager", "KEY_FIRST_NAME   -->> Updated");
    }
    public void saveLastName(String lastname) {
        editor.putString(KEY_LAST_NAME, lastname).apply();
        Log.w("SharedPrefManager", "KEY_LAST_NAME   -->> Updated");
    }

    public void saveCredentials(String email, String password) {
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
        Log.w("SharedPrefManager", "Credentials   -->> Updated");
    }

    public void saveAvatar(String avatar) {
        editor.putString(KEY_AVATAR, avatar).apply();
        Log.w("SharedPrefManager", "KEY_AVATAR   -->> Updated");
    }

    public void saveUserId(int userId) {
        editor.putInt(KEY_USER_ID, userId).apply();
        Log.w("SharedPrefManager", "KEY_USER_ID   -->> Updated");
    }

    public void saveJobId(int jobId) {
        editor.putInt(KEY_JOB_ID, jobId).apply();
        Log.w("SharedPrefManager", "KEY_JOB_ID   -->> Updated");
    }

    public void saveTaskId(int taskId) {
        editor.putInt(KEY_TASK_ID, taskId).apply();
        Log.w("SharedPrefManager", "KEY_TASK_ID   -->> Updated");
    }

    public void saveAddress(String address) {
        editor.putString(KEY_ADDRESS, address).apply();
        Log.w("SharedPrefManager", "KEY_ADDRESS   -->> Updated");
    }



    public void saveCoordinatesList(List<Taskmain.Coordinates> coordinatesList) {
        Gson gson = new Gson();
        String json = gson.toJson(coordinatesList);
        editor.putString(KEY_COORDINATES_LIST, json).apply();
        Log.w("SharedPrefManager", "Coordinates List Saved: " + json);
    }

    public List<Taskmain.Coordinates> getCoordinatesList() {
        String json = sharedPreferences.getString(KEY_COORDINATES_LIST, null);
        if (json == null) return new ArrayList<>();

        Gson gson = new Gson();
        Type type = new TypeToken<List<Taskmain.Coordinates>>() {}.getType();
        return gson.fromJson(json, type);
    }
    public void saveStartDate(String startDate) {
        editor.putString(KEY_START_DATE, startDate).apply();
        Log.w("SharedPrefManager", "KEY_START_DATE   -->> Updated");
    }

    public void saveStopDate(String stopDate) {
        editor.putString(KEY_STOP_DATE, stopDate).apply();
        Log.w("SharedPrefManager", "KEY_STOP_DATE   -->> Updated");
    }



    // Retrieve methods (Getters)
    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    public int getUserId() {
        try {
            return sharedPreferences.getInt(KEY_USER_ID, -1);
        } catch (ClassCastException e) {
            try {
                return Integer.parseInt(sharedPreferences.getString(KEY_USER_ID, "-1"));
            } catch (Exception ex) {
                Log.e("SharedPrefManager", "Error retrieving user ID", ex);
                return -1;
            }
        }
    }


    public String getUserName() {
        return sharedPreferences.getString(KEY_USERNAME, null);
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

    public String getNotiftoken() {
        return sharedPreferences.getString(NOTIFTOKEN, null);
    }

    public int getJobId() {
        return sharedPreferences.getInt(KEY_JOB_ID, -1);
    }

    public int getTaskId() {
        return sharedPreferences.getInt(KEY_TASK_ID, -1);
    }

    public String getAddress() {
        return sharedPreferences.getString(KEY_ADDRESS, null);
    }

    public double getLatitude() {
        return Double.parseDouble(sharedPreferences.getString(KEY_LATITUDE, "0"));
    }

    public double getLongitude() {
        return Double.parseDouble(sharedPreferences.getString(KEY_LONGITUDE, "0"));
    }


    public String getKeyStartDate() {
        return sharedPreferences.getString(KEY_START_DATE, null);
    }

    public String getKeyStopDate() {
        return sharedPreferences.getString(KEY_STOP_DATE, null);
    }


    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public SharedPreferences.Editor getEditor() {
        return editor;
    }


    // Clear all stored user data
    public void clearUserData() {
        editor.clear().apply();
        Log.w("SharedPrefManager", "All user data cleared!");
    }

    // Clear individual data methods
    public void clearAccessToken() {
        editor.remove(KEY_ACCESS_TOKEN).apply();
        Log.w("SharedPrefManager", "Access Token Cleared");
    }

    public void clearUserId() {
        editor.remove(KEY_USER_ID).apply();
        Log.w("SharedPrefManager", "User ID Cleared");
    }

    public void clearUserName() {
        editor.remove(KEY_USERNAME).apply();
        Log.w("SharedPrefManager", "User Name Cleared");
    }

    public void clearFirstName() {
        editor.remove(KEY_FIRST_NAME).apply();
        Log.w("SharedPrefManager", "First Name Cleared");
    }

    public void clearLastName() {
        editor.remove(KEY_LAST_NAME).apply();
        Log.w("SharedPrefManager", "Last Name Cleared");
    }

    public void clearEmail() {
        editor.remove(KEY_EMAIL).apply();
        Log.w("SharedPrefManager", "Email Cleared");
    }

    public void clearPassword() {
        editor.remove(KEY_PASSWORD).apply();
        Log.w("SharedPrefManager", "Password Cleared");
    }

    public void clearAvatarUrl() {
        editor.remove(KEY_AVATAR).apply();
        Log.w("SharedPrefManager", "Avatar URL Cleared");
    }

    public void clearNotificationToken() {
        editor.remove(NOTIFTOKEN).apply();
        Log.w("SharedPrefManager", "Notification Token Cleared");
    }

    public void clearJobId() {
        editor.remove(KEY_JOB_ID).apply();
        Log.w("SharedPrefManager", "Job ID Cleared");
    }

    public void clearTaskId() {
        editor.remove(KEY_TASK_ID).apply();
        Log.w("SharedPrefManager", "Task ID Cleared");
    }

    public void clearAddress() {
        editor.remove(KEY_ADDRESS).apply();
        Log.w("SharedPrefManager", "Address Cleared");
    }

    public void clearCoordinates() {
        editor.remove(KEY_LATITUDE);
        editor.remove(KEY_LONGITUDE);
        editor.apply();
        Log.w("SharedPrefManager", "Coordinates Cleared");
    }

    public void clearStartDate() {
        editor.remove(KEY_START_DATE).apply();
        Log.w("SharedPrefManager", "START DATE Cleared");
    }

    public void clearStopDate() {
        editor.remove(KEY_STOP_DATE).apply();
        Log.w("SharedPrefManager", "STOP DATE Cleared");
    }
}


/*

   // Save user data
    public void saveUserData(String accessToken, String userId, String firstName, String lastName, String email, String avatar, String password, String token, int jobId, Integer taskId) {
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_FIRST_NAME, firstName);
        editor.putString(KEY_LAST_NAME, lastName);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(NOTIFTOKEN, token);
        editor.putInt(KEY_JOB_ID, jobId);
        editor.putInt(KEY_TASK_ID, taskId);
        editor.apply();
    }


// Updated retrieval methods
public int getUserId() {
    return sharedPreferences.getInt(KEY_USER_ID, -1);
}

public int getJobId() {
    return sharedPreferences.getInt(KEY_JOB_ID, -1);
}

public int getTaskId() {
    return sharedPreferences.getInt(KEY_TASK_ID, -1);
}





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