package com.example.cq_mobile.HelperManagers.Notifications.ChatNotif_folder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.cq_mobile.ui.chat.ChatFragment;
import com.example.cq_mobile.ui.chat.InnerChatsFolder.InnerChats;

public class ChatReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String channelUrl = intent.getStringExtra("channel_url");
        Intent chatIntent = new Intent(context, InnerChats.class); // Replace with actual activity
        chatIntent.putExtra("channel_url", channelUrl);
        chatIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(chatIntent);


    }
}
