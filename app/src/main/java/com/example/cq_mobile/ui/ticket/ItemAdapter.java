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

import com.example.cq_mobile.ui.ticket.TicketsFolder.TicketAPIItem;

import java.util.List;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private static final String TAG = "ItemAdapter";
    private List<TicketAPIItem> itemList;
    private String accessToken;
    private Context context;

    public ItemAdapter(Context context, String accessToken, List<TicketAPIItem> itemList) {
        this.context = context;
        this.accessToken = accessToken;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder called for viewType: " + viewType);
        View view = LayoutInflater.from(context).inflate(R.layout.ticket_item_recycler_view, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        TicketAPIItem item = itemList.get(position);
        Log.d(TAG, "Binding item at position: " + position);
        holder.item_subject.setText(item.getSubject());
        holder.item_category.setText(item.getCategory().getName());
       holder.item_status.setText(item.getStatus());
        holder.itemColor.setColorFilter(Color.parseColor(item.getCategory().getColor()));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView item_subject, item_category,item_status;
ImageView itemColor;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            item_status = itemView.findViewById(R.id.item_status);
            item_subject = itemView.findViewById(R.id.item_subject);
            item_category = itemView.findViewById(R.id.item_category);
            itemColor = itemView.findViewById(R.id.indicator_green); // Uncomment if itemColor is used
        }
    }
}