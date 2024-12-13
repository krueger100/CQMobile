package com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.RetrieveDataFromAPIMangers;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.NewBuildApiManager;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.SubTasks.SubTask;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.SubTasks.SubTaskAdapter;

import java.util.ArrayList;
import java.util.List;


public class SetupRecyclerViewManager {

    private Context context;
    private RecyclerView recyclerView;
    private SubTaskAdapter subTaskAdapter;
    private LinearLayoutManager layoutManager;
    private List<SubTask> subTaskList = new ArrayList<>();
    private boolean isLoading = false;
    private int currentPage = 1; // Start from page 1
    private final int pageSize = 15; // Number of items per page
    private String jobId;

    public SetupRecyclerViewManager(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
    }

    public void setupRecyclerView(String jobId) {
        this.jobId = jobId;

        // Set up RecyclerView
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        subTaskAdapter = new SubTaskAdapter(subTaskList, context);
        recyclerView.setAdapter(subTaskAdapter);

        // Fetch the first page of data
        fetchPage(currentPage);

        // Add scroll listener for pagination
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Check if we've reached the last item and we're not already loading more
                if (!isLoading && layoutManager.findLastVisibleItemPosition() == subTaskList.size() - 1) {
                    currentPage++;
                    fetchPage(currentPage);
                }
            }
        });
    }

    private void fetchPage(int page) {
        isLoading = true;

        // Fetch secondary data for the current page
        NewBuildApiManager.fetchSecondaryApiData(jobId, page, pageSize, new NewBuildApiManager.ApiResponseCallback<SubTask>() {
            @Override
            public void onDataFetched(List<SubTask> secondaryData) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (secondaryData != null && !secondaryData.isEmpty()) {
                        subTaskList.addAll(secondaryData); // Add the fetched data to the list
                        subTaskAdapter.notifyDataSetChanged(); // Notify adapter to update the RecyclerView
                    } else {
                        Toast.makeText(context, "No more data available.", Toast.LENGTH_SHORT).show(); // Show a message when there's no more data
                    }
                    isLoading = false; // Set loading to false after the data is fetched
                });
            }

            @Override
            public void onError(String error) {
                ((Activity) context).runOnUiThread(() -> {
                    Toast.makeText(context, "Error fetching data: " + error, Toast.LENGTH_SHORT).show(); // Show error message if fetching fails
                    isLoading = false;
                });
            }
        });
    }
}
