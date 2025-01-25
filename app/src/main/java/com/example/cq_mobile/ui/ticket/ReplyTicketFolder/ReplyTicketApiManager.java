package com.example.cq_mobile.ui.ticket.ReplyTicketFolder;

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
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
public class ReplyTicketApiManager {

    private static final String TAG = "ReplyTicketApiManager";

    // Define the callback interface
    public interface ApiCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public static void replyToTicket(String ticketId, String replyBody, ApiCallback callback) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new ApiReplyTicketTask(ticketId, replyBody, callback));
    }

    private static class ApiReplyTicketTask implements Runnable {

        private final String ticketId;
        private final String replyBody;
        private final ApiCallback callback;

        public ApiReplyTicketTask(String ticketId, String replyBody, ApiCallback callback) {
            this.ticketId = ticketId;
            this.replyBody = replyBody;
            this.callback = callback;
        }

        @Override
        public void run() {
            String baseUrl = "https://aws.customquoter.co.uk";
            String endpoint = String.format("/api/m/tickets/%s/reply", ticketId);
            String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";  // Make sure this is correct

            // Create JSON body for reply
            String jsonBody = String.format("{\"body\": \"%s\", \"ticket_id\": %s, \"ticket_message_id\": [6358, 5675, 5678, 5819]}", replyBody, ticketId);

            // Start a new thread to get the access token
            AccessTokenRequest request1 = new AccessTokenRequest("richard.anthony.wetherell@gmail.com", "123456");
            AccessTokenApiService apiService = RetrofitClientAccessToken.getRetrofitInstance().create(AccessTokenApiService.class);
            Call<AccessTokenResponse> call = apiService.AccessTokenUser(request1);

            call.enqueue(new Callback<AccessTokenResponse>() {
                @Override
                public void onResponse(Call<AccessTokenResponse> call, retrofit2.Response<AccessTokenResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String accessToken = response.body().getAccessToken();
                        Log.d(TAG, "Access Token: " + accessToken);

                        // Make the reply request using the access token
                        postReplyToTicket(baseUrl, endpoint, accessToken, apiKey, jsonBody);
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

        private void postReplyToTicket(String baseUrl, String endpoint, String accessToken, String apiKey, String jsonBody) {
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

            // Use enqueue for async call instead of execute
            // Make sure you're using Retrofit's Callback properly with the response type.
            client.newCall(request).enqueue(new okhttp3.Callback() {  // Use OkHttp's Callback
                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    String responseBody = null;
                    try {
                        if (response.body() != null) {
                            responseBody = response.body().string();  // Read the response body as string
                        }
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Reply sent successfully. Response: " + responseBody);
                            callback.onSuccess();
                        } else {
                            Log.e(TAG, "Request Failed: " + response.code() + " - " + response.message());
                            Log.e(TAG, "Error Body: " + responseBody);
                            callback.onFailure("Failed to send reply: " + responseBody);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading response: " + e.getMessage(), e);
                        callback.onFailure("Error reading response: " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e(TAG, "Error sending reply: " + e.getMessage(), e);
                    callback.onFailure("Error sending reply: " + e.getMessage());
                }
            });

        }
    }
}

/*

curl -v -X POST "https://aws.customquoter.co.uk/api/m/tickets/166/reply" \
-H "Authorization: Bearer 5898|lt8AgacV3I6KYrNkpmfvKbG4cIGLZLQE59LZj38h" \
-H "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2" \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-H "User-Agent: PostmanRuntime/7.43.0" \
-H "Accept-Encoding: gzip, deflate, br" \
-H "Connection: keep-alive" \
-d '{
  "body": "<p>Replying to the ticket.</p>",
  "ticket_id": 166,
  "category_id": 1,
  "ticket_category_id": 1,
  "job_schedule_ids": [5668, 5675, 5678, 5819],
  "job_schedule_id": 5668,
  "task_id": 774,
  "note_id": 125,
  "ticket_message_id": 635
}'

curl -v -X POST "https://aws.customquoter.co.uk/api/m/tickets/166/reply" \
-H "Authorization: Bearer 5898|lt8AgacV3I6KYrNkpmfvKbG4cIGLZLQE59LZj38h" \
-H "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2" \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-H "User-Agent: PostmanRuntime/7.43.0" \
-H "Accept-Encoding: gzip, deflate, br" \
-H "Connection: keep-alive" \
-d '{
  "body": "<p>Replying to Carl'\''s ticket.</p>",
  "ticket_id": 166,
  "ticket_message_id": [6358, 5675, 5678, 5819]
}'



 */