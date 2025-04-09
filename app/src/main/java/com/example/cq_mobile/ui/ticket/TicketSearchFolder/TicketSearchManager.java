package com.example.cq_mobile.ui.ticket.TicketSearchFolder;

import android.content.Context;
import android.util.Log;

import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenApiService;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenResponse;
import com.example.cq_mobile.HelperManagers.getAccessToken.RetrofitClientAccessToken;
import com.example.cq_mobile.ui.ticket.TicketsFolder.TicketAPIItem;
import com.example.cq_mobile.ui.ticket.TicketsFolder.TicketAPIResponse;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TicketSearchManager {
    private final String baseUrl = "https://cqbms.app";//"https://aws.customquoter.co.uk/";  // Base URL
    private String accessToken;
    private final Context context;
    private Call<AccessTokenResponse> accessTokenCall;
    Call<TicketAPIResponse> call2 ;

    public TicketSearchManager(Context context) {
        this.context = context;
    }

    public void getAccessToken(String email, String password, final AccessTokenCallback callback) {
        AccessTokenApiService apiService = RetrofitClientAccessToken.getRetrofitInstance().create(AccessTokenApiService.class);

        AccessTokenRequest request = new AccessTokenRequest(context,email, password);
        accessTokenCall = apiService.AccessTokenUser(request);
        accessTokenCall.enqueue(new Callback<AccessTokenResponse>() {
            @Override
            public void onResponse(Call<AccessTokenResponse> call, Response<AccessTokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AccessTokenResponse accessTokenResponse = response.body();
                    accessToken = accessTokenResponse.getAccessToken();
                    if (accessToken != null) {
                        Log.d("TicketSearchManager", "Access Token: " + accessToken);
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

    // Method to load tickets using the access token

    public void loadSearchTickets(int page, int pageSize, String search, int categoryId, final SearchTicketsCallback callback) {
        if (accessToken == null) {
            Log.e("TicketSearchManager", "Access token is missing.");
            callback.onError("Access token is missing.");
            return;
        }
        String url = baseUrl + "api/m/tickets/";

        Log.d("TicketSearchManager", "API URL: " + url);
        Log.d("TicketSearchManager", "Params - Page: " + page + ", PageSize: " + pageSize + ", Search: " + search + ", CategoryID: " + categoryId);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TicketSearchApi ticketApi = retrofit.create(TicketSearchApi.class);

        // Making the API call with additional parameters
        Call<TicketSearchAPIResponse> call = ticketApi.getSearchTickets(
                page,
                pageSize,
                search,
                categoryId,
                accessToken,
                "Bearer " + accessToken
        );

        call.enqueue(new Callback<TicketSearchAPIResponse>() {
            @Override
            public void onResponse(Call<TicketSearchAPIResponse> call, Response<TicketSearchAPIResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("TicketSearchManager", "Successful response: " + new Gson().toJson(response.body()));
                    callback.onSearchTicketsLoaded(response.body().getData());
                } else {
                    String errorMessage = response.message() != null ? response.message() : "Unknown error";
                    Log.e("TicketSearchManager", "API Error: " + errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<TicketSearchAPIResponse> call, Throwable t) {
                Log.e("TicketSearchManager", "API Failure: " + t.getMessage());
                callback.onError(t.getMessage());
            }
        });
    }


    // Callback interfaces
    public interface AccessTokenCallback {
        void onAccessTokenReceived(String accessToken);
        void onError(String errorMessage);
    }

    public interface SearchTicketsCallback {
        void onSearchTicketsLoaded(List<TicketAPIItem> tickets);
        void onError(String errorMessage);
    }
}


