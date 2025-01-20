package com.example.cq_mobile.ui.ticket;

import android.content.Context;
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

import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.R;
import com.example.cq_mobile.databinding.FragmentTicketBinding;
import com.example.cq_mobile.ui.ticket.TicketAPICategoryFolder.TicketAPICategoryItems;
import com.example.cq_mobile.ui.ticket.TicketAPICategoryFolder.TicketCategoryManager;
import com.example.cq_mobile.ui.ticket.TicketSearchFolder.TicketSearchAPIItem;
import com.example.cq_mobile.ui.ticket.TicketSearchFolder.TicketSearchManager;
import com.example.cq_mobile.ui.ticket.TicketsFolder.TicketAPIItem;
import com.example.cq_mobile.ui.ticket.TicketsFolder.TicketManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTicketBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Retrieve the access token
        SharedPrefManager sharedPrefManager = new SharedPrefManager(requireContext());
        String accessToken = sharedPrefManager.getAccessToken();
        Log.d("TicketFragment", "Access Token: " + accessToken);
        context = getContext();
        ticketCategoryManager = new TicketCategoryManager(context, accessToken);

        setupBottomSheet();

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

    binding.progressBar.setVisibility(View.VISIBLE);
        loadCategoryTickets(accessToken);


        return root;
    }



    private void loadCategoryTickets(String accessToken) {
        if (isLoading) return; // Prevent multiple simultaneous loads
        isLoading = true;

        binding.progressBar.setVisibility(View.VISIBLE);

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
                            loadSearchTickets(accessToken);
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
                                loadTickets();
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

    private void loadTickets() {
        if (isLoading) return; // Prevent multiple simultaneous loads
        isLoading = true;

        binding.progressBar.setVisibility(View.VISIBLE);

        String email = "richard.anthony.wetherell@gmail.com";
        String password = "123456";
        AccessTokenRequest request = new AccessTokenRequest(email, password);

        // Ensure ticketManager is initialized
        if (ticketManager == null) {
            ticketManager = new TicketManager(requireContext()); // Initialize ticketManager if it's null
        }

        // Get the access token
        ticketManager.getAccessToken(request, new TicketManager.AccessTokenCallback() {
            @Override
            public void onAccessTokenReceived(String token) {
                // Now load the tickets with the received access token
                ticketManager.loadTickets(currentPage, pageSize, new TicketManager.AllTicketsCallback() {
                    @Override
                    public void onAllTicketsLoaded(List<TicketAPIItem> tickets) {
                        binding.progressBar.setVisibility(View.GONE);
                        isLoading = false;

                        // Log the received ticket data
                        if (tickets != null && !tickets.isEmpty()) {
                            for (TicketAPIItem ticket : tickets) {
                                Log.d("loadTickets", "Ticket ID: " + ticket.getId());
                                Log.d("loadTickets", "Ticket Name: " + ticket.getCategory().getName());
                                Log.d("loadTickets", "Ticket getSubject: " + ticket.getSubject());
                            }

                            // Set up the RecyclerView LayoutManager
                            binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                            itemAdapter = new ItemAdapter(context, token, tickets);
                            binding.recyclerView.setAdapter(itemAdapter);
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

            @Override
            public void onError(String errorMessage) {
                binding.progressBar.setVisibility(View.GONE);
                isLoading = false;
                Log.e("loadTickets", "Error getting access token: " + errorMessage);
            }
        });
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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