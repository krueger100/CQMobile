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

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.MessageViewHolder> {

    private Context context;
    private List<Job> jobList;

    public TodoAdapter(Context context, List<Job> jobList) {
        this.context = context;
        this.jobList = jobList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_todo, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Job job = jobList.get(position);
        holder.nameTextView.setText(job.getName());
        holder.stateDescription.setText(job.getDescription());
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView, stateDescription;

        public MessageViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.Site_preparation);
            stateDescription = itemView.findViewById(R.id.state_description);
        }
    }
}
