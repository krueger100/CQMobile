package com.example.cq_mobile.ui.chat.InnerChatsFolder;

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
import com.example.cq_mobile.ui.chat.ChatFolder.ChatDetails;

import java.util.ArrayList;
import java.util.List;
public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private Context context;
    private List<ChatDetails> messagesList;
    private String currentUser;

    public MessagesAdapter(Context context, List<ChatDetails> messagesList, String currentUser) {
        this.context = context;
        this.messagesList = messagesList != null ? messagesList : new ArrayList<>();
        this.currentUser = currentUser;
    }

    @Override
    public int getItemViewType(int position) {
        ChatDetails message = messagesList.get(position);
        return message.getName().equals(currentUser) ? 1 : 0;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_sent, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_received, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatDetails message = messagesList.get(position);

        holder.messageText.setText(message.getMessage());

        if (holder.senderName != null) {
            holder.senderName.setText(message.getName());
        }

        if (holder.senderAvatar != null) {
            if (message.getAvatarPath() != null && !message.getAvatarPath().isEmpty()) {
                Glide.with(context)
                        .load(message.getAvatarPath())
                        .placeholder(R.drawable.circular_background)
                        .error(R.drawable.emptyglide)
                        .into(holder.senderAvatar);
            } else {
                holder.senderAvatar.setImageResource(R.drawable.emptyglide);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView senderName, messageText;
        ImageView senderAvatar;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.senderName);
            messageText = itemView.findViewById(R.id.messageText);
            senderAvatar = itemView.findViewById(R.id.senderAvatar);
        }
    }
}
