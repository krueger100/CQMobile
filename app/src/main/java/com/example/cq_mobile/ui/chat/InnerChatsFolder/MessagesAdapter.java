package com.example.cq_mobile.ui.chat.InnerChatsFolder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatMember;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private Context context;
    private List<ChatMessage> messagesList;
    private String currentUser;
    private List<ChatMember> chatMembersList;
    private int id;

    public MessagesAdapter(Context context, List<ChatMessage> messagesList, String currentUser, List<ChatMember> chatMembersList, int id) {
        this.context = context;
        this.messagesList = messagesList != null ? messagesList : new ArrayList<>();
        this.currentUser = currentUser;
        this.chatMembersList = chatMembersList != null ? chatMembersList : new ArrayList<>();
        this.id = id;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messagesList.get(position);
        try {
            int viewType = (message.getId() == id) ? 1 : 0;
            Log.d("MessagesAdapter", "Message ID: " + message.getId() + ", Current User ID: " + id + ", ViewType: " + viewType);
            return viewType;
        } catch (NumberFormatException e) {
            Log.e("MessagesAdapter", "Invalid format for currentUser: " + currentUser, e);
            return 0; // Default to received message if parsing fails
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            Log.d("MessagesAdapter", "Inflating sent message layout.");
            view = LayoutInflater.from(context).inflate(R.layout.item_message_sent, parent, false);
        } else {
            Log.d("MessagesAdapter", "Inflating received message layout.");
            view = LayoutInflater.from(context).inflate(R.layout.item_message_received, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage chatMessage = messagesList.get(position);

        // Log message data
        Log.d("MessagesAdapter", "Binding message - ID: " + chatMessage.getId() + ", Text: " + chatMessage.getText());

        holder.messageText.setText(chatMessage.getText());

        // Get sender details
        ChatMember sender = getSenderFromMessage(chatMessage);
        if (sender != null) {
            Log.d("MessagesAdapter", "Found sender - Name: " + sender.getName() + ", Avatar: " + sender.getAvatarPath());
        } else {
            Log.d("MessagesAdapter", "Sender not found for message ID: " + chatMessage.getId());
        }

        if (holder.senderName != null) {
            holder.senderName.setText(sender != null ? sender.getName() : "Unknown");
        }

        // Load sender avatar dynamically
        if (context != null && holder.senderAvatar != null) {
            if (sender != null && sender.getAvatarPath() != null && !sender.getAvatarPath().isEmpty()) {
                Glide.with(context)
                        .load(sender.getAvatarPath())
                        .placeholder(R.drawable.circular_background)
                        .error(R.drawable.emptyglide)
                        .into(holder.senderAvatar);
            } else {
                holder.senderAvatar.setImageResource(R.drawable.emptyglide);
            }
        } else {
            Log.e("MessagesAdapter", "Context or senderAvatar is null, skipping Glide loading");
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    private ChatMember getSenderFromMessage(ChatMessage chatMessage) {
        for (ChatMember member : chatMembersList) {
            if (member.getId() == chatMessage.getId()) {  // Ensure senderId is properly used
                return member;
            }
        }
        Log.d("MessagesAdapter", "No matching sender found for message ID: " + chatMessage.getId());
        return null;
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView senderName, messageText;
        ImageView senderAvatar;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.senderName);
            messageText = itemView.findViewById(R.id.messageText);
            senderAvatar = itemView.findViewById(R.id.senderAvatar);

            if (senderAvatar == null) {
                Log.e("MessagesAdapter", "senderAvatar is null in ViewHolder constructor!");
            }
        }
    }
}


    /*
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatDetails chatDetails = messagesList.get(position);

        // Parse the JSON string for messages
        Gson gson = new Gson();
        String chatMessagesJson = chatDetails.getMessage();
        Type messageType = new TypeToken<List<MessageItem>>() {}.getType();
        List<MessageItem> messages = gson.fromJson(chatMessagesJson, messageType);

        String chatName = chatDetails.getChatName();
        String receiverName = chatDetails.getName();
        String id = chatDetails.getId();
        String chats = chatDetails.getMessage();

        Log.d("MessagesAdapter", "Message -----ID    " + position + ": " + id);
        Log.d("MessagesAdapter", "Message -----SENDER Name    " + position + ": " + chatName);
        Log.d("MessagesAdapter", "Message -----RECIEVER Name    " + position + ": " + receiverName);
        Log.d("MessagesAdapter", "Message -----CHATS    " + position + ": " + chats);



        if (messages != null && !messages.isEmpty()) {
            Collections.sort(messages, new Comparator<MessageItem>() {
                @Override
                public int compare(MessageItem msg1, MessageItem msg2) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Date date1 = sdf.parse(msg1.getDate());
                        Date date2 = sdf.parse(msg2.getDate());
                        return date2.compareTo(date1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });

            // Get the most recent message
            MessageItem messageItem = messages.get(0);
            String textMessage = messageItem.getText();

            // Log for debugging
            Log.d("MessagesAdapter", "<<<---   textMessage   --->>>" + textMessage);

            // Set the message text
            holder.messageText.setText(textMessage);

            // Set the sender and receiver names dynamically
            if (holder.senderName != null) {
                holder.senderName.setText(chatName);  // Set the sender's name
            }

            // Set the receiver name
            if (holder.receiverName != null) {
                holder.receiverName.setText(receiverName);  // Set the receiver's name
            }

            Glide.with(context)
                    .load(chatDetails.getAvatarPath())
                    .placeholder(R.drawable.circular_background)
                    .error(R.drawable.emptyglide)
                    .into(holder.senderAvatar);
        } else {
            holder.senderAvatar.setImageResource(R.drawable.emptyglide);
        }
    }

     */

