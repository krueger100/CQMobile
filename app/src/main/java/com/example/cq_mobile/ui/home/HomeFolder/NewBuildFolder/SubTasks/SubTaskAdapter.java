package com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.SubTasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.Clock.ClockActivity;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.SpinnerFolder.SubTaskSpinnerAdapter;
import com.example.cq_mobile.ui.home.HomeFolder.TaskFolder.TaskActivity;

import java.util.ArrayList;
import java.util.List;

public class SubTaskAdapter extends RecyclerView.Adapter<SubTaskAdapter.SecondaryViewHolder> {
    private Context context;
    private final List<SubTask> secondaryDataList;
    private String jobId;
    private final List<String> taskIdList = new ArrayList<>(); // List to store task IDs
    String title;
    String description;
    public SubTaskAdapter(List<SubTask> secondaryDataList, Context context, String jobId) {
        this.context = context;
        this.jobId = jobId;
        this.secondaryDataList = secondaryDataList != null ? secondaryDataList : new ArrayList<>();
        initializeTaskIdList(); // Initialize taskId list
    }

    // Initialize the taskIdList during adapter creation
    private void initializeTaskIdList() {
        taskIdList.clear();
        for (SubTask task : secondaryDataList) {
            if (task != null && task.getId() != null) {
                taskIdList.add(String.valueOf(task.getId()));
            }
        }
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

        if (task != null) {
            // Set title and description
            holder.textViewTitle.setText(task.getTitle());
            holder.textViewDescription.setText(task.getDescription());
            title = task.getTitle();
            description = task.getDescription();

            String taskPriority = task.getPriority() != null ? task.getPriority().trim().toLowerCase() : "No Category";
            holder.priority.setText(taskPriority.substring(0, 1).toUpperCase() + taskPriority.substring(1).toLowerCase());

            Integer taskId = task.getId(); // Now it's an Integer object, which can be null
            if (taskId != null && !taskIdList.contains(taskId.toString())) {
                taskIdList.add(taskId.toString());
            }

            Log.d("taskId", "taskId " + taskId);

            // Apply background and text color based on priority
            if (taskPriority != null && !taskPriority.isEmpty()) {
                switch (taskPriority) {
                    case "low":
                        holder.priority.setBackground(ContextCompat.getDrawable(context, R.drawable.button_blue));
                        holder.priority.setTextColor(ContextCompat.getColor(context, R.color.textBtnBlue));
                        break;
                    case "medium":
                        holder.priority.setBackground(ContextCompat.getDrawable(context, R.drawable.button_green));
                        holder.priority.setTextColor(ContextCompat.getColor(context, R.color.textBtnGreen));
                        break;
                    case "high":
                        holder.priority.setBackground(ContextCompat.getDrawable(context, R.drawable.button_red));
                        holder.priority.setTextColor(ContextCompat.getColor(context, R.color.textBtnRed));
                        break;
                    default:
                        holder.priority.setBackground(ContextCompat.getDrawable(context, R.drawable.button_grey));
                        holder.priority.setTextColor(ContextCompat.getColor(context, R.color.textBtnGrey));
                        Log.d("SubTaskAdapter", "Priority not found in options list: " + taskPriority);
                        break;
                }
            } else {
                Log.d("SubTaskAdapter", "Invalid task priority: " + taskPriority);
            }

            SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
            String accessToken = sharedPrefManager.getAccessToken();
            String userId = sharedPrefManager.getUserId();
            String firstName = sharedPrefManager.getFirstName();
            String lastName = sharedPrefManager.getLastName();
            String email = sharedPrefManager.getEmail();

            Log.d("SubTaskAdapterManagerrSharedPreff", "Retrieved User Data: ");
            Log.d("SubTaskAdapterManagerrSharedPreff", "Access Token: " + accessToken);
            Log.d("SubTaskAdapterManagerrSharedPreff", "User ID: " + userId);
            Log.d("SubTaskAdapterManagerrSharedPreff", "First Name: " + firstName);
            Log.d("SubTaskAdapterManagerrSharedPreff", "Last Name: " + lastName);
            Log.d("SubTaskAdapterManagerrSharedPreff", "Email: " + email);




            // Handle task status and spinner setup
            String taskStatus = task.getStatus();
            taskStatus = taskStatus.replace("_", " ").trim().replaceAll("\\s+", " ");
            taskStatus = taskStatus.substring(0, 1).toUpperCase() + taskStatus.substring(1).toLowerCase();

            Log.d("Stats", "subTask -> " + taskStatus);
            List<String> options = new ArrayList<>();
            options.add(taskStatus);

            if (!taskStatus.equals("In progress")) options.add("In Progress");
            if (!taskStatus.equals("Pending")) options.add("Pending");
            if (!taskStatus.equals("Under inspection")) options.add("Under inspection");
            if (!taskStatus.equals("Done")) options.add("Done");

            // Create spinner adapter
            SubTaskSpinnerAdapter spinnerAdapter = new SubTaskSpinnerAdapter(
                    context,
                    R.layout.task_spinner_item,
                    options,
                    jobId,
                    holder.spinner_task_imageBackground,
                    taskStatus,
                    taskId.toString(), // Pass taskId as string to the spinner adapter
                    accessToken );

            holder.taskSpinner.setAdapter(spinnerAdapter);
            int defaultIndex = options.indexOf(taskStatus);
            if (defaultIndex != -1) {
                holder.taskSpinner.setSelection(defaultIndex);
            } else {
                Log.d("SubTaskAdapter", "Task status not found in options list: " + taskStatus);
            }

            // Handle spinner item selection
            holder.taskSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int spinnerPosition, long id) {
                    if (spinnerPosition >= 0 && spinnerPosition < options.size()) {
                        String selectedOption = options.get(spinnerPosition);
                        Log.d("SubTaskAdapter", "Selected spinner option: " + selectedOption);
                    } else {
                        Log.d("SubTaskAdapter", "Selected spinner position is out of bounds");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // No action
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (taskId != null) { // Ensure taskId is not null
                        Intent intent = new Intent(context, TaskActivity.class);
                        intent.putExtra("jobId", jobId); // Pass jobId as a string
                        intent.putExtra("taskId", taskId); // Pass taskId as an integer
                        intent.putExtra("title", title);
                        intent.putExtra("description", description);
                        context.startActivity(intent);
                    } else {

                        Log.d("SubTaskAdapter", "Task ID is null, cannot navigate to TaskActivity.");
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return secondaryDataList.size();
    }

    // Method to get the taskId list
    public List<String> getTaskIdList() {
        return new ArrayList<>(taskIdList); // Return a copy to prevent external modification
    }

    static class SecondaryViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDescription, priority;
        Spinner taskSpinner;
        ImageView spinner_task_imageBackground;

        public SecondaryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.secondary_item_title);
            textViewDescription = itemView.findViewById(R.id.secondary_item_description);
            taskSpinner = itemView.findViewById(R.id.spinner_task);
            priority = itemView.findViewById(R.id.priority);
            spinner_task_imageBackground = itemView.findViewById(R.id.spinner_task_imageBackground);
        }
    }
}
