package com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.RetrieveDataFromAPIMangers;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.NewBuildApiManager;
import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.SubTasks.SubTask;
import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.SubTasks.SubTaskAdapter;

import java.util.ArrayList;
import java.util.List;

public class SetupTaskRecyclerViewManager {

    private Context context;
    private RecyclerView recyclerView;
    private SubTaskAdapter subTaskAdapter;
    private LinearLayoutManager layoutManager;
    private List<SubTask> subTaskList = new ArrayList<>();
    private boolean isLoading = false;
    private int currentPage = 1; // Start from page 1
    private final int pageSize = 10; // Number of items per page (updated to 10)
    private String jobId;
    boolean isChecked;
    String accessToken;
 String taskId;
    String taskID;
    ProgressBar progressbar;
    TextView progress_text;
    public SetupTaskRecyclerViewManager(Context context, RecyclerView recyclerView, ProgressBar progressbar, TextView progress_text, String taskID) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.progressbar = progressbar;
        this.progress_text = progress_text;
        this.taskID = taskID;
    }

    public void setupRecyclerView(String jobId, boolean isChecked, String accessToken, String taskId) {
        this.jobId = jobId;
        this.isChecked = isChecked;
        this.accessToken = accessToken;
        this.taskId = taskId;

        Log.w("SubTaskAdapter", "taskId -> " + taskId);
        Log.w("SubTaskAdapter", "taskId -->>" + taskID);

        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        subTaskAdapter = new SubTaskAdapter(subTaskList, context, jobId,isChecked,accessToken,taskId,progressbar);
        recyclerView.setAdapter(subTaskAdapter);
        Log.d("checkBoxData", "isChecked From SetupTaskRecyclerViewManager: " + isChecked);
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

        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
        String accessToken = sharedPrefManager.getAccessToken();
        int userId = sharedPrefManager.getUserId();
        String firstName = sharedPrefManager.getFirstName();
        String lastName = sharedPrefManager.getLastName();
        String email = sharedPrefManager.getEmail();

        Log.d("SetupRecyclerViewManagerrSharedPreff", "Retrieved User Data: ");
        Log.d("SetupRecyclerViewManagerrSharedPreff", "Access Token: " + accessToken);
        Log.d("SetupRecyclerViewManagerrSharedPreff", "User ID: " + userId);
        Log.d("SetupRecyclerViewManagerrSharedPreff", "First Name: " + firstName);
        Log.d("SetupRecyclerViewManagerrSharedPreff", "Last Name: " + lastName);
        Log.d("SetupRecyclerViewManagerrSharedPreff", "Email: " + email);

        NewBuildApiManager.fetchSecondaryApiData(jobId, page, pageSize,accessToken, new NewBuildApiManager.ApiResponseCallback<SubTask>() {
            @Override
            public void onDataFetched(List<SubTask> secondaryData) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (secondaryData != null && !secondaryData.isEmpty()) {
                        subTaskList.addAll(secondaryData); // Add the fetched data to the list
                        subTaskAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("No more data.", "No more data available.");
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
