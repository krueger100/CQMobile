package com.example.cq_mobile.ui.ticket.ReplyTicketFolder.TicketRepliesFolder;

import android.content.Context;
import android.util.Log;

import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenApiService;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenResponse;
import com.example.cq_mobile.HelperManagers.getAccessToken.RetrofitClientAccessToken;
import com.example.cq_mobile.ui.ticket.TicketSearchFolder.TicketSearchAPIItem;
import com.example.cq_mobile.ui.ticket.TicketSearchFolder.TicketSearchAPIResponse;
import com.example.cq_mobile.ui.ticket.TicketSearchFolder.TicketSearchApi;
import com.example.cq_mobile.ui.ticket.TicketSearchFolder.TicketSearchManager;
import com.example.cq_mobile.ui.ticket.TicketsFolder.TicketAPIItem;
import com.example.cq_mobile.ui.ticket.TicketsFolder.TicketAPIResponse;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TicketRepliesManager {
    private final String baseUrl = "https://aws.customquoter.co.uk/";  // Base URL
    private String accessToken;  // Store the access token
    private final Context context;
    Call<AccessTokenResponse> call;
    Call<TicketAPIResponse> call2 ;

    public TicketRepliesManager(Context context) {
        this.context = context;
    }
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


    public void loadRepliesTickets(int page, int pageSize, final TicketRepliesManager.RepliesTicketsCallback callback) {
        if (accessToken == null) {
            callback.onError("Access token is missing.");
            return;
        }
        String url = baseUrl + "api/m/tickets/" + "?page=" + page + "&per_page=" + pageSize;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TicketSearchApi ticketApi = retrofit.create(TicketSearchApi.class);

        // Making the API call with additional parameters
        Call<TicketSearchAPIResponse> call = ticketApi.getSearchTickets( page, pageSize, accessToken, "Bearer " + accessToken);

        call.enqueue(new Callback<TicketSearchAPIResponse>() {
            @Override
            public void onResponse(Call<TicketSearchAPIResponse> call, Response<TicketSearchAPIResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Successfully received the tickets
                    callback.onRepliesTicketsLoaded(response.body().getData());
                } else {
                    // Handle the error
                    String errorMessage = response.message() != null ? response.message() : "Unknown error";
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<TicketSearchAPIResponse> call, Throwable t) {
                // Handle the failure
                callback.onError(t.getMessage());
            }
        });
    }


    public void loadTicketsReplies(int page, int pageSize, final AllTicketsCallback callback) {
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

        TricketRepliesAPI ticketApi = retrofit.create(TricketRepliesAPI.class);

        // Making the API call with additional parameters
        call2 = ticketApi.getReplies(page, pageSize, accessToken, "Bearer " + accessToken);

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

    public interface RepliesTicketsCallback {
        void onRepliesTicketsLoaded(List<TicketSearchAPIItem> tickets);
        void onError(String errorMessage);
    }

    public interface AllTicketsCallback {
        void onAllTicketsLoaded(List<TicketAPIItem> tickets);
        void onError(String errorMessage);
    }
}
