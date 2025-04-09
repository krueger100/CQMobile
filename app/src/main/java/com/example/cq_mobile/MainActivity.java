package com.example.cq_mobile;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;
import com.example.cq_mobile.Clock.ClockFolder.ClockOutFolder.ClockOutManager;
import com.example.cq_mobile.Clock.ClockFolder.TimerFunctionManager;
import com.example.cq_mobile.Clock.ClockFragment;
import com.example.cq_mobile.Clock.JobDetails;
import com.example.cq_mobile.Clock.SaveJobDataManager;
import com.example.cq_mobile.FirebaseUserData.FirebaseDataManager;
import com.example.cq_mobile.HelperManagers.Animation.TransitionAnimationManager;
import com.example.cq_mobile.HelperManagers.BackPressManager;
import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.ClockOutVisibilityHandler;
import com.example.cq_mobile.HelperManagers.NavigationManager;
import com.example.cq_mobile.HelperManagers.Notifications.ChatNotif_folder.GetChatNotifManager;
import com.example.cq_mobile.HelperManagers.Notifications.GetNotificationToken;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.ServerDataReconnect;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.HelperManagers.StatusBarManager;
import com.example.cq_mobile.LoginFolder.AuthManager;
import com.example.cq_mobile.LoginFolder.Login;
import com.example.cq_mobile.OfflineDataFolder.NetworkManager;
import com.example.cq_mobile.databinding.ActivityMainBinding;
import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.NewBuild;
import com.google.android.material.tabs.TabLayout;
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
    String avatarPath;
    String avatarUrl;
    private NetworkManager networkManager;
    private View rootView;
    private TimerFunctionManager timerFunctionManager;
    int userId;
    String email, password, avatar, startDate;
    ServerDataReconnect serverDataReconnect;
    private BackPressManager backPressManager;
    private ViewPager2 viewPager;
    private ProgressBar progressBar;
    private TabLayout tabLayout;
    String token2;
    SaveJobDataManager saveJobDataManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        networkManager = new NetworkManager(this);
        if (!networkManager.isConnected()) {
            networkManager.showNoConnectionDialog();

        }

        SharedPreferences sharedPreferences = getSharedPreferences("ClockPrefs", MODE_PRIVATE);
        boolean isClockedIn = sharedPreferences.getBoolean("ClockInSuccess", false);
        Log.d("MainActivity", "Clock In Status: " + isClockedIn);
        sharedPreferences.edit().putBoolean("AddressDialogShown", false).apply();
        sharedPrefManager = new SharedPrefManager(this);
         token2 = sharedPrefManager.getNotiftoken();
        Log.d("MainActivity", "token2: ----->>>> " + token2);
         saveJobDataManager = new SaveJobDataManager(this);


        serverDataReconnect = new ServerDataReconnect(getApplicationContext());
        AuthManager authManager = AuthManager.getInstance(this);
        if (authManager != null && authManager.isLoggedIn()) {
            if (authManager.isTokenExpired()) {
                Log.d("MainActivity", "Token is expired. Please log in again.");
                Toast.makeText(this, "Your session has expired. Please log in again.", Toast.LENGTH_LONG).show();

                Intent loginIntent = new Intent(MainActivity.this, Login.class);
                startActivity(loginIntent);
                finish();
            } else {

                accessToken = authManager.getToken();
                userId = authManager.getUserId();
                String firstName = authManager.getFirstName();
                String lastName = authManager.getLastName();
                String email = authManager.getEmail();
                String avatar = authManager.getAvatar();
                Log.d("MainActivity", "<-AuthManager->" );
                Log.d("MainActivity", "Access Token: " + accessToken);
                Log.d("MainActivity", "User ID: " + userId);
                Log.d("MainActivity", "User Name: " + firstName + " " + lastName);
                Log.d("MainActivity", "Avatar Path: ----->>>> " + avatar);
                Log.d("MainActivity", "Email: ----->>>> " + email);


                // Proceed with the rest of the logic
                viewPager = findViewById(R.id.viewPager);
                progressBar = findViewById(R.id.progressBar);
                tabLayout = findViewById(R.id.tabLayout);

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
                            initializeApp(finalUserId);
                        });
                        GetNotificationToken.getToken(this);
                    }
                }
            }
        } else {
            Log.d("MainActivity", "AuthManager is null or user is not logged in. Fetching data from Firebase.");

            FirebaseDataManager firebaseDataManager = new FirebaseDataManager(userId);
            firebaseDataManager.retrieveUserData(new FirebaseDataManager.UserDataCallback() {
                @Override
                public void onUserDataRetrieved(String accessToken, String userId, String avatar, String firstName, String lastName, String notificationToken) {
                    Log.d("MainActivity", "<-FirebaseDataManager->" );
                    Log.d("MainActivity", "Access Token: " + accessToken);
                    Log.d("MainActivity", "User ID: " + userId);
                    Log.d("MainActivity", "User Name: " + firstName + " " + lastName);
                    Log.d("MainActivity", "Avatar Path: ----->>>> " + avatar);
                    Log.d("MainActivity", "Email: ----->>>> " + email);
                    Log.d("MainActivity", "Notification Token: ----->>>> " + notificationToken);
                    viewPager = findViewById(R.id.viewPager);
                    progressBar = findViewById(R.id.progressBar);
                    tabLayout = findViewById(R.id.tabLayout);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                                    NOTIFICATION_PERMISSION_REQUEST_CODE
                            );
                        } else {
                            int finalUserId = Integer.parseInt(userId);
                            GetNotificationToken.setTokenCallback(token2 -> {
                                Log.d("MainActivity", "Received token: " + token2);
                                initializeApp(finalUserId);
                            });
                            GetNotificationToken.getToken(MainActivity.this);
                        }
                    }
                }
            });
        }


        if (!isClockedIn){
            loadFragment(new ClockFragment());
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ClockFragment())
                        .addToBackStack(null)
                        .commit();
            }

        }else {
            Log.d("MainActivity", "Already Clocked In" );

        }




        drawerLayout = binding.drawerLayout;

        backPressManager = new BackPressManager(this);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backPressManager.handleBackPress(MainActivity.class);
            }
        });



    }
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();

    }

    private void initializeApp(int finalUserId) {
        FirebaseApp.initializeApp(this);
        StatusBarManager.setStatusBarLight(this);
        List<JobDetails> savedJobDetailsList = saveJobDataManager.getJobData();
        if (savedJobDetailsList != null && !savedJobDetailsList.isEmpty()) {
            for (JobDetails jobDetails : savedJobDetailsList) {
                Log.d("SavedJobData", "ID: " + jobDetails.getId());
                Log.d("SavedJobData", "Job ID: " + jobDetails.getJobId());
                Log.d("SavedJobData", "Job Started: " + jobDetails.getStartDate());

                navigationManager = new NavigationManager(MainActivity.this, binding.clockoutBtn, binding.navView, binding.navViewDrawer, drawerLayout,
                        binding.progressBar, accessToken,jobDetails.getId(), jobDetails.getJobId(), startDate, userId);
                navigationManager.setupNavigation();

                ClockOutManager clockOutManager = new ClockOutManager(MainActivity.this, binding.progressBar, jobDetails.getId(), jobDetails.getJobId(), userId, sharedPrefManager);
                String jobTitle = sharedPrefManager.getStartJob();
                String jobTitle_started_message = sharedPrefManager.getStartJobMessage();
                String jobTitle_id = sharedPrefManager.getStartJobID();
                String jobTitle_Taskid = sharedPrefManager.getStartJobIDInnerTask();
                boolean jobSuccess = sharedPrefManager.isJobSuccessful();

                if (jobSuccess) {
                    clockOutManager.setupClockOutButton(binding.clockoutBtn, accessToken, jobDetails.getId());
                    binding.jobTitle.setText(jobTitle);
                    binding.timerLayout.setVisibility(View.VISIBLE);
                    binding.progressBarTimer.setVisibility(View.VISIBLE);


                    Log.d("MainActivity", "ID: ----->>>>  " + jobDetails.getId());
                    Log.d("MainActivity", "Job ID: ----->>>> " + jobDetails.getJobId());


                    binding.jobTitle.setOnClickListener(v -> {
                        TransitionAnimationManager.zoomOut(v, 150);
                        v.postDelayed(() -> {
                            TransitionAnimationManager.zoomIn(v, 50);
                            binding.progressBar.setVisibility(View.VISIBLE);
                            Intent intent = new Intent(MainActivity.this, NewBuild.class);
                            intent.putExtra("job_id", jobTitle_id);
                            intent.putExtra("task_id", jobTitle_Taskid);
                            startActivity(intent);
                            binding.progressBar.setVisibility(View.GONE);
                        }, 150);
                    });
                } else {
                    clockOutManager.setupStopJobWithTimeSheet(binding.clockoutBtn, accessToken,jobDetails.getId(), jobDetails.getJobId(), sharedPrefManager, binding.progressBar);
                    binding.jobTitle.setText(jobTitle);
                }

                rootView = findViewById(android.R.id.content);
                timerFunctionManager = new TimerFunctionManager(rootView, jobDetails.getStartDate(),jobDetails.getId(), jobDetails.getJobId(), userId,
                        binding.progressBar, clockOutManager, binding.clockoutBtn, binding.timerLayout);


//                HomeFragment homeFragment = new HomeFragment();
//                Bundle args = new Bundle();
//                homeFragment.setArguments(args);
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_container, homeFragment)
//                        .commit();

                binding.progressBar.setVisibility(View.GONE);


            }
        } else {
            Log.d("MainActivity", "No job data found.");
        }

        if (accessToken != null && !accessToken.isEmpty()) {
            GetChatNotifManager chatNotifManager = new GetChatNotifManager(this);
            chatNotifManager.GetChatNotif(accessToken);
        } else {
            Log.e("MainActivity", "Error: accessToken is null or empty. GetChatNotif skipped.");
        }



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
        serverDataReconnect.reconnectAsync(success -> runOnUiThread(() -> {
            if (success) {
                Log.d("MainActivity", "Reconnected successfully.");
                Log.d(TAG, "Reconnected successfully");
                if (timerFunctionManager != null) {
                    timerFunctionManager.resumeTimerAfterReopen(this);
                    Log.d(TAG, "resumeTimerAfterReopen called in MainActivity");
                }
            } else {
                Log.e("MainActivity", "Reconnection failed. Redirecting to login.");
                new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                    finish();
                }, 500);
            }
        }));

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

    public void hideClockFragment() {
        ClockFragment clockFragment = (ClockFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (clockFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(clockFragment)
                    .commit();
        }
    }

    @Override
    public void setClockOutVisibility(boolean isVisible) {
        if (binding.clockoutBtn != null) {
            binding.clockoutBtn.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

}




/*
   boolean isLoggedIn = loginSavedData.getIsLoggedIn();
                                Log.d("Login", "Is Logged In: " + isLoggedIn);
 */