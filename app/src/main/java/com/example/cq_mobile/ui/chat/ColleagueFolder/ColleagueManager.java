package com.example.cq_mobile.ui.chat.ColleagueFolder;


import android.content.Context;
import android.util.Log;

import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenApiService;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenResponse;
import com.example.cq_mobile.HelperManagers.getAccessToken.RetrofitClientAccessToken;
import com.example.cq_mobile.LoginFolder.AuthManager;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatAPIItem;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatAPIResponse;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatApi;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ColleagueManager {
    private final String baseUrl = "https://cqbms.app";
    private final Context context;
    private Call<ColleagueAPIResponse> call2;
    private Call<ChatAPIResponse> call3;

    public ColleagueManager(Context context) {
        this.context = context;
    }

    public void loadColleagues(int page, int pageSize, final AllColleaguesCallback callback) {
        AuthManager authManager = AuthManager.getInstance(context);
        String accessToken = authManager.getToken();

        if (accessToken == null || accessToken.isEmpty()) {
            callback.onError("Access token is missing.");
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ColleagueApi colleagueApi = retrofit.create(ColleagueApi.class);
        call2 = colleagueApi.getColleagues(page, pageSize, accessToken, "Bearer " + accessToken);

        call2.enqueue(new Callback<ColleagueAPIResponse>() {
            @Override
            public void onResponse(Call<ColleagueAPIResponse> call, Response<ColleagueAPIResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String rawJson = new Gson().toJson(response.body());
                    Log.d("ColleagueManager", "Raw JSON Response: " + rawJson);

                    if (response.body().getData() == null) {
                        Log.e("ColleagueManager", "getData() is null! Check API response structure.");
                        return;
                    }

                    List<ColleagueAPIItem> colleague = response.body().getData().getContacts();
                    if (colleague == null || colleague.isEmpty()) {
                        Log.e("ColleagueManager", "Colleague list is null or empty!");
                        return;
                    }

                    for (ColleagueAPIItem item : colleague) {
                        Log.d("ColleagueManager", "Colleague: " + item.getName()
                                + ", Channel: " + item.getChannel());
                    }

                    callback.onAllColleaguesLoaded(colleague, rawJson);
                } else {
                    Log.e("ColleagueManager", "API Response error: " + response.message());
                    callback.onError("Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ColleagueAPIResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void loadChats(int page, int pageSize, final AllChatsCallback callback) {
        AuthManager authManager = AuthManager.getInstance(context);
        String accessToken = authManager.getToken();

        if (accessToken == null || accessToken.isEmpty()) {
            callback.onError("Access token is missing.");
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ChatApi chatApi = retrofit.create(ChatApi.class);
        call3 = chatApi.getChats(page, pageSize, accessToken, "Bearer " + accessToken);

        call3.enqueue(new Callback<ChatAPIResponse>() {
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

    public void cancelColleagueLoadingCall() {
        if (call2 != null && !call2.isCanceled()) {
            call2.cancel();
        }
    }

    public void cancelAllCalls() {
        cancelColleagueLoadingCall();
        if (call3 != null && !call3.isCanceled()) {
            call3.cancel();
        }
    }

    public interface AllColleaguesCallback {
        void onAllColleaguesLoaded(List<ColleagueAPIItem> colleagues, String rawJson);
        void onError(String errorMessage);
    }

    public interface AllChatsCallback {
        void onAllChatsLoaded(List<ChatAPIItem> chats, String rawJson);
        void onError(String errorMessage);
    }
}
