package com.example.cq_mobile.ui.ticket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.R;
import com.example.cq_mobile.databinding.FragmentTicketBinding;
import com.example.cq_mobile.ui.ticket.CreateFolder.CreateTicket;
import com.example.cq_mobile.ui.ticket.TicketAPICategoryFolder.TicketAPICategoryItems;
import com.example.cq_mobile.ui.ticket.TicketAPICategoryFolder.TicketCategoryManager;
import com.example.cq_mobile.ui.ticket.TicketSearchFolder.TicketSearchAPIItem;
import com.example.cq_mobile.ui.ticket.TicketSearchFolder.TicketSearchManager;
import com.example.cq_mobile.ui.ticket.TicketsFolder.TicketAPIItem;
import com.example.cq_mobile.ui.ticket.TicketsFolder.TicketManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;

public class TicketFragment extends Fragment {

    private FragmentTicketBinding binding;
    private TicketCategoryManager ticketCategoryManager;
    private TicketSearchManager ticketSearchManager;
    private TicketManager ticketManager;
    private boolean isLoading = false;
    private Context context;
    private int currentPage = 1;
    private final int pageSize = 10;
    private ItemAdapter itemAdapter;
    private CategoryAdapter categoryAdapter;
    private BottomSheetBehavior<View> bottomSheetBehavior;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTicketBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Retrieve the access token
        SharedPrefManager sharedPrefManager = new SharedPrefManager(requireContext());
        String accessToken = sharedPrefManager.getAccessToken();
        Log.d("TicketFragment", "Access Token: " + accessToken);
        context = getContext();
        ticketCategoryManager = new TicketCategoryManager(context, accessToken);

        setupBottomSheet();
        if (binding != null) {

            binding.createTicket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CreateTicket.class);
                    startActivity(intent);

                }
            });


            binding.searchBarBtnOff.setOnClickListener(v -> {
                binding.searchBarBtnOn.setVisibility(View.VISIBLE);
                binding.searchBarBtnOff.setVisibility(View.GONE);
                binding.cardView3.setVisibility(View.VISIBLE);
            });

            binding.searchBarBtnOn.setOnClickListener(v -> {
                binding.searchBarBtnOn.setVisibility(View.GONE);
                binding.searchBarBtnOff.setVisibility(View.VISIBLE);
                binding.cardView3.setVisibility(View.GONE);
            });

            // Handle filter buttons
            binding.filterBtnOff.setOnClickListener(v -> {
                binding.filterBtnOn.setVisibility(View.VISIBLE);
                binding.filterBtnOff.setVisibility(View.GONE);
                hideBottomSheet(); // Hide the BottomSheet
            });

            binding.filterBtnOn.setOnClickListener(v -> {
                binding.filterBtnOn.setVisibility(View.GONE);
                binding.filterBtnOff.setVisibility(View.VISIBLE);
                showBottomSheet(); // Show the BottomSheet
            });

            binding.searchIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the text from the search bar
                    String searchQuery = binding.searchBar.getText().toString();
                    String email = "richard.anthony.wetherell@gmail.com";
                    String password = "123456";

                    loadSearchTickets(email, password);

//            // Optionally, you can handle the case where the search bar is empty
//            if (searchQuery.isEmpty()) {
//                Log.d("Search", "Search query is empty");
//            } else {
//                Log.d("Search", "Search query: " + searchQuery);
//            }
                }
            });

            binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadCategoryTickets(accessToken);
                }
            });

            if (binding != null) {  // Check if the binding is still valid
                binding.progressBar.setVisibility(View.VISIBLE);
            }
            loadCategoryTickets(accessToken);

        }
        return root;
    }

    private void loadCategoryTickets(String accessToken) {
        if (isLoading) return; // Prevent multiple simultaneous loads
        isLoading = true;
        if (binding != null) {
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        ticketCategoryManager.loadCategoryTickets(currentPage, pageSize, new TicketCategoryManager.TicketsCallback() {
            @Override
            public void onTicketsLoaded(List<TicketAPICategoryItems> tickets) {
                binding.progressBar.setVisibility(View.GONE);
                isLoading = false;

                // Log the received ticket data
                if (tickets != null && !tickets.isEmpty()) {
                    for (TicketAPICategoryItems ticket : tickets) {
                        Log.d("TicketFragment", "Ticket ID: " + ticket.getId());
                        Log.d("TicketFragment", "Ticket Name: " + ticket.getName());
                        Log.d("TicketFragment", "Ticket Color: " + ticket.getColor());
                    }


                    // Set up the RecyclerView LayoutManager
                    binding.filterRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

                    // Pass the tickets to the CategoryAdapter
                    categoryAdapter = new CategoryAdapter(context, accessToken, tickets);
                    binding.filterRecyclerView.setAdapter(categoryAdapter);
                    categoryAdapter.notifyDataSetChanged();  // Ensure the data is updated


                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //     loadSearchTickets(accessToken);
                            loadTickets();
                        }
                    }, 1000);


                } else {
                    Log.d("TicketFragment", "No tickets received.");
                }
            }

            @Override
            public void onError(String errorMessage) {
                binding.progressBar.setVisibility(View.GONE);
                isLoading = false;
                Log.e("TicketFragment", "Error loading tickets: " + errorMessage);
            }
        });
    }

    private void loadTickets() {
        if (isLoading) return; // Prevent multiple loads
        isLoading = true;

        setProgressBarVisibility(true); // Show progress bar

        String email = "richard.anthony.wetherell@gmail.com";
        String password = "123456";

        AccessTokenRequest request = new AccessTokenRequest(email, password);

        // Lazy initialization of ticketManager
        if (ticketManager == null) {
            ticketManager = new TicketManager(requireContext());
        }

        // Fetch Access Token
        ticketManager.getAccessToken(request, new TicketManager.AccessTokenCallback() {
            @Override
            public void onAccessTokenReceived(String token) {
                loadTicketsWithToken(token);
            }

            @Override
            public void onError(String errorMessage) {
                setProgressBarVisibility(false);
                isLoading = false;
                Log.e("TicketFragment", "Access Token Error: " + errorMessage);
            }
        });
    }

    private void loadTicketsWithToken(String token) {
        ticketManager.loadTickets(currentPage, pageSize, new TicketManager.AllTicketsCallback() {
            @Override
            public void onAllTicketsLoaded(List<TicketAPIItem> tickets) {
                setProgressBarVisibility(false);
                isLoading = false;
                if (tickets != null && !tickets.isEmpty()) {
                    displayTickets(tickets, token);
                } else {
                    Log.d("TicketFragment", "No tickets received.");
                }
            }
            @Override
            public void onError(String errorMessage) {
                setProgressBarVisibility(false);
                isLoading = false;
                Log.e("TicketFragment", "Ticket Loading Error: " + errorMessage);
            }
        });
    }


    private void displayTickets(List<TicketAPIItem> tickets, String token) {
        List<Integer> ticketIDs = new ArrayList<>();

        for (TicketAPIItem ticket : tickets) {
            ticketIDs.add(ticket.getId());
            Log.d("TicketFragment", "Ticket ID: " + ticket.getId());
        }

         binding.swipeRefreshLayout.setRefreshing(false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        itemAdapter = new ItemAdapter(context, token, tickets);
        binding.recyclerView.setAdapter(itemAdapter);
    }


    private void loadSearchTickets(String email, String password) {
        // Log the access token for debugging
        Log.d("loadReplies", "Email: " + email + ", Password: " + password);

        // Ensure ticketSearchManager is initialized
        if (ticketSearchManager == null) {
            ticketSearchManager = new TicketSearchManager(context);
        }

        // Get the access token first
        ticketSearchManager.getAccessToken(email, password, new TicketSearchManager.AccessTokenCallback() {
            @Override
            public void onAccessTokenReceived(String token) {
                Log.d("loadReplies", "Access Token received: " + token);

                // Now load the tickets with the received access token
                ticketSearchManager.loadSearchTickets(1, 10, new TicketSearchManager.SearchTicketsCallback() {
                    @Override
                    public void onSearchTicketsLoaded(List<TicketSearchAPIItem> tickets) {
                        // Log the received ticket data
                        if (tickets != null && !tickets.isEmpty()) {
                            List<TicketAPIItem.Message> newReplies = new ArrayList<>();
                            for (TicketSearchAPIItem ticket : tickets) {
                                Log.d("loadSearchTickets", "Ticket ID: " + ticket.getId());
                                Log.d("loadSearchTickets", "Ticket Subject: " + ticket.getSubject());

                                // Assuming you get messages from each ticket
                                for (TicketSearchAPIItem.Message searchMessage : ticket.getMessages()) {
                                    Log.d("loadSearchTickets", "ID: " + searchMessage.getUser().getId());
                                    Log.d("loadSearchTickets", "Name: " + searchMessage.getUser().getName());
                                }
                            }

                        } else {
                            Log.d("loadTickets", "No tickets received.");
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {

                        Log.e("loadTickets", "Error loading tickets: " + errorMessage);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {

                Log.e("loadReplies", "Error getting access token: " + errorMessage);
            }
        });

    }


    private void setProgressBarVisibility(boolean isVisible) {
        if (binding != null) {
            binding.progressBar.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

    private void setupBottomSheet() {
        View bottomSheet = binding.getRoot().findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        binding.filterRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    }


    private void showBottomSheet() {
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private void hideBottomSheet() {
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ticketManager.cancelAllCalls();
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