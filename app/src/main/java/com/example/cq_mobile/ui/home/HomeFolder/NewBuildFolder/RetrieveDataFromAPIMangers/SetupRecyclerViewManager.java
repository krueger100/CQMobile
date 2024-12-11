package com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.RetrieveDataFromAPIMangers;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.NewBuildApiManager;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.SubTasks.SubTask;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.SubTasks.SubTaskAdapter;

import java.util.List;

public class SetupRecyclerViewManager {

    private Context context;
    private RecyclerView recyclerView;

    public SetupRecyclerViewManager(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
    }

    public void setupRecyclerView(String jobId) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        // Fetch secondary tasks and update RecyclerView adapter using jobId
        Log.d("jobId   ",jobId);
        NewBuildApiManager.fetchSecondaryApiData(jobId, new NewBuildApiManager.ApiResponseCallback<SubTask>() {
            @Override
            public void onDataFetched(List<SubTask> secondaryData) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    SubTaskAdapter subTaskAdapter = new SubTaskAdapter(secondaryData,context);
                    recyclerView.setAdapter(subTaskAdapter);
                });
            }

            @Override
            public void onError(String error) {
                ((Activity) context).runOnUiThread(() ->
                        Toast.makeText(context, "Fetching data: " + error, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}
