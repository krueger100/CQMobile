package com.example.cq_mobile.ui.ticket.ReplyTicketFolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.ticket.TicketsFolder.TicketAPIItem;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder> {

    private static final String TAG = "ReplyAdapter";
    private final Context context;
    private final String accessToken;
    private final List<TicketAPIItem.Message> replyList; // List to store all reply messages

    public ReplyAdapter(Context context, String accessToken, List<TicketAPIItem.Message> replyList) {
        this.context = context;
        this.accessToken = accessToken;
        this.replyList = replyList; // Pass the list during initialization
    }

    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder called.");
        View view = LayoutInflater.from(context).inflate(R.layout.reply_item, parent, false);
        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        TicketAPIItem.Message reply = replyList.get(position); // Get the message at the current position

        // Bind user avatar
        if (reply.getUser() != null && reply.getUser().getAvatar() != null) {
            Glide.with(context)
                    .load(reply.getUser().getAvatar())
                    .placeholder(R.drawable.circular_background)
                    .error(R.drawable.emptyglide)
                    .into(holder.replyAvatar);

            // Set user name
            holder.replyUser.setText(reply.getUser().getName());
        } else {
            // Handle null user or missing avatar
            holder.replyUser.setText("Unknown User");
            holder.replyAvatar.setImageResource(R.drawable.emptyglide); // Default image
        }

        // Clean and bind reply body text
        String cleanBodyText = reply.getBody() != null ? reply.getBody().replaceAll("<[^>]*>", "") : "";
        holder.replyText.setText(cleanBodyText);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount called. Total replies: " + replyList.size());
        return replyList.size(); // Return the size of the reply list
    }

    public static class ReplyViewHolder extends RecyclerView.ViewHolder {
        ImageView replyAvatar;
        TextView replyText, replyUser;

        public ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            replyAvatar = itemView.findViewById(R.id.replyAvatar);
            replyText = itemView.findViewById(R.id.replyText);
            replyUser = itemView.findViewById(R.id.replyUser);
            Log.d(TAG, "ViewHolder initialized.");
        }
    }
}
