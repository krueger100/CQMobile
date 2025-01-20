package com.example.cq_mobile.ui.ticket.TicketSearchFolder;

import android.content.Context;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TicketSearchManager {
    private final String baseUrl = "https://aws.customquoter.co.uk/";  // Base URL
    private final String accessToken;  // Store the access token
    private final Context context;

    public TicketSearchManager(Context context, String accessToken) {
        this.context = context;
        this.accessToken = accessToken;
    }

    public void loadSearchTickets(int page, int pageSize, final SearchTicketsCallback callback) {
        // Construct the proper URL with the provided parameters
        String url = baseUrl + "api/m/tickets?page=" + page + "&per_page=" + pageSize;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TicketSearchApi ticketApi = retrofit.create(TicketSearchApi.class);

        // Making the API call with additional parameters
        Call<TicketSearchAPIResponse> call = ticketApi.getSearchTickets(page, pageSize, accessToken, "Bearer " + accessToken);

        call.enqueue(new Callback<TicketSearchAPIResponse>() {
            @Override
            public void onResponse(Call<TicketSearchAPIResponse> call, Response<TicketSearchAPIResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Successfully received the tickets
                    callback.onSearchTicketsLoaded(response.body().getData());
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

    public interface SearchTicketsCallback {
        void onSearchTicketsLoaded(List<TicketSearchAPIItem> tickets);
        void onError(String errorMessage);
    }
}
