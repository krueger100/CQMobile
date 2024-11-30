package com.example.cq_mobile.ui.home.HomeFolder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.LoginFolder.ApiService;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.API_home.ApiClient_home;
import com.example.cq_mobile.ui.home.HomeFolder.API_home.ApiService_home;
import com.example.cq_mobile.ui.home.HomeFolder.API_home.User;
import com.example.cq_mobile.ui.home.HomeFolder.API_home.UserAdapter;
import com.example.cq_mobile.ui.home.HomeFolder.API_home.UserResponse;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class ToDoFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    private ProgressBar progressBar;
    private int currentPage = 1;
    private int totalPages = 2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new UserAdapter(getContext(), userList);
        recyclerView.setAdapter(userAdapter);

        loadUsers(currentPage);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (!recyclerView.canScrollVertically(1) && currentPage < totalPages) {
                    currentPage++;
                    loadUsers(currentPage);
                }
            }
        });

        return view;
    }

    private void loadUsers(int page) {
        progressBar.setVisibility(View.VISIBLE);

        // Use ApiService_home here instead of ApiService
        ApiService_home apiService = ApiClient_home.getClient().create(ApiService_home.class);
        apiService.getUsers(page).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    totalPages = response.body().getTotalPages();
                    userList.addAll(response.body().getData());
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                // Handle error
            }
        });
    }
}
