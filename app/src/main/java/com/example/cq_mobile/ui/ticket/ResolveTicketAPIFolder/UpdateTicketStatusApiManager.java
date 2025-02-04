package com.example.cq_mobile.ui.ticket.ResolveTicketAPIFolder;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

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

public class UpdateTicketStatusApiManager {

    private static final String TAG = "UpdateTicketStatusApiManager";

    // Define the callback interface
    public interface ApiCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public static void updateTicketStatus(String email, String password, int ticketId, String status, ProgressBar progressBar, ApiCallback callback) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new ApiUpdateTicketStatusTask(email,password,ticketId, status, progressBar, callback));
    }

    private static class ApiUpdateTicketStatusTask implements Runnable {
        String email;
        String password;
        private final int ticketId;
        private final String status;
        ProgressBar progressBar;
        private final ApiCallback callback;

        public ApiUpdateTicketStatusTask(String email, String password, int ticketId, String status, ProgressBar progressBar, ApiCallback callback) {
            this.email = email;
            this.password = password;
            this.ticketId = ticketId;
            this.status = status;
            this.progressBar = progressBar;
            this.callback = callback;
        }

        @Override
        public void run() {
            String baseUrl = "https://aws.customquoter.co.uk";
            String endpoint = "/api/m/tickets/" + ticketId + "/status";
            String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";
            String accessToken = "6331|n98FC0W7s7RlA4o5mnCmfxTDYlzWkWF2qg2B4c0m"; // Replace with dynamic retrieval if needed

            // Create JSON body for ticket status update
            String jsonBody = String.format("{\"status\": \"%s\"}", status);

            postUpdateTicketStatus(email,password,baseUrl, endpoint, accessToken, apiKey, jsonBody);
        }

        private void postUpdateTicketStatus(String email, String password, String baseUrl, String endpoint, String accessToken, String apiKey, String jsonBody) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

          //  RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonBody);
            RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));



            Request request = new Request.Builder()
                    .url(baseUrl + endpoint)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("x-api-key", apiKey)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .post(body)
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
                            Log.d(TAG, "Ticket status updated successfully. Response: " + responseBody);

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

                            callback.onFailure("Failed to update ticket status: " + responseBody);
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
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Error updating ticket status: " + e.getMessage(), e);

                    // Update UI on the main thread
                    progressBar.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                    callback.onFailure("Error updating ticket status: " + e.getMessage());
                }
            });
        }
    }
}



/*
curl -v -X POST "https://aws.customquoter.co.uk/api/m/tickets/178/status" \
-H "Authorization: Bearer 6331|n98FC0W7s7RlA4o5mnCmfxTDYlzWkWF2qg2B4c0m" \
-H "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2" \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-d '{
  "status": "resolved"
}'

 */