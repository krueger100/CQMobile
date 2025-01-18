package com.example.cq_mobile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.cq_mobile.HelperManagers.NavigationManager;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.HelperManagers.StatusBarManager;
import com.example.cq_mobile.NotificationData.APIResponceFolder.FilterNotificationManager;
import com.example.cq_mobile.NotificationData.APIResponceFolder.FilteredNotificationResponse;
import com.example.cq_mobile.NotificationData.ShowNotificationActivity;
import com.example.cq_mobile.databinding.ActivityMainBinding;
import com.example.cq_mobile.ui.home.UpdateJobsFolder.CheckListFolder.RetrofitClient;
import com.example.cq_mobile.ui.home.UpdateJobsFolder.CheckListFolder.TaskChecklistApi;
import com.example.cq_mobile.ui.home.UpdateJobsFolder.CheckListFolder.TaskChecklistUpdateRequest;
import com.example.cq_mobile.ui.home.UpdateJobsFolder.CheckListFolder.TaskChecklistResponse;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationManager navigationManager;
    SharedPreferences sharedPreferences;
    private static final String TAG = "MainActivity";
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;
    String accessToken,userId;
    boolean isNotificationDisplayed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = this.getSharedPreferences("showNotificationPrefs", Context.MODE_PRIVATE);
         isNotificationDisplayed = sharedPreferences.getBoolean("notification_displayed", false);

        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("SharedPreferencesNotif", entry.getKey() + ": " + entry.getValue().toString());

        }


        FirebaseApp.initializeApp(this);
        StatusBarManager.setStatusBarLight(this);

        drawerLayout = binding.drawerLayout;
        navigationManager = new NavigationManager(this, binding.navView, binding.navViewDrawer, drawerLayout);

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
                initializeApp();
            }
        } else {
            initializeApp();
        }



    }


    private void initializeApp() {
        // Retrieve access token and user ID from shared preferences
        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
         accessToken = sharedPrefManager.getAccessToken();
         userId = sharedPrefManager.getUserId();

        // Log retrieved user data
        Log.d(TAG, "Retrieved User Data: ");
        Log.d(TAG, "Access Token: " + accessToken);
        Log.d(TAG, "User ID: " + userId);
         NotifFilter();

        navigationManager.setupNavigation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT).show();
                initializeApp();
            } else {
                Toast.makeText(this, "Notification permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navigationManager.onSupportNavigateUp();
    }

    private void NotifFilter() {

        FilterNotificationManager.fetchApiDataFilterGroupNotification(this, accessToken, userId, 1, 10, new FilterNotificationManager.ApiResponseCallback() {
            @Override
            public void onDataFetched(List<FilteredNotificationResponse.NotificationData> data) {
                // Handle the success response
                Log.d("FilterNotification", "Data fetched successfully: USER " + data);

                List<String> titles = new ArrayList<>();
                List<String> avatars = new ArrayList<>();

                // Iterate over the notification data to populate titles and avatars lists
                for (FilteredNotificationResponse.NotificationData notification : data) {
                    Log.d("NotificationGROUP", "Title: " + notification.getTitle());
                    Log.d("NotificationGROUP", "Avatar URL: " + notification.getAvatar());


                    titles.add(notification.getTitle());
                    avatars.add(notification.getAvatar());
                }



                if (isNotificationDisplayed) {
                    Log.d("SharedPreferencesNotif", "TRUE");

                }else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("notification_displayed", false);
                    editor.apply();
                    Log.d("SharedPreferencesNotif", "FALSE");
                    new ArrayList<>(titles);
                    new ArrayList<>(avatars) ;
                    navigateToShowNotificationActivity(titles,avatars);
                }

            }

            @Override
            public void onError(String error) {
                // Handle the error
                Log.e("FilterNotification", "Error fetching data: " + error);
            }
        });

    }

    private void showNotification(Context context, List<String> titles, List<String> avatars) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Find ImageView by ID
            ImageView notificationIndicator = findViewById(R.id.Notification_indication_on);
            ImageView header_Notification = findViewById(R.id.header_Notification);

            // Check if the ImageView is found
            if (notificationIndicator != null) {
                notificationIndicator.setVisibility(View.VISIBLE);
                Log.d(TAG, "Notification indicator set to VISIBLE because there are notifications");
            } else {
                Log.e(TAG, "Notification indicator not found in the layout");
            }

            // Set up click listener if header_Notification is found
            if (header_Notification != null) {
                header_Notification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RetrieveStoredNoticationData(context, accessToken, userId, header_Notification, titles, avatars);
                    }
                });
            } else {
                Log.e(TAG, "Header notification not found in the layout");
            }
        }, 1000);
    }

    private void RetrieveStoredNoticationData(Context context, String accessToken, String userId, ImageView header_Notification, List<String> titles, List<String> avatars) {

            Intent intent = new Intent(MainActivity.this, ShowNotificationActivity.class);
            intent.putStringArrayListExtra("teamNames", new ArrayList<>(titles));
            intent.putStringArrayListExtra("teamAvatars", new ArrayList<>(avatars));
            startActivity(intent);


    }

    private void navigateToShowNotificationActivity(List<String> titles, List<String> avatars) {

        Intent intent = new Intent(MainActivity.this, ShowNotificationActivity.class);
        intent.putStringArrayListExtra("teamNames", new ArrayList<>(titles));
        intent.putStringArrayListExtra("teamAvatars", new ArrayList<>(avatars));
        startActivity(intent);
        finish();
    }



}


