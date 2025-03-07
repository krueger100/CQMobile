package com.example.cq_mobile.ui.ticket;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cq_mobile.HelperManagers.Animation.ClickAnimationManager;
import com.example.cq_mobile.HelperManagers.Animation.TransitionAnimationManager;
import com.example.cq_mobile.HelperManagers.CloseKeyboardManager;
import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.ClockOutVisibilityHandler;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;
import com.example.cq_mobile.databinding.FragmentTicketBinding;
import com.example.cq_mobile.ui.ticket.CreateFolder.CreateTicket;
import com.example.cq_mobile.ui.ticket.TicketAPICategoryFolder.TicketAPICategoryItems;
import com.example.cq_mobile.ui.ticket.TicketAPICategoryFolder.TicketCategoryManager;
import com.example.cq_mobile.ui.ticket.TicketSearchFolder.TicketSearchManager;
import com.example.cq_mobile.ui.ticket.TicketsFolder.TicketAPIItem;
import com.example.cq_mobile.ui.ticket.TicketsFolder.TicketManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class TicketFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener{
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
    String email;
    String password;
    private ClockOutVisibilityHandler visibilityHandler;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTicketBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPrefManager sharedPrefManager = new SharedPrefManager(requireContext());
        String accessToken = sharedPrefManager.getAccessToken();
        email = sharedPrefManager.getEmail();
        password = sharedPrefManager.getPassword();
        Log.d("TicketFragment", "Access Token: " + accessToken);
        Log.d("TicketFragment", "Email: "+email);
        Log.d("TicketFragment", "Password  : "+password);
        context = getContext();
        ticketCategoryManager = new TicketCategoryManager(context, accessToken);


        setupBottomSheet();
        if (binding != null) {
            binding.createTicket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TransitionAnimationManager.slideOutToRight(v, 150);
                v.postDelayed(() -> {
                        v.postDelayed(() -> {
                            TransitionAnimationManager.slideInFromRight(v, 50);
                        }, 150);
                    Intent intent = new Intent(context, CreateTicket.class);
                    startActivity(intent);
        }, 150);

                }
            });


            binding.searchBarBtnOff.setOnClickListener(v -> {
                ClickAnimationManager.applyClickAnimation(v);

                binding.searchBarBtnOn.setVisibility(View.VISIBLE);
                binding.searchBarBtnOff.setVisibility(View.GONE);
                binding.cardView3.setVisibility(View.VISIBLE);
                loadTickets();

                binding.progressBar.setVisibility(View.VISIBLE);

            });

            binding.searchBarBtnOn.setOnClickListener(v -> {
                ClickAnimationManager.applyClickAnimation(v);

                binding.searchBarBtnOn.setVisibility(View.GONE);
                binding.searchBarBtnOff.setVisibility(View.VISIBLE);
                binding.cardView3.setVisibility(View.GONE);


            });

            // Handle filter buttons
            binding.filterBtnOff.setOnClickListener(v -> {
                ClickAnimationManager.applyClickAnimation(v);

                binding.filterBtnOn.setVisibility(View.VISIBLE);
                binding.filterBtnOff.setVisibility(View.GONE);

                binding.searchBarBtnOn.setVisibility(View.GONE);
                binding.searchBarBtnOff.setVisibility(View.VISIBLE);
                binding.cardView3.setVisibility(View.GONE);

                hideBottomSheet();
            });
            binding.filterBtnOn.setOnClickListener(v -> {
                ClickAnimationManager.applyClickAnimation(v);

                binding.filterBtnOn.setVisibility(View.GONE);
                binding.filterBtnOff.setVisibility(View.VISIBLE);

                binding.searchBarBtnOn.setVisibility(View.VISIBLE);
                binding.searchBarBtnOff.setVisibility(View.GONE);
                binding.cardView3.setVisibility(View.VISIBLE);


                showBottomSheet();
            });






            binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    isLoading = true;
                    binding.swipeRefreshLayout.setRefreshing(false);
                    reloadFragment();
                }
            });



            binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
               @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (!isLoading && !recyclerView.canScrollVertically(1)) {
                        binding.swipeRefreshLayout.setRefreshing(true);
                        loadTicketsWithToken(accessToken);

                    }
                }
            });


            if (binding != null) {  // Check if the binding is still valid
                binding.progressBar.setVisibility(View.VISIBLE);
            }
            loadCategoryTickets(accessToken);


            registerTimerReceiver();


        }
        return root;
    }

    private void registerTimerReceiver() {



    }


    @Override
    public void onCategoryClick(int categoryId) {
        Log.d("TicketFragment_onCategoryClick", "Category selected with ID: " + categoryId);
        loadTicketsByCategory(categoryId);
        hideBottomSheet();
        binding.filterBtnOn.setVisibility(View.VISIBLE);
        binding.filterBtnOff.setVisibility(View.GONE);
    }

    private void loadTicketsByCategory(int categoryId) {
        Log.d("TicketFragment_onCategoryClick", "Loading tickets for category: " + categoryId);
        binding.searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickAnimationManager.applyClickAnimation(v);
                CloseKeyboardManager.closeKeyboard((Activity) context);
                hideBottomSheet();

                String searchQuery = binding.searchBar.getText().toString();

                if (searchQuery.isEmpty()) {
                    Toast.makeText(context, "Please enter a search query", Toast.LENGTH_SHORT).show();
                    Log.d("Search", "Search query is empty");
                } else {
                    Toast.makeText(context, "Searching for: " + searchQuery, Toast.LENGTH_SHORT).show();
                    Log.d("Search", "Search query: " + searchQuery);
                    loadSearchTickets(email, password, searchQuery, categoryId);

                }
            }
        });


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

                    // Pass the tickets to the CategoryAdapter, now with listener
                    categoryAdapter = new CategoryAdapter(context, accessToken, tickets, TicketFragment.this);
                    binding.filterRecyclerView.setAdapter(categoryAdapter);
                    categoryAdapter.notifyDataSetChanged();

                    new Handler(Looper.getMainLooper()).postDelayed(() -> loadTickets(), 1000);
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
        // Current page number
        if (currentPage == 0) {
            currentPage = 1;  // Initialize to page 1
        }

        ticketManager.loadTickets(currentPage, pageSize, new TicketManager.AllTicketsCallback() {
            @Override
            public void onAllTicketsLoaded(List<TicketAPIItem> tickets) {
                setProgressBarVisibility(false);
                isLoading = false;
                if (tickets != null && !tickets.isEmpty()) {
                    displayTickets(tickets, token);
                    currentPage++;
                    binding.swipeRefreshLayout.setRefreshing(false);

                } else {
                    Log.d("TicketFragment", "No tickets received.");
                    binding.swipeRefreshLayout.setRefreshing(false);

                }
            }

            @Override
            public void onError(String errorMessage) {
                setProgressBarVisibility(false);
                isLoading = false;
                binding.swipeRefreshLayout.setRefreshing(false);
                Log.e("TicketFragment", "Ticket Loading Error: " + errorMessage);
            }
        });
    }
    private void displayTickets(List<TicketAPIItem> tickets, String token) {
        if (itemAdapter == null) {
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            itemAdapter = new ItemAdapter(context, token, tickets);
            binding.recyclerView.setAdapter(itemAdapter);

        } else {
            itemAdapter.addTickets(tickets);
        }
    }


    private void loadSearchTickets(String email, String password, String search, int categoryId) {
        if (ticketSearchManager == null) {
            ticketSearchManager = new TicketSearchManager(context);
        }
        ticketSearchManager.getAccessToken(email, password, new TicketSearchManager.AccessTokenCallback() {
            @Override
            public void onAccessTokenReceived(String token) {
                ticketSearchManager.loadSearchTickets(1, 10, search, categoryId, new TicketSearchManager.SearchTicketsCallback() {
                    @Override
                    public void onSearchTicketsLoaded(List<TicketAPIItem> tickets) {
                        if (tickets != null && !tickets.isEmpty()) {
                            for (TicketAPIItem ticket : tickets) {
                                Log.d("loadSearchTickets", "Ticket ID: " + ticket.getId());
                                Log.d("loadSearchTickets", "Ticket Subject: " + ticket.getSubject());


                                binding.searchBarBtnOn.setVisibility(View.GONE);
                                binding.searchBarBtnOff.setVisibility(View.VISIBLE);
                                binding.cardView3.setVisibility(View.GONE);

                                binding.swipeRefreshLayout.setRefreshing(false);
                                binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                                itemAdapter = new ItemAdapter(context, token, tickets);
                                binding.recyclerView.setAdapter(itemAdapter);

                            }
                        } else {
                            Log.d("loadSearchTickets", "No tickets found.");
                            Toast.makeText(context, "No tickets found: " + "There are no match", Toast.LENGTH_SHORT).show();
                            hideBottomSheet();
                            binding.filterBtnOn.setVisibility(View.VISIBLE);
                            binding.filterBtnOff.setVisibility(View.GONE);

                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e("loadSearchTickets", "Error: " + errorMessage);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("loadSearchTickets", "Error getting access token: " + errorMessage);
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


    private void checkProgressBarAndReload() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (binding != null && binding.progressBar.getVisibility() == View.GONE) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("ReplyData", Context.MODE_PRIVATE);
                boolean isReplySent = sharedPreferences.getBoolean("isReplySent", false);
                if (isReplySent) {
                    Log.d("SharedPreferences", "Reply was sent successfully.");
                    reloadFragment();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("isReplySent");
                    editor.apply();
                } else {
                    Log.d("SharedPreferences", "Reply was not sent.");

                }
            } else {
                Log.d("ProgressBar", "ProgressBar is still visible, checking again...");
                checkProgressBarAndReload();
            }
        }, 1000);
    }


    public void reloadFragment() {
        if (getActivity() != null) {
            int count = itemAdapter.getItemCount();
            if (count > 0) {
                Log.d("reloadFragment", "Data has been added, you can perform any necessary actions here");
                getActivity().recreate();

            } else {
                Log.d("reloadFragment", " No data, handle accordingly");
            }
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ClockOutVisibilityHandler) {
            visibilityHandler = (ClockOutVisibilityHandler) context;
        } else {
            Log.d("MoreFragment", "Activity does not implement ClockOutVisibilityHandler");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (visibilityHandler != null) {
            visibilityHandler.setClockOutVisibility(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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