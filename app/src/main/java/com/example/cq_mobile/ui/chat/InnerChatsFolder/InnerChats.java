package com.example.cq_mobile.ui.chat.InnerChatsFolder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cq_mobile.HelperManagers.Animation.TransitionAnimationManager;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatMember;
import com.example.cq_mobile.ui.chat.sendMessageFolder.SendMessageApiManager;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class InnerChats extends AppCompatActivity {
    private static final String TAG = "InnerChats";
    private TextView receiver_Name;
    private CircleImageView Avatar;
    private RecyclerView recyclerView_messages;
    private MessagesAdapter messagesAdapter;
    private List<InnerChatDetails> messageList = new ArrayList<>();
    private EditText messageInput;
    private ImageButton sendButton;
    private MessageManager messageManager;
    private String currentUser;
    private String email;
    private String password;

    private boolean isLoading = false;
    private String accessToken;
    private ImageView chatOff;
    String avatar;
    CardView cardView4;
    ImageView showChatInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_chats);
        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
        email = sharedPrefManager.getEmail();
        password = sharedPrefManager.getPassword();

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        cardView4 = findViewById(R.id.cardViewMessage);
        receiver_Name = findViewById(R.id.receiver_Name);
        Avatar = findViewById(R.id.Avatar);
        recyclerView_messages = findViewById(R.id.recyclerView_messages);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        chatOff = findViewById(R.id.chatInputHide);
        showChatInput = findViewById(R.id.showChatINput);
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


        int channel = intent.getIntExtra("channel", -1);
        Log.d("InnerChats", "Received Channel: " + channel);








        String chatMessagesJson = getIntent().getStringExtra("chatAPIData");
        List<ChatMember> messagesList = null;
        if (chatMessagesJson != null) {
            Gson gson = new Gson();
            Type messageType = new TypeToken<List<ChatMember>>() {
            }.getType();
            messagesList = gson.fromJson(chatMessagesJson, messageType);

            Log.d("InnerChats", "Parsed chatMessagesJson: " + messagesList);
        }


        receiver_Name.setText(receiver);

        if (messageManager == null) {
            messageManager = new MessageManager(getApplicationContext());
        }

        chatOff.setOnClickListener(v -> {
            v.postDelayed(() -> {
                showChatInput.setVisibility(View.VISIBLE);
                v.postDelayed(() -> {
                    TransitionAnimationManager.slideInFromRight(v, 50);
                    cardView4.setVisibility(View.GONE);
                }, 200);
            }, 200);
        });

        showChatInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionAnimationManager.slideOutToRight(v, 150);
                v.postDelayed(() -> {
                    cardView4.setVisibility(View.VISIBLE);
                    v.postDelayed(() -> {
                        TransitionAnimationManager.slideInFromRight(v, 50);
                        showChatInput.setVisibility(View.GONE);
                    }, 150);
                }, 150);

            }
        });


        currentUser = getIntent().getStringExtra("Sender");
        Log.d("InnerChats", "Sender : " + currentUser);

        String chatMembersJson = getIntent().getStringExtra("chatAPIData");
        List<ChatMember> membersList = null;
        int senderMemberId = -1;
        int receiverMemberId = -1;
        String avatar_sender = null;
        String avatar_receiver = null;
        String senderName = "";
        String receiverName = "";

        if (chatMembersJson != null) {
            Gson gson = new Gson();
            Type messageType = new TypeToken<List<ChatMember>>() {}.getType();
            membersList = gson.fromJson(chatMembersJson, messageType);

            Log.d("InnerChats", "Parsed chatMembersJson: " + membersList);

            if (membersList.size() == 2) {
                ChatMember member1 = membersList.get(0);
                ChatMember member2 = membersList.get(1);

                // Determine sender and receiver based on currentUser
                if (currentUser != null && currentUser.equals(member1.getName())) {
                    senderName = member1.getName();
                    avatar_sender = member1.getAvatarPath();
                    receiverName = member2.getName();
                    avatar_receiver = member2.getAvatarPath();
                    senderMemberId = member1.getId();  // Sender's ID
                    receiverMemberId = member2.getId();  // Receiver's ID
                } else {
                    senderName = member2.getName();
                    avatar_sender = member2.getAvatarPath();
                    receiverName = member1.getName();
                    avatar_receiver = member1.getAvatarPath();
                    senderMemberId = member2.getId();  // Sender's ID
                    receiverMemberId = member1.getId();  // Receiver's ID
                }

                Log.w(TAG, "Sender Name: " + senderName);
                Log.w(TAG, "Sender Avatar: " + avatar_sender);
                Log.w(TAG, "Receiver Name: " + receiverName);
                Log.w(TAG, "Receiver Avatar: " + avatar_receiver);
            }
        }



        AccessTokenRequest tokenRequest = new AccessTokenRequest(email, password);
        getAccessTokenAndLoadChats(tokenRequest, progressBar, senderMemberId, channel, membersList, receiverMemberId, avatar_receiver, currentUser);


        recyclerView_messages.setHasFixedSize(true);
        recyclerView_messages.setLayoutManager(new LinearLayoutManager(this));
    }



    private void getAccessTokenAndLoadChats(AccessTokenRequest tokenRequest, ProgressBar progressBar, int id, int channel, List<ChatMember> membersList, int secondMemberId, String avatar_receiver, String currentUser) {
        messageManager.getAccessToken(tokenRequest, new MessageManager.AccessTokenCallback() {
            @Override
            public void onAccessTokenReceived(String token) {
                accessToken = token;
                Log.d("InnerChats", "Access Token received: " + token);
                Log.d("InnerChats", "Receiver Avatar: " + avatar_receiver);
                Log.d("InnerChats", "Sender Name: " + currentUser);
                Log.d("InnerChats", "Sender ID: " + id);
                Log.d("InnerChats", "Receiver ID: " + secondMemberId);

                sendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String receiver = String.valueOf(secondMemberId);
                        String chatchannel = String.valueOf(channel);
                        String sender = String.valueOf(id);
                        String message = messageInput.getText().toString().trim();
                        String avatar = avatar_receiver;
                        String date = "2025-02-03";
                        String time = "11:02:59 PM";
                        String name = currentUser;

                        if (!message.isEmpty()) {
                            SendMessageApiManager.sendMessage(receiver, chatchannel, sender, message, avatar, date, time, name, token, new SendMessageApiManager.ApiCallback() {
                                @Override
                                public void onSuccess() {
                                    messageInput.post(() -> {
                                        Toast.makeText(v.getContext(), "Message sent successfully!", Toast.LENGTH_SHORT).show();
                                        messageInput.setText("");

                                        InnerChatAPIItem newMessage = new InnerChatAPIItem();
                                        newMessage.setSender(sender);
                                        newMessage.setMessage(message);
                                        newMessage.setAvatar_path(avatar);
                                        newMessage.setName(name);

                                        // ✅ Update RecyclerView
                                        messagesAdapter.addMessage(newMessage);
                                        recyclerView_messages.scrollToPosition(messagesAdapter.getItemCount() - 1);

                                    });
                                }

                                @Override
                                public void onFailure(String error) {
                                    messageInput.post(() ->
                                            Toast.makeText(v.getContext(), "Failed to send message: " + error, Toast.LENGTH_SHORT).show()
                                    );
                                }
                            });
                        } else {
                            Toast.makeText(v.getContext(), "Message cannot be empty!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                loadChatsWithToken(progressBar, id, channel, membersList,avatar_receiver);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("InnerChats", "Error fetching access token: " + errorMessage);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loadChatsWithToken(ProgressBar progressBar, int id, int channel, List<ChatMember> membersList, String avatar_receiver) {
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
                displayChats(chats, id,channel,membersList,progressBar);
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


    private void displayChats(List<InnerChatAPIItem> chatItems, int id, int channel, List<ChatMember> membersList, ProgressBar progressBar) {
        if (messagesAdapter == null) {
            messagesAdapter = new MessagesAdapter(this, chatItems, id,channel,membersList,currentUser,accessToken,progressBar);
            recyclerView_messages.setAdapter(messagesAdapter);
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
  "receiver": 331,
  "chatchannel": 58,
  "replied_to": null,
  "message": {
    "text": "test chat from rich",
    "avatar": "asdad",
    "date": "2025-02-03",
    "time": "11:02:59 PM",
    "sender": 3,
    "name": "Richard Wetherell"
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



curl -v -X DELETE "https://aws.customquoter.co.uk/api/m/chats/delete" \
-H "Authorization: Bearer 6331|n98FC0W7s7RlA4o5mnCmfxTDYlzWkWF2qg2B4c0m" \
-H "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2" \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-d '{
  "id": "12511"
}'


curl -v -X PATCH "https://aws.customquoter.co.uk/api/m/chats/update_read" \
-H "Authorization: Bearer 6331|n98FC0W7s7RlA4o5mnCmfxTDYlzWkWF2qg2B4c0m" \
-H "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2" \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-d '{
    "channel": 58
}'

NOTIFICATIONS TICKETS
curl -X GET "https://aws.customquoter.co.uk/api/m/notifications" \
-H "Authorization: Bearer 7898|QVu8LPIEoPkdOLqJToYdAYEoE3ydz1Qu95vx4npS" \
-H "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2" \
-H "Accept: application/json" \
-H "Content-Type: application/json" \
-H "User-Agent: PostmanRuntime/7.43.0" \
-H "Accept-Encoding: gzip, deflate, br" \
-H "Connection: keep-alive"


 */



