package com.example.cq_mobile.LoginFolder;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AuthManager {

    private static AuthManager instance;
    private SharedPreferences sharedPreferences;
    private Context context;

    // Key names for SharedPreferences
    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_AVATAR = "avatar";
    private static final String KEY_EXPIRATION_TIME = "expiration_time";


    private AuthManager(Context context) {
        if (context == null) {
            Log.e("AuthManager", "Context is null!");
        }

        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.context = context;
    }

    // Public method to get the single instance of the class
    public static synchronized AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context);
        }
        return instance;
    }


    // Method to save the access token and user info (with encryption)
    public void saveToken(String token, int userId, String firstName, String lastName, String email, String avatar, long expirationTime) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            // Encrypt the values before saving
            String encryptedToken = EncryptionUtil.encrypt(token);
            editor.putLong(KEY_EXPIRATION_TIME, expirationTime);
            String encryptedFirstName = EncryptionUtil.encrypt(firstName);
            String encryptedLastName = EncryptionUtil.encrypt(lastName);
            String encryptedEmail = EncryptionUtil.encrypt(email);
            String encryptedAvatar = EncryptionUtil.encrypt(avatar);

            editor.putString(KEY_ACCESS_TOKEN, encryptedToken);
            editor.putInt(KEY_USER_ID, userId);  // User ID is saved as plain since it's not sensitive
            editor.putString(KEY_FIRST_NAME, encryptedFirstName);
            editor.putString(KEY_LAST_NAME, encryptedLastName);
            editor.putString(KEY_EMAIL, encryptedEmail);
            editor.putString(KEY_AVATAR, encryptedAvatar);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Method to get the user ID
    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    // Method to get the stored access token (decrypting it)
    public String getToken() {
        String encryptedToken = sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
        if (encryptedToken != null) {
            try {
                return EncryptionUtil.decrypt(encryptedToken);  // Decrypt the value before returning
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // Other getter methods, with decryption
    public String getFirstName() {
        String encryptedFirstName = sharedPreferences.getString(KEY_FIRST_NAME, "");
        try {
            return EncryptionUtil.decrypt(encryptedFirstName);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getLastName() {
        String encryptedLastName = sharedPreferences.getString(KEY_LAST_NAME, "");
        try {
            return EncryptionUtil.decrypt(encryptedLastName);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getEmail() {
        String encryptedEmail = sharedPreferences.getString(KEY_EMAIL, "");
        try {
            return EncryptionUtil.decrypt(encryptedEmail);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getAvatar() {
        String encryptedAvatar = sharedPreferences.getString(KEY_AVATAR, "");
        try {
            return EncryptionUtil.decrypt(encryptedAvatar);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    public long getTokenExpirationTime() {
        return sharedPreferences.getLong(KEY_EXPIRATION_TIME, 0);
    }


    // Method to clear the stored data
    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_ACCESS_TOKEN);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_FIRST_NAME);
        editor.remove(KEY_LAST_NAME);
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_AVATAR);
        editor.apply();
    }

    // Method to check if the user is logged in by verifying the token
    public boolean isLoggedIn() {
        return getToken() != null;
    }
    public boolean isTokenExpired() {
        long expirationTime = getTokenExpirationTime();
        return System.currentTimeMillis() > expirationTime;
    }

}
