package com.example.cq_mobile.HelperManagers.Notifications;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.example.cq_mobile.R;
import com.google.auth.oauth2.GoogleCredentials;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import android.content.Context;

import java.io.InputStream;

public class FCM {
    private static final String TAG = "FCM";
    private static final String BASE_URL = "https://fcm.googleapis.com";
    private static final String FCM_SEND_ENDPOINT = "/v1/projects/cqbms-app/messages:send";
    private static final String[] SCOPES = {"https://www.googleapis.com/auth/firebase.messaging"};

    /*
       * Generate access token
       *
         FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get the new FCM registration token
                    String token = task.getResult();
                    Log.d(TAG, "FCM Token: " + token);
                });


     */

    /**
     * Retrieves an OAuth2 access token from Firebase Service Account credentials.
     */
    private static String getAccessToken(Context context) throws IOException {
        try {
            // Load the service account JSON file dynamically from res/raw/
            InputStream inputStream = context.getResources().openRawResource(R.raw.service_account);

            // Create Google Credentials with Firebase Messaging Scope
            GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(inputStream)
                    .createScoped(Arrays.asList(SCOPES));

            googleCredentials.refresh();
            return googleCredentials.getAccessToken().getTokenValue();
        } catch (IOException e) {
            Log.e(TAG, "Failed to load Firebase credentials: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Sets up an authenticated HTTP connection for Firebase Messaging API.
     */
    private static HttpURLConnection getHttpURLConnection(Context context) throws IOException {
        URL url = new URL(BASE_URL + FCM_SEND_ENDPOINT);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + getAccessToken(context));
        httpURLConnection.setRequestProperty("Content-Type", "application/json; UTF-8");
        return httpURLConnection;
    }

    /**
     * Sends an FCM push notification using Firebase Cloud Messaging API.
     *
     * @param context                 Application context to access resources
     * @param token                   FCM device token
     * @param title                   Notification title
     * @param body                    Notification body
     */
    public void sendNotification(Context context, String token, String title, String body) {
        OkHttpClient client = new OkHttpClient();

        try {
            // Get access token dynamically
            String accessToken = getAccessToken(context);

            // Create notification JSON payload
            JSONObject notification = new JSONObject();
            notification.put("title", title != null ? title : "Default Title");
            notification.put("body", body != null ? body : "Default Body Message");

            JSONObject message = new JSONObject();
            message.put("token", token);
            message.put("notification", notification);

            JSONObject requestBodyJson = new JSONObject();
            requestBodyJson.put("message", message);

            // Prepare the HTTP request
            RequestBody requestBody = RequestBody.create(
                    requestBodyJson.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(BASE_URL + FCM_SEND_ENDPOINT)
                    .post(requestBody)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("Content-Type", "application/json")
                    .build();

            // Execute the HTTP request asynchronously
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Request Failed: " + e.getMessage(), e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (response.isSuccessful()) {
                            Log.i(TAG, "Notification Sent Successfully: " + response.body().string());
                        } else {
                            String errorBody = response.body() != null ? response.body().string() : "Error body is null";
                            Log.e(TAG, "Error Sending Notification: " + response.code() + " " + response.message() + " - Body: " + errorBody);
                        }
                    } finally {
                        if (response.body() != null) {
                            response.body().close();
                        }
                    }
                }
            });

        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error: " + e.getMessage(), e);
        }
    }




}
