package com.example.cq_mobile.ui.home.HomeFolder.API_todo;

import com.google.gson.Gson;

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

    public static void fetchApiDataPaginated(int page, int pageSize, ApiResponseCallback callback) {
        String baseUrl = "https://aws.customquoter.co.uk";
        String endpoint = "/api/m/jobs/schedules/today";
        String token = "3805|2NzKCMW8T6zH7sA25uEhxX2BOi1nzsqvvI2CRao4";
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
                    Gson gson = new Gson();

                    // Parse the JSON into a TodoResponse object
                    TodoResponse todoResponse = gson.fromJson(jsonResponse, TodoResponse.class);

                    // Pass the paginated list of Todo objects to the callback
                    if (todoResponse != null && todoResponse.getData() != null && !todoResponse.getData().isEmpty()) {
                        callback.onDataFetched(todoResponse.getData());
                    } else {
                        callback.onError("No jobs found.");
                    }
                } else {
                    callback.onError("Request Failed: " + response.code());
                }
            }
        });
    }
}
