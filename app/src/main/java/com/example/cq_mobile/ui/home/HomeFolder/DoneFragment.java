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
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_done, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        clockout_btn = view.findViewById(R.id.clockout_btn);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        doneAdapter = new DoneAdapter(getContext(), donelist);
        recyclerView.setAdapter(doneAdapter);

        SharedPrefManager sharedPrefManager = new SharedPrefManager(getContext());
        String accessToken = sharedPrefManager.getAccessToken();
        String userId = sharedPrefManager.getUserId();
        String firstName = sharedPrefManager.getFirstName();
        String lastName = sharedPrefManager.getLastName();
        String email = sharedPrefManager.getEmail();

        Log.d("DoneFragmentSharedPreff", "Retrieved User Data: ");
        Log.d("DoneFragmentSharedPreff", "Access Token: " + accessToken);
        Log.d("DoneFragmentSharedPreff", "User ID: " + userId);
        Log.d("DoneFragmentSharedPreff", "First Name: " + firstName);
        Log.d("DoneFragmentSharedPreff", "Last Name: " + lastName);
        Log.d("DoneFragmentSharedPreff", "Email: " + email);
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

}
