package com.example.cq_mobile.ui.chat.InnerChatsFolder;

import android.app.AlertDialog;
import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatMember;
import com.example.cq_mobile.ui.chat.sendMessageFolder.DeleteMessageApiManager;

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
    String accessToken;
    ProgressBar progressBar;
    String username;
    String currentUserID;
    String httpsAvatar_url;
    public MessagesAdapter(Context context, List<InnerChatAPIItem> messagesList, int id, int channel, List<ChatMember> memberslist, String currentUser, String accessToken,
                           ProgressBar progressBar, String username, String currentUserID, String httpsAvatar_url) {
        this.context = context;
        this.id = id;
        this.channel = channel;
        this.currentUser = currentUser;
        this.messagesList = messagesList != null ? messagesList : new ArrayList<>();
        this.memberslist = memberslist != null ? memberslist : new ArrayList<>();
        this.accessToken = accessToken;
        this.progressBar = progressBar;
        this.username = username;
        this.currentUserID = currentUserID;
        this.httpsAvatar_url = httpsAvatar_url;
    }

    public void addChats(List<InnerChatAPIItem> newChats) {
        messagesList.addAll(newChats);
        notifyDataSetChanged();
    }

    public void addMessage(InnerChatAPIItem newMessage) {
        messagesList.add(newMessage);
        notifyItemInserted(messagesList.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        InnerChatAPIItem chatAPIItem = messagesList.get(position);
        try {
            int senderId = Integer.parseInt(chatAPIItem.getSender());
            int viewType = (senderId == id) ? 0 : 1; // 0 = Current User, 1 = Receiver
            Log.d("MessagesAdapter", "Message Sender ID: " + senderId + ", ViewType: " + viewType);
            return viewType;
        } catch (NumberFormatException e) {
            Log.e("MessagesAdapter", "Error parsing Sender ID", e);
            return 1; // Default to receiver if error
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0: // Current user
                view = LayoutInflater.from(context).inflate(R.layout.item_message_received, parent, false);
                break;
            case 1: // Receiver
            default:
                view = LayoutInflater.from(context).inflate(R.layout.item_message_sent, parent, false);
                break;
        }
        return new MessageViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        InnerChatAPIItem chatAPIItem = messagesList.get(position);
        int senderInt = Integer.parseInt(chatAPIItem.getSender());

        if (memberslist == null || memberslist.isEmpty()) { // CHANGED: Null check
            holder.senderName.setText("Unknown Sender");
            holder.messageText.setText("Unknown Messages");
            return;
        }


        ChatMember currentUserMember = null;
        ChatMember receiverMember = null;
        String senderMessages = null;
        String receiverMessages = null;
        String AvatarHttp = httpsAvatar_url.trim();

        for (ChatMember member : memberslist) {
            if (member.getId() == senderInt) {
                currentUserMember = member;
                senderMessages = chatAPIItem.getMessage();
            } else if (member.getId() != senderInt) {
                receiverMember = member;
                receiverMessages = chatAPIItem.getMessage();
            }
        }

        if (receiverMember != null && receiverMember.getAvatarPath() != null && !receiverMember.getAvatarPath().isEmpty()) {
            holder.senderName.setText(currentUser);
            holder.messageText.setText(receiverMessages);
            Glide.with(context)
                    .load(AvatarHttp)
                    //   .load(receiverMember.getAvatarPath())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .into(holder.senderAvatar);
        } else {
            Log.e("MessagesAdapter", "receiverMember is null Avatar is single  " + senderInt);
            holder.senderName.setText(username);
            holder.messageText.setText("Unknown Messages");

            Glide.with(context)
                    .load(AvatarHttp)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .into(holder.senderAvatar);
        }

        if (currentUserMember != null) {
            holder.senderName.setText(currentUserMember.getName());
            holder.messageText.setText(senderMessages);

            Glide.with(context)
                    .load(currentUserMember.getAvatarPath())
                    .placeholder(R.drawable.circular_background)
                    .error(R.drawable.emptyglide)
                    .into(holder.senderAvatar);
        }

        holder.itemView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Message Options")
                    .setMessage("Do you want to delete this message?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        progressBar.setVisibility(View.VISIBLE);
                        DeleteMessageApiManager.deleteMessage(String.valueOf(chatAPIItem.getId()), progressBar, accessToken, new DeleteMessageApiManager.ApiCallback() {
                            @Override
                            public void onSuccess() {
                                Log.d("DeleteMessage", "Message deleted successfully.");

                                int position = holder.getBindingAdapterPosition();
                                if (position != RecyclerView.NO_POSITION) {
                                    messagesList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, messagesList.size());

                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                Log.e("DeleteMessage", "Failed to delete message: " + error);
                            }
                        });
                    }).setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        });
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

            if (viewType == 1) { // Only for received messages
                receiverName = itemView.findViewById(R.id.name);
            } else {
                receiverName = null;
            }
        }
    }
}
/*

        ChatMember currentUserMember = null;
        ChatMember receiverMember = null;
        String senderMessages = null;
        String receiverMessages = null;


        for (ChatMember member : memberslist) {

            if (member.getId() == senderInt) {
                currentUserMember = member;
                senderMessages = chatAPIItem.getMessage();
            } else {
                receiverMember = member;
                receiverMessages = chatAPIItem.getMessage();
            }
        }


        if (receiverMember != null) {
            Log.d("MessagesAdapter", "Receiver Details:");
            Log.d("MessagesAdapter", "id=" + currentUserID);
            Log.d("MessagesAdapter", "name='" + receiverMember.getName() + "'");
            Log.d("MessagesAdapter", "avatarPath='" + receiverMember.getAvatarPath() + "'");
            Log.d("MessagesAdapter", "email='" + receiverMember.getEmail() + "'");
            Log.d("MessagesAdapter", "lastRead=" + receiverMember.getLastRead());

            Log.d("MessagesAdapter", "Current USER  AVATAR:   "+" -ID- "+senderInt+" -Member-  "+receiverMember.getId() +"\n"+"   -----  " +hhtpAvatar_url+"\n"+" - "+ receiverMember.getAvatarPath());


            holder.senderName.setText(currentUser);
            holder.messageText.setText(receiverMessages);
            Glide.with(context)
                    .load(receiverMember.getAvatarPath())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .into(holder.senderAvatar);
            holder.senderName.setText(receiverMember.getName());

        } else {
            Log.e("MessagesAdapter", "receiverMember is null for sender ID: " + senderInt);
            holder.senderName.setText(username);
            holder.messageText.setText("Unknown Messages");

            try {
                Uri avatarUri = Uri.parse(hhtpAvatar_url);
                if (avatarUri.getScheme() == null || avatarUri.getHost() == null) {
                    throw new IllegalArgumentException("Invalid URL");
                }

                holder.senderName.setText(currentUser);
                holder.messageText.setText(receiverMessages);

                Glide.with(context)
                        .load(avatarUri.toString())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .into(holder.senderAvatar);

            } catch (Exception e) {
                Log.e("MessagesAdapter", "Invalid URL or failed to load image: " + hhtpAvatar_url, e);
            }

        }



        if (currentUserMember != null) {
            Log.d("MessagesAdapter", "Sender Details:");
            Log.d("MessagesAdapter", "id=" + currentUserMember.getId());
            Log.d("MessagesAdapter", "name='" + currentUserMember.getName() + "'");
            Log.d("MessagesAdapter", "avatarPath='" + currentUserMember.getAvatarPath() + "'");
            Log.d("MessagesAdapter", "email='" + currentUserMember.getEmail() + "'");
            Log.d("MessagesAdapter", "lastRead=" + currentUserMember.getLastRead());


            holder.senderName.setText(currentUserMember.getName());
            holder.messageText.setText(senderMessages);

            Glide.with(context)
                    .load(currentUserMember.getAvatarPath())
                    .placeholder(R.drawable.circular_background)
                    .error(R.drawable.emptyglide)
                    .into(holder.senderAvatar);
        }

 */
