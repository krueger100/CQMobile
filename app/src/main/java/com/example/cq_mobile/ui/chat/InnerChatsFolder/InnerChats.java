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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatMember;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class InnerChats extends AppCompatActivity {
    private static final String TAG = "InnerChats";
    private TextView receiver_Name;
    private ImageView Avatar;
    private RecyclerView recyclerView_messages;
    private MessagesAdapter messagesAdapter;
    private List<InnerChatDetails> messageList = new ArrayList<>();
    private EditText messageInput;
    private ImageView sendButton;
    private MessageManager messageManager;
    private String currentUser;
    private String email;
    private String password;

    private boolean isLoading = false;
    private String accessToken;
    private ImageView chatOff;
    String avatar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_chats);
        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
        email = sharedPrefManager.getEmail();
        password = sharedPrefManager.getPassword();

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        receiver_Name = findViewById(R.id.receiver_Name);
        Avatar = findViewById(R.id.Avatar);
        recyclerView_messages = findViewById(R.id.recyclerView_messages);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        chatOff = findViewById(R.id.chatInputHide);

        Intent intent = getIntent();
        String receiver = intent.getStringExtra("Receiver");
        avatar = intent.getStringExtra("Avatar");

        currentUser = intent.getStringExtra("Sender");
        accessToken = intent.getStringExtra("token");

        Glide.with(this)
                .load(avatar)
                .placeholder(R.drawable.circular_background)
                .error(R.drawable.emptyglide)
                .into(Avatar);


        int id = intent.getIntExtra("id", -1); // Defaulting to -1 to check if it's missing
        int channel = intent.getIntExtra("channel", -1);

        Log.w("InnerChats", "Received ID: " + id);
        Log.w("InnerChats", "Received Channel: " + channel);


        String chatMembersJson = getIntent().getStringExtra("chatAPIData");
        List<ChatMember> membersList = null;
        if (chatMembersJson != null) {
            Gson gson = new Gson();
            Type messageType = new TypeToken<List<ChatMember>>() {
            }.getType();
            membersList = gson.fromJson(chatMembersJson, messageType);

            Log.w("InnerChats", "Parsed chatMembersJson: " + membersList);
        }

        String chatMessagesJson = getIntent().getStringExtra("chatAPIData");
        List<ChatMember> messagesList = null;
        if (chatMessagesJson != null) {
            Gson gson = new Gson();
            Type messageType = new TypeToken<List<ChatMember>>() {
            }.getType();
            messagesList = gson.fromJson(chatMessagesJson, messageType);

            Log.w("InnerChats", "Parsed chatMessagesJson: " + messagesList);
        }


        receiver_Name.setText(receiver);

        if (messageManager == null) {
            messageManager = new MessageManager(getApplicationContext());
        }

        chatOff.setOnClickListener(v -> {
            // Handle Chat Off action if needed
        });


        AccessTokenRequest tokenRequest = new AccessTokenRequest(email, password);
        getAccessTokenAndLoadChats(tokenRequest, progressBar, id, channel, membersList);

        recyclerView_messages.setHasFixedSize(true);
        recyclerView_messages.setLayoutManager(new LinearLayoutManager(this));

    }

    private void getAccessTokenAndLoadChats(AccessTokenRequest tokenRequest, ProgressBar progressBar, int id, int channel, List<ChatMember> membersList) {
        messageManager.getAccessToken(tokenRequest, new MessageManager.AccessTokenCallback() {
            @Override
            public void onAccessTokenReceived(String token) {
                accessToken = token;
                Log.d("InnerChats", "Access Token received: " + token);
                loadChatsWithToken(progressBar,id,channel,membersList);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("InnerChats", "Error fetching access token: " + errorMessage);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loadChatsWithToken(ProgressBar progressBar, int id, int channel, List<ChatMember> membersList) {
        if (isLoading) return;
        isLoading = true;

        if (accessToken == null) {
            Log.e("InnerChats", "Access token is missing.");
            progressBar.setVisibility(View.GONE);
            isLoading = false;
            return;
        }

        Log.d("InnerChats", "Loading chats for ID: " + id + ", Channel: " + channel);

        messageManager.loadMessages(id, channel, new MessageManager.AllChatsCallback() {
            @Override
            public void onAllChatsLoaded(List<InnerChatAPIItem> chats, String rawJson) {
                progressBar.setVisibility(View.GONE);
                isLoading = false;
                List<InnerMessageDetails> chatDetailsList = extractMessageData(rawJson);
                Log.d("ChatPageFragment", "Extracted chat details: " + chatDetailsList);



                Log.d("InnerChats", "Raw JSON received: " + rawJson);
               // extractMessageData(rawJson);
                displayChats(chats, id,channel,membersList);
            }

            @Override
            public void onError(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                isLoading = false;
                Log.e("InnerChats", "Chat Loading Error: " + errorMessage);
            }
        });
    }


    public List<InnerMessageDetails> extractMessageData(String rawJson) {
        List<InnerMessageDetails> chatDetailsList = new ArrayList<>();

        if (rawJson == null || rawJson.isEmpty()) {
            Log.e("ChatLoader", "Raw JSON is empty or null");
            return chatDetailsList;
        }

        try {
            Gson gson = new Gson();
            MessageAPIResponse response = gson.fromJson(rawJson, MessageAPIResponse.class);

            if (response == null || response.getData() == null || response.getData().getContact() == null) {
                Log.e("ChatLoader", "Invalid API response");
                return chatDetailsList;
            }

            Contact contact = response.getData().getContact();
            Map<String, List<InnerChatMessage>> messagesMap = contact.getMessages();

            if (messagesMap == null || messagesMap.isEmpty()) {
                Log.e("ChatLoader", "No messages found.");
                return chatDetailsList;
            }

            for (Map.Entry<String, List<InnerChatMessage>> entry : messagesMap.entrySet()) {
                String date = entry.getKey();
                List<InnerChatMessage> messages = entry.getValue();

                Log.d("ChatLoader", "Date: " + date + " (" + messages.size() + " messages)");

                for (InnerChatMessage message : messages) {
                    if (message.getMessage() == null) {
                        Log.e("ChatLoader", "Message object is null");
                        continue;
                    }


                    String name = message.getMessage().getName() != null ? message.getMessage().getName() : "Unknown";
                    String sender = message.getMessage().getSender() != null ? message.getMessage().getSender() : "Unknown";
                    String text = message.getMessage().getText() != null ? message.getMessage().getText() : "No Text";
                    String time = message.getMessage().getTime() != null ? message.getMessage().getTime() : "Unknown Time";

                    Log.d("ChatLoader", "Message Details:");
                    Log.d("ChatLoader", " - Date: " + date);
                    Log.d("ChatLoader", " - Avatar: " + avatar);
                    Log.d("ChatLoader", " - Name: " + name);
                    Log.d("ChatLoader", " - Sender: " + sender);
                    Log.d("ChatLoader", " - Text: " + text);
                    Log.d("ChatLoader", " - Time: " + time);
                }
            }
        } catch (JsonSyntaxException e) {
            Log.e("ChatLoader", "JSON Parsing Error: " + e.getMessage(), e);
        }

        return chatDetailsList;
    }


    private void displayChats(List<InnerChatAPIItem> chatItems, int id, int channel, List<ChatMember> membersList) {
        if (messagesAdapter == null) {
            messagesAdapter = new MessagesAdapter(this, chatItems, id,channel,membersList,currentUser);
            recyclerView_messages.setAdapter(messagesAdapter);
            Log.d("InnerChats", "Adapter set with " + chatItems.size() + " items.");
        } else {
            messagesAdapter.addChats(chatItems);
            messagesAdapter.notifyDataSetChanged();
            Log.d("InnerChats", "Added " + chatItems.size() + " new chats.");
        }
    }



}


/*
SENDING CHAT
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

GET THE CHATS BY CHANNEL

curl -X GET "https://aws.customquoter.co.uk/api/m/chats/chat?id=331&channel=58&unread=" \
-H "Authorization: Bearer 7898|QVu8LPIEoPkdOLqJToYdAYEoE3ydz1Qu95vx4npS" \
-H "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2" \
-H "Accept: application/json" \
-H "Content-Type: application/json" \
-H "User-Agent: PostmanRuntime/7.43.0" \
-H "Accept-Encoding: gzip, deflate, br" \
-H "Connection: keep-alive"


 */
  /*
    public void extractMessageData(String rawJson) {
        if (rawJson == null || rawJson.isEmpty()) {
            Log.e("ChatLoader", "Raw JSON is empty or null");
            return;
        }

        try {
            Gson gson = new Gson();
            MessageAPIResponse response = gson.fromJson(rawJson, MessageAPIResponse.class);

            if (response == null || response.getData() == null || response.getData().getContact() == null) {
                Log.e("ChatLoader", "Invalid API response");
                return;
            }

            Contact contact = response.getData().getContact();
            Map<String, List<InnerChatMessage>> messagesMap = contact.getMessages();

            if (messagesMap == null || messagesMap.isEmpty()) {
                Log.e("ChatLoader", "No messages found.");
                return;
            }

            for (Map.Entry<String, List<InnerChatMessage>> entry : messagesMap.entrySet()) {
                String date = entry.getKey();
                List<InnerChatMessage> messages = entry.getValue();

                Log.d("ChatLoader", "Date: " + date + " (" + messages.size() + " messages)");

                for (InnerChatMessage message : messages) {
                    if (message.getMessage() == null) {
                        Log.e("ChatLoader", "Message object is null");
                        continue;
                    }

                    String avatar = message.getMessage().getAvatar() != null ? message.getMessage().getAvatar() : "No Avatar";
                    String name = message.getMessage().getName() != null ? message.getMessage().getName() : "Unknown";
                    String sender = message.getMessage().getSender() != null ? message.getMessage().getSender() : "Unknown";
                    String text = message.getMessage().getText() != null ? message.getMessage().getText() : "No Text";
                    String time = message.getMessage().getTime() != null ? message.getMessage().getTime() : "Unknown Time";

                    Log.d("ChatLoader", "Message Details:");
                    Log.d("ChatLoader", " - Date: " + date);
                    Log.d("ChatLoader", " - Avatar: " + avatar);
                    Log.d("ChatLoader", " - Name: " + name);
                    Log.d("ChatLoader", " - Sender: " + sender);
                    Log.d("ChatLoader", " - Text: " + text);
                    Log.d("ChatLoader", " - Time: " + time);
                }
            }
        } catch (JsonSyntaxException e) {
            Log.e("ChatLoader", "JSON Parsing Error: " + e.getMessage(), e);
        }
    }

     */
