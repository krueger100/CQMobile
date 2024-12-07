package com.example.cq_mobile.ui.home.HomeFolder.API_done;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cq_mobile.R;


import java.util.List;

public class DoneAdapter extends RecyclerView.Adapter<DoneAdapter.DoneViewHolder> {
    private Context context;
    private List<Done> doneList;

    public DoneAdapter(Context context, List<Done> doneList) {
        this.context = context;
        this.doneList = doneList;
    }

    @NonNull
    @Override
    public DoneAdapter.DoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_done, parent, false);
        return new DoneAdapter.DoneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoneAdapter.DoneViewHolder holder, int position) {
        Done done = doneList.get(position);
        holder.nameTextView.setText(done.getName());
        holder.stateDescription.setText(done.getDescription());
    }

    @Override
    public int getItemCount() {
        return doneList.size();

    }
    public static class DoneViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView, stateDescription;

        public DoneViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.Site_preparation);
            stateDescription = itemView.findViewById(R.id.state_description);
        }
    }
}