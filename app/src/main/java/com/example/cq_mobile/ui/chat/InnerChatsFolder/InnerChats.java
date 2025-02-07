package com.example.cq_mobile.ui.chat.InnerChatsFolder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatDetails;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatManager;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatMember;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatMessage;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

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
    String email;
    String password;
    private int currentPage = 1;  // Track pagination
    private final int pageSize = 20;
    private boolean isLoading = false;
    private String accessToken;
    ImageView chatOff;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_chats);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        receiver_Name = findViewById(R.id.receiver_Name);
        Avatar = findViewById(R.id.Avatar);
        recyclerView_messages = findViewById(R.id.recyclerView_messages);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        chatOff = findViewById(R.id.chatOff);

        Intent intent = getIntent();
        String receiver = intent.getStringExtra("Receiver");
        String avatar = intent.getStringExtra("avatar");
        currentUser = intent.getStringExtra("Sender");
        accessToken = intent.getStringExtra("token");
        int id = intent.getIntExtra("id", 0);
        String chatListJson = intent.getStringExtra("chatListJson");

        // Logging receiver and avatar data
        Log.d("InnerChats", "Sender: " + currentUser);
        Log.d("InnerChats", "Receiver: " + receiver);
        Log.d("InnerChats", "Avatar URL: " + avatar);
        Log.d("InnerChats", "chatListJson: " + chatListJson);
        Log.d("InnerChats", "id: " + id);

        receiver_Name.setText(receiver);
        Glide.with(this)
                .load(avatar)
                .placeholder(R.drawable.circular_background)
                .error(R.drawable.emptyglide)
                .into(Avatar);

        // Ensure ChatManager is initialized here
        if (chatManager == null) {
            chatManager = new ChatManager(getApplicationContext());
        }
        String chatMessagesJson = intent.getStringExtra("chatAPIData");
        Type type = new TypeToken<List<ChatMessage>>(){}.getType();
        List<ChatMessage> chatMessages = new Gson().fromJson(chatMessagesJson, type);

        chatOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        // Deserialize chatListJson safely
        if (chatListJson != null) {
            try {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<ChatDetails>>() {}.getType();
                messageList = gson.fromJson(chatListJson, listType);

                // Log the parsed chat details and messages
                Log.d("InnerChats", "Parsed chat details: " + messageList.size());
                for (ChatDetails chatDetails : messageList) {
                    Log.d("InnerChats", "Chat: " + chatDetails.getChatName() + ", ID: " + chatDetails.getId() +
                            ", Members: " + chatDetails.getMembers().size() + ", Messages count: " + chatDetails.getMessages().size());

                    // Log individual messages for each ChatDetails
                    for (ChatMessage chatMessage : chatDetails.getMessages()) {
                        Log.d("InnerChats", "Message ID: " + chatMessage.getId() + ", Text: " + chatMessage.getText() +
                                ", Time: " + chatMessage.getTime() + ", Date: " + chatMessage.getDate());
                        progressBar.setVisibility(View.GONE);
                    }
                }

                // Get the messages for the current chat
                ChatDetails selectedChat = null;
                for (ChatDetails chat : messageList) {
                    if (chat.getChatName().equals(receiver)) {
                        selectedChat = chat;
                        break;
                    }
                }

                // If selectedChat is found, set up the RecyclerView
                if (selectedChat != null) {
                    List<ChatMessage> chatMessagesForSelectedChat = selectedChat.getMessages();
                    List<ChatMember> chatMemberForSelectedChat = selectedChat.getMembers();

                    MessagesAdapter messagesAdapter = new MessagesAdapter(this, chatMessagesForSelectedChat, currentUser, chatMemberForSelectedChat,id);
                    recyclerView_messages.setAdapter(messagesAdapter);
                    recyclerView_messages.setLayoutManager(new LinearLayoutManager(this));

                }

            } catch (JsonSyntaxException e) {
                Log.e("InnerChats", "Failed to deserialize chatListJson", e);
                // Handle error: show an error message or empty list
            }
        } else {
            Log.e("InnerChats", "chatListJson is null");
        }


    }


    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (!messageText.isEmpty()) {
            // Ensure that you pass all required parameters for ChatDetails
            ChatDetails newMessage = new ChatDetails(chatName, id, currentUser, receiver_Name.getText().toString(), messageText, "", channel, status, channelStatus, "");

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


/*

curl -v -X POST "https://aws.customquoter.co.uk/api/m/chats/send" \
-H "Authorization: Bearer 5898|lt8AgacV3I6KYrNkpmfvKbG4cIGLZLQE59LZj38h" \
-H "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2" \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-H "User-Agent: PostmanRuntime/7.43.0" \
-H "Accept-Encoding: gzip, deflate, br" \
-H "Connection: keep-alive" \
-d '{
  "receiver": 278,
  "chatchannel": 1,
  "replied_to": null,
  "message": {
    "text": "asd",
    "avatar": "asdad",
    "date": "2025-02-03",
    "time": "11:02:59 PM",
    "sender": 379,
    "name": "Marwin Intal"
  }
}'

 */
