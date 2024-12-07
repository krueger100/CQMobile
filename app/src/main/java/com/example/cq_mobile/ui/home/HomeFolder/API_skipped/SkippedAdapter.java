package com.example.cq_mobile.ui.home.HomeFolder.API_skipped;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cq_mobile.R;


import java.util.List;

public class SkippedAdapter extends RecyclerView.Adapter<SkippedAdapter.SkippedViewHolder> {
    private Context context;
    private List<Skipped> skippedList;

    public SkippedAdapter(Context context, List<Skipped> skippedList) {
        this.context = context;
        this.skippedList = skippedList;
    }

    @NonNull
    @Override
    public SkippedAdapter.SkippedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_skipped, parent, false);
        return new SkippedAdapter.SkippedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SkippedAdapter.SkippedViewHolder holder, int position) {
        Skipped skipped = skippedList.get(position);
        holder.nameTextView.setText(skipped.getName());
        holder.stateDescription.setText(skipped.getDescription());
    }

    @Override
    public int getItemCount() {
        return skippedList.size();
    }
    public static class SkippedViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView, stateDescription;

        public SkippedViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.Site_preparation);
            stateDescription = itemView.findViewById(R.id.state_description);
        }
    }
}