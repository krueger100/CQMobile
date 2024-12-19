package com.example.cq_mobile.ui.home.HomeFolder.TaskFolder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.R;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<String> taskList;  // A list of task names (or any other data you want to display)
    private Context context;

    public TaskAdapter(Context context, List<String> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task_checklist, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        String task = taskList.get(position);
        holder.taskNameTextView.setText(task);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle the CheckBox change event, e.g., mark task as completed
            if (isChecked) {
                Log.d("TaskAdapter", task + " is completed.");
            } else {
                Log.d("TaskAdapter", task + " is not completed.");
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskNameTextView;
        CheckBox checkBox;

        public TaskViewHolder(View itemView) {
            super(itemView);
            taskNameTextView = itemView.findViewById(R.id.taskNameTextView);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}
