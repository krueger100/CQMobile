package com.example.cq_mobile.ui.chat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.HelperManagers.Animation.TransitionAnimationManager;
import com.bumptech.glide.Glide;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.LoginFolder.AuthManager;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatDetails;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatMember;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatMessage;
import com.example.cq_mobile.ui.chat.InnerChatsFolder.InnerChats;

import com.example.cq_mobile.ui.chat.sendMessageFolder.UpdateReadAPIManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
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
    private boolean isSeen = false;


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

        int id = (chatItem.getId() != null) ? Integer.parseInt(chatItem.getId()) : 0;
        Log.w("ChatAdapter", "ID:---->>> " + id );

        List<ChatMessage> messages = chatItem.getMessages();


        if (messages != null && !messages.isEmpty()) {
            int totalUnread = 0;


            for (ChatMessage message : messages) {
                totalUnread += message.getUnread();
            }


            Log.d("ChatAdapter", "Total Unread Messages: " + totalUnread);
            Log.w("ChatAdapter", "message_read ->: " +message_read);

            SharedPrefManager sharedPrefManager = new SharedPrefManager(context);


            int chatIsRead = Integer.parseInt(String.valueOf(chatItem.getMessage_read()));

            if (chatIsRead != 0 || isSeen) {
                holder.chatCount.setVisibility(View.GONE);
                Log.d("ChatAdapter", "chatIsRead: " + chatIsRead);
            } else {
                TransitionAnimationManager.zoomOut(holder.chatCount, 1500);
                TransitionAnimationManager.zoomIn(holder.chatCount, 1700);
                holder.chatCount.setBackgroundResource(R.drawable.indicator_on);
                Log.d("ChatAdapter", "chatIsRead: " + chatIsRead);
                holder.chatCount.setVisibility(View.VISIBLE);
                holder.chatCount.setText(String.valueOf(totalUnread));

                ChatMessage lastMessage = messages.get(messages.size() - 1);
                holder.chatName.setText(lastMessage.getText());
            }

        } else {
            holder.chatCount.setText("0");

        }



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
            Log.d("ITEMS_COLLEGUE", "Item clicked: Channel=" + chatChannels + ", Receiver=" + receivers_name +",  Sender=  , "+senders_name +  "  SenderID=  , "+id);
            if (members == null || members.isEmpty()) {
                Log.e(TAG, "No members found for this chat. Aborting click action.");
                Toast.makeText(context, "No contact data available.", Toast.LENGTH_SHORT).show();
                return;
            }
            TransitionAnimationManager.zoomOut(v, 100);
            v.postDelayed(() -> {
                TransitionAnimationManager.zoomIn(v, 50);
                isSeen = true;
                holder.chatCount.setVisibility(View.GONE);

                UpdateReadAPIManager.updateReadStatus(context, chatChannels, new UpdateReadAPIManager.ApiCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("ChatAdapter", "Total Unread Messages: Read status updated successfully!");
                        Intent intent = new Intent(context, InnerChats.class);
                        intent.putExtra("token", AuthManager.getInstance(context).getAccessToken());
                        intent.putExtra("channel", chatChannels);
                        intent.putExtra("Sender", senders_name);
                        intent.putExtra("Receiver", receivers_name);
                        intent.putExtra("Avatar", chatItem.getAvatarPath());
                        intent.putExtra("id", id);
                        intent.putExtra("isSeen", isSeen);

                        Gson gson = new Gson();
                        String membersJson = gson.toJson(members);
                        intent.putExtra("chatAPIData", membersJson);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.d("ChatAdapter", "Total Unread Messages: Read status update failed: " + error);
                    }
                });


            }, 100);
        });

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }




    public void addChats(List<ChatDetails> newChats) {
        if (newChats != null) {
            chatList.clear();
            chatList.addAll(newChats);

            // Sort the list based on unread message count (chatCount visibility)
            Collections.sort(chatList, (chat1, chat2) -> {
                int unreadCount1 = getTotalUnreadMessages(chat1);
                int unreadCount2 = getTotalUnreadMessages(chat2);

                // If unread messages exist, place at top
                return Integer.compare(unreadCount2, unreadCount1);
            });

            Log.d(TAG, "Total number of chats after sorting: " + chatList.size());
            notifyDataSetChanged();
        }
    }

    private int getTotalUnreadMessages(ChatDetails chatItem) {
        if (chatItem.getMessages() != null) {
            int totalUnread = 0;
            for (ChatMessage message : chatItem.getMessages()) {
                totalUnread += message.getUnread();
            }
            return totalUnread;
        }
        return 0;
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView chatName, chatCount;
        CircleImageView chatAvatar;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            chatName = itemView.findViewById(R.id.chatName);
            chatAvatar = itemView.findViewById(R.id.avatar);
            chatCount = itemView.findViewById(R.id.chatCount);

        }
    }

}
/*
    public void addChats(List<ChatDetails> newChats ) {
        if (newChats != null) {
            chatList.clear();
            chatList.addAll(newChats);
            Log.d(TAG, "Total number of chats after adding new ones: " + chatList.size());
            notifyDataSetChanged();
        }
    }

 */

/*
    public void reloadFragment(String chatCount) {
        if (context instanceof AppCompatActivity) {
            int count = Integer.parseInt(chatCount); // Convert String to int

            if (count > 0) {
                Log.d("reloadFragment", "Data has been added, reloading fragment.");

                AppCompatActivity activity = (AppCompatActivity) context;
                Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.fragment_container); // Use the correct fragment ID
                if (currentFragment != null) {
                    activity.getSupportFragmentManager().beginTransaction()
                            .detach(currentFragment)
                            .attach(currentFragment)
                            .commit();
                }
            } else {
                Log.d("reloadFragment", "No data, handle accordingly.");
            }
        }
    }

 */