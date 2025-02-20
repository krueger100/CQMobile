package com.example.cq_mobile;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.cq_mobile.Clock.ClockActivity;
import com.example.cq_mobile.FirebaseUserData.FirebaseDataManager;
import com.example.cq_mobile.HelperManagers.NavigationManager;

import com.example.cq_mobile.HelperManagers.Notifications.GetNotificationToken;
import com.example.cq_mobile.HelperManagers.Notifications.NotificationManagerHelper;
import com.example.cq_mobile.HelperManagers.Notifications.ShowNotifFolder.ShowNotificationManager;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.HelperManagers.StatusBarManager;
import com.example.cq_mobile.UserDetailsFolder.UseDetails;
import com.example.cq_mobile.databinding.ActivityMainBinding;
import com.example.cq_mobile.ui.chat.ChatNotif.ChatNotificationItem;

import com.example.cq_mobile.ui.chat.ChatNotif.ChatsNotificationsApiManager;
import com.example.cq_mobile.ui.chat.ChatNotif.NotificationAPIResponse;
import com.google.firebase.FirebaseApp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private UseDetails useDetails;
    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationManager navigationManager;
    SharedPreferences sharedPreferences;
    private static final String TAG = "MainActivity";
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;
    String accessToken, userId;
    boolean isNotificationDisplayed;
    private static final String NOTIFICATION_CHANNEL_ID = "chat_channel";
    String  email ;
    String password;
    String avatar;
    private int currentPage = 1;
    private final int pageSize = 20;
    int message_read;
    String chatCount;
    String avatarUrl;
    String firstName ;
    String lastName;
String notification_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        useDetails = new UseDetails(this);
        sharedPreferences = this.getSharedPreferences("showNotificationPrefs", Context.MODE_PRIVATE);
        isNotificationDisplayed = sharedPreferences.getBoolean("notification_displayed", false);

        FirebaseApp.initializeApp(this);

        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
        accessToken = sharedPrefManager.getAccessToken();
        email = sharedPrefManager.getEmail();
        password = sharedPrefManager.getPassword();
        avatarUrl = sharedPrefManager.getAvatarUrl();

        Log.d("MainActivity", "Email: " + email);
        Log.d("MainActivity", "Password: " + password);
        Log.d("MainActivity", "Avatar: " + avatarUrl);



        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("SharedPreferencesNotif", entry.getKey() + ": " + entry.getValue().toString());

        }


        FirebaseApp.initializeApp(this);
        StatusBarManager.setStatusBarLight(this);

        drawerLayout = binding.drawerLayout;
        navigationManager = new NavigationManager(this, binding.navView, binding.navViewDrawer, drawerLayout);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                );
            } else {
                GetNotificationToken.setTokenCallback(token2 -> {
                    Log.d("MainActivity", "Received token: " + token2);
                    initializeApp(token2);
                });
                GetNotificationToken.getToken(this);
            }
        } else {
            // For older API levels, simulate permission request behavior
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                );
            } else {
                GetNotificationToken.setTokenCallback(token2 -> {
                    Log.d("MainActivity", "Received token: " + token2);
                    initializeApp(token2);
                });
                GetNotificationToken.getToken(this);
            }
        }





    }

    private void initializeApp(String currentUser_notification_token) {
        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
        String accessToken = sharedPrefManager.getAccessToken();
        String userId = sharedPrefManager.getUserId();
        String avatar = sharedPrefManager.getAvatarUrl();
        String firstName = sharedPrefManager.getFirstName();
        String lastName = sharedPrefManager.getLastName();
        String notificationToken = sharedPrefManager.getNotiftoken();


        notificationToken = (notificationToken == null || notificationToken.isEmpty())
                ? currentUser_notification_token
                : notificationToken;

        sharedPrefManager.saveNewNotificationToken(notificationToken);
        Log.w("MainActivity", "Notification token updated: " + notificationToken);

        FirebaseDataManager firebaseDataManager = new FirebaseDataManager(userId);
        firebaseDataManager.saveUserData(accessToken, userId, avatar, firstName, lastName, notificationToken);

        firebaseDataManager.retrieveUserData(new FirebaseDataManager.UserDataCallback() {
            @Override
            public void onUserDataRetrieved(String accessToken, String userId, String avatar, String firstName, String lastName, String notificationToken) {
                // Use the retrieved data
                Log.d("MainActivity", "Access Token: " + accessToken);
                Log.d("MainActivity", "User ID: " + userId);
                Log.d("MainActivity", "Avatar: " + avatar);
                Log.d("MainActivity", "First Name: " + firstName);
                Log.d("MainActivity", "Last Name: " + lastName);
                Log.d("MainActivity", "Notification token: 2  " + notificationToken);

            }
        });

        Log.w("MainActivity", "Notification token --->: " + currentUser_notification_token);

        ShowNotificationManager.NotifFilter(this, accessToken, userId,isNotificationDisplayed,sharedPreferences);

        GetChatNotif(accessToken);
        navigationManager.setupNavigation();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT).show();
                GetNotificationToken.getToken(this);

                GetNotificationToken.setTokenCallback(token2 -> {
                    Log.d("MainActivity", "Received token: " + token2);
                });
                GetNotificationToken.getToken(this);

            } else {
                Toast.makeText(this, "Notification permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navigationManager.onSupportNavigateUp();
    }



    private void GetChatNotif(String accessToken) {
        ChatsNotificationsApiManager.fetchChatNotifications(accessToken, new ChatsNotificationsApiManager.ApiCallback() {
            @Override
            public void onSuccess(NotificationAPIResponse response) {
                if (response != null && response.isSuccess() && response.getData() != null && response.getData().getChat() != null) {
                    List<ChatNotificationItem> notifications = response.getData().getChat().getData(); // Accessing correct field

                    // Convert response to JSON for better logging
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String jsonResponse = gson.toJson(notifications);

                    Log.d("GetChatNotif", "Chat Notification Response: \n" + jsonResponse);
                    Log.d("GetChatNotif", "SIZE: " + notifications.size());

                    // Send notifications using FMC
                    for (ChatNotificationItem notification : notifications) {
                         avatarUrl = notification.getAvatar();

                        // Check if avatar URL is null or empty
                        if (avatarUrl == null || avatarUrl.isEmpty()) {
                            // If avatar URL is null or empty, set a drawable resource as fallback
                            Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.emptyglide);
                            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();  // Convert the drawable to a Bitmap

                            NotificationManagerHelper.getInstance(getApplicationContext()).showNotification(
                                    notification.getSender(),
                                    notification.getText(),
                                    bitmap,
                                    notification.getTime(),
                                    notification.getDate(),
                                    String.valueOf(notification.getChannel())
                            );
                        } else {
                            NotificationManagerHelper.getInstance(getApplicationContext()).showNotification(
                                    notification.getSender(),
                                    notification.getText(),
                                    avatarUrl,
                                    notification.getTime(),
                                    notification.getDate(),
                                    String.valueOf(notification.getChannel())
                            );
                        }
                    }

                    // Send notifications Local
                    // displayChatNotifications(MainActivity.this, notifications);

                } else {
                    Toast.makeText(getApplicationContext(), "Failed to fetch notifications: Invalid response", Toast.LENGTH_LONG).show();
                    Log.e("GetChatNotif", "Invalid or null response received");
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getApplicationContext(), "Failed to fetch notifications: " + error, Toast.LENGTH_LONG).show();
                Log.e("GetChatNotif", "API Request Failed: " + error);
            }
        });
    }



    private void displayChatNotifications(Context context, List<ChatNotificationItem> notifications) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "chat_notifications";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Chat Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for chat updates");
            notificationManager.createNotificationChannel(channel);
        }

        for (ChatNotificationItem notification : notifications) {
            String title = notification.getSender();
            String content = notification.getText();
            String avatarUrl = notification.getAvatar();
            String time = notification.getTime();
            String date = notification.getDate();

            String fullContent = content + "\n📅 " + date + " 🕒 " + time; // Add date & time to the notification content

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://aws.customquoter.co.uk/tasks?task=" + notification.getChannel()));
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            // Generate a unique notification ID
            int notificationId = (title + time + date).hashCode();

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.nav_chat)
                    .setContentTitle(title)
                    .setContentText("Tap to view details")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(fullContent)) // Show full message + timestamp
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            // Show initial notification without image
            notificationManager.notify(notificationId, builder.build());

            // Load avatar asynchronously and update notification
            Glide.with(context)
                    .asBitmap()
                    .load(avatarUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            NotificationCompat.Builder updatedBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                                    .setSmallIcon(R.drawable.nav_chat)
                                    .setContentTitle(title)
                                    .setContentText("Tap to view details")
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(fullContent))
                                    .setLargeIcon(resource) // Set the avatar as the large icon
                                    .setAutoCancel(true)
                                    .setContentIntent(pendingIntent)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH);

                            notificationManager.notify(notificationId, updatedBuilder.build());
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // No action needed
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            // Keep the notification as it is, without an avatar
                        }
                    });
        }
    }




}

///|----------------------------------------------|
/*

    // Request notification permissions if required
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                );
            } else {
                GetNotificationToken.setTokenCallback(token2 -> {
                    Log.d("MainActivity", "Received token: " + token2);
                    initializeApp(token2);
                });
                GetNotificationToken.getToken(this);
            }
        } else {
            GetNotificationToken.setTokenCallback(currentUser_notification_token -> {
                Log.d("MainActivity", "Received token: " + currentUser_notification_token);
                initializeApp(currentUser_notification_token);
            });
            GetNotificationToken.getToken(this);

        }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            } else {

                GetNotificationToken.setTokenCallback(token2 -> {
                    Log.d("MainActivity", "Received token: " + token2);
                });
                GetNotificationToken.getToken(this);

            }
        } else {
            GetNotificationToken.setTokenCallback(token2 -> {
                Log.d("MainActivity", "Received token: " + token2);
            });
            GetNotificationToken.getToken(this);

        }

 */


      /*

    AccessTokenRequest tokenRequest = new AccessTokenRequest(email, password);
        getAccessTokenAndLoadChats(tokenRequest,  binding.progressBar, currentPage, pageSize);
       */

/////|----------------------------------------------------------|

/*
   private void getAccessTokenAndLoadChats(AccessTokenRequest tokenRequest, ProgressBar progressBar, int page, int pageSize) {
        useDetails.getAccessToken(tokenRequest, new UseDetails.AccessTokenCallback() {
            @Override
            public void onAccessTokenReceived(String token) {
                accessToken = token;
                Log.d(TAG, "Access Token received: " + token);
                loadChatsWithToken(token,progressBar);
                progressBar.setVisibility(View.GONE);
            }


            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error fetching access token: " + errorMessage);
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    private void loadChatsWithToken(String token, ProgressBar progressBar) {

        useDetails.loadUseDetails(currentPage, pageSize, new UseDetails.AllUserCallback() {
            @Override
            public void onAllUseDetailsLoaded(List<UseDetails> user_details, String rawJson) {

                if (user_details != null && !user_details.isEmpty()) {
                    Log.d(TAG, "Raw chat JSON: " + rawJson);

                    // List<ChatDetails> chatDetailsList = extractChatDetails(rawJson);

                    Log.d(TAG, "Extracted chat details: " + rawJson);
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

                        List<ChatMember> members = null;

                        ChatDetails chatDetails = new ChatDetails(chatName, id, name, avatarPath, messages, online, channel, status, channelStatus, members,message_read);
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
 */

/////|----------------------------------------------------------|

    /*
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

                        ChatDetails chatDetails = new ChatDetails(chatName, id, name, avatarPath, messages, online, channel, status, channelStatus, members,message_read);
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

 */
