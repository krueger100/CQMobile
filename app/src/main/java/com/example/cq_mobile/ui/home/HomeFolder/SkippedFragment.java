package com.example.cq_mobile.ui.home.HomeFolder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.Clock.ClockActivity;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.API_todo.Job;
import com.example.cq_mobile.ui.home.HomeFolder.API_todo.TodoAdapter;
import com.example.cq_mobile.ui.home.HomeFolder.API_todo.TodoApiManager;

import java.util.ArrayList;
import java.util.List;

public class SkippedFragment extends Fragment {
    private RecyclerView recyclerView;
    private TodoAdapter todoAdapter;
    private List<Job> joblist = new ArrayList<>(); // Use List<Job>
    private ProgressBar progressBar;
    private TextView clockout_btn;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate and setup the "Done" layout here
        View view = inflater.inflate(R.layout.fragment_skipped, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        clockout_btn = view.findViewById(R.id.clockout_btn);
        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        todoAdapter = new TodoAdapter(getContext(), joblist);
        recyclerView.setAdapter(todoAdapter);


        loadMessages();
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
    private void loadMessages() {
        progressBar.setVisibility(View.VISIBLE);

        TodoApiManager.fetchApiData(new TodoApiManager.ApiResponseCallback() {
            @Override
            public void onDataFetched(List<Job> data) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    if (data != null && !data.isEmpty()) {
                        joblist.clear();
                        joblist.addAll(data); // Add the List<Job>
                        todoAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "No jobs available", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error loading data: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


}
