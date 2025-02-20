package com.example.cq_mobile.Clock;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import com.example.cq_mobile.Clock.ClockFolder.ClockInAPIFolder.ClockINApiManager;
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
import com.google.firebase.messaging.FirebaseMessaging;


import android.widget.ImageView;
import android.widget.ProgressBar;
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
     String saved_accessToken ;
   int saved_userId;
    private String accessToken;
    int userId;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;
    private static final String NOTIFICATION_CHANNEL_ID = "default_channel";
    private static final String TAG = "ClockActivity";
    ProgressBar progressBar;
    String Name;
    String avatar;
    String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Intent intent = getIntent();
        String accessToken = intent.getStringExtra("accessToken");
         userId = intent.getIntExtra("userId", -1);
        String firstName = intent.getStringExtra("firstName");
        String lastName = intent.getStringExtra("lastName");
        String email1 = intent.getStringExtra("email");
        String password1 = intent.getStringExtra("password");
        avatar = intent.getStringExtra("avatar");
        progressBar = findViewById(R.id.progressBar);


        // If any of the intent values are null, retrieve from SharedPreferences
        if (accessToken == null || firstName == null || lastName == null || email1 == null || password1 == null) {
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
             saved_accessToken = sharedPreferences.getString("accessToken", null);
             saved_userId = Integer.parseInt(sharedPreferences.getString("userId", null));
            String saved_firstName = sharedPreferences.getString("firstName", null);
            String saved_lastName = sharedPreferences.getString("lastName", null);
            String saved_email = sharedPreferences.getString("email", null);
            String saved_password = sharedPreferences.getString("password", null);
             avatar = sharedPreferences.getString("avatar", null);
            String email =  saved_email;
             password = saved_password;
            String Name_sh = firstName +" "+ lastName;
            AccessTokenRequest request = new AccessTokenRequest(email, password);
            getAccessToken(request,Name_sh,saved_userId,avatar,accessToken);
            Log.d("ClockActivity", "Avatar: SharedPreferences " + avatar);

        } else {
            String email =  email1;
             password = password1;
            String Name = firstName +" "+ lastName;
            AccessTokenRequest request = new AccessTokenRequest(email, password);
            getAccessToken(request,Name,userId,avatar,accessToken);
        }

         Name = firstName +" "+ lastName;

    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT).show();
                initializeViews(Name,userId,avatar,accessToken,avatar);
            } else {
                Toast.makeText(this, "Notification permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getAccessToken(AccessTokenRequest request, String name, int userId, String avatar_path, String accessToken) {
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
                        ClockActivity.this.accessToken = accessTokenResponse.getAccessToken() != null
                                ? accessTokenResponse.getAccessToken()
                                : "N/A";

                        // Extract user information safely
                        if (accessTokenResponse.getUser() != null) {
                            AccessTokenResponse.User user = accessTokenResponse.getUser();
                            ClockActivity.this.userId = user.getId() > 0 ? user.getId() : -1;
                            String firstName = user.getFirstName() != null ? user.getFirstName() : "N/A";
                            String lastName = user.getLastName() != null ? user.getLastName() : "N/A";
                            String email = user.getEmail() != null ? user.getEmail() : "N/A";
                            String avatarUrl = user.getAvatar();

                            if (ClockActivity.this.userId > 0) {
                                Log.d("ClockActivity", "Access Token: " + ClockActivity.this.accessToken);
                                Log.d("ClockActivity", "User ID: " + ClockActivity.this.userId);
                                Log.d("ClockActivity", "User First Name: " + firstName);
                                Log.d("ClockActivity", "User Last Name: " + lastName);
                                Log.d("ClockActivity", "User Email: " + email);
                                Log.d("ClockActivity", "Avatar URL: " + avatarUrl);

                                // Request notification permissions if required
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    if (ContextCompat.checkSelfPermission(ClockActivity.this, Manifest.permission.POST_NOTIFICATIONS)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(
                                                ClockActivity.this,
                                                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                                                NOTIFICATION_PERMISSION_REQUEST_CODE
                                        );
                                    } else {
                                        initializeViews(name, userId, avatar_path, accessToken, avatarUrl);
                                    }
                                } else {
                                    initializeViews(name, userId, avatar_path, accessToken, avatarUrl);

                                }

                                FirebaseMessaging.getInstance().getToken()
                                        .addOnCompleteListener(task -> {
                                            if (!task.isSuccessful()) {
                                                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                                return;
                                            }

                                            // Get the new FCM registration token
                                            token = task.getResult();


                                            SharedPrefManager sharedPrefManager = new SharedPrefManager(ClockActivity.this);
                                            String finalAvatarUrl = avatarUrl != null && !avatarUrl.isEmpty()
                                                    ? avatarUrl
                                                    : avatar;

                                            sharedPrefManager.saveUserData(ClockActivity.this.accessToken, String.valueOf(ClockActivity.this.userId), firstName, lastName, email, finalAvatarUrl, password, token);
                                            NotifFilter(ClockActivity.this.accessToken, ClockActivity.this.userId);


                                            Log.d(TAG, "FCM Token: " + token);
                                        });

                            } else {
                                Log.e("ClockActivity", "Invalid user ID: " + ClockActivity.this.userId);
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


    private void initializeViews(String name, int userId, String avatar_path, String accessToken, String avatarUrl) {
        // Initialize views using findViewById
        clockView = findViewById(R.id.analogClock);
        TextView digitalClock = findViewById(R.id.digitalClock);
        checkInButton = findViewById(R.id.check_in);

        digitalClockManager = new DigitalClockManager(digitalClock);
        digitalClockManager.startClock();


        Log.w("ClockActivity", "Access Token: " + accessToken);
        Log.w("ClockActivity", "User ID: " + userId);
        Log.w("ClockActivity", "User  Name: " + name);
        Log.w("ClockActivity", "User Avatar: " + avatarUrl);


        checkInButton.setOnClickListener(v -> {
            if (this.accessToken != null && this.userId > 0) {

                ClockINApiManager.clockIN(5682, 774, 7672, progressBar, this.accessToken, new ClockINApiManager.ApiCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("ClockActivity", "Clock IN Successful");

/*
                        SharedPreferences sharedPreferences = getSharedPreferences("ClockPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("ClockInSuccess", true);
                        editor.apply();

                        Intent intent = new Intent(ClockActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

 */
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.d("ClockActivity", "Clock in Error  " + error);
                    }
                });


                SharedPreferences sharedPreferences = getSharedPreferences("ClockPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("ClockInSuccess", true);
                editor.apply();
                Intent intent = new Intent(ClockActivity.this, MainActivity.class);
                intent.putExtra("username", name);
                intent.putExtra("user_id", userId);
                intent.putExtra("avatar", avatar_path);
                startActivity(intent);
                finish();

            } else {
                Log.e("ClockActivity", "Access token or user ID is missing.");
            }
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
                    int id =  notification.getId();

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
        int notificationId = (int) System.currentTimeMillis();

        Intent intent = new Intent(context, MainActivity.class);
        SharedPreferences sharedPreferences = context.getSharedPreferences("showNotificationPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notification_displayed", true);
        editor.apply();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

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
                                .setContentIntent(pendingIntent) // Set the PendingIntent
                                .setAutoCancel(true) // Automatically cancel the notification when clicked
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
                                .setContentIntent(pendingIntent) // Set the PendingIntent
                                .setAutoCancel(true)
                                .build();

                        notificationManager.notify(notificationId, notification);
                    }
                });
    }


}

