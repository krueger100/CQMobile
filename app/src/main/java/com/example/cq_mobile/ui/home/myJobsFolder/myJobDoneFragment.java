package com.example.cq_mobile.ui.home.myJobsFolder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.LoginFolder.AuthManager;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.API_done.Done;
import com.example.cq_mobile.ui.home.HomeFolder.API_done.DoneAdapter;
import com.example.cq_mobile.ui.home.HomeFolder.API_done.DoneApiManager;

import java.util.ArrayList;
import java.util.List;

public class myJobDoneFragment extends Fragment {
    private RecyclerView recyclerView;
    private DoneAdapter doneAdapter;
    private List<Done> joblist = new ArrayList<>(); // Use List<Todo>
    private ProgressBar progressBar;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = 1;
    private final int PAGE_SIZE = 15;
    TextView goback ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_jobs_done, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        goback = view.findViewById(R.id.goback);

        // Set up RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        doneAdapter = new DoneAdapter(getContext(), joblist);
        recyclerView.setAdapter(doneAdapter);

        SharedPrefManager sharedPrefManager = new SharedPrefManager(getContext());
        String accessToken = AuthManager.getInstance(getContext()).getToken();

        int userId = sharedPrefManager.getUserId();
        String firstName = sharedPrefManager.getFirstName();
        String lastName = sharedPrefManager.getLastName();
        String email = sharedPrefManager.getEmail();

        Log.d("myJobDoneFragment", "Retrieved User Data: ");
        Log.d("myJobDoneFragment", "Access Token: " + accessToken);
        Log.d("myJobDoneFragment", "User ID: " + userId);
        Log.d("myJobDoneFragment", "First Name: " + firstName);
        Log.d("myJobDoneFragment", "Last Name: " + lastName);
        Log.d("myJobDoneFragment", "Email: " + email);

        // Add scroll listener for pagination
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

        // Load initial data
        loadMessages(accessToken);

        goback.setOnClickListener(v -> {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
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

        DoneApiManager.fetchDoneApiData(accessToken,currentPage, PAGE_SIZE, new DoneApiManager.ApiResponseCallback() {
            @Override
            public void onDataFetched(List<Done> data) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    isLoading = false;

                    if (data != null && !data.isEmpty()) {
                        joblist.addAll(data);
                        doneAdapter.notifyDataSetChanged();
                        currentPage++;

                        // Check if total data count has reached 100
                        if (joblist.size() >= 100 && currentPage == 1) {
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
