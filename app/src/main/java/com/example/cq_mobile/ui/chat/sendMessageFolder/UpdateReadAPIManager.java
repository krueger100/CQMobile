package com.example.cq_mobile.ui.chat.sendMessageFolder;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.cq_mobile.LoginFolder.AuthManager;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class UpdateReadAPIManager {
    private static final String TAG = "UpdateReadAPIManager";

    // Define the callback interface
    public interface ApiCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public static void updateReadStatus(Context context, int channelId, ApiCallback callback) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new ApiUpdateReadTask(context, channelId, callback));
    }

    private static class ApiUpdateReadTask implements Runnable {
        private final int channelId;
        private final Context context;
        private final ApiCallback callback;

        public ApiUpdateReadTask(Context context, int channelId, ApiCallback callback) {
            this.context = context;
            this.channelId = channelId;
            this.callback = callback;
        }

        @Override
        public void run() {
            String baseUrl = "https://cqbms.app";
            String endpoint = "/api/m/chats/update_read";
            String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";

            // Fetch access token from AuthManager
            String accessToken = AuthManager.getInstance(context).getToken();
            if (accessToken == null || accessToken.isEmpty()) {
                callback.onFailure("Access token is missing.");
                return;
            }

            // Create JSON body for read update
            String jsonBody = String.format("{\"channel\": %d}", channelId);
            updateReadRequest(baseUrl, endpoint, accessToken, apiKey, jsonBody);
        }

        private void updateReadRequest(String baseUrl, String endpoint, String accessToken, String apiKey, String jsonBody) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));

            Request request = new Request.Builder()
                    .url(baseUrl + endpoint)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("x-api-key", apiKey)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .patch(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Read status updated successfully. Response: " + responseBody);
                        callback.onSuccess();
                    } else {
                        Log.e(TAG, "Request Failed: " + response.code() + " - " + response.message());
                        Log.e(TAG, "Error Body: " + responseBody);
                        callback.onFailure("Failed to update read status: " + responseBody);
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Error updating read status: " + e.getMessage(), e);
                    callback.onFailure("Error updating read status: " + e.getMessage());
                }
            });
        }
    }
}

/*
SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
        String accessToken = sharedPrefManager.getAccessToken();
        int channelId = 58;
        UpdateReadAPIManager.updateReadStatus(channelId, accessToken, new UpdateReadAPIManager.ApiTimeSheetCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, "Read status updated successfully!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(context, "Failed to update read status: " + error, Toast.LENGTH_SHORT).show();
            }
        });

 */
