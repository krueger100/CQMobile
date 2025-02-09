package com.example.cq_mobile.ui.ticket.CreateFolder.TicketCreateFolder;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;


import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class TicketCreateApiManager {

    private static final String TAG = "TicketCreateApiManager";

    // Define the callback interface
    public interface ApiCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public static void createTicket(String subject, String body, int categoryId, ProgressBar progressBar, String accessToken, ApiCallback callback) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new ApiCreateTicketTask(subject, body, categoryId, progressBar,accessToken, callback));
    }

    private static class ApiCreateTicketTask implements Runnable {

        private final String subject;
        private final String body;
        private final int categoryId;
        ProgressBar progressBar;
        private final ApiCallback callback;
        String accessToken;

        public ApiCreateTicketTask(String subject, String body, int categoryId, ProgressBar progressBar, String accessToken, ApiCallback callback) {
            this.subject = subject;
            this.body = body;
            this.accessToken = accessToken;
            this.categoryId = categoryId;
            this.progressBar = progressBar;
            this.callback = callback;
        }

        @Override
        public void run() {
            String baseUrl = "https://aws.customquoter.co.uk";
            String endpoint = "/api/m/tickets";
            String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";

            // Create JSON body for ticket creation
            String jsonBody = String.format(
                    "{\"subject\": \"%s\", \"body\": \"%s\", \"category_id\": %d}",
                    subject, body, categoryId
            );

            postCreateTicket(baseUrl, endpoint, accessToken, apiKey, jsonBody);
        }

        private void postCreateTicket(String baseUrl, String endpoint, String accessToken, String apiKey, String jsonBody) {
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
                    .post(body)
                    .build();

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

                            // Update UI on the main thread
                            progressBar.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);

                                }
                            });

                            callback.onSuccess();
                        } else {
                            Log.e(TAG, "Request Failed: " + response.code() + " - " + response.message());
                            Log.e(TAG, "Error Body: " + responseBody);

                            // Update UI on the main thread
                            progressBar.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                }
                            });

                            callback.onFailure("Failed to create ticket: " + responseBody);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading response: " + e.getMessage(), e);

                        // Update UI on the main thread
                        progressBar.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                            }
                        });

                        callback.onFailure("Error reading response: " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e(TAG, "Error creating ticket: " + e.getMessage(), e);

                    // Update UI on the main thread
                    progressBar.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                    callback.onFailure("Error creating ticket: " + e.getMessage());
                }
            });
        }
    }
}



/*

curl -v -X POST "https://aws.customquoter.co.uk/api/m/tickets" \
-H "Authorization: Bearer 6331|n98FC0W7s7RlA4o5mnCmfxTDYlzWkWF2qg2B4c0m" \
-H "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2" \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-d '{
  "subject": "ticket from MOBILE APP",
  "body": "<p>Ticket Create using CQ APP second Test</p>",
  "category_id": 2
}'

 */