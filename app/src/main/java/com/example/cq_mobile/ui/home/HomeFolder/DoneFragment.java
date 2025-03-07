package com.example.cq_mobile.ui.home.HomeFolder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cq_mobile.Clock.ClockActivity;

import com.example.cq_mobile.Clock.ClockFolder.ClockOutFolder.ClockOutManager;
import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.ClockOutVisibilityHandler;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.API_done.Done;
import com.example.cq_mobile.ui.home.HomeFolder.API_done.DoneAdapter;
import com.example.cq_mobile.ui.home.HomeFolder.API_done.DoneApiManager;

import java.util.ArrayList;
import java.util.List;

public class DoneFragment extends Fragment {
    private RecyclerView recyclerView;
    private DoneAdapter doneAdapter;
    private List<Done> donelist = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView clockout_btn;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = 1;
    private final int PAGE_SIZE = 15;
    private ClockOutVisibilityHandler visibilityHandler;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_done, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        doneAdapter = new DoneAdapter(getContext(), donelist);
        recyclerView.setAdapter(doneAdapter);


        SharedPrefManager sharedPrefManager = new SharedPrefManager(getContext());
        String accessToken = sharedPrefManager.getAccessToken();
        int userID = sharedPrefManager.getUserId();
        String firstName = sharedPrefManager.getFirstName();
        String lastName = sharedPrefManager.getLastName();
        String email = sharedPrefManager.getEmail();
        int savedJobId = sharedPrefManager.getJobId();
        int savedTaskId = sharedPrefManager.getTaskId();
        String startDate = sharedPrefManager.getKeyStartDate();


        Log.d("DoneFragmentSharedPreff", "Retrieved User Data: ");
        Log.d("DoneFragmentSharedPreff", "Access Token: " + accessToken);
        Log.d("DoneFragmentSharedPreff", "User ID: " + userID);
        Log.d("DoneFragmentSharedPreff", "First Name: " + firstName);
        Log.d("DoneFragmentSharedPreff", "Last Name: " + lastName);
        Log.d("DoneFragmentSharedPreff", "Email: " + email);
        Log.d("DoneFragmentSharedPreff", "Retrieved Job ID: " + savedJobId);
        Log.d("DoneFragmentSharedPreff", "Retrieved Task ID: " + savedTaskId);
        Log.d("DoneFragmentSharedPreff", "Retrieved user ID: " + userID);
        Log.d("DoneFragmentSharedPreff", "Retrieved startDate: " + startDate);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!isLoading && !isLastPage) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        loadMessages(accessToken);
                    }
                }
            }
        });

        loadMessages(accessToken);



        return view;


    }



    private void loadMessages(String accessToken) {
        if (isLoading) return; // Prevent multiple calls while already loading
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);

        DoneApiManager.fetchDoneApiData(accessToken,currentPage, PAGE_SIZE, new DoneApiManager.ApiResponseCallback() {
            @Override
            public void onDataFetched(List<Done> data) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    isLoading = false;

                if (data != null && !data.isEmpty()) {
                    donelist.addAll(data);
                    doneAdapter.notifyDataSetChanged();
                    currentPage++;

                    // Check if total data count has reached 100
                    if (donelist.size() >= 100 && currentPage == 1) {
                        // If data reaches 100, skip to page 2 directly, if we are still on page 1
                        currentPage = 1;
                        loadMessages(accessToken); // Recurse to load data from page 2
                    } else {
                        // Check if this is the last page
                        if (data.size() < PAGE_SIZE) {
                            isLastPage = true;
                        }
                    }
                } else {
                    isLastPage = true; // No more data to load
                }
            });


        }

            @Override
            public void onError(String error) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    isLoading = false;
                    Log.d("Paginated Data", "Error loading data: " + error);
                });
            }
        });


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ClockOutVisibilityHandler) {
            visibilityHandler = (ClockOutVisibilityHandler) context;
        } else {
            Log.d("DoneFragment", "Activity does not implement ClockOutVisibilityHandler");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (visibilityHandler != null) {
            visibilityHandler.setClockOutVisibility(true);
        }
    }


}
