package com.example.cq_mobile.ui.ticket.TicketsFolder;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenApiService;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenResponse;
import com.example.cq_mobile.HelperManagers.getAccessToken.GetUserInfoManager;
import com.example.cq_mobile.HelperManagers.getAccessToken.RetrofitClientAccessToken;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TicketManager {
    private final String baseUrl = "https://cqbms.app";//"https://aws.customquoter.co.uk/";  // Base URL
    private String accessToken;  // Store the access token
    private final Context context;
    Call<AccessTokenResponse> call;
    Call<TicketAPIResponse> call2 ;
    public TicketManager(Context context) {
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
                        Log.d("TicketManager", "Access Token: " + accessToken);
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

    // Method to load tickets using the access token
    public void loadTickets(int page, int pageSize, final AllTicketsCallback callback) {
        if (accessToken == null) {
            callback.onError("Access token is missing.");
            return;
        }

        // Construct the proper URL with the provided parameters
        String url = baseUrl + "api/m/tickets/categories?page=" + page + "&per_page=" + pageSize;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TicketApi ticketApi = retrofit.create(TicketApi.class);

        // Making the API call with additional parameters
       call2 = ticketApi.getTickets(page, pageSize, accessToken, "Bearer " + accessToken);

        call2.enqueue(new Callback<TicketAPIResponse>() {
            @Override
            public void onResponse(Call<TicketAPIResponse> call2, Response<TicketAPIResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    callback.onAllTicketsLoaded(response.body().getData());
                } else {
                    // Handle the error
                    String errorMessage = response.message() != null ? response.message() : "Unknown error";
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<TicketAPIResponse> call2, Throwable t) {
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
     * Cancels the ticket loading API call.
     */
    public void cancelTicketLoadingCall() {
        if (call2 != null && !call2.isCanceled()) {
            call2.cancel();
        }
    }

    /**
     * Cancels both API calls if they are in progress.
     */
    public void cancelAllCalls() {
        cancelAccessTokenCall();
        cancelTicketLoadingCall();
    }

    // Callback interfaces
    public interface AccessTokenCallback {
        void onAccessTokenReceived(String accessToken);
        void onError(String errorMessage);
    }

    public interface AllTicketsCallback {
        void onAllTicketsLoaded(List<TicketAPIItem> tickets);
        void onError(String errorMessage);
    }
}
/*
curl -X GET "https://aws.customquoter.co.uk/api/m/tickets/117" \
-H "Authorization: Bearer 5623|qi1c6mlU56torLCTinGwoaeyqqm9Ocxn0nTZX63W" \
-H "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2" \
-H "Accept: application/json" \
-H "Content-Type: application/json" \
-H "User-Agent: PostmanRuntime/7.43.0" \
-H "Accept-Encoding: gzip, deflate, br" \
-H "Connection: keep-alive"



 */