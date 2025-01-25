package com.example.cq_mobile.ui.ticket.TicketSearchFolder;

import android.content.Context;
import android.util.Log;

import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenApiService;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenResponse;
import com.example.cq_mobile.HelperManagers.getAccessToken.RetrofitClientAccessToken;
import com.example.cq_mobile.ui.ticket.TicketsFolder.TicketAPIResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TicketSearchManager {
    private final String baseUrl = "https://aws.customquoter.co.uk/";  // Base URL
    private String accessToken;  // Store the access token
    private final Context context;
    private Call<AccessTokenResponse> accessTokenCall;
    Call<TicketAPIResponse> call2 ;

    public TicketSearchManager(Context context) {
        this.context = context;
    }

    public void getAccessToken(String email, String password, final AccessTokenCallback callback) {
        // Create an instance of the API service
        AccessTokenApiService apiService = RetrofitClientAccessToken.getRetrofitInstance().create(AccessTokenApiService.class);

        AccessTokenRequest request = new AccessTokenRequest(email, password);

        // Call the API to get the access token
        accessTokenCall = apiService.AccessTokenUser(request);

        // Enqueue the call to execute asynchronously
        accessTokenCall.enqueue(new Callback<AccessTokenResponse>() {
            @Override
            public void onResponse(Call<AccessTokenResponse> call, Response<AccessTokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AccessTokenResponse accessTokenResponse = response.body();
                    accessToken = accessTokenResponse.getAccessToken();

                    // Check if the access token was fetched successfully
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
                // Log the failure (e.g., network error)
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }

    // Method to load tickets using the access token

    public void loadSearchTickets(int page, int pageSize, final SearchTicketsCallback callback) {
        if (accessToken == null) {
            callback.onError("Access token is missing.");
            return;
        }
        String url = baseUrl + "api/m/tickets/" + "?page=" + page + "&per_page=" + pageSize;  /// /api/m/tickets/166?page=1&per_page=10

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


    // Callback interfaces
    public interface AccessTokenCallback {
        void onAccessTokenReceived(String accessToken);
        void onError(String errorMessage);
    }

    public interface SearchTicketsCallback {
        void onSearchTicketsLoaded(List<TicketSearchAPIItem> tickets);
        void onError(String errorMessage);
    }
}


/*
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



                            loadSearchTickets(accessToken);


    private void loadSearchTickets(String accessToken) {
        if (isLoading) return; // Prevent multiple simultaneous loads
        isLoading = true;

        // Ensure ticketManager is initialized
        if (ticketSearchManager == null) {
            ticketSearchManager = new TicketSearchManager(requireContext(), accessToken); // Initialize ticketManager if it's null
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        ticketSearchManager.loadSearchTickets(currentPage, pageSize, new TicketSearchManager.SearchTicketsCallback() {
            @Override
            public void onSearchTicketsLoaded(List<TicketSearchAPIItem> tickets) {
                binding.progressBar.setVisibility(View.GONE);
                isLoading = false;

                // Log the received ticket data
                if (tickets != null && !tickets.isEmpty()) {
                    for (TicketSearchAPIItem ticket : tickets) {
                        Log.d("loadSearchTickets", "Ticket ID: " + ticket.getId());
                        Log.d("loadSearchTickets", "Ticket Name: " + ticket.getCategory().getName());
                        Log.d("loadSearchTickets", "Ticket Subject: " + ticket.getSubject());
                        Log.d("loadSearchTickets", "Ticket Status: " + ticket.getStatus());

                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {

                            }
                        }, 2000);
                    }


                } else {
                    Log.d("loadTickets", "No tickets received.");
                }
            }

            @Override
            public void onError(String errorMessage) {
                binding.progressBar.setVisibility(View.GONE);
                isLoading = false;
                Log.e("loadTickets", "Error loading tickets: " + errorMessage);
            }
        });
    }

     */