package com.example.cq_mobile;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.cq_mobile.Clock.ClockFolder.ClockOutFolder.ClockOutManager;
import com.example.cq_mobile.Clock.ClockFolder.TimerFunctionManager;
import com.example.cq_mobile.FirebaseUserData.FirebaseDataManager;
import com.example.cq_mobile.FirebaseUserData.FirebaseDatabaseManager;
import com.example.cq_mobile.HelperManagers.Animation.TransitionAnimationManager;
import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.ClockOutVisibilityHandler;
import com.example.cq_mobile.HelperManagers.NavigationManager;
import com.example.cq_mobile.HelperManagers.Notifications.ChatNotif_folder.GetChatNotifManager;
import com.example.cq_mobile.HelperManagers.Notifications.GetNotificationToken;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.HelperManagers.StatusBarManager;
import com.example.cq_mobile.OfflineDataFolder.NetworkManager;
import com.example.cq_mobile.databinding.ActivityMainBinding;
import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.NewBuild;
import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.NewBuildApiManager;
import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.TaskMainFolder.Taskmain;
import com.google.firebase.FirebaseApp;

import java.util.List;


public class MainActivity extends AppCompatActivity implements ClockOutVisibilityHandler {
    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationManager navigationManager;
    SharedPrefManager sharedPrefManager;
    private static final String TAG = "MainActivity";
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;
    String accessToken;
    String userName;
    String notificationToken;
    int jobId = -1 ;
    int taskId = -1;
    String firstName;
    String lastName;
    String avatarPath;
    String avatarUrl;
    private NetworkManager networkManager;
    FirebaseDatabaseManager firebaseDatabaseManager;
    LinearLayout timer_layout2;
    private View rootView;
   private TimerFunctionManager timerFunctionManager;
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


        sharedPreferences.edit().putBoolean("AddressDialogShown", false).apply();

        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String accessToken = userPrefs.getString("accessToken", null);
        String userID = userPrefs.getString("userId", null);
        String email = userPrefs.getString("email", null);
        String password = userPrefs.getString("password", null);
        String avatar = userPrefs.getString("avatar", null);


        Intent intent = getIntent();
        accessToken = intent.getStringExtra("accessToken");
        int userId = intent.getIntExtra("userId", -1);
        if (userId == -1) {
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
        Log.d("MainActivity", "Avatar URL: Intent ----->>>> " + avatarUrl);
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

        Log.w(TAG, "ClockOutManager: " + "\njobId  ->\n" + jobId + "\n taskId ->\n" + taskId +"\n userId  \n->"+userId
                +"\n startDate  \n->"+ startDate);


        navigationManager = new NavigationManager(this,binding.clockoutBtn, binding.navView, binding.navViewDrawer, drawerLayout,
                binding.progressBar, accessToken, jobId, taskId, startDate, userId);



        ClockOutManager clockOutManager = new ClockOutManager(MainActivity.this, binding.progressBar, jobId, taskId, userId, startDate, sharedPrefManager);
        String jobTitle = sharedPrefManager.getStartJob();
        String jobTitle_started_message = sharedPrefManager.getStartJobMessage();
        String jobTitle_id = sharedPrefManager.getStartJobID();
        String jobTitle_Taskid = sharedPrefManager.getStartJobIDInnerTask();
        boolean jobSuccess = sharedPrefManager.isJobSuccessful();

        Log.w(TAG, "jobTitle ->> " + jobTitle);
        Log.w(TAG, "jobMessage ->> " + jobTitle_started_message);
        Log.w(TAG, "jobTitle ID ->> " + jobTitle_id);
        Log.w(TAG, "jobTitle TASKID ->> " + jobTitle_Taskid);
        Log.w(TAG, "Job Success ->> " + jobSuccess);

        if (jobSuccess) {
            clockOutManager.setupClockOutButton(binding.clockoutBtn, accessToken, jobId);
            Log.w(TAG, "From: MainActivity -> " + jobTitle);
            binding.jobTitle.setText(jobTitle);

            binding.timerLayout.setVisibility(View.VISIBLE);
            binding.progressBarTimer.setVisibility(View.VISIBLE);

            binding.jobTitle.setOnClickListener(v -> {
                TransitionAnimationManager.zoomOut(v, 150);
                    v.postDelayed(() -> {
                        TransitionAnimationManager.zoomIn(v, 50);
                        binding.progressBar.setVisibility(View.VISIBLE);
                        Intent intent = new Intent(MainActivity.this, NewBuild.class);
                        intent.putExtra("job_id", jobTitle_id);
                        intent.putExtra("task_id", jobTitle_Taskid);
                        try {
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }, 150);
            });

        } else {
            clockOutManager.setupStopJobWithTimeSheet(binding.clockoutBtn, accessToken, jobId, taskId, sharedPrefManager, binding.progressBar);
            binding.jobTitle.setText(jobTitle);

            Log.w(TAG, "From: MainActivity <-  " + jobTitle);
            if (timerFunctionManager != null) {
                timerFunctionManager.resetTimer(this);
                Log.d(TAG, "Timer Not Started : No Job Started");
            }

        }



        if (startDate == null) {
            Log.w("MainActivity", "Warning: Start date is null, using default value 0.");
        }
        Log.w("MainActivity", "Start time ----->>>> " + startDate);

        rootView = findViewById(android.R.id.content);
        timerFunctionManager = new TimerFunctionManager(rootView, startDate, jobId, taskId, userId,
                binding.progressBar, clockOutManager,binding.clockoutBtn,binding.timerLayout);

        Log.d("MainActivity", "FirebaseDataManager User ID:  --------->>> " + userId );

        if (notificationToken == null || notificationToken.isEmpty()) {
            notificationToken = (currentUser_notification_token != null) ? currentUser_notification_token : "";
            sharedPrefManager.saveNewNotificationToken(notificationToken);
            Log.w("MainActivity", "Notification token updated: " + notificationToken);
        }

        if (userId != 0) {
            FirebaseDataManager firebaseDataManager = new FirebaseDataManager(userId);
            firebaseDataManager.saveUserData(
                    accessToken != null ? accessToken : "",
                    String.valueOf(userId),
                    avatarPath != null ? avatarPath : "",
                    firstName != null ? firstName : "",
                    lastName != null ? lastName : "",
                    notificationToken);

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
    protected void onResume() {
        super.onResume();
        if (timerFunctionManager != null) {
            timerFunctionManager.resumeTimerAfterReopen(this);
            Log.d(TAG, "resumeTimerAfterReopen called in MainActivity");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timerFunctionManager != null) {
            timerFunctionManager.getTimerManager().saveTimeState(this);
            Log.d(TAG, "Timer state saved in onPause()");
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerFunctionManager != null) {
            timerFunctionManager.cleanup();
        }
    }

    @Override
    public void setClockOutVisibility(boolean isVisible) {
        if (binding.clockoutBtn != null) {
            binding.clockoutBtn.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }


}




