package com.example.cq_mobile.HelperManagers.Notifications.ChatNotif_folder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatAPIItem;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatDetails;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatManager;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatMember;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatMessage;
import com.example.cq_mobile.ui.chat.ChatNotif.ChatNotificationItem;
import com.example.cq_mobile.ui.chat.ChatNotif.ChatsNotificationsApiManager;
import com.example.cq_mobile.ui.chat.ChatNotif.NotificationAPIResponse;


import com.example.cq_mobile.ui.chat.ColleagueFolder.Contact;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatNotificationAdapter adapter;
    private List<ChatNotificationItem> notifications = new ArrayList<>();

    private List<ChatDetails> notificationsItems = new ArrayList<>();
    private static final String TAG = "ChatActivity";
    private int currentPage = 1;  // Track pagination
    private final int pageSize = 20;
    String accessToken;
    private ChatManager chatManager;
    String chatCount;
    int message_read;
    private String currentUser;
    String  email ;
    String password;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats_receiver);

        chatManager = new ChatManager(this);

        Intent intent = getIntent();
        String channelUrl = intent.getStringExtra("channel_url");

        if (channelUrl != null) {
            Log.d("ChatActivity", "Navigated to ChatActivity with Channel URL: " + channelUrl);
            // Load chat based on `channelUrl`
        }

        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
        accessToken = sharedPrefManager.getAccessToken();
        email = sharedPrefManager.getEmail();
        password = sharedPrefManager.getPassword();

        Log.d("ChatActivity", "Email: " + email);
        Log.d("ChatActivity", "Password: " + password);

         progressBar = findViewById(R.id.progressBar);
         progressBar.setVisibility(View.VISIBLE);
        recyclerView = findViewById(R.id.recyclerView);
        if (recyclerView == null) {
            Log.e(TAG, "RecyclerView is not initialized");
            return;
        }

        AccessTokenRequest tokenRequest = new AccessTokenRequest(email, password);
        getAccessTokenAndLoadChats(tokenRequest, progressBar, currentPage, pageSize);

    }


    private void getAccessTokenAndLoadChats(AccessTokenRequest tokenRequest, ProgressBar progressBar, int page, int pageSize) {
        chatManager.getAccessToken(tokenRequest, new ChatManager.AccessTokenCallback() {
            @Override
            public void onAccessTokenReceived(String token) {
                accessToken = token;
                Log.d(TAG, "Access Token received: " + token);
                fetchChatNotifications(accessToken);
                loadChatsWithToken(token,progressBar);

            }


            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error fetching access token: " + errorMessage);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loadChatsWithToken(String token, ProgressBar progressBar) {

        chatManager.loadChats(currentPage, pageSize, new ChatManager.AllChatsCallback() {
            @Override
            public void onAllChatsLoaded(List<ChatAPIItem> chats, String rawJson) {

                if (chats != null && !chats.isEmpty()) {
                    List<ChatDetails> chatDetailsList = extractChatDetails(rawJson);
                    Log.d(TAG, "Extracted chat details: " + chatDetailsList);


                    displayChats(chatDetailsList, token, progressBar,chatCount,message_read);
                    currentPage++;  // Increment page after successful load
                } else {
                    Log.d(TAG, "No chats received.");
                }
            }

            @Override
            public void onError(String errorMessage) {

                Log.e(TAG, "Chat Loading Error: " + errorMessage);
            }
        });
    }

    private List<ChatDetails> extractChatDetails(String rawJson) {
        List<ChatDetails> chatDetailsList = new ArrayList<>();
        Log.d(TAG, "Raw JSON Input: " + rawJson);

        try {
            JSONObject jsonObject = new JSONObject(rawJson);
            if (jsonObject.has("data")) {
                JSONObject dataObject = jsonObject.getJSONObject("data");
                if (dataObject.has("chats")) {
                    JSONArray chatsArray = dataObject.getJSONArray("chats");
                    Log.d(TAG, "Total Chats Found: " + chatsArray.length());


                    for (int i = 0; i < chatsArray.length(); i++) {
                        JSONObject chatObject = chatsArray.getJSONObject(i);

                        Log.d(TAG, "Parsing Chat " + (i + 1) + "/" + chatsArray.length());


                        String chatName = chatObject.optString("chat_name", "");
                        String id = chatObject.optString("id", "");
                        String name = chatObject.optString("name", "");
                        String avatarPath = chatObject.optString("avatar_path", "");
                        String online = chatObject.optString("online", "");
                        int channel = chatObject.optInt("channel", 0);
                        int status = chatObject.optInt("status", 0);
                        int channelStatus = chatObject.optInt("channel_status", 0);

                        message_read = chatObject.optInt("message_read", 0);

                        Log.d(TAG, "Extracted Chat: " +
                                "chatName=" + chatName + ", id=" + id + ", name=" + name +
                                ", avatarPath=" + avatarPath + ", online=" + online +
                                ", channel=" + channel + ", status=" + status +
                                ", channelStatus=" + channelStatus);

                        List<ChatMessage> messages = new ArrayList<>();
                        if (chatObject.has("message")) {
                            try {
                                JSONArray messagesArray = new JSONArray(chatObject.getString("message"));
                                Log.d(TAG, "Total Messages Found: " + messagesArray.length());

                                for (int j = 0; j < messagesArray.length(); j++) {
                                    JSONObject messageObject = messagesArray.getJSONObject(j);
                                    //    int messageId = messageObject.optInt("id", 0);

                                    int messageId = messageObject.optInt("id", 0);
                                    String text = messageObject.optString("text", "");
                                    String time = messageObject.optString("time", "");
                                    String date = messageObject.optString("date", "");
                                    int unread = messageObject.optInt("unread", 0);

                                    Log.d(TAG, "Message " + (j + 1) + ": " +
                                            "id=" + messageId + ", text=" + text +
                                            ", time=" + time + ", date=" + date + ", unread=" + unread);

                                    messages.add(new ChatMessage(messageId, text, time, date, unread));
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, "Error parsing messages: " + e.getMessage());
                            }
                        }

                        List<ChatMember> members = new ArrayList<>();
                        if (chatObject.has("members")) {
                            try {
                                JSONArray membersArray = new JSONArray(chatObject.getString("members"));
                                Log.d(TAG, "Total Members Found: " + membersArray.length());

                                for (int j = 0; j < membersArray.length(); j++) {
                                    JSONObject memberObject = membersArray.getJSONObject(j);
                                    int memberId = memberObject.optInt("id", 0);
                                    String memberName = memberObject.optString("name", "");
                                    String memberAvatarPath = memberObject.optString("avatar_path", "");
                                    String email = memberObject.optString("email", "");
                                    int lastRead = memberObject.optInt("last_read", 0);

                                    Log.d(TAG, "Member " + (j + 1) + ": " +
                                            "id=" + memberId + ", name=" + memberName +
                                            ", avatarPath=" + memberAvatarPath + ", email=" + email +
                                            ", lastRead=" + lastRead);

                                    members.add(new ChatMember(memberId, memberName, memberAvatarPath, email, lastRead));
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, "Error parsing members: " + e.getMessage());
                            }
                        }
                        List<Contact> contacts = new ArrayList<>();
                        if (chatObject.has("contacts")) {
                            try {
                                JSONArray contactsArray = new JSONArray(chatObject.getString("contacts"));
                                for (int j = 0; j < contactsArray.length(); j++) {
                                    JSONObject contactObject = contactsArray.getJSONObject(j);
                                    int contactId = contactObject.optInt("id", 0);
                                    String contactName = contactObject.optString("name", "");
                                    String contactAvatarPath = contactObject.optString("avatar_path", "");
                                    String initials = contactObject.optString("initials", "");
                                    String contactOnline = contactObject.optString("online", "");
                                    String color = contactObject.optString("color", "");
                                    String email = contactObject.optString("email", "");
                                    String contactChannel = contactObject.optString("channel", "");

                                    Log.d("ChatPageFragment", "Contatcs " + (j + 1) + ": " +
                                            "id=" + contactId + ", name=" + contactName +
                                            ", contactAvatarPath=" + contactAvatarPath + ", initials=" + initials +
                                            ", contactChannel=" + contactChannel);


                                    contacts.add(new Contact(contactId, contactName, contactAvatarPath, initials, contactOnline, color, email, contactChannel));
                                }
                            } catch (JSONException e) {
                                Log.e("ChatPageFragment", "Error parsing contacts: " + e.getMessage());
                            }
                        }

                        ChatDetails chatDetails = new ChatDetails(chatName, id, name, avatarPath, messages, online, channel, status, channelStatus, members, message_read);
                        chatDetailsList.add(chatDetails);
                    }
                } else {
                    Log.w(TAG, "No 'chats' array found in JSON.");
                }
            } else {
                Log.w(TAG, "No 'data' object found in JSON.");
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
        }


        chatCount = String.valueOf( chatDetailsList.size());

        Log.d(TAG, "Final Extracted Chats Count: " + chatCount);
        return chatDetailsList;
    }

    private void displayChats(List<ChatDetails> chatDetailsList, String token, ProgressBar progressBar, String chatCount, int message_read) {
        if (adapter == null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ChatNotificationAdapter(ChatActivity.this, chatDetailsList, accessToken, email, password,progressBar,chatCount,message_read);
            recyclerView.setAdapter(adapter);

            progressBar.setVisibility(View.GONE);
        } else {
            adapter.addChats(chatDetailsList);
            progressBar.setVisibility(View.GONE);
        }
    }



    private void fetchChatNotifications(String accessToken) {
        ChatsNotificationsApiManager.fetchChatNotifications(accessToken, new ChatsNotificationsApiManager.ApiCallback() {
            @Override
            public void onSuccess(NotificationAPIResponse response) {
                if (response != null && response.isSuccess() && response.getData() != null && response.getData().getChat() != null) {
                    runOnUiThread(() -> {
                        Log.d(TAG, "Chat Notification Response: \n" + "Notifications fetched successfully");
                    });

                    // Log JSON response
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String jsonResponse = gson.toJson(response.getData().getChat().getData());
                    Log.d(TAG, "Chat Notification Response: \n" + jsonResponse);


                } else {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Failed to fetch notifications", Toast.LENGTH_LONG).show());
                    Log.e(TAG, "Invalid or null response received");
                }
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Failed to fetch notifications: " + error, Toast.LENGTH_LONG).show());
                Log.e(TAG, "API Request Failed: " + error);
            }
        });
    }

    /*

    private List<ChatNotificationItem> notifications = new ArrayList<>();


    //        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        adapter = new ChatNotificationAdapter(this, notifications,accessToken,notificationsItems);
//        recyclerView.setAdapter(adapter);

    fetchChatNotifications(accessToken);
    private void fetchChatNotifications(String accessToken) {
        ChatsNotificationsApiManager.fetchChatNotifications(accessToken, new ChatsNotificationsApiManager.ApiCallback() {
            @Override
            public void onSuccess(NotificationAPIResponse response) {
                if (response != null && response.isSuccess() && response.getData() != null && response.getData().getChat() != null) {
                    runOnUiThread(() -> {
                        notifications.clear();
                        notifications.addAll(response.getData().getChat().getData());
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "Notifications fetched successfully", Toast.LENGTH_SHORT).show();
                    });

                    // Log JSON response
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String jsonResponse = gson.toJson(response.getData().getChat().getData());
                    Log.d("GetChatNotif", "Chat Notification Response: \n" + jsonResponse);
                } else {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Failed to fetch notifications", Toast.LENGTH_LONG).show());
                    Log.e("GetChatNotif", "Invalid or null response received");
                }
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Failed to fetch notifications: " + error, Toast.LENGTH_LONG).show());
                Log.e("GetChatNotif", "API Request Failed: " + error);
            }
        });
    }

     */
}
