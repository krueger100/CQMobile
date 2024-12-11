package com.example.cq_mobile.ui.home.HomeFolder.API_done;
import com.google.gson.Gson;
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
         public static void fetchDoneApiData(ApiResponseCallback callback) {
            String baseUrl = "https://aws.customquoter.co.uk";
            String endpoint = "/api/m/jobs/schedules/today?page=1&per_page=100&status=done";
            String token = "3805|2NzKCMW8T6zH7sA25uEhxX2BOi1nzsqvvI2CRao4";
            String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";

            // Construct the full URL
            String url = String.format("%s%s?page=1&per_page=100&status=done", baseUrl, endpoint);

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
                        DoneResponse doneResponse = gson.fromJson(jsonResponse, DoneResponse.class);

                        // Pass the full list of Todo objects to the callback
                        if (doneResponse != null && doneResponse.getData() != null && !doneResponse.getData().isEmpty()) {
                            callback.onDataFetched(doneResponse.getData());
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

