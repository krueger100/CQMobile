package com.example.cq_mobile.ui.chat.ChatFolder;

import android.content.Context;
import android.util.Log;

import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenResponse;
import com.example.cq_mobile.LoginFolder.AuthManager;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatManager {
    private final String baseUrl = "https://cqbms.app";
    private final Context context;
    private Call<ChatAPIResponse> call2;

    public ChatManager(Context context) {
        this.context = context;
    }

    public void loadChats(int page, int pageSize, final AllChatsCallback callback) {
        AuthManager authManager = AuthManager.getInstance(context);
        String accessToken = authManager.getToken();

        if (accessToken == null || accessToken.isEmpty()) {
            callback.onError("Access token is missing or invalid.");
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
            public void onResponse(Call<ChatAPIResponse> call, Response<ChatAPIResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String rawJson = new Gson().toJson(response.body());
                    Log.d("ChatManager", "Raw JSON Response: " + rawJson);

                    if (response.body().getData() == null) {
                        Log.e("ChatManager", "getData() is null! Check API response structure.");
                        return;
                    }

                    List<ChatAPIItem> chats = response.body().getData().getChats();
                    if (chats == null || chats.isEmpty()) {
                        Log.e("ChatManager", "Chat list is null or empty!");
                        return;
                    }

                    callback.onAllChatsLoaded(chats, rawJson);
                } else {
                    Log.e("ChatManager", "API Response error: " + response.message());
                    callback.onError("Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ChatAPIResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void cancelChatLoadingCall() {
        if (call2 != null && !call2.isCanceled()) {
            call2.cancel();
        }
    }

    public void cancelAllCalls() {
        cancelChatLoadingCall();
    }

    public interface AllChatsCallback {
        void onAllChatsLoaded(List<ChatAPIItem> chats, String rawJson);
        void onError(String errorMessage);
    }
}



