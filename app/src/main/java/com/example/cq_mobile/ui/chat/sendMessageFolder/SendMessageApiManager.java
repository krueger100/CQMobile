package com.example.cq_mobile.ui.chat.sendMessageFolder;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class SendMessageApiManager {
    private static final String TAG = "SendMessageApiManager";
    private static final String BASE_URL = "https://aws.customquoter.co.uk";
    private static final String ENDPOINT = "/api/m/chats/send";
    private static final String API_KEY = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";

    public interface ApiCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public static void sendMessage(String receiver, String chatchannel, String sender, String message, String avatar, String date, String time, String name, String token, ApiCallback callback) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new ApiSendMessageTask(receiver, chatchannel, sender, message, avatar, date, time, name,token, callback));
    }

    private static class ApiSendMessageTask implements Runnable {
        private final String receiver;
        private final String chatchannel;
        private final String sender;
        private final String message;
        private final String avatar;
        private final String date;
        private final String time;
        private final String name;
        String token;
        private final ApiCallback callback;

        public ApiSendMessageTask(String receiver, String chatchannel, String sender, String message, String avatar, String date, String time, String name, String token, ApiCallback callback) {
            this.receiver = receiver;
            this.chatchannel = chatchannel;
            this.sender = sender;
            this.message = message;
            this.avatar = avatar;
            this.date = date;
            this.time = time;
            this.name = name;
            this.token = token;
            this.callback = callback;
        }

        @Override
        public void run() {
            String jsonBody = String.format(
                    "{\"receiver\": \"%s\", \"chatchannel\": \"%s\", \"replied_to\": null, \"message\": {\"text\": \"%s\", \"avatar\": \"%s\", \"date\": \"%s\", \"time\": \"%s\", \"sender\": \"%s\", \"name\": \"%s\"}}",
                    receiver, chatchannel, message, avatar, date, time, sender, name);

            postSendMessage(BASE_URL, ENDPOINT, API_KEY, jsonBody,token);
        }

        private void postSendMessage(String baseUrl, String endpoint, String apiKey, String jsonBody,String token) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));

            Request request = new Request.Builder()
                    .url(baseUrl + endpoint)
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("x-api-key", apiKey)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .post(body)
                    .build();
            Log.d(TAG, "token: " + token);

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    String responseBody = null;
                    try {
                        if (response.body() != null) {
                            responseBody = response.body().string();
                        }
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Ticket created successfully. Response: " + responseBody);




                            callback.onSuccess();
                        } else {
                            Log.e(TAG, "Request Failed: " + response.code() + " - " + response.message());
                            Log.e(TAG, "Error Body: " + responseBody);


                            callback.onFailure("Failed to create ticket: " + responseBody);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading response: " + e.getMessage(), e);


                        callback.onFailure("Error reading response: " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e(TAG, "Error creating ticket: " + e.getMessage(), e);

                    callback.onFailure("Error creating ticket: " + e.getMessage());
                }
            });

        }
    }
}
