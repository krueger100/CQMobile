package com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.SubTasks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.R;

import java.util.ArrayList;
import java.util.List;

public class SecondaryAdapter extends RecyclerView.Adapter<SecondaryAdapter.SecondaryViewHolder> {

    private final List<SecondaryTask> secondaryDataList;

    public SecondaryAdapter(List<SecondaryTask> secondaryDataList) {
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
    public void onBindViewHolder(@NonNull SecondaryViewHolder holder, int position) {
        SecondaryTask task = secondaryDataList.get(position);
        holder.textViewTitle.setText(task.getTitle());
        holder.textViewDescription.setText(task.getDescription());
    }

    @Override
    public int getItemCount() {
        return secondaryDataList.size();
    }

    static class SecondaryViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDescription;

        public SecondaryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.secondary_item_title);
            textViewDescription = itemView.findViewById(R.id.secondary_item_description);
        }
    }
}
