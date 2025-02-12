package com.example.cq_mobile.ui.chat;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.HelperManagers.Animation.TransitionAnimationManager;
import com.bumptech.glide.Glide;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatDetails;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatMember;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatMessage;
import com.example.cq_mobile.ui.chat.InnerChatsFolder.InnerChats;
import com.example.cq_mobile.ui.ticket.CreateFolder.CreateTicket;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private static final String TAG = "ChatAdapter";
    private List<ChatDetails> chatList;
    private Context context;
    private String email;
    private String password;
    private String accessToken;
    ProgressBar progressBar;
    String chatCount;
    int message_read;


    public ChatAdapter(Context context, List<ChatDetails> chatList, String accessToken, String email, String password, ProgressBar progressBar, String chatCount, int message_read) {
        this.context = context;
        this.email = email;
        this.password = password;
        this.accessToken = accessToken;
        this.message_read = message_read;
        this.progressBar = progressBar;
        this.chatCount = chatCount;

        this.chatList = chatList != null ? chatList : new ArrayList<>();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatDetails chatItem = chatList.get(position);

        if (chatItem == null) {
            Log.d(TAG, "Chat item at position " + position + " is null.");
            return;
        }


        int chatChannels = chatItem.getChannel();

        // Set the chat name
        holder.chatName.setText(chatItem.getChatName() != null ? chatItem.getChatName() : "Unknown Chat");

        // Set the sender and receiver names
        String receivers_name = chatItem.getChatName() != null ? chatItem.getChatName() : "No Receiver Name";
        String senders_name = chatItem.getName() != null ? chatItem.getName() : "No Senders Name";

//        int id = (chatItem.getId() != null) ? Integer.parseInt(chatItem.getId()) : 0;
//        Log.w("ChatAdapter", "ID:---->>> " + id );

        List<ChatMessage> messages = chatItem.getMessages();


        if (messages != null && !messages.isEmpty()) {
            int totalUnread = 0;

            for (ChatMessage message : messages) {
                totalUnread += message.getUnread();
            }

            holder.chatCount.setText(String.valueOf(totalUnread));
            Log.d("ChatAdapter", "Total Unread Messages: " + totalUnread);


            int chatIsRead = Integer.parseInt(String.valueOf(chatItem.getMessage_read()));
            if (chatIsRead != 0) {
                holder.chatCount.setBackgroundResource(R.drawable.indicator_off);
                Log.d("ChatAdapter", "chatIsRead: " + chatIsRead);
            } else  {
                TransitionAnimationManager.zoomOut(holder.chatCount, 1500);
                TransitionAnimationManager.zoomIn(holder.chatCount, 1700);
                holder.chatCount.setBackgroundResource(R.drawable.indicator_on);
                Log.d("ChatAdapter", "chatIsRead: " + chatIsRead);

            }


        } else {
            holder.chatCount.setText("0");
        }



        int firstMemberId = -1;
        int secondMemberId = -1;

        List<ChatMember> members = chatItem.getMembers();
        if (members != null && !members.isEmpty()) {
            String membersString = members.toString();
            Log.w(TAG, "MEMBERS " + membersString);
        } else {
            Log.e(TAG, "MEMBERS " + "No MEMBERS");
        }



        // Load the avatar image for the chat
        String avatarPath = chatItem.getAvatarPath();
        if (avatarPath != null && !avatarPath.isEmpty()) {
            Glide.with(context)
                    .load(chatItem.getAvatarPath())
                    .placeholder(R.drawable.circular_background)
                    .error(R.drawable.emptyglide)
                    .into(holder.chatAvatar);
        } else {
            holder.chatAvatar.setImageResource(R.drawable.emptyglide);
        }

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            TransitionAnimationManager.zoomOut(v, 100);
            v.postDelayed(() -> {
                TransitionAnimationManager.zoomIn(v, 50);
                // Start a new activity with necessary data   messagesList
                Intent intent = new Intent(context, InnerChats.class);
                intent.putExtra("token", accessToken);
                intent.putExtra("channel", chatChannels);
                intent.putExtra("Email", email);
                intent.putExtra("Password", password);
                intent.putExtra("Sender", senders_name);
                intent.putExtra("Receiver", receivers_name);
                intent.putExtra("Avatar", chatItem.getAvatarPath());


                Gson gson = new Gson();
                String membersJson = gson.toJson(members);
                intent.putExtra("chatAPIData", membersJson);




                context.startActivity(intent);
            }, 100);
        });

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void addChats(List<ChatDetails> newChats) {
        if (newChats != null) {
            chatList.addAll(newChats);
            Log.d(TAG, "Total number of chats after adding new ones: " + chatList.size());
            notifyDataSetChanged();
        }
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView chatName,chatCount;
        CircleImageView chatAvatar;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            chatName = itemView.findViewById(R.id.chatName);
            chatAvatar = itemView.findViewById(R.id.avatar);
            chatCount = itemView.findViewById(R.id.chatCount);

        }
    }
}

