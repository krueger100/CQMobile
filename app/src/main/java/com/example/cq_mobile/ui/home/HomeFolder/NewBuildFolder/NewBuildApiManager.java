package com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class NewBuildApiManager {

    public interface ApiResponseCallback {
        void onDataFetched(List<Taskmain> data);
        void onError(String error);
    }

    public static void fetchNewBuiltApiData(String jobId, ApiResponseCallback callback) {
        String baseUrl = "https://aws.customquoter.co.uk";
        String endpoint = String.format("/api/m/jobs/schedules/%s", jobId);
        String token = "3817|bEOb2Euof0Wdq9Qi7153VCMovHnhbO8qbEXRIgw6";
        String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";
        String url = String.format("%s%s?page=1&per_page=100&status=todo", baseUrl, endpoint);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("x-api-key", apiKey)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Use Handler to switch to the main thread for the error callback
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    Gson gson = new Gson();
                    TaskmainResponse taskmainResponse = gson.fromJson(jsonResponse, TaskmainResponse.class);

                    if (taskmainResponse != null && taskmainResponse.getData() != null) {
                        // Wrap the task into a list
                        List<Taskmain> taskmainList = new ArrayList<>();
                        taskmainList.add(taskmainResponse.getData());

                        // Switch to the main thread before invoking the callback
                        new Handler(Looper.getMainLooper()).post(() -> callback.onDataFetched(taskmainList));
                    } else {
                        new Handler(Looper.getMainLooper()).post(() -> callback.onError("No data found."));
                    }
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError("Request Failed: " + response.code()));
                }
            }
        });
    }
}

