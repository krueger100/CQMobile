package com.example.cq_mobile.ui.home.HomeFolder.TaskFolder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.UpdateJobsFolder.UpdateMainTaskApiManagerCheckBox;
import com.example.cq_mobile.ui.home.UpdateJobsFolder.UpdateSubTaskApiManagerCheckBox;

import java.util.List;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<String> taskList;
    private List<String> checked_List;
    List<String> checked_Id;
    private Context context;
    String accessToken;
    String jobId;
    int taskId;
    public TaskAdapter(Context context, List<String> taskList, List<String> checked_List, List<String> checked_Id, String accessToken, String jobId, int taskId) {
        this.context = context;
        this.taskList = taskList;
        this.checked_List = checked_List;
        this.checked_Id = checked_Id;
        this.accessToken = accessToken;
        this.jobId = jobId;
        this.taskId = taskId;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task_checklist, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        String name = taskList.get(position);
        String checked = checked_List.get(position);
        String Id = checked_Id.get(position);


        holder.taskNameTextView.setText(name);
        Log.d("TaskAdapter", "Displaying name: " + name +" checked: "+checked);
        if (checked.equals("1")){
          holder.checkBox.setChecked(true);
        }else {
            holder.checkBox.setChecked(false);
        }
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Update the local variable to reflect the new state
                boolean isChecked_task = isChecked;
                // Log the state of the checkbox for debugging
                Log.d("checkBoxData", "Selected checkBox " + (isChecked_task ? "checked" : "unchecked"));

                // Get the checklist item ID and pass it to the API
                String checklistItemId = String.valueOf(Id); // Replace with actual checklist item ID

                // Call the API to update the task status
                UpdateMainTaskApiManagerCheckBox.updateMainTaskApiManager(accessToken, jobId, String.valueOf(taskId), checklistItemId, isChecked_task);
                UpdateSubTaskApiManagerCheckBox.updateSub_TaskApiManager(accessToken, jobId, String.valueOf(taskId), checklistItemId, isChecked_task);
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
