package com.example.cq_mobile.ui.chat.ChatNotif;

import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatsNotificationsApiManager {
    private static final String TAG = "ChatsNotificationsApiManager";
    private static final String BASE_URL = "https://aws.customquoter.co.uk";
    private static Retrofit retrofit = null;

    public interface ApiCallback {
        void onSuccess(NotificationAPIResponse response);
        void onFailure(String error);
    }

    private static NotifApi getNotifApi() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(NotifApi.class);
    }

    public static void fetchChatNotifications(String accessToken, ApiCallback callback) {
        NotifApi apiService = getNotifApi();
        Call<NotificationAPIResponse> call = apiService.getNotifications("Bearer " + accessToken);

        call.enqueue(new Callback<NotificationAPIResponse>() {
            @Override
            public void onResponse(Call<NotificationAPIResponse> call, Response<NotificationAPIResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Notifications fetched successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorResponse = response.errorBody() != null ? response.errorBody().toString() : "Unknown error";
                    Log.e(TAG, "Request Failed: " + response.code() + " - " + errorResponse);
                    callback.onFailure("Failed to fetch notifications: " + errorResponse);
                }
            }

            @Override
            public void onFailure(Call<NotificationAPIResponse> call, Throwable t) {
                Log.e(TAG, "Error fetching notifications: " + t.getMessage(), t);
                callback.onFailure("Error fetching notifications: " + t.getMessage());
            }
        });
    }
}
