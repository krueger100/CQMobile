package com.example.cq_mobile.ui.ticket.ReplyTicketFolder;

import android.content.Context;

import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.ui.ticket.TicketsFolder.TicketAPIItem;
import com.example.cq_mobile.ui.ticket.TicketsFolder.TicketManager;

import java.util.List;

public class LoadTicketsManager {

    private final Context context;
    private final TicketManager ticketManager;
    private boolean isLoading;

    public interface TicketsLoadedCallback {
        void onTicketsLoaded(List<TicketAPIItem> tickets, String token);
        void onError(String errorMessage);
    }

    public LoadTicketsManager(Context context) {
        this.context = context;
        this.ticketManager = new TicketManager(context);
        this.isLoading = false;
    }
/*
    public void loadTickets(int currentPage, int pageSize, String email, String password, TicketsLoadedCallback callback) {
        if (isLoading) return; // Prevent multiple loads
        isLoading = true;

        // Create AccessTokenRequest
        AccessTokenRequest request = new AccessTokenRequest(email, password);

        // Fetch Access Token
        ticketManager.getAccessToken(request, new TicketManager.AccessTokenCallback() {
            @Override
            public void onAccessTokenReceived(String token) {
                loadTicketsWithToken(token, currentPage, pageSize, callback);
            }

            @Override
            public void onError(String errorMessage) {
                isLoading = false;
                callback.onError("Access Token Error: " + errorMessage);
            }
        });
    }


 */
    private void loadTicketsWithToken(String token, int currentPage, int pageSize, TicketsLoadedCallback callback) {
        ticketManager.loadTickets(currentPage, pageSize, new TicketManager.AllTicketsCallback() {
            @Override
            public void onAllTicketsLoaded(List<TicketAPIItem> tickets) {
                isLoading = false;
                if (tickets != null && !tickets.isEmpty()) {
                    callback.onTicketsLoaded(tickets, token);
                } else {
                    callback.onError("No tickets received.");
                }
            }

            @Override
            public void onError(String errorMessage) {
                isLoading = false;
                callback.onError("Ticket Loading Error: " + errorMessage);
            }
        });
    }
}


/*

        LoadTicketsManager loadTicketsManager = new LoadTicketsManager(this);


    private void ticketMainDataManager(LoadTicketsManager loadTicketsManager) {
        loadTicketsManager.loadTickets(1, 10, "richard.anthony.wetherell@gmail.com", "123456", new LoadTicketsManager.TicketsLoadedCallback() {
            @Override
            public void onTicketsLoaded(List<TicketAPIItem> tickets, String token) {
                if (tickets != null && !tickets.isEmpty()) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(ReplyTicket.this));
                    itemAdapter = new ItemAdapter(ReplyTicket.this, token, tickets);
                    recyclerView.setAdapter(itemAdapter);

                    TicketAPIItem item = null;

                    if (item.getMessages() != null && !item.getMessages().isEmpty()) {
                        Gson gson = new Gson();
                        String messagesJsonList = gson.toJson(item.getMessages());
                        TicketAPIItem.Message firstMessage = item.getMessages().get(0);
                        String messageJson = gson.toJson(firstMessage);

                    }


                } else {
                    Log.d("TicketFragment", "No tickets received.");
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Handle error
                Log.e("LoadTicketsError", errorMessage);
            }
        });
    }

 */
