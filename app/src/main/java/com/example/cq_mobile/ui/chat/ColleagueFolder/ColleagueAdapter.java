package com.example.cq_mobile.ui.chat.ColleagueFolder;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cq_mobile.HelperManagers.Animation.TransitionAnimationManager;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatDetails;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatMember;
import com.example.cq_mobile.ui.chat.CombinedItem;
import com.example.cq_mobile.ui.chat.InnerChatsFolder.InnerChats;
import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.List;
public class ColleagueAdapter extends RecyclerView.Adapter<ColleagueAdapter.ColleagueViewHolder> {
    private static final String TAG = "ColleagueAdapter";

    private Context context;
    private List<CombinedItem> combinedList;
    private String email;
    private String password;
    private String accessToken;
    private String currentUserName;
    List<ChatDetails> chatDetailsList;


    public ColleagueAdapter(Context context, List<CombinedItem> combinedList, String email, String password, String currentUserName, String accessToken, List<ChatDetails> chatDetailsList) {
        this.context = context;
        this.email = email;
        this.password = password;
        this.currentUserName = currentUserName;
        this.accessToken = accessToken;
        this.combinedList = combinedList != null ? combinedList : new ArrayList<>();
        this.chatDetailsList = chatDetailsList != null ? chatDetailsList : new ArrayList<>();
    }

    @NonNull
    @Override
    public ColleagueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_colleague, parent, false);
        return new ColleagueViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ColleagueViewHolder holder, int position) {
        CombinedItem item = combinedList.get(position);
        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
        accessToken = sharedPrefManager.getAccessToken();
        email = sharedPrefManager.getEmail();
        password = sharedPrefManager.getPassword();
        int id_user = sharedPrefManager.getUserId();
        String avatar_url = sharedPrefManager.getAvatarUrl();

        Log.d(TAG, "Email: " + email);
        Log.d(TAG, "Password: " + password);
        Log.d(TAG, "id_user: " + id_user);
        Log.d(TAG, "avatar_url: " + avatar_url);
        Log.d(TAG, "currentUserName: " + currentUserName);

        String channel = null;
        String avatar = null;
        String receiverName = null;
        String senderName = null;
        List<ChatMember> members = null;
        int senderID = 0;

        Log.d("ColleagueAdapter", "combinedList size: " + combinedList.size());
        for (CombinedItem item2 : combinedList) {
            ColleagueAPIItem colleague = item2.getColleague();
            ChatDetails chat = item2.getChat();
            if (colleague != null) {
                Log.d("ColleagueAdapter", "Colleague: " + colleague.getName() + "\n" + ", ID: " + colleague.getId() + "\n" + "AVATAR : " + colleague.getAvatar_path());
                senderID = colleague.getId();
            }
            if (chat != null) {
                Log.d("ColleagueAdapter", "Chat: " + chat.getChatName() + "\n" + ", ID: " + chat.getId() + "\n" + "AVATAR : " + chat.getAvatarPath());

                try {
                    senderID = Integer.parseInt(chat.getId());
                } catch (NumberFormatException e) {
                    Log.e("ColleagueAdapter", "Invalid chat ID format: " + chat.getId(), e);
                    senderID = 0;
                }
            }
        }

        if (item.getColleague() != null) {
            ColleagueAPIItem colleagueItem = item.getColleague();
            receiverName = colleagueItem.getName() != null ? colleagueItem.getName() : "Unknown Colleague";
            channel = colleagueItem.getChannel() != null ? colleagueItem.getChannel() : "No channel";
            avatar = colleagueItem.getAvatar_path();
            members = new ArrayList<>();
            members.add(new ChatMember(colleagueItem.getId(), colleagueItem.getName(), avatar, colleagueItem.getChannel(), 0));
            holder.colleagueName.setText(receiverName);

        } else if (item.getChat() != null) {
            ChatDetails chatItem = item.getChat();
            receiverName = chatItem.getChatName() != null ? chatItem.getChatName() : "Unknown Chat";
            channel = String.valueOf(chatItem.getChannel());
            avatar = chatItem.getAvatarPath();
            List<ChatMember> chatMembers = chatItem.getMembers();  // Directly get the members list

            if (chatMembers != null && !chatMembers.isEmpty()) {
                members = new ArrayList<>(chatMembers);
            } else {
                int id = Integer.parseInt(String.valueOf(chatItem.getId()));
                members = new ArrayList<>();
                members.add(new ChatMember(id, chatItem.getName(), avatar, "email", 0));
            }
        }

        // Check if members is not null or empty before proceeding
        if (members != null && !members.isEmpty()) {
            if (avatar != null) {
                Glide.with(context)
                        .load(avatar)
                        .placeholder(R.drawable.circular_background)
                        .error(R.drawable.emptyglide)
                        .into(holder.colleagueAvatar);
            }

            // If senderID is 0, assume it's a colleague item and handle the button setup
            if (senderID == 0) {
                Log.d("ColleagueAdapter", "Colleague: " + "  --  " + senderID + " - - - - " + currentUserName + " - - - - " + avatar + " - - - - " + avatar_url);
                btn_Item(holder.itemView, channel, avatar, avatar_url, receiverName, currentUserName, senderID, members);
            } else {
                Log.d("ColleagueAdapter", "Chat: " + "  --  " + senderID + " - - - - " + currentUserName + " - - - - " + avatar + " - - - - " + avatar_url);
                btn_Item(holder.itemView, channel, avatar, avatar_url, receiverName, currentUserName, senderID, members);
            }
        } else {
            // If members is null or empty, remove the item from the list completely
            combinedList.remove(position);

            // Notify the adapter that the item has been removed
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, combinedList.size());
        }
    }


    private void btn_Item(View itemView, String channel, String avatar, String avatar_url, String receiverName, String senderName, int senderID, List<ChatMember> members) {
        String finalChannel = channel;
        String finalReceiverName = receiverName;
        String finalSenderName = senderName;
        int finalsenderID = senderID;
        List<ChatMember> finalMembers = members;

        itemView.setOnClickListener(v -> {
            if (finalMembers == null || finalMembers.isEmpty()) {
                Log.e(TAG, "No members found for this chat. Aborting click action.");
                return;
            }
            TransitionAnimationManager.zoomOut(v, 100);
            v.postDelayed(() -> {
                TransitionAnimationManager.zoomIn(v, 50);
                Intent intent = new Intent(context, InnerChats.class);
                intent.putExtra("token", accessToken);
                intent.putExtra("channel", finalChannel);
                intent.putExtra("Email", email);
                intent.putExtra("Password", password);
                intent.putExtra("Sender", currentUserName);
                intent.putExtra("Receiver", finalReceiverName);
                intent.putExtra("Avatar", avatar);
                intent.putExtra("Avatar_url", avatar_url);
                intent.putExtra("id", finalsenderID);
                intent.putExtra("source_adapter", "ColleagueAdapter");

                if (finalMembers != null) {
                    Gson gson = new Gson();
                    String membersJson = gson.toJson(finalMembers);
                    intent.putExtra("chatAPIData", membersJson);
                }

                Log.d(TAG, "Starting InnerChats activity with intent: " + intent.toString());
                context.startActivity(intent);
            }, 100);
        });

    }


    @Override
    public int getItemCount() {
        return combinedList.size();
    }

    public void addMoreItems(List<CombinedItem> newItems) {
        int startPosition = combinedList.size();
        combinedList.addAll(newItems);
        notifyItemRangeInserted(startPosition, newItems.size());
    }

    static class ColleagueViewHolder extends RecyclerView.ViewHolder {
        TextView colleagueName;
        ImageView colleagueAvatar;

        public ColleagueViewHolder(@NonNull View itemView) {
            super(itemView);
            colleagueName = itemView.findViewById(R.id.colleagueName);
            colleagueAvatar = itemView.findViewById(R.id.colleagueAvatar);
        }
    }
}

