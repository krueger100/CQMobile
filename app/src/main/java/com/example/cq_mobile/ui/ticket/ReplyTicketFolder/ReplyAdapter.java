package com.example.cq_mobile.ui.ticket.ReplyTicketFolder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.ticket.ReplyTicketFolder.DeleteMessageFolder.DeleteTicketMessageApiManager;
import com.example.cq_mobile.ui.ticket.TicketsFolder.TicketAPIItem;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;
import android.widget.Toast;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder> {

    private static final String TAG = "ReplyAdapter";
    private final Context context;
    private final String accessToken;
    private final List<TicketAPIItem.Message> replyList; // List to store all reply messages
    int ticketID;
    ProgressBar progressBar;
    public ReplyAdapter(Context context, String accessToken, List<TicketAPIItem.Message> replyList, int ticketID, ProgressBar progressBar) {
        this.context = context;
        this.accessToken = accessToken;
        this.replyList = replyList;
        this.ticketID = ticketID;
        this.progressBar = progressBar;
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


        holder.itemView.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);

            new AlertDialog.Builder(context)
                    .setTitle("Delete this Ticket reply")
                    .setMessage("Are you sure you want to delete this ticket reply?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Use ExecutorService for proper threading
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(() -> {
                            Log.d(TAG, "Starting delete operation for Ticket ID: " + ticketID + ", Message ID: " + reply.getId());
                            String response = DeleteTicketMessageApiManager.deleteTicketMessage(context,ticketID, reply.getId());
                            new Handler(Looper.getMainLooper()).post(() -> {
                                if (!response.startsWith("Error:")) {
                                    Log.d(TAG, "Reply deleted successfully!" + response);
                                    progressBar.setVisibility(View.GONE);
                                    Intent intent = new Intent(context, MainActivity.class);
                                    context.startActivity(intent);
                                    replyList.remove(position);
                                    notifyItemRemoved(position);
                                } else {
                                    Log.d(TAG, "Failed to delete reply: " + response);
                                    Toast.makeText(context, "Ticket reply deleted successfully!", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    Intent intent = new Intent(context, MainActivity.class);
                                    context.startActivity(intent);

                                }
                            });
                        });
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        progressBar.setVisibility(View.GONE);
                        dialog.dismiss();
                    })
                    .show();
        });

    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount called. Total replies: " + replyList.size());
        return replyList.size();
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
