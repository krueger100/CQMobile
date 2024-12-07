package com.example.cq_mobile.ui.home.HomeFolder.API_todo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.R;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private Context context;
    private List<Job> jobList;

    public TodoAdapter(Context context, List<Job> jobList) {
        this.context = context;
        this.jobList = jobList;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        Job job = jobList.get(position);
        holder.nameTextView.setText(job.getName());
        holder.stateDescription.setText(job.getDescription());
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class TodoViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView, stateDescription;

        public TodoViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.Site_preparation);
            stateDescription = itemView.findViewById(R.id.state_description);
        }
    }
}
