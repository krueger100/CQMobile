package com.example.cq_mobile.ui.home.HomeFolder.API_todo;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.List;

public class TodoApiManager {

    public interface ApiResponseCallback {
        void onDataFetched(List<Todo> data); // Return List<Todo>
        void onError(String error);
    }

    public static void fetchApiDataPaginated(String accessToken, int page, int pageSize, ApiResponseCallback callback) {
        String baseUrl = "https://cqbms.app";//"https://aws.customquoter.co.uk";
        String endpoint = "/api/m/jobs/schedules/today";
        String token = accessToken;
        String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";

        // Construct the full URL with pagination parameters
        String url = String.format("%s%s?page=%d&per_page=%d&status=todo", baseUrl, endpoint, page, pageSize);

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
                    Log.d("API Response", jsonResponse);  // Log the raw response

                    Gson gson = new Gson();

                    try {
                        // Try to parse the response into the expected object
                        TodoResponse todoResponseObject = gson.fromJson(jsonResponse, TodoResponse.class);

                        if (todoResponseObject != null && todoResponseObject.getData() != null && !todoResponseObject.getData().isEmpty()) {
                            callback.onDataFetched(todoResponseObject.getData());
                        } else {
                            callback.onError("No jobs found.");
                        }
                    } catch (JsonSyntaxException e) {
                        Log.d("JSON Error", "Failed to parse JSON response: " + jsonResponse);
                        callback.onError("JSON parsing error: " + e.getMessage());
                    }
                } else {
                    callback.onError("Request Failed: " + response.code());
                }
            }

        });
    }
}
