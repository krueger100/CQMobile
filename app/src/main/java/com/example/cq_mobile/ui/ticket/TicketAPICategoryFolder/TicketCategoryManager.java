package com.example.cq_mobile.ui.ticket.TicketAPICategoryFolder;

import android.content.Context;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TicketCategoryManager {
    private final String baseUrl = "https://aws.customquoter.co.uk/";  // Base URL
    private final String accessToken;  // Store the access token
    private final Context context;
    private Call<TicketAPICategoryResponse> currentCall;
    // Updated constructor to accept accessToken
    public TicketCategoryManager(Context context, String accessToken) {
        this.context = context;
        this.accessToken = accessToken;
    }

    public void loadCategoryTickets(int page, int pageSize, final TicketsCallback callback) {
        // Construct the proper URL with the provided parameters
        String url = baseUrl + "api/m/tickets/categories?page=" + page + "&per_page=" + pageSize;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TicketCategoryApi ticketCategoryApi = retrofit.create(TicketCategoryApi.class);

        // Making the API call with additional parameters
        currentCall = ticketCategoryApi.getTickets(page, pageSize, accessToken, "Bearer " + accessToken);

        currentCall.enqueue(new Callback<TicketAPICategoryResponse>() {
            @Override
            public void onResponse(Call<TicketAPICategoryResponse> currentCall, Response<TicketAPICategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Successfully received the tickets
                    callback.onTicketsLoaded(response.body().getData());
                } else {
                    // Handle the error
                    String errorMessage = response.message() != null ? response.message() : "Unknown error";
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<TicketAPICategoryResponse> call, Throwable t) {
                // Handle the failure
                callback.onError(t.getMessage());
            }
        });
    }
    public void cancelLoading() {
        if (currentCall != null && !currentCall.isCanceled()) {
            currentCall.cancel();
        }
    }


    public interface TicketsCallback {
        void onTicketsLoaded(List<TicketAPICategoryItems> tickets);
        void onError(String errorMessage);
    }

}
