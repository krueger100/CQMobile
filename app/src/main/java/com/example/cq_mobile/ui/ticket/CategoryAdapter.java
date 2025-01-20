package com.example.cq_mobile.ui.ticket;

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

import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.ticket.TicketAPICategoryFolder.TicketAPICategoryItems;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private static final String TAG = "CategoryAdapter";
    private List<TicketAPICategoryItems> itemList;
    private String accessToken;
    private Context context;

    public CategoryAdapter(Context context, String accessToken, List<TicketAPICategoryItems> tickets) {
        this.context = context;
        this.accessToken = accessToken;
        this.itemList = tickets;  // Make sure to initialize the list with the provided tickets
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

        // Set the category name
        holder.categoryName.setText(item.getName());

        // If you want to set the color of the category, assuming it's a color string or resource ID
        if (item.getColor() != null) {
            // Example, assuming getColor() returns a color code or color resource ID
            holder.categoryNameColor.setColorFilter(Color.parseColor(item.getColor())); // or use getColor(context)
        }
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
