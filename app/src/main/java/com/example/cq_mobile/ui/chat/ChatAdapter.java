package com.example.cq_mobile.ui.chat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.R;
import com.bumptech.glide.Glide;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatAPIItem;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private static final String TAG = "ChatAdapter";
    private List<ChatAPIItem> chatList;
    private Context context;

    public ChatAdapter(Context context, List<ChatAPIItem> chatList) {
        this.context = context;
        this.chatList = chatList != null ? chatList : new ArrayList<>();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder called for viewType: " + viewType);
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatAPIItem chatItem = chatList.get(position);
        Gson gson = new Gson();
        holder.chatName.setText(chatItem.getChat_name() != null ? chatItem.getChat_name() : "Unknown Chat");

        String chatMembers = gson.toJson(chatItem.getMembers() !=null ? chatItem.getMembers(): "No Member Found");
        Log.d(TAG, "Members Item at position " + position + ": " + chatMembers);

        String chatMessages = gson.toJson(chatItem.getMessage() !=null ? chatItem.getMessage(): "No Messages Found");

        String chatAPIData = gson.toJson(chatItem);
        Log.d(TAG, "Chat Item at position   " + position + ": " + chatAPIData);
        Log.d(TAG, "<<<------  Message ----->>>   " + position + ": " + chatMessages);
        Log.d(TAG, "Message ----- Count     " + position + ": " + chatList.size());
        Log.d(TAG, "Message ----- IsRead   " + position + ": " + chatItem.getMessage_read());
        Log.d(TAG, "Message -----SENDER Name    " + position + ": " + chatItem.getName());
        Log.d(TAG, "Message -----RECIEVER Name    " + position + ": " + chatItem.getChat_name());

        // Load avatar image with Glide
        if (chatItem.getAvatar_path() != null && !chatItem.getAvatar_path().isEmpty()) {
            Glide.with(context)
                    .load(chatItem.getAvatar_path())
                    .into(holder.chatAvatar);
        } else {
            holder.chatAvatar.setImageResource(R.drawable.emptyglide);
        }
    }


    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void addChats(List<ChatAPIItem> newChats) {
        if (newChats != null) {
            chatList.addAll(newChats);
            notifyDataSetChanged();
        }
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView chatName;
        ImageView chatAvatar;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            chatName = itemView.findViewById(R.id.chatName);
            chatAvatar = itemView.findViewById(R.id.avatar);
        }
    }
}
