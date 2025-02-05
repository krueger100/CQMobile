package com.example.cq_mobile.ui.chat;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.HelperManagers.Animation.TransitionAnimationManager;
import com.bumptech.glide.Glide;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatDetails;
import com.example.cq_mobile.ui.chat.ChatFolder.MessageItem;
import com.example.cq_mobile.ui.chat.InnerChatsFolder.InnerChats;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private static final String TAG = "ChatAdapter";
    private List<ChatDetails> chatList;
    private Context context;
    private String email;
    private String password;
    private String accessToken;
    private String receivers_name;
    private String senders_name;

    public ChatAdapter(Context context, List<ChatDetails> chatList, String accessToken, String email, String password) {
        this.context = context;
        this.email = email;
        this.password = password;
        this.accessToken = accessToken;
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
        ChatDetails chatItem = chatList.get(position);

        Gson gson = new Gson();
        String chatMessagesJson = chatItem.getMessage();
        Type messageType = new TypeToken<List<MessageItem>>() {}.getType();
        List<MessageItem> messages = gson.fromJson(chatMessagesJson, messageType);


        holder.chatName.setText(chatItem.getChatName() != null ? chatItem.getChatName() : "Unknown Chat");
        String messageChat = chatItem.getMessage() != null ? chatItem.getMessage() : "No Chat";

        receivers_name = chatItem.getChatName() != null ? chatItem.getChatName() : "No Receiver Name";
        senders_name = chatItem.getName() != null ? chatItem.getName() : "No Senders Name";

        if (messages != null && !messages.isEmpty()) {
            MessageItem lastMessage = messages.get(messages.size() - 1);
            holder.chatCount.setText(String.valueOf(messages.size()));
        } else {
            holder.chatCount.setText("0");
        }


        Log.d(TAG, "Chat Item at position " + position + ": " + new Gson().toJson(chatItem));
        Log.d(TAG, "Message -----SENDER Name " + position + ": " + senders_name);
        Log.d(TAG, "Message -----RECEIVER Name " + position + ": " + receivers_name);
        Log.d(TAG, "Message -----CHAT Data: " + messageChat);

        // Load avatar image
        if (chatItem.getAvatarPath() != null && !chatItem.getAvatarPath().isEmpty()) {
            Glide.with(context)
                    .load(chatItem.getAvatarPath())
                    .placeholder(R.drawable.circular_background)
                    .error(R.drawable.emptyglide)
                    .into(holder.chatAvatar);
        } else {
            holder.chatAvatar.setImageResource(R.drawable.emptyglide);
        }

        // Handle click event
// Handle click event
        holder.itemView.setOnClickListener(v -> {
            TransitionAnimationManager.zoomOut(v, 100);
            v.postDelayed(() -> {
                TransitionAnimationManager.zoomIn(v, 50);

                Intent intent = new Intent(context, InnerChats.class);
                intent.putExtra("token", accessToken);
                intent.putExtra("Email", email);
                intent.putExtra("Password", password);
                intent.putExtra("avatar", chatItem.getAvatarPath());
                intent.putExtra("Sender", chatItem.getName());
                intent.putExtra("Receiver", chatItem.getChatName());
                intent.putExtra("chatAPIData", messageChat);


                String chatListJson = gson.toJson(chatList);
                intent.putExtra("chatListJson", chatListJson);

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
        ImageView chatAvatar;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            chatName = itemView.findViewById(R.id.chatName);
            chatAvatar = itemView.findViewById(R.id.avatar);
            chatCount = itemView.findViewById(R.id.chatCount);

        }
    }
}

/*


        holder.itemView.setOnClickListener(v -> {
            TransitionAnimationManager.zoomOut(v, 100);
            v.postDelayed(() -> {
                TransitionAnimationManager.zoomIn(v, 50);

                Intent intent = new Intent(context, InnerChats.class);
                intent.putExtra("token", accessToken);
                intent.putExtra("avatar", chatItem.getAvatar_path());
                intent.putExtra("Sender", senders_name);
                intent.putExtra("Receiver", receivers_name);
                intent.putExtra("MessageText", messageChat);
                intent.putExtra("IsRead", chatItem.getMessage_read());

                if (messages != null && !messages.isEmpty()) {
                    MessageItem lastMessage = messages.get(messages.size() - 1);
                    intent.putExtra("MessageID", lastMessage.getId());
                    intent.putExtra("MessageTime", lastMessage.getTime());
                    intent.putExtra("MessageDate", lastMessage.getDate());
                    intent.putExtra("UnreadCount", lastMessage.getUnread());
                }

                context.startActivity(intent);
            }, 100);
        });




        if (chatMessagesJson != null && !chatMessagesJson.isEmpty()) {
            try {
                // Convert JSON String to a List of MessageItem
                Type messageType = new TypeToken<List<MessageItem>>() {}.getType();

                // Ensure it's a valid JSON array format
                if (chatMessagesJson.startsWith("[") && chatMessagesJson.endsWith("]")) {
                    List<MessageItem> messages = gson.fromJson(chatMessagesJson, messageType);

                    // Log messages
                    if (messages != null && !messages.isEmpty()) {
                        for (MessageItem message : messages) {
                            Log.d(TAG, "-|- POSITION -|-   >"+  position +"< -|- Message ID -|-   >"+  message.getId()+"< "
                            + " \n " +"Message Text:  --->>>"+  message.getText()  + " \n " +"Message Time:   --->>>"+ message.getTime() + " \n " +"Message Date:   --->>>"+message.getDate()
                            +"\n"+ "Message Unread Count:   --->>>" + message.getUnread());
                        }
                    } else {
                        Log.d(TAG, "No messages found.");
                    }
                    Log.d(TAG, "Total messages in chat: " + (messages != null ? messages.size() : 0));
                    holder.chatCounts.setText(String.valueOf(messages != null ? messages.size() : 0));
                } else {
                    Log.e(TAG, "Invalid message format. Expected JSON array but got: " + chatMessagesJson);
                }
            } catch (JsonSyntaxException e) {
                Log.e(TAG, "Error parsing messages JSON: " + e.getMessage());
            }
        } else {
            Log.d(TAG, "No messages found.");
        }

 */