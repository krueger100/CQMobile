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

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private Context context;
    private List<InnerChatAPIItem> messagesList;
    private int id;  // User ID of the current user
    private int channel;
    private List<ChatMember> memberslist;
    String currentUser;
    public MessagesAdapter(Context context, List<InnerChatAPIItem> messagesList, int id, int channel, List<ChatMember> memberslist, String currentUser) {
        this.context = context;
        this.id = id;
        this.channel = channel;
        this.currentUser = currentUser;
        this.messagesList = messagesList != null ? messagesList : new ArrayList<>();
        this.memberslist = memberslist != null ? memberslist : new ArrayList<>();
    }

    public void addChats(List<InnerChatAPIItem> newChats) {
        messagesList.addAll(newChats);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        InnerChatAPIItem chatAPIItem = messagesList.get(position);
        try {
            int senderId = Integer.parseInt(chatAPIItem.getSender());
            int viewType = (senderId == id) ? 1 : 0; // 1 = Sent, 0 = Received
            Log.d("MessagesAdapter", "Message Sender ID: " + senderId + ", ViewType: " + viewType);
            return viewType;
        } catch (NumberFormatException e) {
            Log.e("MessagesAdapter", "Error parsing Sender ID", e);
            return 0; // Default to received if error
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) { // Sent message
            Log.d("MessagesAdapter", "Inflating sent message layout.");
            view = LayoutInflater.from(context).inflate(R.layout.item_message_sent, parent, false);
        } else { // Received message
            Log.d("MessagesAdapter", "Inflating received message layout.");
            view = LayoutInflater.from(context).inflate(R.layout.item_message_received, parent, false);
        }
        return new MessageViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        InnerChatAPIItem chatAPIItem = messagesList.get(position);



        int senderInt = Integer.parseInt(chatAPIItem.getSender());
        if (memberslist != null && !memberslist.isEmpty()) {
            ChatMember senderMember = null;
            ChatMember receiverMember = null;
            String senderMessages = null;
            String receiverMessages= null;


            for (ChatMember member : memberslist) {
                if (member.getId() == senderInt) {
                    senderMember = member;
                    senderMessages =  chatAPIItem.getMessage();
                } else {
                    receiverMember = member;
                     receiverMessages =  chatAPIItem.getMessage();
                }
            }


            // Log and initialize sender details
            if (senderMember != null) {
                Log.d("MessagesAdapter", "Sender Details:");
                Log.d("MessagesAdapter", "id=" + senderMember.getId());
                Log.d("MessagesAdapter", "name='" + senderMember.getName() + "'");
                Log.d("MessagesAdapter", "avatarPath='" + senderMember.getAvatarPath() + "'");
                Log.d("MessagesAdapter", "email='" + senderMember.getEmail() + "'");
                Log.d("MessagesAdapter", "lastRead=" + senderMember.getLastRead());

                if (receiverMember != null) {
                    holder.senderName.setText(receiverMember.getName());
                    holder.messageText.setText(receiverMessages);

                    Glide.with(context)
                            .load( receiverMember.getAvatarPath())
                            .placeholder(R.drawable.circular_background)
                            .error(R.drawable.emptyglide)
                            .into(holder.senderAvatar);

                } else {
                    holder.senderName.setText("Unknown Sender");
                    holder.messageText.setText("Unknown Messages");
                }

            }

            // Log and initialize receiver details
            if (receiverMember != null) {
                Log.d("MessagesAdapter", "Receiver Details:");
                Log.d("MessagesAdapter", "id=" + receiverMember.getId());
                Log.d("MessagesAdapter", "name='" + receiverMember.getName() + "'");
                Log.d("MessagesAdapter", "avatarPath='" + receiverMember.getAvatarPath() + "'");
                Log.d("MessagesAdapter", "email='" + receiverMember.getEmail() + "'");
                Log.d("MessagesAdapter", "lastRead=" + receiverMember.getLastRead());  //senderAvatar

                if (senderMember != null) {
                    holder.senderName.setText(senderMember.getName());
                    holder.messageText.setText(senderMessages);

                    Glide.with(context)
                            .load( senderMember.getAvatarPath())
                            .placeholder(R.drawable.circular_background)
                            .error(R.drawable.emptyglide)
                            .into(holder.senderAvatar);
                } else {
                    holder.senderName.setText("Unknown Sender");
                    holder.messageText.setText("Unknown Messages");

                }
            }





        }

    }


    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView senderName, messageText, receiverName;
        CircleImageView senderAvatar;
        int viewType;

        public MessageViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;

            senderName = itemView.findViewById(R.id.name);
            messageText = itemView.findViewById(R.id.messageText);
            senderAvatar = itemView.findViewById(R.id.senderAvatar);

            if (viewType == 0) { // Only for received messages
                receiverName = itemView.findViewById(R.id.name);
            } else {
                receiverName = null;
            }
        }
    }
}

/*
if (holder.viewType == 1) { // Sent Message
                holder.messageText.setText(chatAPIItem.getMessage());
                Log.w("MessagesAdapter", "Sent Message: " + chatAPIItem.getMessage());

            } else { // Received Message
                holder.messageText.setText(chatAPIItem.getMessage());
                Log.w("MessagesAdapter", "Received Message: " + chatAPIItem.getMessage());

            }
 */