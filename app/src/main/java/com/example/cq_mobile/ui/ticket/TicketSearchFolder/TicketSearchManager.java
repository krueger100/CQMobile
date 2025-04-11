package com.example.cq_mobile.ui.ticket.TicketSearchFolder;

import android.content.Context;
import android.util.Log;

import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenApiService;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenResponse;
import com.example.cq_mobile.HelperManagers.getAccessToken.RetrofitClientAccessToken;
import com.example.cq_mobile.LoginFolder.AuthManager;
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
    private final String baseUrl = "https://cqbms.app";
    private String accessToken;
    private final Context context;
    private Call<TicketSearchAPIResponse> call;

    public TicketSearchManager(Context context) {
        this.context = context;
    }

    public void loadSearchTickets(int page, int pageSize, String search, int categoryId, final SearchTicketsCallback callback) {
        accessToken = AuthManager.getInstance(context).getToken();
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

        call = ticketApi.getSearchTickets(
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
