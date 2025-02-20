package com.example.cq_mobile.HelperManagers.CacheFolder;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

public class CacheManager {

    private static final String PREF_NAME = "app_cache";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    public CacheManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
    }

    // Method to cache an object (e.g., colleagues, chats)
    public <T> void cacheData(String key, T data) {
        try {
            String json = gson.toJson(data);
            editor.putString(key, json);
            editor.apply();
            Log.d("CacheManager", "Data cached with key: " + key);
        } catch (Exception e) {
            Log.e("CacheManager", "Error caching data: " + e.getMessage());
        }
    }

    // Method to retrieve cached data
    public <T> T getCachedData(String key, Class<T> clazz) {
        try {
            String json = sharedPreferences.getString(key, null);
            if (json != null) {
                return gson.fromJson(json, clazz);
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.e("CacheManager", "Error retrieving cached data: " + e.getMessage());
            return null;
        }
    }

    // Method to clear specific cached data
    public void clearCache(String key) {
        editor.remove(key);
        editor.apply();
        Log.d("CacheManager", "Cache cleared for key: " + key);
    }

    // Method to clear all cached data
    public void clearAllCache() {
        editor.clear();
        editor.apply();
        Log.d("CacheManager", "All cached data cleared");
    }
}
/*

CALL
 CacheManager cacheManager = new CacheManager(getContext());
Retrieve
    String cachedToken = cacheManager.getCachedData("access_token", String.class);
    if (cachedToken != null) {
        accessToken = cachedToken;
    }

 */