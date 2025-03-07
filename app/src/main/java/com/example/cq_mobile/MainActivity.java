package com.example.cq_mobile;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.cq_mobile.Clock.ClockFolder.ClockInAPIFolder.TimerManager;
import com.example.cq_mobile.Clock.ClockFolder.ClockOutFolder.ClockOutManager;
import com.example.cq_mobile.Clock.ClockFolder.TimerUIManager;
import com.example.cq_mobile.FirebaseUserData.FirebaseDataManager;
import com.example.cq_mobile.FirebaseUserData.FirebaseDatabaseManager;
import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.ClockOutVisibilityHandler;
import com.example.cq_mobile.HelperManagers.NavigationManager;

import com.example.cq_mobile.HelperManagers.Notifications.GetNotificationToken;
import com.example.cq_mobile.HelperManagers.Notifications.NotificationManagerHelper;

import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.HelperManagers.StatusBarManager;
import com.example.cq_mobile.OfflineDataFolder.NetworkManager;
import com.example.cq_mobile.databinding.ActivityMainBinding;
import com.example.cq_mobile.ui.chat.ChatFragment;
import com.example.cq_mobile.ui.chat.ChatNotif.ChatNotificationItem;

import com.example.cq_mobile.ui.chat.ChatNotif.ChatsNotificationsApiManager;
import com.example.cq_mobile.ui.chat.ChatNotif.NotificationAPIResponse;

import com.example.cq_mobile.ui.chat.ChatPageFragment;
import com.example.cq_mobile.ui.chat.InnerChatsFolder.InnerChats;
import com.google.firebase.FirebaseApp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;


public class MainActivity extends AppCompatActivity implements ClockOutVisibilityHandler {
    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationManager navigationManager;
    SharedPrefManager sharedPrefManager;
    private static final String TAG = "MainActivity";
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;
    String accessToken;
    private TimerUIManager timerUIManager;
    String email;
    String password;
    String userName;
    String notificationToken;
    int jobId = -1 ;
    int taskId = -1;
    String firstName;
    String lastName;
    String avatarPath;
    String avatarUrl;
    private TimerManager timerManager;
    private NetworkManager networkManager;
    FirebaseDatabaseManager firebaseDatabaseManager;

    private View rootView;

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



        SharedPreferences sharedPreferences = getSharedPreferences("ClockPrefs", MODE_PRIVATE);
        boolean isClockedIn = sharedPreferences.getBoolean("ClockInSuccess", false);
        Log.d("MainActivity", "Clock In Status: " + isClockedIn);


        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String accessToken = userPrefs.getString("accessToken", null);
        String userID = userPrefs.getString("userId", null);
        String email = userPrefs.getString("email", null);
        String password = userPrefs.getString("password", null);
        String avatar = userPrefs.getString("avatar", null);


        Intent intent = getIntent();
        accessToken = intent.getStringExtra("accessToken");
        int userId = intent.getIntExtra("userId", -1); // Get userId from Intent
        if (userId == -1) { // If not found in Intent, get from SharedPreferences
            SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
            String userIdStr = userID;

            if (userIdStr != null) {
                userId = Integer.parseInt(userIdStr);
            }
        }
        jobId = intent.getIntExtra("jobId", -1);
        taskId = intent.getIntExtra("taskId", -1);
        userName = intent.getStringExtra("userName");
        if (userName == null || userName.trim().isEmpty()) {
            userName = firstName + " " + lastName;
        }
        avatarPath = intent.getStringExtra("avatarPath");
        firstName = intent.getStringExtra("firstName");
        lastName = intent.getStringExtra("lastName");
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
        avatarUrl = intent.getStringExtra("avatarUrl");



        Log.d("MainActivity", "<<<<----- MainActivity ----->>>> " );
        Log.d("MainActivity", "Access Token: Intent ----->>>> " + accessToken);
        Log.d("MainActivity", "User ID: Intent ----->>>> " + userId);
        Log.d("MainActivity", "Job ID: Intent ----->>>>  " + jobId);
        Log.d("MainActivity", "Task ID: Intent ----->>>> " + taskId);
        Log.d("MainActivity", "User Name: Intent ----->>>> " + userName);
        Log.d("MainActivity", "Avatar Path: Intent ----->>>> " + avatarPath);
        Log.d("MainActivity", "First Name: Intent ----->>>> " + firstName);
        Log.d("MainActivity", "Last Name: Intent ----->>>> " + lastName);
        Log.d("MainActivity", "Email: Intent ----->>>> " + email);
        Log.d("MainActivity", "Avatar URL: Intent ----->>>> " + avatarUrl);  //




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
                int finalUserId = userId;
                GetNotificationToken.setTokenCallback(token2 -> {
                    Log.d("MainActivity", "Received token: " + token2);
                    initializeApp(token2, finalUserId,avatarUrl);
                });
                GetNotificationToken.getToken(this);
            }
        }



    //// ------------->>>>>>>>  Access Token Data
        firebaseDatabaseManager = new FirebaseDatabaseManager();
        firebaseDatabaseManager.getUserData(String.valueOf(userId), new FirebaseDatabaseManager.UserDataCallback() {
            @Override
            public void onSuccess(FirebaseDatabaseManager.User user) {
                Log.d("MainActivity", "User Retrieved: " + user.firstName + " " + user.lastName);
            }

            @Override
            public void onFailure(String error) {
                Log.e("MainActivity", "Failed to retrieve user: " + error);
           //     AccessTokenData();
            }
        });





    }



    private void initializeApp(String currentUser_notification_token, int userId, String avatarUrl) {
        FirebaseApp.initializeApp(this);
        StatusBarManager.setStatusBarLight(this);
        sharedPrefManager = new SharedPrefManager(this);
        accessToken = sharedPrefManager.getAccessToken();
        userId = sharedPrefManager.getUserId();
        avatarPath = sharedPrefManager.getAvatarUrl();
        userName = sharedPrefManager.getUserName();
        firstName = sharedPrefManager.getFirstName();
        lastName = sharedPrefManager.getLastName();
        notificationToken = sharedPrefManager.getNotiftoken();
        jobId = sharedPrefManager.getJobId();
        taskId = sharedPrefManager.getTaskId();
        String startDate = sharedPrefManager.getKeyStartDate();

        ClockOutManager clockOutManager = new ClockOutManager(this,  binding.progressBar,  jobId,  taskId,  userId,  startDate);
        clockOutManager.setupClockOutButton(binding.clockoutBtn, accessToken, jobId);



        if (startDate == null) {
            Log.w("MainActivity", "Warning: Start date is null, using default value 0.");
        }
        Log.w("MainActivity", "Start time ----->>>> " + startDate);

        rootView = findViewById(android.R.id.content);
        timerManager = TimerManager.getInstance(this,startDate);
        timerUIManager = new TimerUIManager(rootView,startDate);
        timerManager.startTimer();


        timerManager.restoreSavedTime(this);



        Log.d("MainActivity", "FirebaseDataManager User ID:  --------->>> " + userId );


        // Handle null or empty notification token
        if (notificationToken == null || notificationToken.isEmpty()) {
            notificationToken = (currentUser_notification_token != null) ? currentUser_notification_token : "";
            sharedPrefManager.saveNewNotificationToken(notificationToken);
            Log.w("MainActivity", "Notification token updated: " + notificationToken);
        }



        // Ensure userId is not null before proceeding
        if (userId != 0) {
            FirebaseDataManager firebaseDataManager = new FirebaseDataManager(userId);
            firebaseDataManager.saveUserData(
                    accessToken != null ? accessToken : "",
                    String.valueOf(userId),
                    avatarPath != null ? avatarPath : "",
                    firstName != null ? firstName : "",
                    lastName != null ? lastName : "",
                    notificationToken);


            // Retrieve and log user data
            firebaseDataManager.retrieveUserData((accessToken1, userId1, avatar1, firstName1, lastName1, notificationToken1) -> {
                Log.d("MainActivity", "FirebaseDataManager Access Token: " + (accessToken1 != null ? accessToken1 : "N/A"));
                Log.d("MainActivity", "FirebaseDataManager User ID: " + (userId1 != null ? userId1 : "N/A"));
                Log.d("MainActivity", "FirebaseDataManager Avatar: " + (avatar1 != null ? avatar1 : "N/A"));
                Log.d("MainActivity", "FirebaseDataManager First Name: " + (firstName != null ? firstName : "N/A"));
                Log.d("MainActivity", "FirebaseDataManager Last Name: " + (lastName != null ? lastName : "N/A"));
                Log.d("MainActivity", "FirebaseDataManager Notification Token: " + (notificationToken1 != null ? notificationToken1 : "N/A"));
            });


        } else {
            Log.d("MainActivity", "Error: userId is null or empty. FirebaseDataManager initialization skipped.");
        }



        Log.w("MainActivity", "Notification token ---> : " + currentUser_notification_token);


        // Proceed only if accessToken is not null or empty
        if (accessToken != null && !accessToken.isEmpty()) {
            GetChatNotif(accessToken);
        } else {
            Log.e("MainActivity", "Error: accessToken is null or empty. GetChatNotif skipped.");
        }

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
        // Validate accessToken before proceeding
        if (accessToken == null || accessToken.isEmpty()) {
            Log.e("GetChatNotif", "Error: accessToken is null or empty. API request skipped.");
            return;
        }
        Log.w("GetChatNotif", "AccessToken --->>> "  +accessToken);

        ChatsNotificationsApiManager.fetchChatNotifications(accessToken, new ChatsNotificationsApiManager.ApiCallback() {
            @Override
            public void onSuccess(NotificationAPIResponse response) {
                if (response == null || !response.isSuccess() || response.getData() == null || response.getData().getChat() == null) {
                    Log.e("GetChatNotif", "Invalid or null response received");
                    return;
                }

                List<ChatNotificationItem> notifications = response.getData().getChat().getData();
                if (notifications == null || notifications.isEmpty()) {
                    Log.w("GetChatNotif", "No new chat notifications.");
                    return;
                }

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String jsonResponse = gson.toJson(notifications);
                Log.d("GetChatNotif", "Chat Notification Response: \n" + jsonResponse);
                Log.d("GetChatNotif", "SIZE: " + notifications.size());

                Handler handler = new Handler(Looper.getMainLooper());
                int delay = 1000; // 1 second delay between notifications
                int[] count = {0};

                for (ChatNotificationItem notification : notifications) {
                    handler.postDelayed(() -> {
                        // Ensure notification fields are non-null
                        String sender = (notification.getSender() != null) ? notification.getSender() : "Unknown Sender";
                        String message = (notification.getText() != null) ? notification.getText() : "No message available";
                         avatarUrl = notification.getAvatar();
                        String time = (notification.getTime() != null) ? notification.getTime() : "Unknown Time";
                        String date = (notification.getDate() != null) ? notification.getDate() : "Unknown Date";
                        int channelValue = notification.getChannel();
                        String channel = (channelValue > 0) ? String.valueOf(channelValue) : "Unknown Channel";
                        Log.w("GetChatNotif", "Channel:  " + channel);

                        if (avatarUrl == null || avatarUrl.isEmpty()) {
                            Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.emptyglide);
                            Bitmap bitmap;

                            if (drawable instanceof BitmapDrawable) {
                                bitmap = ((BitmapDrawable) drawable).getBitmap();
                            } else if (drawable != null) {
                                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                                Canvas canvas = new Canvas(bitmap);
                                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                                drawable.draw(canvas);
                            } else {
                                Log.e("GetChatNotif", "Drawable resource emptyglide not found. Using default bitmap.");
                                bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                            }

                            NotificationManagerHelper.getInstance(getApplicationContext()).showNotification(
                                    sender, message, avatarUrl, time, date, channel
                            );
                        } else {
                            NotificationManagerHelper.getInstance(getApplicationContext()).showNotification(
                                    sender, message, avatarUrl, time, date, channel
                            );
                        }
                    }, count[0] * delay);
                    count[0]++;


                }
            }

            @Override
            public void onFailure(String error) {
                Log.e("GetChatNotif", "API Request Failed: " + (error != null ? error : "Unknown error"));
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        timerManager.saveTimeState(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerUIManager.cleanup();

    }

    public TimerManager getTimerManager() {
        return timerManager;
    }

    @Override
    public void setClockOutVisibility(boolean isVisible) {
        if (binding.clockoutBtn != null) {
            binding.clockoutBtn.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }


}


    /*    sharedPreferences = this.getSharedPreferences("showNotificationPrefs", Context.MODE_PRIVATE);
        isNotificationDisplayed = sharedPreferences.getBoolean("notification_displayed", false);
        FirebaseApp.initializeApp(this);

        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
        accessToken = sharedPrefManager.getAccessToken();
        email = sharedPrefManager.getEmail();
        password = sharedPrefManager.getPassword();
        avatarUrl = sharedPrefManager.getAvatarUrl();

        name = sharedPrefManager.getFirstName();
        id = sharedPrefManager.getUserId();

     */



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


