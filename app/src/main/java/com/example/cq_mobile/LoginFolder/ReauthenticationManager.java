package com.example.cq_mobile.LoginFolder;

import android.content.Context;
import android.util.Log;

import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.google.gson.Gson;

public class ReauthenticationManager {

    public interface ReauthenticationListener {
        void onReauthenticated(String email, String password);
    }

    private ReauthenticationListener listener;

    public ReauthenticationManager(ReauthenticationListener listener) {
        this.listener = listener;
    }

    public void handle422Error(Context context, final String email, final String password) {
        AccessTokenRequest request = new AccessTokenRequest(context,email, password);
        Log.d("Reauthentication", "Request Data: " + new Gson().toJson(request));

        listener.onReauthenticated(email, password);
    }
}
