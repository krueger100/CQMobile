package com.example.cq_mobile.ui.ticket;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.HelperManagers.CloseKeyboardManager;
import com.example.cq_mobile.LoginFolder.ThreadManager;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.ticket.TicketAPICategoryFolder.TicketAPICategoryItems;

import java.util.List;
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private static final String TAG = "CategoryAdapter";
    private List<TicketAPICategoryItems> itemList;
    private String accessToken;
    private Context context;
    private OnCategoryClickListener onCategoryClickListener;

    public interface OnCategoryClickListener {
        void onCategoryClick(int categoryId);
    }

    public CategoryAdapter(Context context, String accessToken, List<TicketAPICategoryItems> tickets, OnCategoryClickListener listener) {
        this.context = context;
        this.accessToken = accessToken;
        this.itemList = tickets;
        this.onCategoryClickListener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder called for viewType: " + viewType);
        View view = LayoutInflater.from(context).inflate(R.layout.ticket_category_recycler_view, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        TicketAPICategoryItems item = itemList.get(position);
        Log.d(TAG, "Binding item at position: " + position);

        // Using the main thread to update the UI
        ThreadManager.runOnMainThread(() -> {
            holder.categoryName.setText(item.getName());
            if (item.getColor() != null) {
                holder.categoryNameColor.setColorFilter(Color.parseColor(item.getColor()));
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (onCategoryClickListener != null) {
                onCategoryClickListener.onCategoryClick(item.getId());
                CloseKeyboardManager.closeKeyboard((Activity) context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        ImageView categoryNameColor;

        public CategoryViewHolder(@NonNull View categoryView) {
            super(categoryView);
            categoryName = categoryView.findViewById(R.id.categoryName_name);
            categoryNameColor = categoryView.findViewById(R.id.categoryName_color);
        }
    }

}
