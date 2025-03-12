package com.example.cq_mobile.TestFolder;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiManager {
    private static final String TAG = "ApiManager";

    public static void fetch_ApiData() {
        // Use ExecutorService to run the task in a background thread
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new ApiTestTask());
    }

    private static class ApiTestTask implements Runnable {

        @Override
        public void run() {

            String baseUrl = "https://cqbms.app";///"https://aws.customquoter.co.uk";
            String endpoint ="/jobs/schedules/today?page=1&per_page=100&status=todo"; //   "/api/m/jobs/schedules/today?page=1&per_page=100&status=todo";
            String token = "3805|2NzKCMW8T6zH7sA25uEhxX2BOi1nzsqvvI2CRao4";
            String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";
            int page = 1;
            int perPage = 100;
            String status = "todo";

            // Construct the full URL
            String url = String.format("%s%s?page=%d&per_page=%d&status=%s",
                    baseUrl, endpoint, page, perPage, status);

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("x-api-key", apiKey)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    // Log the successful response
                    Log.d(TAG, "API Response: " + response.body().string());
                } else {
                    // Log the failure
                    Log.d(TAG, "Request Failed: " + response.code());
                }
            } catch (IOException e) {
                Log.d(TAG, "Error: " + e.getMessage());
            }
        }
    }
}
