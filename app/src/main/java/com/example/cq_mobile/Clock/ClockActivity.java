package com.example.cq_mobile.Clock;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.cq_mobile.Clock.ViewListFolder.ViewListActivity;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenApiService;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenResponse;
import com.example.cq_mobile.HelperManagers.getAccessToken.RetrofitClientAccessToken;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.NotificationData.APIResponceFolder.FilterNotificationManager;
import com.example.cq_mobile.NotificationData.APIResponceFolder.FilteredNotificationResponse;
import com.example.cq_mobile.R;
import com.example.cq_mobile.Clock.ClockFolder.ClockView;
import com.example.cq_mobile.Clock.ClockFolder.DigitalClockManager;


import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClockActivity extends AppCompatActivity {

    private ClockView clockView;
    private DigitalClockManager digitalClockManager;
    private RecyclerView recyclerView;
    private TextView checkInButton, viewListButton;
    ImageView nav_drawer;
    String password;
     String saved_accessToken ,saved_userId;
    private String accessToken;
    int userId;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;
    private static final String NOTIFICATION_CHANNEL_ID = "default_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Intent intent = getIntent();
        String accessToken = intent.getStringExtra("accessToken");
        int userId = intent.getIntExtra("userId", -1);  // Default value is -1 if not found
        String firstName = intent.getStringExtra("firstName");
        String lastName = intent.getStringExtra("lastName");
        String email1 = intent.getStringExtra("email");
        String password1 = intent.getStringExtra("password");




        // If any of the intent values are null, retrieve from SharedPreferences
        if (accessToken == null || firstName == null || lastName == null || email1 == null || password1 == null) {
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
             saved_accessToken = sharedPreferences.getString("accessToken", null);
             saved_userId = sharedPreferences.getString("userId", null);
            String saved_firstName = sharedPreferences.getString("firstName", null);
            String saved_lastName = sharedPreferences.getString("lastName", null);
            String saved_email = sharedPreferences.getString("email", null);
            String saved_password = sharedPreferences.getString("password", null);
            String email =  saved_email;
             password = saved_password;
            AccessTokenRequest request = new AccessTokenRequest(email, password);
            getAccessToken(request);
        } else {
            String email =  email1;
             password = password1;
            AccessTokenRequest request = new AccessTokenRequest(email, password);
            getAccessToken(request);
        }

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
                initializeViews();
            }
        } else {
            initializeViews();

        }





    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT).show();
                initializeViews();
            } else {
                Toast.makeText(this, "Notification permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void getAccessToken(AccessTokenRequest request) {
        // Create an instance of the API service
        AccessTokenApiService apiService = RetrofitClientAccessToken.getRetrofitInstance().create(AccessTokenApiService.class);

        // Call the API
        Call<AccessTokenResponse> call = apiService.AccessTokenUser(request);

        // Enqueue the call to execute asynchronously
        call.enqueue(new Callback<AccessTokenResponse>() {
            @Override
            public void onResponse(Call<AccessTokenResponse> call, Response<AccessTokenResponse> response) {
                try {
                    // Check if the response is successful
                    if (response.isSuccessful() && response.body() != null) {
                        AccessTokenResponse accessTokenResponse = response.body();

                        // Extract the access token safely
                        accessToken = accessTokenResponse.getAccessToken() != null
                                ? accessTokenResponse.getAccessToken()
                                : "N/A";

                        // Extract user information safely
                        if (accessTokenResponse.getUser() != null) {
                            AccessTokenResponse.User user = accessTokenResponse.getUser();
                            userId = user.getId() > 0 ? user.getId() : -1;
                            String firstName = user.getFirstName() != null ? user.getFirstName() : "N/A";
                            String lastName = user.getLastName() != null ? user.getLastName() : "N/A";
                            String email = user.getEmail() != null ? user.getEmail() : "N/A";
                            String avatarUrl = user.getAvatar();

                            if (userId > 0) {
                                Log.d("ClockActivity", "Access Token: " + accessToken);
                                Log.d("ClockActivity", "User ID: " + userId);
                                Log.d("ClockActivity", "User First Name: " + firstName);
                                Log.d("ClockActivity", "User Last Name: " + lastName);
                                Log.d("ClockActivity", "User Email: " + email);
                                Log.d("ClockActivity", "Avatar URL: " + avatarUrl);

                                // Save user data to SharedPreferences
                                SharedPrefManager sharedPrefManager = new SharedPrefManager(ClockActivity.this);
                                String finalAvatarUrl = avatarUrl != null && !avatarUrl.isEmpty()
                                        ? avatarUrl
                                        : "2/uploads/contacts/avatars/colleague_avatar_31734941421.png";

                                sharedPrefManager.saveUserData(accessToken, String.valueOf(userId), firstName, lastName, email, finalAvatarUrl, password);
                                NotifFilter(accessToken, userId);
                            } else {
                                Log.e("ClockActivity", "Invalid user ID: " + userId);
                            }
                        } else {
                            Log.e("ClockActivity", "User data is null");
                        }
                    } else {
                        // Log unsuccessful response or null body
                        Log.e("ClockActivity", "API Error: " + response.message());
                        if (response.errorBody() != null) {
                            Log.e("ClockActivity", "Error body: " + response.errorBody().string());
                        }
                    }
                } catch (Exception e) {
                    Log.e("ClockActivity", "Exception occurred: " + e.getMessage(), e);
                }
            }

            @Override
            public void onFailure(Call<AccessTokenResponse> call, Throwable t) {
                Log.e("ClockActivity", "API call failed: " + t.getMessage(), t);
            }

        });


    }

    private void initializeViews() {
        // Initialize views using findViewById
        clockView = findViewById(R.id.analogClock);
        TextView digitalClock = findViewById(R.id.digitalClock);
        checkInButton = findViewById(R.id.check_in);
        viewListButton = findViewById(R.id.viewlist);


        digitalClockManager = new DigitalClockManager(digitalClock);



        digitalClockManager.startClock();

            checkInButton.setOnClickListener(v -> {
                    Intent intent = new Intent(ClockActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
            });


            viewListButton.setOnClickListener(v -> {
            Log.d("ClockActivity", "View-list button clicked");
            Intent intent = new Intent(ClockActivity.this, ViewListActivity.class);
            startActivity(intent);
        });
    }


    private void NotifFilter(String accessToken, int userId) {
        FilterNotificationManager.fetchApiDataFilterUserNotification(this, accessToken, String.valueOf(userId), 1, 10, new FilterNotificationManager.ApiResponseCallback() {
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

                    displayNotification(ClockActivity.this, title, message, avatarUrl);

                }
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

        // Generate a unique notification ID for each notification
        int notificationId = (int) System.currentTimeMillis(); // Use current time in milliseconds as a unique ID

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

                        // Display the notification with a unique ID
                        notificationManager.notify(notificationId, notification);
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

                        notificationManager.notify(notificationId, notification);
                    }
                });
    }



}

