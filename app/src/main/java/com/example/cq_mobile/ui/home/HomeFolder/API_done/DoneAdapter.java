package com.example.cq_mobile.ui.home.HomeFolder.API_done;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.HelperManagers.Animation.TransitionAnimationManager;
import com.example.cq_mobile.HelperManagers.CategoryColorManager;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.NewBuild;
import com.example.cq_mobile.ui.ticket.CreateFolder.CreateTicket;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DoneAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private Context context;
    private List<Done> doneList;
    private Set<String> uniqueIds;  // Set to track unique IDs

    public DoneAdapter(Context context, List<Done> doneList) {
        this.context = context;
        this.doneList = doneList;
        this.uniqueIds = new HashSet<>();
        addUniqueItemsToList(doneList);
    }
    private void addUniqueItemsToList(List<Done> doneList) {
        if (doneList != null) {
            for (Done done : doneList) {
                if (done != null && done.getId() != -1) { // Check for invalid ID value instead of null
                    uniqueIds.add(String.valueOf(done.getId()));
                }
            }
        }
    }

    public void setDoneList(List<Done> newDoneList) {
        // Clear the current skipped list
        doneList.clear();
        uniqueIds.clear();

        // Add new items to the list and set
        addUniqueItemsToList(newDoneList);
        doneList.addAll(newDoneList);

        // Notify that the data has been updated
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (doneList == null || doneList.isEmpty()) {
            return VIEW_TYPE_EMPTY;
        }
        return VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_EMPTY) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_empty_state, parent, false);
            return new EmptyViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_done, parent, false);
            return new DoneViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DoneViewHolder) {
            DoneViewHolder doneHolder = (DoneViewHolder) holder;
            Done done = doneList.get(position);

            doneHolder.nameTextView.setText(done.getName());
            doneHolder.stateDescription.setText(done.getDescription());
            String id = String.valueOf(done.getId());

            String categories = done.getCategory() != null ? done.getCategory().trim() : "Empty Category";
            String categoriesColors = String.valueOf(done.getCategory_color()).trim();
            Drawable categoryBackground = CategoryColorManager.getCategoryBackground(context, categoriesColors);

            if (id != null) {
                Log.d("User ID ->", "Received Todo ID's: " + id);
                Log.d("Category ->", "Category: " + categories + " | Color: " + categoriesColors);

                doneHolder.category.setText(categories);
                if (categoriesColors != null && !categoriesColors.isEmpty()) {
                    try {
                        int categoryColor = Color.parseColor(categoriesColors);
                        doneHolder.category.setTextColor(categoryColor);
                        doneHolder.category.setBackground(categoryBackground);
                    } catch (IllegalArgumentException e) {
                        Log.e("DoneAdapter", "Invalid category color format: " + categoriesColors, e);
                        doneHolder.category.setTextColor(ContextCompat.getColor(context, R.color.textBtnGrey));
                    }
                } else {
                    doneHolder.category.setTextColor(ContextCompat.getColor(context, R.color.textBtnGrey));
                }
            }


            doneHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TransitionAnimationManager.zoomOut(v, 100);
                    v.postDelayed(() -> {
                        v.postDelayed(() -> {
                            TransitionAnimationManager.zoomIn(v, 50);
                        }, 100);
                        doneHolder.progressBar.setVisibility(View.VISIBLE);
                        Intent intent = new Intent(context, NewBuild.class);
                        intent.putExtra("job_id", id);

                        try {
                            context.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            doneHolder.progressBar.setVisibility(View.GONE);
                        }
                    }, 100);


                }
            });


        } else if (holder instanceof EmptyViewHolder) {
            // Optional: Handle empty view logic if needed.
            ((EmptyViewHolder) holder).empty_state_text.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return (doneList == null || doneList.isEmpty()) ? 1 : doneList.size();
    }

    public static class DoneViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        TextView nameTextView, stateDescription, category;

        public DoneViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.Site_preparation);
            stateDescription = itemView.findViewById(R.id.state_description);
            category = itemView.findViewById(R.id.category);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    public static class EmptyViewHolder extends RecyclerView.ViewHolder {
        TextView empty_state_text;
        public EmptyViewHolder(View itemView) {
            super(itemView);
            empty_state_text = itemView.findViewById(R.id.empty_state_text);
        }
    }
}