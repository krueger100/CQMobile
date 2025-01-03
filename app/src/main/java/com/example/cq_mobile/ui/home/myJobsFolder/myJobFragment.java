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
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.API_todo.Todo;
import com.example.cq_mobile.ui.home.HomeFolder.API_todo.TodoAdapter;
import com.example.cq_mobile.ui.home.HomeFolder.API_todo.TodoApiManager;

import java.util.ArrayList;
import java.util.List;

public class myJobFragment  extends Fragment {

    private RecyclerView recyclerView;
    private TodoAdapter todoAdapter;
    private List<Todo> joblist = new ArrayList<>(); // Use List<Todo>
    private ProgressBar progressBar;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = 1;
    private final int PAGE_SIZE = 15;
    TextView goback ;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_jobs_todo, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        goback = view.findViewById(R.id.goback);

        // Set up RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        todoAdapter = new TodoAdapter(getContext(), joblist);
        recyclerView.setAdapter(todoAdapter);

        SharedPrefManager sharedPrefManager = new SharedPrefManager(getContext());
        String accessToken = sharedPrefManager.getAccessToken();
        String userId = sharedPrefManager.getUserId();
        String firstName = sharedPrefManager.getFirstName();
        String lastName = sharedPrefManager.getLastName();
        String email = sharedPrefManager.getEmail();

        Log.d("myJobFragment", "Retrieved User Data: ");
        Log.d("myJobFragment", "Access Token: " + accessToken);
        Log.d("myJobFragment", "User ID: " + userId);
        Log.d("myJobFragment", "First Name: " + firstName);
        Log.d("myJobFragment", "Last Name: " + lastName);
        Log.d("myJobFragment", "Email: " + email);

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
        if (isLoading) return; // Prevent fetching while already loading data
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);

        TodoApiManager.fetchApiDataPaginated(accessToken,currentPage, PAGE_SIZE, new TodoApiManager.ApiResponseCallback() {
            @Override
            public void onDataFetched(List<Todo> data) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    isLoading = false;

                    if (data != null && !data.isEmpty()) {
                        joblist.addAll(data);
                        todoAdapter.notifyDataSetChanged();
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
