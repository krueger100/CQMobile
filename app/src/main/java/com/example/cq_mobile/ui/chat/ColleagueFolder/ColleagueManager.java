package com.example.cq_mobile.ui.chat.ColleagueFolder;


import android.content.Context;
import android.util.Log;

import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenApiService;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenResponse;
import com.example.cq_mobile.HelperManagers.getAccessToken.RetrofitClientAccessToken;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatAPIItem;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ColleagueManager {
    private final String baseUrl = "https://aws.customquoter.co.uk/";
    private String accessToken;
    private final Context context;
    private Call<AccessTokenResponse> call;
    private Call<ColleagueAPIResponse> call2;

    public ColleagueManager(Context context) {
        this.context = context;
    }

    public void getAccessToken(AccessTokenRequest request, final AccessTokenCallback callback) {
        AccessTokenApiService apiService = RetrofitClientAccessToken.getRetrofitInstance().create(AccessTokenApiService.class);
        call = apiService.AccessTokenUser(request);
        call.enqueue(new Callback<AccessTokenResponse>() {
            @Override
            public void onResponse(Call<AccessTokenResponse> call, Response<AccessTokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    accessToken = response.body().getAccessToken();
                    if (accessToken != null) {
                        Log.d("ColleagueManager", "Access Token: " + accessToken);
                        callback.onAccessTokenReceived(accessToken);
                    } else {
                        callback.onError("Access token not received.");
                    }
                } else {
                    callback.onError("Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<AccessTokenResponse> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }

    public void loadColleagues(int page, int pageSize, final AllColleaguesCallback callback) {
        if (accessToken == null || accessToken.isEmpty()) {
            callback.onError("Access token is missing.");
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ColleagueApi colleagueApi = retrofit.create(ColleagueApi.class);
        call2 = colleagueApi.getColleagues(page, pageSize,accessToken, "Bearer " + accessToken);

        call2.enqueue(new Callback<ColleagueAPIResponse>() {
            @Override
            public void onResponse(Call<ColleagueAPIResponse> call2, Response<ColleagueAPIResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String rawJson = new Gson().toJson(response.body());
                    Log.d("ColleagueManager", "Raw JSON Response: " + rawJson);

                    if (response.body().getData() == null) {
                        Log.e("ColleagueManager", "getData() is null! Check API response structure.");
                        return;
                    }
                    List<ColleagueAPIItem> colleague = response.body().getData().getContacts();
                    if (colleague == null || colleague.isEmpty()) {
                        Log.e("ColleagueManager", "Chat list is null or empty!");
                        return;
                    }


                    callback.onAllColleaguesLoaded(colleague, rawJson);  // Pass both chats and rawJson

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


    public void cancelAccessTokenCall() {
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }
    }

    public void cancelColleagueLoadingCall() {
        if (call2 != null && !call2.isCanceled()) {
            call2.cancel();
        }
    }

    public void cancelAllCalls() {
        cancelAccessTokenCall();
        cancelColleagueLoadingCall();
    }

    public interface AccessTokenCallback {
        void onAccessTokenReceived(String accessToken);
        void onError(String errorMessage);
    }

    public interface AllColleaguesCallback {
        void onAllColleaguesLoaded(List<ColleagueAPIItem> colleagues, String rawJson);
        void onError(String errorMessage);
    }
}
