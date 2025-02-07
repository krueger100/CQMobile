package com.example.cq_mobile.ui.chat.sendMessageFolder;

import android.util.Log;

import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenApiService;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenResponse;
import com.example.cq_mobile.HelperManagers.getAccessToken.RetrofitClientAccessToken;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class SendMessageApiManager {

    private static final String TAG = "SendMessageApiManager";

    // Define the callback interface
    public interface ApiCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public static void sendMessage(String email, String password, String ticketId, String replyBody, ApiCallback callback) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new ApiSendMessageTask(email, password, ticketId, replyBody, callback));
    }

    private static class ApiSendMessageTask implements Runnable {

        private final String ticketId;
        private final String replyBody;
        private final ApiCallback callback;
        String email;
        String password;

        public ApiSendMessageTask(String email, String password, String ticketId, String replyBody, ApiCallback callback) {
            this.email = email;
            this.password = password;
            this.ticketId = ticketId;
            this.replyBody = replyBody;
            this.callback = callback;
        }

        @Override
        public void run() {
            String baseUrl = "https://aws.customquoter.co.uk";
            String endpoint = String.format("/api/m/tickets/%s/reply", ticketId);
            String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";  // Make sure this is correct

            // Sample data to be used from the previous response
            String message = "Richard: asd";  // You can select any message from the response
            int msgId = 12494;  // Example message ID from your response

            // Create JSON body for reply, add the msg_id from the previous response as ticket_message_id
            String jsonBody = String.format(
                    "{\"body\": \"%s\", \"ticket_id\": %s, \"ticket_message_id\": [%d]}",
                    replyBody, ticketId, msgId);

            // Start a new thread to get the access token
            AccessTokenRequest request1 = new AccessTokenRequest(email, password);
            AccessTokenApiService apiService = RetrofitClientAccessToken.getRetrofitInstance().create(AccessTokenApiService.class);
            Call<AccessTokenResponse> call = apiService.AccessTokenUser(request1);

            call.enqueue(new Callback<AccessTokenResponse>() {
                @Override
                public void onResponse(Call<AccessTokenResponse> call, retrofit2.Response<AccessTokenResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String accessToken = response.body().getAccessToken();
                        Log.d(TAG, "Access Token: " + accessToken);

                        // Make the reply request using the access token
                        postSendMessageToTicket(baseUrl, endpoint, accessToken, apiKey, jsonBody);
                    } else {
                        Log.e(TAG, "Failed to get access token: " + response.message());
                        callback.onFailure("Failed to get access token: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<AccessTokenResponse> call, Throwable t) {
                    Log.e(TAG, "Failed to get access token: " + t.getMessage());
                    callback.onFailure("Failed to get access token: " + t.getMessage());
                }
            });
        }

        private void postSendMessageToTicket(String baseUrl, String endpoint, String accessToken, String apiKey, String jsonBody) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonBody);

            Request request = new Request.Builder()
                    .url(baseUrl + endpoint)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("x-api-key", apiKey)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {  // Use OkHttp's Callback
                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    String responseBody = null;
                    try {
                        if (response.body() != null) {
                            responseBody = response.body().string();  // Read the response body as string
                        }
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Message sent successfully. Response: " + responseBody);
                            callback.onSuccess();
                        } else {
                            Log.e(TAG, "Request Failed: " + response.code() + " - " + response.message());
                            Log.e(TAG, "Error Body: " + responseBody);
                            callback.onFailure("Failed to send message: " + responseBody);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading response: " + e.getMessage(), e);
                        callback.onFailure("Error reading response: " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e(TAG, "Error sending message: " + e.getMessage(), e);
                    callback.onFailure("Error sending message: " + e.getMessage());
                }
            });
        }
    }
}
