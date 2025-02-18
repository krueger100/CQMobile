package com.example.cq_mobile.UserDetailsFolder;

import android.content.Context;
import android.util.Log;

import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenApiService;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenResponse;
import com.example.cq_mobile.HelperManagers.getAccessToken.RetrofitClientAccessToken;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UseDetails {
    private final String baseUrl = "https://aws.customquoter.co.uk/";  // Base URL
    private String accessToken;  // Store the access token
    private final Context context;
    Call<AccessTokenResponse> call;
    Call<UserDetailsAPIResponse> call2;

    public UseDetails(Context context) {
        this.context = context;
    }

    // Method to get the access token and notify the caller via a callback
    public void getAccessToken(AccessTokenRequest request, final AccessTokenCallback callback) {
        // Create an instance of the API service
        AccessTokenApiService apiService = RetrofitClientAccessToken.getRetrofitInstance().create(AccessTokenApiService.class);

        // Call the API
        call = apiService.AccessTokenUser(request);

        // Enqueue the call to execute asynchronously
        call.enqueue(new Callback<AccessTokenResponse>() {
            @Override
            public void onResponse(Call<AccessTokenResponse> call, Response<AccessTokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AccessTokenResponse accessTokenResponse = response.body();
                    accessToken = accessTokenResponse.getAccessToken();

                    // Check if the access token was fetched successfully
                    if (accessToken != null) {
                        Log.d("UseDetails", "Access Token: " + accessToken);
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
                // Log the failure (e.g., network error)
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }

    public void loadUseDetails(int page, int pageSize, final AllUserCallback callback) {
        if (accessToken == null) {
            callback.onError("Access token is missing.");
            return;
        }


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        UserApi userApi = retrofit.create(UserApi.class);
        call2 = userApi.getUser(page, pageSize, accessToken, "Bearer " + accessToken);

        call2.enqueue(new Callback<UserDetailsAPIResponse>() {
            @Override
            public void onResponse(Call<UserDetailsAPIResponse> call2, Response<UserDetailsAPIResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String rawJson = new Gson().toJson(response.body()); // Convert response to JSON string
                    Log.d("UseDetails", "Raw JSON Response: " + rawJson);

                    if (response.body().getData() == null) {
                        Log.e("UseDetails", "getData() is null! Check API response structure.");
                        return;
                    }

                    List<UseDetails> chats = response.body().getData().getUser();
                    if (chats == null || chats.isEmpty()) {
                        Log.e("UseDetails", "Chat list is null or empty!");
                        return;
                    }



                    callback.onAllUseDetailsLoaded(chats, rawJson);  // Pass both chats and rawJson
                } else {
                    Log.e("UseDetails", "API Response error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<UserDetailsAPIResponse> call2, Throwable t) {
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

    /**
     * Cancels the chat loading API call.
     */
    public void cancelUseDetailsLoadingCall() {
        if (call2 != null && !call2.isCanceled()) {
            call2.cancel();
        }
    }

    /**
     * Cancels both API calls if they are in progress.
     */
    public void cancelAllCalls() {
        cancelAccessTokenCall();
        cancelUseDetailsLoadingCall();
    }

    // Callback interfaces
    public interface AccessTokenCallback {
        void onAccessTokenReceived(String accessToken);
        void onError(String errorMessage);
    }

    public interface AllUserCallback {
        void onAllUseDetailsLoaded(List<UseDetails> chats, String rawJson);
        void onError(String errorMessage);
    }

}



