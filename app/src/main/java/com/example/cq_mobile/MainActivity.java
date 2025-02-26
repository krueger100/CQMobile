package com.example.cq_mobile;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.cq_mobile.Clock.ClockFolder.ClockInAPIFolder.TimerManager;
import com.example.cq_mobile.Clock.ClockFolder.ClockInAPIFolder.TimerService;
import com.example.cq_mobile.FirebaseUserData.FirebaseDataManager;
import com.example.cq_mobile.HelperManagers.NavigationManager;

import com.example.cq_mobile.HelperManagers.Notifications.GetNotificationToken;
import com.example.cq_mobile.HelperManagers.Notifications.NotificationManagerHelper;

import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.HelperManagers.StatusBarManager;
import com.example.cq_mobile.LogoutFolder.LogoutManager;
import com.example.cq_mobile.OfflineDataFolder.NetworkManager;
import com.example.cq_mobile.databinding.ActivityMainBinding;
import com.example.cq_mobile.ui.chat.ChatNotif.ChatNotificationItem;

import com.example.cq_mobile.ui.chat.ChatNotif.ChatsNotificationsApiManager;
import com.example.cq_mobile.ui.chat.ChatNotif.NotificationAPIResponse;

import com.google.firebase.FirebaseApp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationManager navigationManager;
    SharedPreferences sharedPreferences;
    private static final String TAG = "MainActivity";
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;
    String accessToken, userId;
    boolean isNotificationDisplayed;
    private static final String NOTIFICATION_CHANNEL_ID = "chat_channel";
    String email;
    String password;
    String avatar;
    private int currentPage = 1;
    private final int pageSize = 20;
    String avatarUrl;
    String name;
    String id;
    private AlertDialog sessionExpiredDialog;

    private TimerManager timerManager;
    private NetworkManager networkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // -->>> Check Network Status
        networkManager = new NetworkManager(this);
        if (!networkManager.isConnected()) {
            networkManager.showNoConnectionDialog();
        }

        sharedPreferences = this.getSharedPreferences("showNotificationPrefs", Context.MODE_PRIVATE);
        isNotificationDisplayed = sharedPreferences.getBoolean("notification_displayed", false);
        FirebaseApp.initializeApp(this);

        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
        accessToken = sharedPrefManager.getAccessToken();
        email = sharedPrefManager.getEmail();
        password = sharedPrefManager.getPassword();
        avatarUrl = sharedPrefManager.getAvatarUrl();

        name = sharedPrefManager.getFirstName();
        id = sharedPrefManager.getUserId();

        if (accessToken == null || accessToken.isEmpty() ||
                email == null || email.isEmpty() ||
                password == null || password.isEmpty()) {

            Log.d("MainActivity", "Invalid session data found. Logging out...");

            if (!isFinishing()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Session Expired")
                        .setMessage("Login session expired. Please login again.")
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, which) -> {
                            LogoutManager.logoutUser(getApplicationContext());
                            dialog.dismiss();
                        });

                sessionExpiredDialog = builder.create();
                sessionExpiredDialog.show();
            }
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
        }








        // --- START TIMER SERVICE WITH CORRECT FOREGROUND SERVICE TYPE ---
        Intent serviceIntent = new Intent(this, TimerService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14 (SDK 34)
            serviceIntent.putExtra("FOREGROUND_SERVICE_TYPE", ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        }
        ContextCompat.startForegroundService(this, serviceIntent);

        timerManager = TimerManager.getInstance();

      //  ShowNotificationManager.NotifFilter(this, accessToken, userId, isNotificationDisplayed, sharedPreferences);


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

        firebaseDataManager.retrieveUserData((accessToken1, userId1, avatar1, firstName1, lastName1, notificationToken1) -> {
            Log.d("MainActivity", "Access Token: " + accessToken1);
            Log.d("MainActivity", "User ID: " + userId1);
            Log.d("MainActivity", "Avatar: " + avatar1);
            Log.d("MainActivity", "First Name: " + firstName1);
            Log.d("MainActivity", "Last Name: " + lastName1);
            Log.d("MainActivity", "Notification token: 2  " + notificationToken1);
        });

        Log.w("MainActivity", "Notification token --->: " + currentUser_notification_token);

        GetChatNotif(accessToken);
        navigationManager.setupNavigation();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            timerManager.startTimer();
        }, 2000);
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

    public TimerManager getTimerManager() {
        return timerManager;
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
                    List<ChatNotificationItem> notifications = response.getData().getChat().getData();

                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String jsonResponse = gson.toJson(notifications);

                    Log.d("GetChatNotif", "Chat Notification Response: \n" + jsonResponse);
                    Log.d("GetChatNotif", "SIZE: " + notifications.size());

                    Handler handler = new Handler(Looper.getMainLooper());
                    int delay = 1000; // 1 second delay between notifications
                    int[] count = {0};

                    for (ChatNotificationItem notification : notifications) {
                        handler.postDelayed(() -> {
                            String avatarUrl = notification.getAvatar();

                            if (avatarUrl == null || avatarUrl.isEmpty()) {
                                Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.emptyglide);

                                Bitmap bitmap;
                                if (drawable instanceof BitmapDrawable) {
                                    bitmap = ((BitmapDrawable) drawable).getBitmap();
                                } else {
                                    bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                                    Canvas canvas = new Canvas(bitmap);
                                    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                                    drawable.draw(canvas);
                                }

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
                        }, count[0] * delay);
                        count[0]++;
                    }
                } else {
                    Log.e("GetChatNotif", "Invalid or null response received");
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e("GetChatNotif", "API Request Failed: " + error);
            }
        });
    }







    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerManager != null) {
            timerManager.setListener(null);
        }
    }

}


/*

        UseDetails_JobID(name, Integer.parseInt(id), avatarUrl, accessToken, avatarUrl, ticketsIDClockINManager);


    private void UseDetails_JobID(String name, int userId, String avatar_path, String accessToken, String avatarUrl, TicketsIDClockINManager ticketsIDClockINManager) {
        IDsManager.fetchJobIdPaginated(accessToken, new IDsManager.ApiResponseCallback<Taskmain>() {
            Gson gson = new Gson();

            @Override
            public void onDataFetched(List<Taskmain> data) {
                if (data != null && !data.isEmpty()) {

                    // Store globally
                    UserJobData.getInstance().setJobs(data);

                    for (Taskmain task : data) {
                        Log.w("ClockActivity", "FetchJobId Task ID: " + task.getId());
                        Log.w("ClockActivity", "FetchJobId Name: " + task.getName());
                        Log.w("ClockActivity", "FetchJobId Category: " + task.getCategory());
                        Log.w("ClockActivity", "FetchJobId Status: " + task.getStatus());
                        Log.w("ClockActivity", "FetchJobId Start Date: " + task.getStart_date());
                        Log.w("ClockActivity", "FetchJobId End Date: " + task.getEnd_date());

                        // Address details
                        if (task.getAddress() != null) {
                            Taskmain.Address addr = task.getAddress();
                            Log.d("ClockActivity", "Address: " + addr.getAddress() + ", " + addr.getCity() + ", " + addr.getCountry());
                        }

                        // Client details
                        if (task.getClient_details() != null) {
                            Taskmain.ClientDetails client = task.getClient_details();
                            Log.d("ClockActivity", "Client: " + client.getFirst_name() + " " + client.getLast_name() + " (" + client.getCompany() + ")");
                        }

                        // Coordinates
                        if (task.getCoordinates() != null) {
                            Taskmain.Coordinates coords = task.getCoordinates();
                            Log.d("ClockActivity", "Coordinates: Lat=" + coords.getLatitude() + ", Lon=" + coords.getLongitude());

                        }

                        // Continue as normal
                        UserTaskID(task.getId(), name, userId, avatar_path, accessToken, avatarUrl, ticketsIDClockINManager);
                    }

                } else {
                    UserTaskID(-1, name, userId, avatar_path, accessToken, avatarUrl, ticketsIDClockINManager);

                    Log.d("ClockActivity", "No tasks fetched.");
                }
            }


            @Override
            public void onError(String error) {
                Log.w("ClockActivity", "Error  -->>: " + error);
                Toast.makeText(MainActivity.this, error +"\n"+ "Press the Clock in to Continue", Toast.LENGTH_SHORT).show();


            }
        });


    }

    private void UserTaskID(int jobId, String name, int userId, String avatar_path, String accessToken, String avatarUrl, TicketsIDClockINManager ticketsIDClockINManager) {

        IDsManager.fetchTaskIdDataPaginated(String.valueOf(jobId), 1, 10, accessToken, new IDsManager.ApiResponseCallback<SubTask>() {
            @Override
            public void onDataFetched(List<SubTask> data) {
                if (data != null && !data.isEmpty()) {

                    UserTaskIdData.getInstance().setSubTasks(data);
                    UserTaskIdData.getInstance().setAccessToken(accessToken);
                    UserTaskIdData.getInstance().setUserId(userId);
                    UserTaskIdData.getInstance().setUserName(name);
                    UserTaskIdData.getInstance().setAvatarPath(avatar_path);

                    for (SubTask subTask : data) {
                        Log.d("MainActivity", "SubTask ID: " + subTask.getId());
                        Log.d("MainActivity", "Title: " + subTask.getTitle());



                        ticketsIDClockINManager.loadTicketsWithToken(accessToken, 1, 10, new TicketsIDClockINManager.TicketsCallback() {
                            @Override
                            public void onTicketsLoaded(List<TicketAPICategoryItems> tickets) {
                                Log.d("MainActivity", "Successfully loaded " + tickets.size() + " category tickets.");

                                for (TicketAPICategoryItems ticketAPICategoryItem : tickets) {
                                    Log.d("MainActivity", "Ticket ID: " + ticketAPICategoryItem.getId());
                                    Log.d("MainActivity", "Ticket Name: " + ticketAPICategoryItem.getName());
                                }
                            }

                            @Override
                            public void onTicketsWithTokenLoaded(List<TicketAPIItem> ticketsID) {
                                Log.d("MainActivity", "Successfully loaded " + ticketsID.size() + " tickets with token.");

                                for (TicketAPIItem ticketAPIItem : ticketsID) {
                                    int ticketMessageID = ticketAPIItem.getId();
                                    String name = ticketAPIItem.getCategory().getName();
                                    String subject = ticketAPIItem.getSubject();
                                    String status = ticketAPIItem.getStatus();

                                    Log.w("MainActivity", "Ticket Message ID: --->>> " + ticketMessageID);
                                    Log.w("MainActivity", "Ticket Name: ---->>> " + name);
                                    Log.w("MainActivity", "Ticket Subject: ---->>> " + subject);
                                    Log.w("MainActivity", "Ticket Status: ---->>> " + status);
                                }
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Log.e("MainActivity", "Error:  -->> " + errorMessage);

                            }
                        });
                    }
                } else {
                    Log.d("MainActivity", "No sub-tasks fetched.");

                }
            }

            @Override
            public void onError(String error) {
                Log.e("MainActivity", "Error: <<--- " + error);
            }
        });

    }



 */


/*
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



 */


