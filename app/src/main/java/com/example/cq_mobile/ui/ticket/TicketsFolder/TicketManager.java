package com.example.cq_mobile.ui.ticket.TicketsFolder;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenApiService;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenResponse;
import com.example.cq_mobile.HelperManagers.getAccessToken.RetrofitClientAccessToken;
import com.example.cq_mobile.LoginFolder.AuthManager;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class TicketManager {
    private final String baseUrl = "https://cqbms.app"; // Base URL
    private final Context context;
    private Call<TicketAPIResponse> call2;  // Store the current API call

    public TicketManager(Context context) {
        this.context = context;
    }

    public void loadTickets(int page, int pageSize, final AllTicketsCallback callback) {
        AuthManager authManager = AuthManager.getInstance(context);

        // Check if the user is logged in and if the token is valid
        if (!authManager.isLoggedIn() || authManager.isTokenExpired()) {
            callback.onError("Access token is missing or expired.");
            return;
        }

        String accessToken = authManager.getToken();
        if (accessToken == null) {
            callback.onError("Access token is missing.");
            return;
        }

        // Construct the URL for the API request
        String url = baseUrl + "/api/m/tickets/categories?page=" + page + "&per_page=" + pageSize;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TicketApi ticketApi = retrofit.create(TicketApi.class);

        // Making the API call with the Bearer token
        call2 = ticketApi.getTickets(page, pageSize, accessToken, "Bearer " + accessToken);

        // Execute the request asynchronously
        call2.enqueue(new Callback<TicketAPIResponse>() {
            @Override
            public void onResponse(Call<TicketAPIResponse> call, Response<TicketAPIResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onAllTicketsLoaded(response.body().getData());
                } else {
                    // Handle the error (e.g., response not successful)
                    String errorMessage = response.message() != null ? response.message() : "Unknown error";
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<TicketAPIResponse> call, Throwable t) {
                // Handle the failure (e.g., network error)
                callback.onError(t.getMessage());
            }
        });
    }

    // Cancel the ongoing ticket loading call
    public void cancelTicketLoadingCall() {
        if (call2 != null && !call2.isCanceled()) {
            call2.cancel();
        }
    }

    // Cancel all ongoing API calls
    public void cancelAllCalls() {
        cancelTicketLoadingCall();
    }

    // Callback interface for loading all tickets
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