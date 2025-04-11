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
import okhttp3.Response;public class DeleteMessageApiManager {

    private static final String TAG = "DeleteMessageApiManager";

    // Define the callback interface
    public interface ApiCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public static void deleteMessage(String messageId, ProgressBar progressBar, Context context, ApiCallback callback) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new ApiDeleteMessageTask(messageId, progressBar, callback, context));
    }

    private static class ApiDeleteMessageTask implements Runnable {
        private final String messageId;
        private final Context context;
        private final ProgressBar progressBar;
        private final ApiCallback callback;

        public ApiDeleteMessageTask(String messageId, ProgressBar progressBar, ApiCallback callback, Context context) {
            this.messageId = messageId;
            this.context = context;
            this.progressBar = progressBar;
            this.callback = callback;
        }

        @Override
        public void run() {
            String baseUrl = "https://cqbms.app";//"https://aws.customquoter.co.uk";
            String endpoint = "/api/m/chats/delete";
            String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";

            // Get the access token using AuthManager
            String accessToken = AuthManager.getInstance(context).getToken();

            if (accessToken == null || accessToken.isEmpty()) {
                Log.e(TAG, "Access token is missing.");
                callback.onFailure("Access token is missing.");
                return;
            }

            // Create JSON body for message deletion
            String jsonBody = String.format("{\"id\": %s}", messageId);

            // Log the payload before sending
            Log.d(TAG, "Payload being sent: " + jsonBody);

            deleteMessageRequest(baseUrl, endpoint, accessToken, apiKey, jsonBody);
        }

        private void deleteMessageRequest(String baseUrl, String endpoint, String accessToken, String apiKey, String jsonBody) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));

            Request request = new Request.Builder()
                    .url(baseUrl + endpoint)
                    .addHeader("Authorization", "Bearer " + accessToken.trim()) // Ensure token is trimmed
                    .addHeader("x-api-key", apiKey)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .delete(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = null;
                    try {
                        if (response.body() != null) {
                            responseBody = response.body().string();
                        }
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Message deleted successfully. Response: " + responseBody);

                            progressBar.post(() -> progressBar.setVisibility(View.GONE));
                            callback.onSuccess();
                        } else {
                            Log.e(TAG, "Request Failed: " + response.code() + " - " + response.message());
                            Log.e(TAG, "Error Body: " + responseBody);

                            progressBar.post(() -> progressBar.setVisibility(View.GONE));
                            callback.onFailure("Failed to delete message: " + responseBody);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading response: " + e.getMessage(), e);

                        progressBar.post(() -> progressBar.setVisibility(View.GONE));
                        callback.onFailure("Error reading response: " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Error deleting message: " + e.getMessage(), e);

                    progressBar.post(() -> progressBar.setVisibility(View.GONE));
                    callback.onFailure("Error deleting message: " + e.getMessage());
                }
            });
        }
    }
}
