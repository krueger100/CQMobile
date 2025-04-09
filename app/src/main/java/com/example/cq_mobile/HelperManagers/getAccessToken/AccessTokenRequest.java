package com.example.cq_mobile.HelperManagers.getAccessToken;

import android.content.Context;

public class AccessTokenRequest {
    private String email;
    private String password;

    public AccessTokenRequest(Context context,String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getter and Setter methods
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}



/*

Context context = requireContext(); <- Call in Fragments
Context context = getApplicationContext(); <- Call in Activity Class


public class AccessTokenRequest {
    private String email;
    private String password;

    public AccessTokenRequest(Context context, String email, String password) {
        LoginSavedData loginSavedData = new LoginSavedData(context);

        // Check and retrieve email if missing
        if (email == null || email.isEmpty()) {
            email = loginSavedData.getEmail();
        }

        // Check and retrieve password if missing
        if (password == null || password.isEmpty()) {
            password = loginSavedData.getPassword();
        }

        this.email = email;
        this.password = password;

        // Log the email and password
        Log.d("AccessTokenRequest", "Email: " + (email != null ? email : "NULL"));
        Log.d("AccessTokenRequest", "Password: " + (password != null ? password : "NULL"));

        // Additional check for empty values
        if (email == null || email.isEmpty()) {
            Log.e("AccessTokenRequest", "Email is missing or empty!");
        }
        if (password == null || password.isEmpty()) {
            Log.e("AccessTokenRequest", "Password is missing or empty!");
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        Log.d("AccessTokenRequest", "Email updated to: " + email);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        Log.d("AccessTokenRequest", "Password updated to: " + password);
    }
}

 */


