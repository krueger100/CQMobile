package com.example.cq_mobile.ui.home.HomeFolder.NotesNFilesAPI_folder.NotesFolder;

import android.content.Context;
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
import retrofit2.Call;
import retrofit2.Callback;

public class UpdateNoteApiManager {

    private static final String TAG = "UpdateNoteApiManager";

    public interface ApiCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public static void updateNote(Context context, String email, String password, String scheduleId, String note, ApiCallback callback) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new ApiUpdateNoteTask(context,email, password, scheduleId, note, callback));
    }

    private static class ApiUpdateNoteTask implements Runnable {
        private final String scheduleId;
        private final String note;
        private final ApiCallback callback;
        private final String email;
        private final String password;
        Context context;
        public ApiUpdateNoteTask(Context context, String email, String password, String scheduleId, String note, ApiCallback callback) {
            this.email = email;
            this.password = password;
            this.scheduleId = scheduleId;
            this.note = note;
            this.context = context;
            this.callback = callback;
        }

        @Override
        public void run() {
            String baseUrl = "https://cqbms.app";//"https://aws.customquoter.co.uk";
            String endpoint = String.format("/api/m/jobs/schedules/%s/notes", scheduleId);
            String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";

            String jsonBody = String.format("{\"note\": \"%s\"}", note);
            AccessTokenRequest request1 = new AccessTokenRequest(context,email, password);
            AccessTokenApiService apiService = RetrofitClientAccessToken.getRetrofitInstance().create(AccessTokenApiService.class);
            Call<AccessTokenResponse> call = apiService.AccessTokenUser(request1);

            call.enqueue(new Callback<AccessTokenResponse>() {
                @Override
                public void onResponse(Call<AccessTokenResponse> call, retrofit2.Response<AccessTokenResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String accessToken = response.body().getAccessToken();
                        Log.d(TAG, "Access Token: " + accessToken);
                        postUpdateNote(baseUrl, endpoint, accessToken, apiKey, jsonBody);
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

        private void postUpdateNote(String baseUrl, String endpoint, String accessToken, String apiKey, String jsonBody) {
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
