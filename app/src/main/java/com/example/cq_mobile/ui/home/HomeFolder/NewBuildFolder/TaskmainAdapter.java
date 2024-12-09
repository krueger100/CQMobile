package com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.SubTasks.SpinnerFolder.CustomSpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class TaskmainAdapter extends RecyclerView.Adapter<TaskmainAdapter.TaskViewHolder> {

    private final List<Taskmain> taskmainList;

    public TaskmainAdapter(List<Taskmain> taskmainList) {
        this.taskmainList = taskmainList != null ? taskmainList : new ArrayList<>();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Taskmain taskmain = taskmainList.get(position);

        // Set text views
        holder.taskTitle.setText(taskmain.getName());
        holder.taskDescription.setText(taskmain.getDescription());

        String clientInfo = taskmain.getClient_details() != null ?
                taskmain.getClient_details().getPhone() : "No client info";
        holder.clientInfo.setText(clientInfo);

        String address = taskmain.getAddress() != null ? taskmain.getAddress().getAddress() : "No address";
        holder.taskAddress.setText(address);

        // Populate Spinner with options
        List<String> options = new ArrayList<>();
        options.add("In Progress");
        options.add("Pending");
        options.add("Under Inspection");
        options.add("Done");

        // Custom Adapter for Spinner
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(
                holder.itemView.getContext(),
                R.layout.task_spinner_item,  // Custom layout for dropdown items
                options,                // List of options for the spinner
                R.drawable.arrow_down_24  // Icon for each item
        );

        // Set custom adapter to Spinner
        holder.taskSpinner.setAdapter(adapter);

        // Spinner item selection listener
        holder.taskSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int spinnerPosition, long id) {
                if (spinnerPosition > 0) { // Ignore "Select Action"
                    String selectedOption = options.get(spinnerPosition);
                    Toast.makeText(view.getContext(),
                            "Selected: " + selectedOption + " for Task: " + taskmain.getName(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action
            }
        });


        holder.taskSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int spinnerPosition, long id) {
                if (spinnerPosition > 0) { // Ignore "Select Action"
                    String selectedOption = options.get(spinnerPosition);
                    Toast.makeText(view.getContext(),
                            "Selected: " + selectedOption + " for Task: " + taskmain.getName(),
                            Toast.LENGTH_SHORT).show();
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
        return taskmainList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle;
        TextView taskDescription;
        TextView clientInfo;
        TextView taskAddress;
        Spinner taskSpinner; // Reference to Spinner

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.task_title);
            taskDescription = itemView.findViewById(R.id.task_description);
            clientInfo = itemView.findViewById(R.id.task_number);
            taskAddress = itemView.findViewById(R.id.task_location);
            taskSpinner = itemView.findViewById(R.id.spinner_task); // Link Spinner
        }
    }
}
