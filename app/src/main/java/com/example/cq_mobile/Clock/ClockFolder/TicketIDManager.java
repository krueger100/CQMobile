package com.example.cq_mobile.Clock.ClockFolder;

import android.content.Context;

import com.example.cq_mobile.ui.ticket.TicketsFolder.TicketAPIItem;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketIDManager {
    private static final String TAG = "TicketIDManager";
    private TicketIDApi ticketIDApi;

    // ✅ Constructor accepting TicketIDApi
    public TicketIDManager(Context context) {
        this.ticketIDApi = RetrofitClient.getInstance().create(TicketIDApi.class);
    }

    public interface TicketCallback {
        void onTicketLoaded(TicketAPIItem ticket);
        void onError(String errorMessage);
    }

    public void fetchTicketById(int ticketId, String token, TicketCallback callback) {
        Log.d(TAG, "Fetching ticket with ID: " + ticketId);

        Call<TicketAPIItem> call = ticketIDApi.getTicketById(
                ticketId,
                "Bearer " + token,
                "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2", // API Key
                "application/json",
                "application/json",
                "PostmanRuntime/7.43.0",
                "gzip, deflate, br",
                "keep-alive"
        );

        call.enqueue(new Callback<TicketAPIItem>() {
            @Override
            public void onResponse(Call<TicketAPIItem> call, Response<TicketAPIItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Successfully retrieved ticket: " + response.body().getId());
                    callback.onTicketLoaded(response.body());
                } else {
                    Log.e(TAG, "Failed to fetch ticket: " + response.code());
                    callback.onError("Failed to fetch ticket: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TicketAPIItem> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage());
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
}

