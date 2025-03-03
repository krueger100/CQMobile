package com.example.cq_mobile.ui.home.HomeFolder.API_todo;

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

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private Context context;
    private List<Todo> todoList;

    public TodoAdapter(Context context, List<Todo> todoList) {
        this.context = context;
        this.todoList = todoList;
    }

    @Override
    public int getItemViewType(int position) {
        if (todoList == null || todoList.isEmpty()) {
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
            View view = LayoutInflater.from(context).inflate(R.layout.item_todo, parent, false);
            return new TodoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TodoViewHolder) {
            TodoViewHolder todoHolder = (TodoViewHolder) holder;
            Todo todo = todoList.get(position);




            todoHolder.nameTextView.setText(todo.getName());
            todoHolder.stateDescription.setText(todo.getDescription());
            String id = String.valueOf(todo.getId());
            String categories = todo.getCategory() != null ? todo.getCategory().trim() : "No Category";
            String categoriesColors = String.valueOf(todo.getCategory_color()).trim();
            Drawable categoryBackground = CategoryColorManager.getCategoryBackground(context, categoriesColors);

            if (id != null) {
                Log.d("User ID ->", "Received Todo ID's: " + id);
                Log.d("Category ->", "Category: " + categories + " | Color: " + categoriesColors);

                todoHolder.category.setText(categories);
                if (categoriesColors != null && !categoriesColors.isEmpty()) {
                    try {
                        int categoryColor = Color.parseColor(categoriesColors);
                        todoHolder.category.setTextColor(categoryColor);
                        todoHolder.category.setBackground(categoryBackground);
                    } catch (IllegalArgumentException e) {
                        Log.e("SubTaskAdapter", "Invalid category color format: " + categoriesColors, e);
                        todoHolder.category.setTextColor(ContextCompat.getColor(context, R.color.textBtnGrey));
                    }
                } else {
                    todoHolder.category.setTextColor(ContextCompat.getColor(context, R.color.textBtnGrey));
                }
            }

            todoHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TransitionAnimationManager.zoomOut(v, 150);
                    v.postDelayed(() -> {
                        v.postDelayed(() -> {
                            TransitionAnimationManager.zoomIn(v, 50);
                        todoHolder.progressBar.setVisibility(View.VISIBLE);
                        Intent intent = new Intent(context, NewBuild.class);
                        intent.putExtra("job_id", id);
                            intent.putExtra("task_id", id);
                        try {
                            context.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            todoHolder.progressBar.setVisibility(View.GONE);
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
        return (todoList == null || todoList.isEmpty()) ? 1 : todoList.size();
    }

    public static class TodoViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        TextView nameTextView, stateDescription, category;

        public TodoViewHolder(View itemView) {
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
