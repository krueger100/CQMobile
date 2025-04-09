package com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.SubTasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefTaskADandJobID;
import com.example.cq_mobile.LoginFolder.AuthManager;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.SpinnerFolder.SubTaskSpinnerAdapter;
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
    boolean isChecked;
    boolean isChecked_subTask;
    String accessToken;
    String taskId;
    String taskStatus;
    ProgressBar progressbar;
    public SubTaskAdapter(List<SubTask> secondaryDataList, Context context, String jobId, boolean isChecked, String accessToken, String taskId, ProgressBar progressbar) {
        this.context = context;
        this.jobId = jobId;
        this.accessToken = accessToken;
        this.isChecked = isChecked;
        this.taskId = taskId;
        this.progressbar = progressbar;

        this.secondaryDataList = secondaryDataList != null ? secondaryDataList : new ArrayList<>();
        initializeTaskIdList();
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

            isChecked_subTask  = task.isChecked();
            isChecked  = task.isChecked();
            Log.d("checkBoxData", "SUBTASK " + isChecked_subTask);
            Log.d("checkBoxData", "MAINTASK " + isChecked);


            String taskPriority = task.getPriority() != null ? task.getPriority().trim().toLowerCase() : "No Category";
            holder.priority.setText(taskPriority.substring(0, 1).toUpperCase() + taskPriority.substring(1).toLowerCase());

            Integer taskId = task.getId();
            if (taskId != null && !taskIdList.contains(taskId.toString())) {
                taskIdList.add(taskId.toString());
            }

            Log.w("SubTaskAdapter", "taskId <- " + taskId);

            SharedPrefTaskADandJobID sharedPrefTaskADandJobID = new SharedPrefTaskADandJobID(context);
            sharedPrefTaskADandJobID.saveIdAndJobId(String.valueOf(taskId));

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
            String accessToken = AuthManager.getInstance(context).getToken();
            int userId = sharedPrefManager.getUserId();
            String firstName = sharedPrefManager.getFirstName();
            String lastName = sharedPrefManager.getLastName();
            String email = sharedPrefManager.getEmail();

            Log.d("SubTaskAdapterManagerrSharedPreff", "Retrieved User Data: ");
            Log.d("SubTaskAdapterManagerrSharedPreff", "Access Token: " + accessToken);
            Log.d("SubTaskAdapterManagerrSharedPreff", "User ID: " + userId);
            Log.d("SubTaskAdapterManagerrSharedPreff", "First Name: " + firstName);
            Log.d("SubTaskAdapterManagerrSharedPreff", "Last Name: " + lastName);
            Log.d("SubTaskAdapterManagerrSharedPreff", "Email: " + email);

            Log.d("SubTaskAdapter", "Task ID" + taskId);

            // Handle task status and spinner setup
             taskStatus = task.getStatus();
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
            SubTaskSpinnerAdapter spinnerAdapter = new SubTaskSpinnerAdapter(context, R.layout.task_spinner_item, options, jobId, holder.spinner_task_imageBackground, taskStatus, taskId.toString(),accessToken,
            holder.progressbar_subtask,holder.progress_text_subtask);

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

            String is_check_singleData = String.valueOf(isChecked);
            String is_check_listData = String.valueOf(isChecked_subTask);

            if (is_check_singleData.equals("true") && is_check_listData.equals(true)){
                holder.checkBox.setChecked(false);
                Log.d("checkBoxData", "is_check1->>>>>" + is_check_singleData);
                Log.d("checkBoxData", "is_check2->>>>>" + is_check_listData);
            }else {
                holder.checkBox.setChecked(true);
                Log.d("checkBoxData", "is_check1->>>>>" + is_check_singleData);
                Log.d("checkBoxData", "is_check2->>>>>" + is_check_listData);
            }
            holder.checkBox.setEnabled(false);
          holder.checkBox.setChecked(isChecked);



            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (taskId != null) { // Ensure taskId is not null
                        Intent intent = new Intent(context, TaskActivity.class);
                        intent.putExtra("jobId", jobId);
                        intent.putExtra("taskId", taskId);
                        intent.putExtra("title", title);
                        intent.putExtra("description", description);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
        TextView textViewTitle, textViewDescription, priority,progress_text_subtask;
        Spinner taskSpinner;
        ImageView spinner_task_imageBackground;
        ProgressBar progressbar_subtask;

CheckBox checkBox;
        public SecondaryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.secondary_item_title);
            textViewDescription = itemView.findViewById(R.id.secondary_item_description);
            taskSpinner = itemView.findViewById(R.id.spinner_task);
            priority = itemView.findViewById(R.id.priority);
            spinner_task_imageBackground = itemView.findViewById(R.id.spinner_task_imageBackground);
            checkBox = itemView.findViewById(R.id.checkBox);
            progressbar_subtask  = itemView.findViewById(R.id.progressbar_subtask);
            progress_text_subtask  = itemView.findViewById(R.id.progress_text_subtask);

        }
    }
}
