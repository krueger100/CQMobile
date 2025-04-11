package com.example.cq_mobile.ui.home.HomeFolder.NotesNFilesAPI_folder.NotesFolder;

import android.content.Context;
import android.util.Log;

import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenApiService;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenResponse;
import com.example.cq_mobile.HelperManagers.getAccessToken.RetrofitClientAccessToken;
import com.example.cq_mobile.LoginFolder.AuthManager;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
public class UpdateNoteApiManager {

    private static final String TAG = "UpdateNoteApiManager";

    public interface ApiCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public static void updateNote(Context context, String scheduleId, String note, ApiCallback callback) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new ApiUpdateNoteTask(context, scheduleId, note, callback));
    }

    private static class ApiUpdateNoteTask implements Runnable {
        private final String scheduleId;
        private final String note;
        private final ApiCallback callback;
        Context context;

        public ApiUpdateNoteTask(Context context, String scheduleId, String note, ApiCallback callback) {
            this.scheduleId = scheduleId;
            this.note = note;
            this.context = context;
            this.callback = callback;
        }

        @Override
        public void run() {
            String baseUrl = "https://cqbms.app"; // or another base URL
            String endpoint = String.format("/api/m/jobs/schedules/%s/notes", scheduleId);
            String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";
            String accessToken = AuthManager.getInstance(context).getToken(); // Retrieve token from AuthManager
            String jsonBody = String.format("{\"note\": \"%s\"}", note);

            if (accessToken == null || accessToken.isEmpty()) {
                Log.e(TAG, "Access token is null or empty.");
                callback.onFailure("Access token is null or empty.");
                return;
            }

            // Proceed with updating the note using the retrieved token
            postUpdateNote(baseUrl, endpoint, accessToken, apiKey, jsonBody);
        }

        private void postUpdateNote(String baseUrl, String endpoint, String accessToken, String apiKey, String jsonBody) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            // Update the way RequestBody is created
            RequestBody body = RequestBody.create(MediaType.get("application/json"), jsonBody);

            Request request = new Request.Builder()
                    .url(baseUrl + endpoint)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("x-api-key", apiKey)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    String responseBody = null;
                    try {
                        if (response.body() != null) {
                            responseBody = response.body().string();
                        }
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Note updated successfully. Response: " + responseBody);
                            callback.onSuccess();
                        } else {
                            Log.e(TAG, "Request Failed: " + response.code() + " - " + response.message());
                            Log.e(TAG, "Error Body: " + responseBody);
                            callback.onFailure("Failed to update note: " + responseBody);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading response: " + e.getMessage(), e);
                        callback.onFailure("Error reading response: " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e(TAG, "Error updating note: " + e.getMessage(), e);
                    callback.onFailure("Error updating note: " + e.getMessage());
                }
            });
        }
    }
}
