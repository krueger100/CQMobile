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

import com.example.cq_mobile.HelperManagers.CategoryColorManager;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.NewBuild;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private Context context;
    private List<Todo> todoList;

    public TodoAdapter(Context context, List<Todo> todoList) {
        this.context = context;
        this.todoList = todoList;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        Todo todo = todoList.get(position);
        holder.nameTextView.setText(todo.getName());
        holder.stateDescription.setText(todo.getDescription());
        String id = String.valueOf(todo.getId());
        String categories = String.valueOf(todo.getCategory()).trim();
        String categoriesColors = String.valueOf(todo.getCategory_color()).trim();
        Drawable categoryBackground = CategoryColorManager.getCategoryBackground(context, categoriesColors);

        if (id != null) {
            Log.d("User ID ->", "Received Todo ID's: " + id);
            Log.d("Category ->", "Category: " + categories + " | Color: " + categoriesColors);
        holder.category.setText(categories);
        if (categoriesColors != null && !categoriesColors.isEmpty()) {
            try {
                int categoryColor = Color.parseColor(categoriesColors);
                holder.category.setTextColor(categoryColor);
                holder.category.setBackground(categoryBackground);


            } catch (IllegalArgumentException e) {
                Log.e("SubTaskAdapter", "Invalid category color format: " + categoriesColors, e);
                holder.category.setTextColor(ContextCompat.getColor(context, R.color.textBtnRed));


            }
        } else {
            holder.category.setTextColor(ContextCompat.getColor(context, R.color.textBtnRed));


        }

        }
        holder.category.setOnClickListener(new View.OnClickListener() {
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
        return todoList.size();
    }

    public static class TodoViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        TextView nameTextView, stateDescription,category;

        public TodoViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.Site_preparation);
            stateDescription = itemView.findViewById(R.id.state_description);
            category = itemView.findViewById(R.id.category);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
