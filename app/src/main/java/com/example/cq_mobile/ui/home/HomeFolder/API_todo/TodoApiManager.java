package com.example.cq_mobile.ui.home.HomeFolder.API_todo;

import android.content.Context;
import android.util.Log;

import com.example.cq_mobile.LoginFolder.AuthManager;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TodoApiManager {

    public interface ApiResponseCallback {
        void onDataFetched(List<Todo> data);
        void onError(String error);
    }

    public static void fetchApiDataPaginated(Context context, int page, int pageSize, ApiResponseCallback callback) {
        AuthManager authManager = AuthManager.getInstance(context);

        if (!authManager.isLoggedIn() || authManager.isTokenExpired()) {
            callback.onError("Authentication token is missing or expired. Please log in again.");
            return;
        }

        String accessToken = authManager.getToken();  // Decrypted access token
        String baseUrl = "https://cqbms.app";
        String endpoint = "/api/m/jobs/schedules/today";
        String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";

        String url = String.format("%s%s?page=%d&per_page=%d&status=todo", baseUrl, endpoint, page, pageSize);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("x-api-key", apiKey)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String message;
                if (e instanceof SocketTimeoutException) {
                    message = "Request timed out. Please check your internet connection.";
                } else if (e instanceof UnknownHostException) {
                    message = "No internet connection. Please check your network.";
                } else {
                    message = "Network error: " + e.getMessage();
                }

                Log.e("API Failure", message, e);
                postToMainThread(() -> callback.onError(message));
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if (!response.isSuccessful()) {
                        postToMainThread(() -> callback.onError("Server error: " + response.code() + " - " + response.message()));
                        return;
                    }

                    ResponseBody responseBody = response.body();
                    if (responseBody == null) {
                        postToMainThread(() -> callback.onError("Empty response from server."));
                        return;
                    }

                    String jsonResponse = responseBody.string();
                    Log.d("TodoApiManager -> Raw", jsonResponse);

                    if (jsonResponse.trim().startsWith("<!DOCTYPE html") || jsonResponse.trim().startsWith("<html")) {
                        postToMainThread(() -> callback.onError("Unexpected HTML response. Possibly unauthorized or wrong endpoint."));
                        return;
                    }

                    Gson gson = new Gson();
                    TodoResponse todoResponse = gson.fromJson(jsonResponse, TodoResponse.class);

                    if (todoResponse != null && todoResponse.getData() != null && !todoResponse.getData().isEmpty()) {
                        postToMainThread(() -> callback.onDataFetched(todoResponse.getData()));
                    } else {
                        postToMainThread(() -> callback.onError("No jobs found."));
                    }

                } catch (JsonSyntaxException | JsonIOException e) {
                    Log.e("JSON Error", "Failed to parse JSON: " + e.getMessage(), e);
                    postToMainThread(() -> callback.onError("Invalid JSON received from server."));
                } catch (Exception e) {
                    Log.e("General Error", "Unexpected error: " + e.getMessage(), e);
                    postToMainThread(() -> callback.onError("Unexpected error: " + e.getMessage()));
                } finally {
                    response.close();
                }
            }
        });
    }

    private static void postToMainThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }
}
