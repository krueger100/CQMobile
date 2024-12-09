package com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cq_mobile.R;
import java.util.ArrayList;
import java.util.List;

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
        holder.taskTitle.setText(taskmain.getName());
        holder.taskDescription.setText(taskmain.getDescription());

        String clientInfo = taskmain.getClient_details() != null ?
                taskmain.getClient_details().getPhone()  : "No client info";
        holder.clientInfo.setText(clientInfo);

        // Example for address
        String address = taskmain.getAddress() != null ? taskmain.getAddress().getAddress() : "No address";
        holder.taskAddress.setText(address);
    }

    @Override
    public int getItemCount() {
        return taskmainList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle;
        TextView taskDescription;
        TextView clientInfo;  // Assuming you added a view for client info in task_item.xml
        TextView taskAddress; // Assuming you added a view for address in task_item.xml

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.task_title);
            taskDescription = itemView.findViewById(R.id.task_description);
            clientInfo = itemView.findViewById(R.id.task_number);  // New TextView for client info
            taskAddress = itemView.findViewById(R.id.task_location); // New TextView for address
        }
    }
}
