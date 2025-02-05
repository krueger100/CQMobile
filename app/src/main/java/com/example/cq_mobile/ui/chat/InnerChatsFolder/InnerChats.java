package com.example.cq_mobile.ui.chat.InnerChatsFolder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatDetails;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatManager;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
public class InnerChats extends AppCompatActivity {

    private TextView receiver_Name;
    private ImageView Avatar;
    private RecyclerView recyclerView_messages;
    private MessagesAdapter messagesAdapter;
    private List<ChatDetails> messageList = new ArrayList<>();
    private EditText messageInput;
    private ImageView sendButton;
    private ChatManager chatManager;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_chats);

        receiver_Name = findViewById(R.id.receiver_Name);
        Avatar = findViewById(R.id.Avatar);
        recyclerView_messages = findViewById(R.id.recyclerView_messages);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        Intent intent = getIntent();
        String receiver = intent.getStringExtra("Receiver");
        String avatar = intent.getStringExtra("avatar");
        currentUser = intent.getStringExtra("Sender");
        String chatListJson = intent.getStringExtra("chatListJson");

        receiver_Name.setText(receiver);
        Glide.with(this)
                .load(avatar)
                .placeholder(R.drawable.circular_background)
                .error(R.drawable.emptyglide)
                .into(Avatar);

        if (chatListJson != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<ChatDetails>>() {}.getType();
            messageList = gson.fromJson(chatListJson, listType);
        }

        recyclerView_messages.setLayoutManager(new LinearLayoutManager(this));
        messagesAdapter = new MessagesAdapter(this, messageList, currentUser);
        recyclerView_messages.setAdapter(messagesAdapter);

        chatManager = new ChatManager(this);

        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (!messageText.isEmpty()) {
            // Ensure that you pass all required parameters for ChatDetails
            ChatDetails newMessage = new ChatDetails(currentUser, receiver_Name.getText().toString(), messageText, "", "");

            messageList.add(newMessage);
            messagesAdapter.notifyItemInserted(messageList.size() - 1);
            recyclerView_messages.scrollToPosition(messageList.size() - 1);
            messageInput.setText("");

            if (chatManager != null) {
                chatManager.sendMessage(newMessage); // Ensure this method exists
            }
        }
    }

}
