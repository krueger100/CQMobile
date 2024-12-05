package com.example.cq_mobile.Clock.ViewListFolder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.Clock.ClockActivity;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.API_home.ApiClient_home;
import com.example.cq_mobile.ui.home.HomeFolder.API_home.ApiService_home;
import com.example.cq_mobile.ui.home.HomeFolder.API_home.MessageAdapter;
import com.example.cq_mobile.ui.home.HomeFolder.API_home.UserResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class ViewListTodoFragment extends Fragment {

    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<String> messageList = new ArrayList<>();
    private ProgressBar progressBar;
    private int currentPage = 1;
    TextView goback ;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewlist_todo, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        goback = view.findViewById(R.id.goback);
        messageAdapter = new MessageAdapter(getContext(), messageList);
        recyclerView.setAdapter(messageAdapter);

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ClockActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        loadMessages();

        return view;
    }

    private void loadMessages() {
        progressBar.setVisibility(View.VISIBLE);

        ApiService_home apiService = ApiClient_home.getClient().create(ApiService_home.class);
        apiService.getUserInfo(1).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    messageList.add(response.body().getMessage()); // Add the message to the list
                    Log.d("ToDoFragment", "API Response: " + messageList);
                    messageAdapter.notifyDataSetChanged(); // Refresh the adapter
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show(); // Fix context
                Log.e("ToDoFragment", "API Call Failed: " + t.getMessage(), t);
            }
        });
    }
}
