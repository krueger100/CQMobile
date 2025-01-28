package com.example.cq_mobile.ui.ticket;

import android.content.Context;
import android.content.Intent;
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

import com.example.cq_mobile.ui.ticket.ReplyTicketFolder.ReplyTicket;
import com.example.cq_mobile.ui.ticket.TicketsFolder.TicketAPIItem;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private static final String TAG = "ItemAdapter";
    private List<TicketAPIItem> itemList;
    List<String> messagesData = new ArrayList<>();

    private String accessToken;
    private Context context;
    View itemView;
    TicketAPIItem item;

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

        holder.item_subject.setText(item.getSubject());
        holder.item_category.setText(item.getCategory().getName());
       holder.item_status.setText(item.getStatus());
        holder.indicator_green1.setColorFilter(Color.parseColor(item.getCategory().getColor()));

        Log.d(TAG, "Ticket ID ->>item: " + item.getId());
        List<TicketAPIItem.Message> messageList = new ArrayList<>();

        if (item.getMessages() != null) {
            for (TicketAPIItem.Message ticketReplyData : item.getMessages()) {
                Log.d(TAG, "Ticket ID <<<-------------: " + ticketReplyData.getId());
                Log.d(TAG, "Ticket Name<<<-------------: " + ticketReplyData.getUser().getName());
                Log.d(TAG, "Ticket Body<<<-------------: " + ticketReplyData.getBody());
                messageList.add(ticketReplyData);

            }

        } else {
            Log.d("loadTickets", "No tickets received.");
        }



        if (item.getStatus().equals("open")) {
            Log.d("Status is OPEN","NO Color is set");
        } else if (item.getStatus().equals("resolved")){
            holder.itemColor.setColorFilter(Color.parseColor("#4E7CFF"));
        }else {
            holder.itemColor.setColorFilter(Color.parseColor("#E626ABF4"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReplyTicket.class);
                intent.putExtra("token", accessToken);
                intent.putExtra("subject", item.getSubject());
                intent.putExtra("categoryName", item.getCategory().getName());
                intent.putExtra("status", item.getStatus());
                intent.putExtra("categoryColor", item.getCategory().getColor());
                intent.putExtra("ticketID", item.getId());


                // Serialize messages if present
                if (item.getMessages() != null && !item.getMessages().isEmpty()) {
                    Gson gson = new Gson();

                    // Serialize all messages
                    String messagesJsonList = gson.toJson(item.getMessages());
                    intent.putExtra("messagesJsonList", messagesJsonList);

                    // Optional: Serialize first message only if needed
                    TicketAPIItem.Message firstMessage = item.getMessages().get(0);
                    String messageJson = gson.toJson(firstMessage);
                    intent.putExtra("messageJson", messageJson);
                }

                context.startActivity(intent);
            }
        });

    }
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView item_subject, item_category,item_status;
     ImageView itemColor,indicator_green1;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            item_status = itemView.findViewById(R.id.item_status);
            item_subject = itemView.findViewById(R.id.item_subject);
            item_category = itemView.findViewById(R.id.item_category);
            itemColor = itemView.findViewById(R.id.indicator_green); // Uncomment if itemColor is used
            indicator_green1 = itemView.findViewById(R.id.indicator_green1);
        }
    }
}