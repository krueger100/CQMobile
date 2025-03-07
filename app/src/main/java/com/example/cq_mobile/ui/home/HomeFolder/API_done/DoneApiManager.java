package com.example.cq_mobile.ui.home.HomeFolder.API_done;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DoneApiManager {
    public interface ApiResponseCallback {
        void onDataFetched(List<Done> data);
        void onError(String error);
    }

    public static void fetchDoneApiData(String accessToken, int page, int pageSize, ApiResponseCallback callback) {
        String baseUrl = "https://aws.customquoter.co.uk";
        String endpoint = "/api/m/jobs/schedules/today?page=1&per_page=100&status=done";
        String token = accessToken; // Your actual access token
        String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2"; // Your API Key

        // Construct the full URL with pagination parameters
        String url = String.format("%s%s?page=%d&per_page=%d&status=done", baseUrl, endpoint, page, pageSize);

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
                    Log.d("API Response", jsonResponse); // Log the raw response

                    // Check if the response starts with '{' (indicating a valid JSON object)
                    if (jsonResponse.startsWith("{")) {
                        Gson gson = new Gson();
                        try {
                            DoneResponse doneResponse = gson.fromJson(jsonResponse, DoneResponse.class);

                            // Pass the data to the callback if valid data is present
                            if (doneResponse != null && doneResponse.getData() != null && !doneResponse.getData().isEmpty()) {
                                callback.onDataFetched(doneResponse.getData());
                            } else {
                                callback.onError("No jobs found.");
                            }
                        } catch (JsonSyntaxException e) {
                            callback.onError("JSON parsing error: " + e.getMessage());
                        }
                    } else {
                        // If the response is a string (likely an error message), log and handle accordingly
                        callback.onError("Unexpected response format: " + jsonResponse);
                    }
                } else {
                    callback.onError("Request Failed: " + response.code());
                }
            }
        });
    }
}

