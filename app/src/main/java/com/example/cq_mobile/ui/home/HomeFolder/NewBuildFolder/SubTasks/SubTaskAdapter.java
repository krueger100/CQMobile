package com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.SubTasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
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
    int[] dropDownColors;
    int[] ViewItemColors;
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


        holder.textViewTitle.setText(task.getTitle());
        holder.textViewDescription.setText(task.getDescription());

        String taskPriority = task.getPriority().trim().toLowerCase();  // Clean and standardize priority
        holder.priority.setText(taskPriority.substring(0, 1).toUpperCase() + taskPriority.substring(1).toLowerCase());
        Log.d("taskStatus", "Priority not found in options list: " + taskPriority);

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
                    // Handle unexpected values
                    holder.priority.setBackground(ContextCompat.getDrawable(context, R.drawable.button_red));
                    holder.priority.setTextColor(ContextCompat.getColor(context, R.color.textBtnRed)); // Default color
                    Log.d("SubTaskAdapter", "Priority not found in options list: " + taskPriority);
                    break;
            }

        }else {
            Log.d("SubTaskAdapter", "Invalid task priority: " + taskPriority);
        }




        String taskStatus = task.getStatus();
        taskStatus = taskStatus.replace("_", " ").trim() .replaceAll("\\s+", " ");
        taskStatus = taskStatus.substring(0, 1).toUpperCase() + taskStatus.substring(1).toLowerCase();


        List<String> options = new ArrayList<>();
        options.add(taskStatus);

        if (!taskStatus.equals("In progress")) {
            options.add("In Progress");
        }
        if (!taskStatus.equals("Pending")) {
            options.add("Pending");

        }
        if (!taskStatus.equals("Under inspection")) {
            options.add("Under inspection");

        }
        if (!taskStatus.equals("Done")) {
            options.add("Done");

        }

        int imageResource;
        switch (taskStatus) {
            case "In progress":
                imageResource = R.drawable.button_orange;
                break;
            case "Pending":
                imageResource = R.drawable.button_red;
                break;
            case "Under inspection":
                imageResource = R.drawable.button_blue;
                break;
            case "Done":
                imageResource = R.drawable.button_green;
                break;
            default:
                imageResource = R.drawable.button_red;
                break;
        }
        holder.spinner_task_imageBackground.setImageResource(imageResource);


        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(
                context,
                R.layout.task_spinner_item,
                options
        );



        holder.taskSpinner.setAdapter(adapter);
        int defaultIndex = options.indexOf(taskStatus);
        if (defaultIndex != -1) {
            holder.taskSpinner.setSelection(defaultIndex);
        } else {
            Log.d("SubTaskAdapter", "Priority not found in options list: " + taskStatus);
        }
        holder.taskSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int spinnerPosition, long id) {
                // Ensure spinnerPosition is within bounds of the options list
                if (spinnerPosition >= 0 && spinnerPosition < options.size()) {
                    String selectedOption = options.get(spinnerPosition);
                } else {
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
TextView priority;
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

