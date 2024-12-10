package com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.SubTasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.SpinnerFolder.CustomSpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SubTaskAdapter extends RecyclerView.Adapter<SubTaskAdapter.SecondaryViewHolder> {
    private Context context;

    private final List<SubTask> secondaryDataList;

    public SubTaskAdapter(List<SubTask> secondaryDataList,Context context) {
        this.context = context;
        this.secondaryDataList = secondaryDataList != null ? secondaryDataList : new ArrayList<>();

    }

    @NonNull
    @Override
    public SecondaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subtask_item, parent, false);
        return new SecondaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SecondaryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        SubTask task = secondaryDataList.get(position);
        holder.textViewTitle.setText(task.getName());
        holder.textViewDescription.setText(task.getDescription());

        List<String> options = new ArrayList<>();
        options.add("In Progress");
        options.add("Pending");
        options.add("Under Inspection");
        options.add("Done");

// Set up the adapter
        int[] dropDownColors = new int[]{
                ContextCompat.getColor(context, R.color.cq_secondary_color),
                ContextCompat.getColor(context, R.color.textBtnRed),
                ContextCompat.getColor(context, R.color.color_inspection),
                ContextCompat.getColor(context, R.color.color_done)
        };

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(
                context,
                R.layout.task_spinner_item,
                options,
                dropDownColors
        );
        holder.taskSpinner.setAdapter(adapter);
        holder.taskSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int spinnerPosition, long id) {
                // Ensure spinnerPosition is within bounds of the options list
                if (spinnerPosition >= 0 && spinnerPosition < options.size()) {
                    if (spinnerPosition > 0) { // Ignore "Select Action"
                        String selectedOption = options.get(spinnerPosition);
                        Toast.makeText(view.getContext(),
                                "Selected: " + selectedOption + " for Task: " + task.getName(),
                                Toast.LENGTH_SHORT).show();

                    }
                } else {
                    // Handle out-of-bounds case if necessary
                    Log.d("SubTaskAdapter", "Selected spinner position is out of bounds");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action
            }
        });
    }

    @Override
    public int getItemCount() {
        return secondaryDataList.size();
    }

    static class SecondaryViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDescription;
        Spinner taskSpinner;

        public SecondaryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.secondary_item_title);
            textViewDescription = itemView.findViewById(R.id.secondary_item_description);
            taskSpinner = itemView.findViewById(R.id.spinner_task);
        }
    }
}


/*
        setupSecondaryRecyclerView(jobId);

 private void setupSecondaryRecyclerView(String jobId) {
        RecyclerView secondaryRecyclerView = findViewById(R.id.recycler_view_secondary);
        secondaryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Fetch secondary tasks and update RecyclerView adapter using jobId
        NewBuildApiManager.fetchSecondaryApiData(jobId, new NewBuildApiManager.ApiResponseCallback<SubTask>() {
            @Override
            public void onDataFetched(List<SubTask> secondaryData) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    SubTaskAdapter secondaryAdapter = new SubTaskAdapter(secondaryData);
                    secondaryRecyclerView.setAdapter(secondaryAdapter);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() ->
                        Toast.makeText(NewBuild.this, "Error fetching secondary data: " + error, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }


 */


/*

API call

public class NewBuildApiManager {

    public interface ApiResponseCallback<T> {
        void onDataFetched(List<T> data);
        void onError(String error);
    }

    public static void fetchSecondaryApiData(String jobId, ApiResponseCallback<SubTask> callback) {
        String baseUrl = "https://aws.customquoter.co.uk";
        String endpoint = "/api/m/jobs/schedules/today?page=1&per_page=100&status=todo";
        String token = "3805|2NzKCMW8T6zH7sA25uEhxX2BOi1nzsqvvI2CRao4";
        String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";

        // Construct the full URL
        String url = String.format("%s%s?page=1&per_page=100&status=todo", baseUrl, endpoint);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("x-api-key", apiKey)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    Gson gson = new Gson();
                    SubTaskResponse secondaryResponse = gson.fromJson(jsonResponse, SubTaskResponse.class);

                    if (secondaryResponse != null && secondaryResponse.getData() != null) {
                        new Handler(Looper.getMainLooper()).post(() -> callback.onDataFetched(secondaryResponse.getData()));
                    } else {
                        new Handler(Looper.getMainLooper()).post(() -> callback.onError("No secondary data found."));
                    }
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError("Request Failed: " + response.code()));
                }
            }
        });
    }
}



 */