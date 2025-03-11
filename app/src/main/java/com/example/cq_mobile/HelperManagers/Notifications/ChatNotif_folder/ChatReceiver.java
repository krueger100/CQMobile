package com.example.cq_mobile.HelperManagers.Notifications.ChatNotif_folder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.cq_mobile.ui.chat.ChatFragment;
import com.example.cq_mobile.ui.chat.InnerChatsFolder.InnerChats;

public class ChatReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String channelUrl = intent.getStringExtra("channel_url");
        Log.d("ChatReceiver", "Received channel URL: " + channelUrl);

        Intent chatIntent = new Intent(context, ChatActivity.class);
        chatIntent.putExtra("channel_url", channelUrl);
        chatIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(chatIntent);
    }
}
