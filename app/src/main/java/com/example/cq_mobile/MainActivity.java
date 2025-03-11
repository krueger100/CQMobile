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
import android.widget.TextView;
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
import com.example.cq_mobile.HelperManagers.Notifications.ChatNotif_folder.GetChatNotifManager;
import com.example.cq_mobile.HelperManagers.Notifications.GetNotificationToken;
import com.example.cq_mobile.HelperManagers.Notifications.NotificationManagerHelper;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.HelperManagers.StatusBarManager;
import com.example.cq_mobile.OfflineDataFolder.NetworkManager;
import com.example.cq_mobile.databinding.ActivityMainBinding;
import com.example.cq_mobile.ui.chat.ChatNotif.ChatNotificationItem;
import com.example.cq_mobile.ui.chat.ChatNotif.ChatsNotificationsApiManager;
import com.example.cq_mobile.ui.chat.ChatNotif.NotificationAPIResponse;
import com.example.cq_mobile.ui.chat.ChatPageFragment;
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
    TextView job_title;
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

        navigationManager = new NavigationManager(this, binding.navView, binding.navViewDrawer, drawerLayout,binding.progressBar,accessToken,jobId,taskId,startDate,userId);


        ClockOutManager clockOutManager = new ClockOutManager(this,  binding.progressBar,  jobId,  taskId,  userId,  startDate);
        clockOutManager.setupClockOutButton(binding.clockoutBtn, accessToken, jobId);

        String jobTitle = sharedPrefManager.getStartJob();
        Log.w("JobTitle", "jobTitle  ->> "  + jobTitle);
        if (jobTitle != null && !jobTitle.trim().isEmpty()) {
            binding.jobTitle.setVisibility(View.VISIBLE);
            binding.jobTitle.setText(jobTitle);

        } else {
            binding.jobTitle.setVisibility(View.GONE);
        }


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

       if (accessToken != null && !accessToken.isEmpty()) {
           GetChatNotifManager chatNotifManager = new GetChatNotifManager(getApplicationContext());
           chatNotifManager.GetChatNotif(accessToken);
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




