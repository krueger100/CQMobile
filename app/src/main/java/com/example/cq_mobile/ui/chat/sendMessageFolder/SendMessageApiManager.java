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
import retrofit2.Response;

public class SendMessageApiManager {

    private static final String TAG = "SendMessageApiManager";

    // Define the callback interface
    public interface ApiCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public static void sendMessage(String receiver, String chatchannel, String message, ApiCallback callback) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new ApiSendMessageTask(receiver, chatchannel, message, callback));
    }

    private static class ApiSendMessageTask implements Runnable {

        private final String receiver;
        private final String chatchannel;
        private final String message;
        private final ApiCallback callback;

        public ApiSendMessageTask(String receiver, String chatchannel, String message, ApiCallback callback) {
            this.receiver = receiver;
            this.chatchannel = chatchannel;
            this.message = message;
            this.callback = callback;
        }

        @Override
        public void run() {
            // Create the request for the access token
            AccessTokenRequest request1 = new AccessTokenRequest("email", "password");

            // Use getAccessToken method to retrieve the access token
            new SendMessageApiManager().getAccessToken(request1, new AccessTokenCallback() {
                @Override
                public void onAccessTokenReceived(String accessToken) {
                    // Successfully received the access token, proceed with sending the message
                    Log.d(TAG, "Access Token: " + accessToken);
                    String baseUrl = "https://aws.customquoter.co.uk";
                    String endpoint = "/api/m/chats/send";
                    String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";  // Correct API key

                    // Prepare the JSON body with parameters
                    String jsonBody = String.format(
                            "{\"receiver\": \"%s\", \"chatchannel\": \"%s\", \"message\": \"%s\", \"name\": \"Marwin Intal\"}",
                            receiver, chatchannel, message);

                    // Make the send message request using the access token
                    postSendMessage(baseUrl, endpoint, accessToken, apiKey, jsonBody);
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e(TAG, "Failed to get access token: " + errorMessage);
                    callback.onFailure("Failed to get access token: " + errorMessage);
                }
            });
        }

        private void postSendMessage(String baseUrl, String endpoint, String accessToken, String apiKey, String jsonBody) {
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

            client.newCall(request).enqueue(new okhttp3.Callback() {
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

    // Place your getAccessToken method here as is
    public void getAccessToken(AccessTokenRequest request, final AccessTokenCallback callback) {
        // Create an instance of the API service
        AccessTokenApiService apiService = RetrofitClientAccessToken.getRetrofitInstance().create(AccessTokenApiService.class);

        // Call the API
        Call<AccessTokenResponse> call = apiService.AccessTokenUser(request);

        // Enqueue the call to execute asynchronously
        call.enqueue(new Callback<AccessTokenResponse>() {
            @Override
            public void onResponse(Call<AccessTokenResponse> call, Response<AccessTokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AccessTokenResponse accessTokenResponse = response.body();
                    String accessToken = accessTokenResponse.getAccessToken();

                    // Check if the access token was fetched successfully
                    if (accessToken != null) {
                        Log.d(TAG, "Access Token: " + accessToken);
                        callback.onAccessTokenReceived(accessToken);
                    } else {
                        callback.onError("Access token not received.");
                    }
                } else {
                    callback.onError("Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<AccessTokenResponse> call, Throwable t) {
                // Log the failure (e.g., network error)
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }

    // AccessTokenCallback interface
    public interface AccessTokenCallback {
        void onAccessTokenReceived(String accessToken);
        void onError(String errorMessage);
    }
}
