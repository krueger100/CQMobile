package com.example.cq_mobile.ui.home.HomeFolder;

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
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.API_skipped.Skipped;
import com.example.cq_mobile.ui.home.HomeFolder.API_skipped.SkippedAdapter;
import com.example.cq_mobile.ui.home.HomeFolder.API_skipped.SkippedApiManager;

import java.util.ArrayList;
import java.util.List;

public class SkippedFragment extends Fragment {
    private RecyclerView recyclerView;
    private SkippedAdapter skippedAdapter;
    private List<Skipped> skippedList = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView clockout_btn;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = 1;
    private final int PAGE_SIZE = 15;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate and setup the "Done" layout here
        View view = inflater.inflate(R.layout.fragment_skipped, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        clockout_btn = view.findViewById(R.id.clockout_btn);
        // Set up RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        skippedAdapter = new SkippedAdapter(getContext(), skippedList);
        recyclerView.setAdapter(skippedAdapter);

        SharedPrefManager sharedPrefManager = new SharedPrefManager(getContext());
        String accessToken = sharedPrefManager.getAccessToken();
        int userID = sharedPrefManager.getUserId();
        String firstName = sharedPrefManager.getFirstName();
        String lastName = sharedPrefManager.getLastName();
        String email = sharedPrefManager.getEmail();
        int savedJobId = sharedPrefManager.getJobId();
        int savedTaskId = sharedPrefManager.getTaskId();


        Log.d("SkippedFragmentSharedPreff", "Retrieved User Data: ");
        Log.d("SkippedFragmentSharedPreff", "Access Token: " + accessToken);
        Log.d("SkippedFragmentSharedPreff", "User ID: " + userID);
        Log.d("SkippedFragmentSharedPreff", "First Name: " + firstName);
        Log.d("SkippedFragmentSharedPreff", "Last Name: " + lastName);
        Log.d("SkippedFragmentSharedPreff", "Email: " + email);
        Log.d("SkippedFragmentSharedPreff", "Retrieved Job ID: " + savedJobId);
        Log.d("SkippedFragmentSharedPreff", "Retrieved Task ID: " + savedTaskId);
        Log.d("SkippedFragmentSharedPreff", "Retrieved user ID: " + userID);

        ClockOutManager clockOutManager = new ClockOutManager(getContext(),progressBar,savedJobId,savedTaskId,userID);
        clockOutManager.setupClockOutButton(clockout_btn, accessToken, savedJobId);

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
        clockout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ClockActivity.class);
                intent.putExtra("key", "value");
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }
    private void loadMessages(String accessToken) {
        if (isLoading) return; // Prevent multiple calls while already loading
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);

        SkippedApiManager.fetchApiDataPaginated(accessToken,currentPage, PAGE_SIZE, new SkippedApiManager.ApiResponseCallback() {
            @Override
            public void onDataFetched(List<Skipped> data) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    isLoading = false;

                    if (data != null && !data.isEmpty()) {
                        skippedList.addAll(data);
                        skippedAdapter.notifyDataSetChanged();
                        currentPage++;

                        // Check if total data count has reached 100
                        if (skippedList.size() >= 100 && currentPage == 1) {
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



}
