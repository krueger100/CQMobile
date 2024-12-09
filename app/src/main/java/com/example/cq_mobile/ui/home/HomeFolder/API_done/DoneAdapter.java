package com.example.cq_mobile.ui.home.HomeFolder.API_done;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.NewBuild;


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
        String id = String.valueOf(done.getId());
        holder.new_built.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.progressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(context, NewBuild.class);
                intent.putExtra("job_id", id);
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    holder.progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return doneList.size();

    }
    public static class DoneViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        TextView nameTextView, stateDescription,new_built;

        public DoneViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.Site_preparation);
            stateDescription = itemView.findViewById(R.id.state_description);
            new_built = itemView.findViewById(R.id.new_built);
            progressBar = itemView.findViewById(R.id.progressBar);

        }
    }
}