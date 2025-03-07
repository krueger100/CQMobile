package com.example.cq_mobile.NotificationData;

import android.content.Context;
import android.util.Log;

import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenApiService;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenResponse;
import com.example.cq_mobile.HelperManagers.getAccessToken.RetrofitClientAccessToken;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatAPIItem;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatAPIResponse;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatApi;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatManager;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationsChatManager {
    private final String baseUrl = "https://aws.customquoter.co.uk/";  // Base URL
    private String accessToken;  // Store the access token
    private final Context context;
    Call<AccessTokenResponse> call;
    Call<ChatAPIResponse> call2;

    public NotificationsChatManager(Context context) {
        this.context = context;
    }


    public void loadNotifChats(int page, int pageSize, final ChatManager.AllChatsCallback callback) {
        if (accessToken == null) {
            callback.onError("Access token is missing.");
            return;
        }


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        ChatApi chatApi = retrofit.create(ChatApi.class);
        call2 = chatApi.getChats(page, pageSize, accessToken, "Bearer " + accessToken);

        call2.enqueue(new Callback<ChatAPIResponse>() {
            @Override
            public void onResponse(Call<ChatAPIResponse> call2, Response<ChatAPIResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String rawJson = new Gson().toJson(response.body()); // Convert response to JSON string
                    Log.d("ChatManager", "Raw JSON Response: " + rawJson);

                    if (response.body().getData() == null) {
                        Log.e("ChatManager", "getData() is null! Check API response structure.");
                        Log.d("ChatManager", "Extracted Access Token: " + accessToken);

                        return;
                    }

                    List<ChatAPIItem> chats = response.body().getData().getChats();
                    if (chats == null || chats.isEmpty()) {
                        Log.e("ChatManager", "Chat list is null or empty!");
                        return;
                    }



                    callback.onAllChatsLoaded(chats, rawJson);  // Pass both chats and rawJson
                } else {
                    Log.e("ChatManager", "API Response error: " + response.message());
                }
            }



            @Override
            public void onFailure(Call<ChatAPIResponse> call2, Throwable t) {
                // Handle the failure
                callback.onError(t.getMessage());
            }
        });
    }
    public void cancelAccessTokenCall() {
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }
    }
    public void cancelChatLoadingCall() {
        if (call2 != null && !call2.isCanceled()) {
            call2.cancel();
        }
    }

    /**
     * Cancels both API calls if they are in progress.
     */
    public void cancelAllCalls() {
        cancelAccessTokenCall();
        cancelChatLoadingCall();
    }


    // Callback interfaces
    public interface AccessTokenCallback {
        void onAccessTokenReceived(String accessToken);
        void onError(String errorMessage);
    }

    public interface AllChatsCallback {
        void onAllChatsLoaded(List<ChatAPIItem> chats, String rawJson);
        void onError(String errorMessage);
    }

}
