package com.example.cq_mobile.ui.home.HomeFolder.API_skipped;

import com.example.cq_mobile.ui.home.HomeFolder.API_todo.TodoApiManager;
import com.example.cq_mobile.ui.home.HomeFolder.API_todo.TodoResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SkippedApiManager {
    public interface ApiResponseCallback {
        void onDataFetched(List<Skipped> data); // Return List<Todo>
        void onError(String error);

    }

    public static void fetchApiDataPaginated(int page, int pageSize, ApiResponseCallback callback) {
        String baseUrl = "https://aws.customquoter.co.uk";
        String endpoint = "/api/m/jobs/schedules/today?page=1&per_page=100&status=skipped";
        String token = "3805|2NzKCMW8T6zH7sA25uEhxX2BOi1nzsqvvI2CRao4";
        String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";
        // Construct the full URL with pagination parameters
        String url = String.format("%s%s?page=%d&per_page=%d&status=skipped", baseUrl, endpoint, page, pageSize);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("x-api-key", apiKey)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    Gson gson = new Gson();

                    SkippedResponse skippedResponse = gson.fromJson(jsonResponse, SkippedResponse.class);

                    // Pass the full list of Todo objects to the callback
                    if (skippedResponse != null && skippedResponse.getData() != null && !skippedResponse.getData().isEmpty()) {
                        callback.onDataFetched(skippedResponse.getData());
                    } else {
                        callback.onError("No skipped jobs found.");
                    }
                } else {
                    callback.onError("Request Failed: " + response.code());
                }
            }
        });
    }
}
