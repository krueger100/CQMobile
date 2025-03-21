package com.example.cq_mobile.ui.home.HomeFolder.API_skipped;

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
import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.NewBuild;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SkippedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private Context context;
    private List<Skipped> skippedList;
    private Set<String> uniqueIds;  // Set to track unique IDs

    public SkippedAdapter(Context context, List<Skipped> skippedList) {
        this.context = context;
        this.skippedList = skippedList;
        this.uniqueIds = new HashSet<>(); // Initialize the set to track unique IDs
        addUniqueItemsToList(skippedList); // Add existing items to the set
    }

    private void addUniqueItemsToList(List<Skipped> skippedList) {
        if (skippedList != null) {
            for (Skipped skipped : skippedList) {
                if (skipped != null && skipped.getId() != -1) { // Check for invalid ID value instead of null
                    uniqueIds.add(String.valueOf(skipped.getId()));
                }
            }
        }
    }

    public void setSkippedList(List<Skipped> newSkippedList) {
        // Clear the current skipped list
        skippedList.clear();
        uniqueIds.clear();

        // Add new items to the list and set
        addUniqueItemsToList(newSkippedList);
        skippedList.addAll(newSkippedList);

        // Notify that the data has been updated
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (skippedList == null || skippedList.isEmpty()) {
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
            View view = LayoutInflater.from(context).inflate(R.layout.item_skipped, parent, false);
            return new SkippedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SkippedViewHolder) {
            SkippedViewHolder skippedHolder = (SkippedViewHolder) holder;
            Skipped skipped = skippedList.get(position);

            skippedHolder.nameTextView.setText(skipped.getName());
            skippedHolder.stateDescription.setText(skipped.getDescription());
            String id = String.valueOf(skipped.getId());
            String jobid = String.valueOf(skipped.getJob_id());
            String categories = skipped.getCategory() != null ? skipped.getCategory().trim() : "Empty Category";
            String categoriesColors = String.valueOf(skipped.getCategory_color()).trim();
            if (categoriesColors == null || categoriesColors.trim().isEmpty()) {
                categoriesColors = "#FF0000"; // Default color if null
            } else {
                categoriesColors = categoriesColors.trim();
            }

            Drawable categoryBackground = CategoryColorManager.getCategoryBackground(context, categoriesColors);

            if (id != null) {
                Log.d("User ID ->", "Received Todo ID's: " + id);
                Log.d("User ID ->", "Received Todo jobID's: " +jobid );
                Log.d("Category ->", "Category: " + categories + " | Color: " + categoriesColors);

                skippedHolder.category.setText(categories);
                if (categoriesColors != null && !categoriesColors.isEmpty()) {
                    try {
                        int categoryColor = Color.parseColor(categoriesColors);
                        skippedHolder.category.setTextColor(categoryColor);
                        skippedHolder.category.setBackground(categoryBackground);
                    } catch (IllegalArgumentException e) {
                        Log.e("SkippedAdapter", "Invalid category color format: " + categoriesColors, e);
                        skippedHolder.category.setTextColor(ContextCompat.getColor(context, R.color.textBtnGrey));
                    }
                } else {
                    skippedHolder.category.setTextColor(ContextCompat.getColor(context, R.color.textBtnGrey));
                }
            }


            skippedHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TransitionAnimationManager.zoomOut(v, 150);
                    v.postDelayed(() -> {
                        v.postDelayed(() -> {
                            TransitionAnimationManager.zoomIn(v, 50);
                            skippedHolder.progressBar.setVisibility(View.VISIBLE);
                            Intent intent = new Intent(context, NewBuild.class);
                            intent.putExtra("job_id", id);
                            intent.putExtra("task_id", jobid);
                            try {
                                context.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                skippedHolder.progressBar.setVisibility(View.GONE);
                            }
                        }, 150);

                    }, 150);

                }
            });


        } else if (holder instanceof EmptyViewHolder) {
            // Optional: Handle empty view logic
            ((EmptyViewHolder) holder).empty_state_text.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return (skippedList == null || skippedList.isEmpty()) ? 1 : skippedList.size();
    }

    public static class SkippedViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        TextView nameTextView, stateDescription, category;

        public SkippedViewHolder(View itemView) {
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
