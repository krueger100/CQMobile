package com.example.cq_mobile.ui.chat.InnerChatsFolder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cq_mobile.FirebaseUserData.FirebaseRetrieveDataManager;
import com.example.cq_mobile.HelperManagers.Animation.ClickAnimationManager;
import com.example.cq_mobile.HelperManagers.Animation.TransitionAnimationManager;
import com.example.cq_mobile.HelperManagers.Notifications.FCM;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.ServerDataReconnect;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.LoginFolder.AuthManager;
import com.example.cq_mobile.LoginFolder.ThreadManager;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;

import com.example.cq_mobile.ui.chat.ChatFolder.ChatMember;
import com.example.cq_mobile.ui.chat.sendMessageFolder.SendMessageApiManager;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class InnerChats extends AppCompatActivity {
    private static final String TAG = "InnerChats";
    private TextView receiver_Name;
    private CircleImageView Avatar;
    private RecyclerView recyclerView_messages;
    private MessagesAdapter messagesAdapter;
    private EditText messageInput;
    private ImageButton sendButton;
    private MessageManager messageManager;
    private String currentUser;


    private boolean isLoading = false;
    private String accessToken,username;
    private ImageView chatOff;
    String avatar;
    CardView cardView4;
    ImageView showChatInput;
    String avatar_url;
    String hhtpAvatar_url;
    LinearLayout l_1;
    String receiverId;
    AuthManager authManager;
    ServerDataReconnect serverDataReconnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_chats);
        serverDataReconnect = new ServerDataReconnect(getApplicationContext());
        ThreadManager.runOnMainThread(() -> {

        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
        authManager = AuthManager.getInstance(this);
        accessToken = authManager.getAccessToken();
        String fname  = AuthManager.getInstance(this).getFirstName();
        String lname  = AuthManager.getInstance(this).getLastName();
        username  = fname +" "+ lname;
        int currentUserID  = authManager.getUserId();
        sharedPrefManager.clearCurrentUserChat();

        Log.d("ChatAdapter", "notifCount: " + sharedPrefManager.getKeyisCurrentUserSeen());

        Intent intent2 = getIntent();
        if (intent2 != null && intent2.getBooleanExtra("fromNotification", false)) {
            String title = intent2.getStringExtra("title");
            String content = intent2.getStringExtra("content");
            String avatarUrl = intent2.getStringExtra("avatarUrl");
            String time = intent2.getStringExtra("time");
            String date = intent2.getStringExtra("date");
            String channelUrl = intent2.getStringExtra("channel_url");

            Log.d("InnerChats", "Opened from Notification");
            Log.d("InnerChats", "Title: " + title);
            Log.d("InnerChats", "Content: " + content);
            Log.d("InnerChats", "Avatar URL: " + avatarUrl);
            Log.d("InnerChats", "Time: " + time);
            Log.d("InnerChats", "Date: " + date);
            Log.d("InnerChats", "Channel URL: " + channelUrl);

        }



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
        l_1  = findViewById(R.id.l_1);

        Intent intent = getIntent();
        String receiver = intent.getStringExtra("Receiver");
        String avatar = intent.getStringExtra("Avatar");
        currentUser = intent.getStringExtra("Sender");
        accessToken = intent.getStringExtra("token");
         avatar_url = intent.getStringExtra("Avatar_url");
        hhtpAvatar_url =  " https://customquoteruk-live-uploads.s3.eu-west-2.amazonaws.com/"+avatar_url;

        receiverId = String.valueOf(getIntent().getIntExtra("id", 0));
        String source = getIntent().getStringExtra("source_adapter");




        if (source != null && source.equals("ColleagueAdapter")) {
            Log.d("InnerChats", "Launched from ColleagueAdapter");

        }


        Glide.with(this)
                .load(avatar)
                .placeholder(R.drawable.circular_background)
                .error(R.drawable.emptyglide)
                .into(Avatar);


            int channel;

            if (intent.hasExtra("channel")) {
                Object channelObj;

                channelObj = intent.getSerializableExtra("channel", Serializable.class);

                if (channelObj instanceof Integer) {
                    channel = (Integer) channelObj;
                } else if (channelObj instanceof String channelStr) {
                    if (!channelStr.isEmpty()) {
                        try {
                            channel = Integer.parseInt(channelStr);
                        } catch (NumberFormatException e) {
                            Log.e("InnerChats", "Invalid channel format: " + channelStr, e);
                            showEmptyState(progressBar);
                            return;
                        }
                    } else {
                        Log.e("InnerChats", "Empty channel string received.");
                        showEmptyState(progressBar);
                        return;
                    }
                } else {
                    Log.e("InnerChats", "Unsupported channel object type: " + channelObj);
                    showEmptyState(progressBar);
                    return;
                }
            } else {
                Log.e("InnerChats", "No channel data received.");
                showEmptyState(progressBar);
                return;
            }

            Log.d("InnerChats", "Received Channel: " + channel);

        Log.w("InnerChats", "Receiver ID --->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + receiverId);
        FirebaseRetrieveDataManager firebaseRetrieveDataManager = new FirebaseRetrieveDataManager(receiverId);



        String chatMessagesJson = getIntent().getStringExtra("chatAPIData");
        List<ChatMember> messagesList = null;
        if (chatMessagesJson != null) {
            Gson gson = new Gson();
            Type messageType = new TypeToken<List<ChatMember>>() {
            }.getType();
            messagesList = gson.fromJson(chatMessagesJson, messageType);

        }


        receiver_Name.setText(receiver);

        if (messageManager == null) {
            messageManager = new MessageManager(getApplicationContext());
        }

        chatOff.setOnClickListener(v -> {
            ClickAnimationManager.applyClickAnimation(v);
            v.postDelayed(() -> {
                showChatInput.setVisibility(View.VISIBLE);
                v.postDelayed(() -> {
                    TransitionAnimationManager.slideInFromRight(v, 50);
                    cardView4.setVisibility(View.GONE);
                }, 200);
            }, 200);
        });

        showChatInput.setOnClickListener(v -> {
            TransitionAnimationManager.slideOutToRight(v, 150);
            v.postDelayed(() -> {
                cardView4.setVisibility(View.VISIBLE);
                v.postDelayed(() -> {
                    TransitionAnimationManager.slideInFromRight(v, 50);
                    showChatInput.setVisibility(View.GONE);
                }, 150);
            }, 150);

        });


        currentUser = getIntent().getStringExtra("Sender");
        String chatMembersJson = getIntent().getStringExtra("chatAPIData");


        int senderMemberId;
        int receiverMemberId;
        int currentUserId;
        List<ChatMember> membersList;
        String senderName;
        String avatar_sender;
        String receiverName;
        String avatar_receiver;
        String hhtpAvatar_urlSender;

        try {
            if (receiverId != null && !receiverId.isEmpty()) {
                receiverMemberId = Integer.parseInt(receiverId);
                currentUserId = currentUserID;
            } else {
                receiverMemberId = 0;
                currentUserId = 0;
            }

            membersList = null;
            senderMemberId  = currentUserId;
            senderName = currentUser;
            avatar_sender = avatar_url;
            receiverName = receiver;
            avatar_receiver = avatar;

        } catch (NumberFormatException e) {
            e.printStackTrace();
            receiverMemberId = 0;
            membersList = null;
            senderMemberId = 0;
            senderName = currentUser;
            avatar_sender = avatar_url;
            receiverName = receiver;
            avatar_receiver = avatar;

        }


        Log.w("InnerChats", "Sender ID : "+ senderName +" |Receiver ID - "+ receiverName);
        Log.w("InnerChats", "Sender Avatar : "+ avatar_sender +" |  Receiver Avatar - "+ avatar_receiver);

        if (chatMembersJson != null) {
            Gson gson = new Gson();
            Type messageType = new TypeToken<List<ChatMember>>() {}.getType();
            membersList = gson.fromJson(chatMembersJson, messageType);

            if (membersList.size() == 2) {
                ChatMember member1 = membersList.get(0);
                ChatMember member2 = membersList.get(1);

                // Determine sender and receiver based on currentUser
                if (currentUser != null && currentUser.equals(member1.getName())) {
                    senderName = member1.getName();
                    avatar_sender = member1.getAvatarPath();
                    receiverName = member2.getName();
                    avatar_receiver = member2.getAvatarPath();
                    senderMemberId = member1.getId();
                    receiverMemberId = member2.getId();

                    Log.d(TAG, "Sender Name: <<--" + senderName);
                    Log.d(TAG, "Sender Avatar: <<--" + avatar_sender);
                    Log.d(TAG, "Receiver Name: <<--" + receiverName);
                    Log.d(TAG, "Receiver Avatar: <<--" + avatar_receiver);
                    Log.d(TAG, "Current USer ID:  senderMemberId: <<--" + senderMemberId); //
                } else {
                    senderName = member2.getName();
                    avatar_sender = member2.getAvatarPath();
                    receiverName = member1.getName();
                    avatar_receiver = member1.getAvatarPath();
                    senderMemberId = member2.getId();
                    receiverMemberId = member1.getId();

                    Log.d(TAG, "Sender Name: -->>" + senderName);
                    Log.d(TAG, "Sender Avatar: -->>" + avatar_sender);
                    Log.d(TAG, "Receiver Name: -->>" + receiverName);
                    Log.d(TAG, "Receiver Avatar: -->>" + avatar_receiver);
                    Log.d(TAG, "Current USer ID:  senderMemberId: -->>" + senderMemberId);
                }

            }


        }
        Context context = getApplicationContext();

            LoadChats(accessToken, progressBar, senderMemberId, channel, membersList, receiverMemberId, currentUser,username, String.valueOf(currentUserID),avatar_receiver,hhtpAvatar_url,firebaseRetrieveDataManager);

        recyclerView_messages.setHasFixedSize(true);
        recyclerView_messages.setLayoutManager(new LinearLayoutManager(this));


            // Code to run on the main thread
            Log.d("MainThread", "This is running on the main thread.");
        });

    }


    private void LoadChats(String accessToken, ProgressBar progressBar, int id, int channel, List<ChatMember> membersList, int secondMemberId, String avatar_receiver,
                                            String currentUser, String username, String currentUserID, String hhtpAvatar_url, FirebaseRetrieveDataManager firebaseRetrieveDataManager) {


        firebaseRetrieveDataManager.retrieveUserData(new FirebaseRetrieveDataManager.UserDataCallback() {
            @Override
            public void onUserDataRetrieved(String userId, String notificationToken) {
                if (userId != null && notificationToken != null) {
                    Log.w(TAG, "User ID:  -------------->> " + userId + ", Token: -------------->>  " + notificationToken);

                    Log.d("InnerChats", "|-  AccessTokenAndLoadChats  -| ");
                    Log.d("InnerChats", "Access Token received: " + accessToken);
                    Log.d("InnerChats", "Receiver Avatar: " + avatar_receiver);
                    Log.d("InnerChats", "Receiver ID: " + secondMemberId);
                    Log.d("InnerChats", "CurrentUser Name: " + currentUser);
                    Log.d("InnerChats", "CurrentUser ID: " + id);
                    Log.d("InnerChats", "CurrentUser Avatar: " + hhtpAvatar_url);


                    sendButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClickAnimationManager.applyClickAnimation(v);
                            int receiver = secondMemberId;
                            int chatchannel = channel;
                            int sender =id;
                            String message = messageInput.getText().toString().trim();
                            String avatar = avatar_receiver;
                            String date = "2025-02-03";
                            String time = "11:02:59 PM";
                            String name = currentUser;

                            if (!message.isEmpty()) {
                                SendMessageApiManager.sendMessage(getApplicationContext(), receiver, chatchannel, sender, message, avatar, date, time, name,
                                    new SendMessageApiManager.ApiCallback() {
                                            @Override
                                            public void onSuccess() {
                                                messageInput.post(() -> {
                                                    Toast.makeText(v.getContext(), "Message sent successfully!", Toast.LENGTH_SHORT).show();
                                                    messageInput.setText("");

                                                    // Create a new message item
                                                    InnerChatAPIItem newMessage = new InnerChatAPIItem();
                                                    newMessage.setSender(String.valueOf(sender));
                                                    newMessage.setMessage(message);
                                                    newMessage.setAvatar_path(avatar);
                                                    newMessage.setName(name);

                                                    // Send FCM notification (if applicable)
                                                    sendFCMNotification(notificationToken, message, currentUser);

                                                    // ✅ Update RecyclerView
                                                    messagesAdapter.addMessage(newMessage, recyclerView_messages);
                                                    recyclerView_messages.scrollToPosition(messagesAdapter.getItemCount() - 1);
                                                });
                                            }

                                            @Override
                                            public void onFailure(String error) {
                                                messageInput.post(() -> Toast.makeText(v.getContext(), "Failed to send message: " + error, Toast.LENGTH_SHORT).show());
                                            }
                                        });
                            } else {
                                Toast.makeText(v.getContext(), "Message cannot be empty!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    loadChatsWithToken(progressBar, id, channel, membersList, username, currentUserID, hhtpAvatar_url);



                } else {
                    Log.d(TAG, "No user data found.");
                    UserHasNoNotificationToken(accessToken, progressBar, id, channel, membersList, secondMemberId, avatar_receiver,
                            currentUser, username, currentUserID, hhtpAvatar_url, firebaseRetrieveDataManager,recyclerView_messages);


                }
            }
        });


    }

    private void UserHasNoNotificationToken(String accessToken, ProgressBar progressBar, int id, int channel, List<ChatMember> membersList, int secondMemberId, String avatar_receiver,
                                            String currentUser, String username, String currentUserID, String hhtpAvatar_url, FirebaseRetrieveDataManager firebaseRetrieveDataManager, RecyclerView recyclerView_messages) {


        Log.d("InnerChats", "|-  AccessTokenAndLoadChats  -| ");
        Log.d("InnerChats", "Access Token received: " + accessToken);
        Log.d("InnerChats", "Receiver Avatar: " + avatar_receiver);
        Log.d("InnerChats", "Receiver ID: " + secondMemberId);
        Log.d("InnerChats", "CurrentUser Name: " + InnerChats.this.currentUser);
        Log.d("InnerChats", "CurrentUser ID: " + id);
        Log.d("InnerChats", "CurrentUser Avatar: " + InnerChats.this.hhtpAvatar_url);

        sendButton.setOnClickListener(v -> {
            ClickAnimationManager.applyClickAnimation(v);
            int receiver = secondMemberId;
            int chatchannel = channel;
            int sender =id;
            String message = messageInput.getText().toString().trim();
            String avatar = avatar_receiver;
            String date = "2025-02-03";
            String time = "11:02:59 PM";
            String name = InnerChats.this.currentUser;

            if (!message.isEmpty()) {
                SendMessageApiManager.sendMessage(getApplicationContext(), receiver, chatchannel, sender, message, avatar, date, date, name,
                        new SendMessageApiManager.ApiCallback() {
                    @Override
                    public void onSuccess() {
                        messageInput.post(() -> {
                            Toast.makeText(v.getContext(), "Message sent successfully!", Toast.LENGTH_SHORT).show();
                            messageInput.setText("");

                            InnerChatAPIItem newMessage = new InnerChatAPIItem();
                            newMessage.setSender(String.valueOf(sender));
                            newMessage.setMessage(message);
                            newMessage.setAvatar_path(avatar);
                            newMessage.setName(name);

                            // ✅ Update RecyclerView
                            messagesAdapter.addMessage(newMessage,recyclerView_messages);
                            InnerChats.this.recyclerView_messages.scrollToPosition(messagesAdapter.getItemCount() - 1);

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
        });
        loadChatsWithToken(progressBar, id, channel, membersList,username,currentUserID, InnerChats.this.hhtpAvatar_url);

    }

    private void sendFCMNotification(String notificationToken, String message, String currentUser) {
        new Thread(() -> {
            try {


                FCM fcm = new FCM();
                fcm.sendNotification(getApplicationContext(), notificationToken, currentUser, message);


            } catch (Exception e) {
                Log.e(TAG, "Error sending notification: " + e.getMessage(), e);
            }
        }).start();
    }



    private void loadChatsWithToken(ProgressBar progressBar, int id, int channel, List<ChatMember> membersList, String username, String currentUserID, String hhtpAvatar_url) {
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
                Log.d("InnerChats", "Extracted chat details: " + chatDetailsList);

                Log.d("InnerChats", "Raw JSON received: " + rawJson);
                ThreadManager.runOnMainThread(() -> displayChats(chats, id,channel,membersList,progressBar,username,currentUserID,hhtpAvatar_url));
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

                    InnerMessageDetails chatDetail = new InnerMessageDetails(date, avatar, name, sender, text, time);
                    chatDetailsList.add(chatDetail);
                }
            }
        } catch (JsonSyntaxException e) {
            Log.e("ChatLoader", "JSON Parsing Error: " + e.getMessage(), e);
        }

        return chatDetailsList;
    }


    @SuppressLint("NotifyDataSetChanged")
    private void displayChats(List<InnerChatAPIItem> chatItems, int id, int channel, List<ChatMember> membersList, ProgressBar progressBar, String username, String currentUserID, String hhtpAvatar_url) {
        if (messagesAdapter == null) {
            messagesAdapter = new MessagesAdapter(this, chatItems, id, channel, membersList, currentUser,  progressBar, username, currentUserID, hhtpAvatar_url);
            recyclerView_messages.setAdapter(messagesAdapter);
        } else {
            messagesAdapter.addChats(chatItems, recyclerView_messages);
            messagesAdapter.notifyDataSetChanged();
            Log.d("InnerChats", "Added " + chatItems.size() + " new chats.");
        }

        recyclerView_messages.post(() -> recyclerView_messages.scrollToPosition(messagesAdapter.getItemCount() - 1));
    }


    private void showEmptyState(ProgressBar progressBar) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Messages")
                .setMessage("There are no messages to display.")
                .setIcon(R.drawable.android12splash_orange)
                .setPositiveButton("OK", (dialog, which) -> {
                    progressBar.setVisibility(View.GONE);
                    l_1.setVisibility(View.VISIBLE);
                    cardView4.setVisibility(View.VISIBLE);
                    showChatInput.setVisibility(View.GONE);

                    dialog.dismiss();

                })
                .setCancelable(false);

        AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.alertdialog_background);
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.buttonBlue));
    }


    @Override
    protected void onResume() {
        super.onResume();
        serverDataReconnect.reconnectAsync(success -> runOnUiThread(() -> {
            if (success) {
                Log.d(TAG, "Reconnected successfully");
            } else {
                Log.e(TAG, "Reconnection failed. Redirecting to login.");
                new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Intent intent = new Intent(InnerChats.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }, 500);
            }
        }));
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



