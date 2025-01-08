package com.example.cq_mobile;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.cq_mobile.HelperManagers.NavigationManager;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.HelperManagers.StatusBarManager;
import com.example.cq_mobile.NotificationData.APIResponceFolder.FilterNotificationManager;
import com.example.cq_mobile.NotificationData.APIResponceFolder.FilteredNotificationResponse;
import com.example.cq_mobile.NotificationData.ShowNotificationActivity;
import com.example.cq_mobile.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationManager navigationManager;

    private static final String TAG = "MainActivity";
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;
    String notification_data;
    String accessToken,userId;
    private static final String NOTIFICATION_CHANNEL_ID = "default_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase and set status bar style
        FirebaseApp.initializeApp(this);
        StatusBarManager.setStatusBarLight(this);

        // Set up drawer and navigation manager
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

        // Set up navigation manager
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
        FilterNotificationManager.fetchApiDataFilterUserNotification(this, accessToken, userId, 1, 10, new FilterNotificationManager.ApiResponseCallback() {
            @Override
            public void onDataFetched(List<FilteredNotificationResponse.NotificationData> data) {
                // Handle the success response
                Log.d("FilterNotification", "Data fetched successfully: USER " + data);
                for (FilteredNotificationResponse.NotificationData notification : data) {
                    Log.d("NotificationUSER", "Title: " + notification.getTitle());
                    Log.d("NotificationUSER", "Description " + notification.getDescription());
                    Log.d("NotificationUSER", "Avatar URL: " + notification.getAvatar()); // Log avatar

                    String avatarUrl = notification.getAvatar();
                    String title = notification.getTitle();
                    String message = notification.getDescription();

                    displayNotification(MainActivity.this, title, message, avatarUrl);

                }
            }

            @Override
            public void onError(String error) {
                // Handle the error
                Log.e("FilterNotification", "Error fetching data: " + error);
            }
        });

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



                // Display the notification using the lists
                showNotification(getApplicationContext(), titles, avatars);
            }

            @Override
            public void onError(String error) {
                // Handle the error
                Log.e("FilterNotification", "Error fetching data: " + error);
            }
        });

    }

    private static void displayNotification(Context context, String title, String message, String avatarUrl) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Default Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Load the image using Glide
        Glide.with(context)
                .asBitmap()
                .load(avatarUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        // When the image is ready, create the notification
                        Notification notification = new Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
                                .setContentTitle(title)
                                .setContentText(message)
                                .setSmallIcon(R.drawable.android12splash_orange)
                                .setLargeIcon(resource) // Set the large icon as the avatar
                                .setAutoCancel(true)
                                .build();

                        // Display the notification
                        notificationManager.notify(1, notification);
                    }

                    @Override
                    public void onLoadFailed(Drawable errorDrawable) {
                        // Handle failure (fallback to default icon)
                        Notification notification = new Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
                                .setContentTitle(title)
                                .setContentText(message)
                                .setSmallIcon(R.drawable.android12splash_orange)
                                .setAutoCancel(true)
                                .build();

                        notificationManager.notify(1, notification);
                    }
                });
    }

    private void showNotification(Context context, List<String> titles, List<String> avatars) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            ImageView notificationIndicator = findViewById(R.id.Notification_indication_on);
            ImageView header_Notification = findViewById(R.id.header_Notification);

            // Make notification indicator visible
            notificationIndicator.setVisibility(View.VISIBLE);
            Log.d(TAG, "Notification indicator set to VISIBLE because there are notifications");

            header_Notification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        RetrieveStoredNoticationData(context, accessToken, userId, header_Notification, titles, avatars);
                }
            });



        }, 1000);
    }

    private void RetrieveStoredNoticationData(Context context, String accessToken, String userId, ImageView header_Notification, List<String> titles, List<String> avatars) {

        Intent intent = new Intent(MainActivity.this, ShowNotificationActivity.class);
        intent.putStringArrayListExtra("teamNames", new ArrayList<>(titles));  // Pass titles list
        intent.putStringArrayListExtra("teamAvatars", new ArrayList<>(avatars));  // Pass avatars list

        // Start the activity with the intent
        startActivity(intent);
    }

}


/*
    // Call the method to show notifications
    //    showNotification(getApplicationContext(), accessToken, userId);


    private void showNotification(Context context, String accessToken, String userId) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            ImageView notificationIndicator = findViewById(R.id.Notification_indication_on);
            ImageView header_Notification = findViewById(R.id.header_Notification);

            if (notificationIndicator != null) {
                // Toggle the visibility of the notification indicator
                if (notificationIndicator.getVisibility() == View.VISIBLE) {
                    notificationIndicator.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "Notification indicator set to INVISIBLE because there are no notifications");
                    fetchNotificationData(context, accessToken, userId, header_Notification);
                } else {
                    notificationIndicator.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Notification indicator set to VISIBLE because there are notifications");
                    fetchNotificationData(context, accessToken, userId, header_Notification);
                }
            } else {
                Log.e(TAG, "Notification indicator not found in the layout!");
            }
        }, 1000);
    }

    private void fetchNotificationData(Context context, String accessToken, String userId, ImageView header_Notification) {
        // ExecutorService to handle background tasks
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.submit(() -> {
            NotificationApiManager.fetchApiDataPaginated(context, accessToken, userId, 1, 10, new NotificationApiManager.ApiResponseCallback() {
                @Override
                public void onDataFetched(List<NotificationResponse.NotificationData> data) {
                    // Now, post the UI update to the main thread
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (data != null && !data.isEmpty()) {
                            for (NotificationResponse.NotificationData notification : data) {
                                if (notification != null) {
                                    Log.d(TAG, "Notification Data: " + notification.toString());
                                    notification_data = notification.toString();
                                } else {
                                    Log.e(TAG, "Received a null notification data");
                                }
                            }
                        } else {
                            Log.d(TAG, "No data received or data is empty");
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    // Handle the error
                    Log.e(TAG, "Error fetching data: " + error);
                }
            });
        });

        header_Notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notification_data != null) {
                    Log.d(TAG, "Notification Data: " + notification_data);
                    RetrieveStoredNoticationData(context);
                } else {
                    Log.d(TAG, "Notification data is null when clicked");
                    RetrieveStoredNoticationData(context);
                }
            }
        });
    }


    private void RetrieveStoredNoticationData(Context context) {
        SharedPreffForNotification sharedPreffManager = new SharedPreffForNotification(context);

        // Retrieve team-related data from SharedPreferences
        List<String> teamNames = sharedPreffManager.getTeamNames();
        List<String> teamAvatars = sharedPreffManager.getTeamAvatars();
        List<String> teamInitials = sharedPreffManager.getTeamInitials();
        List<String> teamColors = sharedPreffManager.getTeamColors();

        Intent intent = new Intent(MainActivity.this, ShowNotificationActivity.class);

        // Pass team-related data to ShowNotificationActivity via Intent
        intent.putStringArrayListExtra("teamNames", new ArrayList<>(teamNames));
        intent.putStringArrayListExtra("teamAvatars", new ArrayList<>(teamAvatars));
        intent.putStringArrayListExtra("teamInitials", new ArrayList<>(teamInitials));
        intent.putStringArrayListExtra("teamColors", new ArrayList<>(teamColors));

        startActivity(intent);
    }
 */