package com.example.cq_mobile.Clock;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.Clock.ClockFolder.ClockInAPIFolder.ClockINApiManager;
import com.example.cq_mobile.Clock.ClockFolder.TicketIDManager;
import com.example.cq_mobile.HelperManagers.Animation.ClickAnimationManager;
import com.example.cq_mobile.HelperManagers.GlobalData.GlobalDataManager;
import com.example.cq_mobile.HelperManagers.IDSfolder.UserTaskIdData;
import com.example.cq_mobile.HelperManagers.IDSfolder.IDsManager;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenApiService;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenResponse;
import com.example.cq_mobile.HelperManagers.getAccessToken.RetrofitClientAccessToken;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.OfflineDataFolder.NetworkManager;
import com.example.cq_mobile.R;
import com.example.cq_mobile.Clock.ClockFolder.ClockView;
import com.example.cq_mobile.Clock.ClockFolder.DigitalClockManager;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.SubTasks.SubTask;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.TaskMainFolder.Taskmain;
import com.example.cq_mobile.ui.ticket.TicketsFolder.TicketAPIItem;
import com.google.firebase.messaging.FirebaseMessaging;


import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClockActivity extends AppCompatActivity {
    private NetworkManager networkManager;

    private ClockView clockView;
    private DigitalClockManager digitalClockManager;
    private RecyclerView recyclerView;
    private TextView checkInButton, viewListButton;
    ImageView nav_drawer;
    String password;
     String saved_accessToken ;
   int saved_userId;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;
    private static final String NOTIFICATION_CHANNEL_ID = "default_channel";
    private static final String TAG = "ClockActivity";
    ProgressBar progressBar;
    String Name;
    String avatar;
    String token;
    TicketIDManager ticketIDManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }



        // -->>> Check Network Status
        networkManager = new NetworkManager(this);
        if (!networkManager.isConnected()) {
            networkManager.showNoConnectionDialog();
        } else {


        }
        // <<<-- Check Network Status

        Intent intent = getIntent();
        String accessToken = intent.getStringExtra("accessToken");
        int userId = intent.getIntExtra("userId", -1);
        String firstName = intent.getStringExtra("firstName");
        String lastName = intent.getStringExtra("lastName");
        String email1 = intent.getStringExtra("email");
        String password1 = intent.getStringExtra("password");
        avatar = intent.getStringExtra("avatar");
        progressBar = findViewById(R.id.progressBar);
        checkInButton = findViewById(R.id.check_in);

        Log.d("ClockActivity", "Access Token ---->>>>: " + accessToken);
        Log.d("ClockActivity", "User ID:  ---->>>>:" + userId);
        Log.d("ClockActivity", "User First Name: ---->>>>: " + firstName);
        Log.d("ClockActivity", "User Last Name:  ---->>>>:" + lastName);
        Log.d("ClockActivity", "User Email: ---->>>>:" + email1);
        Log.d("ClockActivity", "Avatar URL: ---->>>>:" + password1);
        Log.d("ClockActivity", "Avatar path: ---->>>>:" + avatar);






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


            Log.d("ClockActivity", "Access Token <<<--------------: " + saved_accessToken);
            Log.d("ClockActivity", "User ID:  <<<--------------:" + saved_userId);
            Log.d("ClockActivity", "User First Name: <<<--------------: " + saved_firstName);
            Log.d("ClockActivity", "User Last Name: <<<--------------:" + saved_lastName);
            Log.d("ClockActivity", "User Email: <<<--------------:" + saved_email);
            Log.d("ClockActivity", "Avatar URL: <<<--------------:" + saved_password);
            Log.d("ClockActivity", "Avatar path: <<<--------------:" + avatar);

        } else {
            String email =  email1;
             password = password1;
            String Name = firstName +" "+ lastName;
            AccessTokenRequest request = new AccessTokenRequest(email, password);
            getAccessToken(request,Name,userId,avatar,accessToken);
        }

         Name = firstName +" "+ lastName;

        SharedPreferences sharedPreferences = getSharedPreferences("ClockPrefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();



         ticketIDManager = new TicketIDManager(this);


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
                showPermissionDeniedDialog();
            }
        }
    }

    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("Notification permission is essential for proper functioning. Please enable it in settings.")
                .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }


    private void getAccessToken(AccessTokenRequest request, String name, int userId, String avatar_path, String accessToken) {
        // Create an instance of the API service
        AccessTokenApiService apiService = RetrofitClientAccessToken.getRetrofitInstance().create(AccessTokenApiService.class);

        // Call the API
        Call<AccessTokenResponse> call = apiService.AccessTokenUser(request);
        call.enqueue(new Callback<AccessTokenResponse>() {
            @Override
            public void onResponse(Call<AccessTokenResponse> call, Response<AccessTokenResponse> response) {
                try {
                    // Check if the response is successful
                    if (response.isSuccessful() && response.body() != null) {
                        AccessTokenResponse accessTokenResponse = response.body();

                        String accessToken = accessTokenResponse.getAccessToken() != null
                                ? accessTokenResponse.getAccessToken()
                                : "N/A";

                        // Extract user information safely
                        if (accessTokenResponse.getUser() != null) {
                            AccessTokenResponse.User user = accessTokenResponse.getUser();
                       int userId = user.getId() > 0 ? user.getId() : -1;
                            String firstName = user.getFirstName() != null ? user.getFirstName() : "N/A";
                            String lastName = user.getLastName() != null ? user.getLastName() : "N/A";
                            String email = user.getEmail() != null ? user.getEmail() : "N/A";
                            String avatarUrl = user.getAvatar();

                            if (userId > 0) {
                                Log.d("ClockActivity", "Access Token ---->>>>: " + accessToken);
                                Log.d("ClockActivity", "User ID: " + userId);
                                Log.d("ClockActivity", "User First Name: " + firstName);
                                Log.d("ClockActivity", "User Last Name: " + lastName);
                                Log.d("ClockActivity", "User Email: " + email);
                                Log.d("ClockActivity", "Avatar URL: " + avatarUrl);
                                Log.d("ClockActivity", "Avatar path: " + avatar_path);
                                String name = firstName +" "+ lastName;

                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                    UseDetails_JobID(name,userId,  avatarUrl,  accessToken,  avatarUrl);

                                }, 1000);



                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    if (ContextCompat.checkSelfPermission(ClockActivity.this, Manifest.permission.POST_NOTIFICATIONS)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(
                                                ClockActivity.this,
                                                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                                                NOTIFICATION_PERMISSION_REQUEST_CODE
                                        );
                                    } else {
                                        initializeViews();
                                    }
                                } else {
                                    initializeViews();

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

                                            sharedPrefManager.saveUserData(accessToken, String.valueOf(userId), firstName, lastName, email, finalAvatarUrl, password, token);
                                //            NotifFilter(accessToken, userId);


                                            Log.d(TAG, "FCM Token: " + token);
                                        });

                            } else {
                                Log.e("ClockActivity", "Invalid user ID: " + userId);
                                progressBar.setVisibility(View.GONE);
                            }
                        } else {
                            Log.e("ClockActivity", "User data is null");
                            progressBar.setVisibility(View.GONE);
                        }
                    } else {
                        // Log unsuccessful response or null body
                        Log.e("ClockActivity", "API Error: getAccessToken" + response.message());
                        progressBar.setVisibility(View.GONE);
                        if (response.errorBody() != null) {
                            Log.e("ClockActivity", "Error body: getAccessToken" + response.errorBody().string());
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    Log.e("ClockActivity", "Exception occurred: getAccessToken" + e.getMessage(), e);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<AccessTokenResponse> call, Throwable t) {
                Log.e("ClockActivity", "API call failed: getAccessToken" + t.getMessage(), t);
                progressBar.setVisibility(View.GONE);
            }

        });


    }


    private void initializeViews() {
        clockView = findViewById(R.id.analogClock);
        TextView digitalClock = findViewById(R.id.digitalClock);
        checkInButton = findViewById(R.id.check_in);

        digitalClockManager = new DigitalClockManager(digitalClock);
        digitalClockManager.startClock();


        // Clear SharedPreferences after successful clock out
        SharedPreferences sharedClockPrefs= ClockActivity.this.getSharedPreferences("ClockPrefs", Context.MODE_PRIVATE);
        sharedClockPrefs.edit().clear().apply();
    }


    private void UseDetails_JobID(String name, int userId, String avatar_path, String accessToken, String avatarUrl) {
        IDsManager.fetchJobIdPaginated(accessToken, new IDsManager.ApiResponseCallback<Taskmain>() {
            @Override
            public void onDataFetched(List<Taskmain> data) {
                if (data != null && !data.isEmpty()) {
                    GlobalDataManager.getInstance().setJobs(data);
                    GlobalDataManager.getInstance().setAccessToken(accessToken);
                    GlobalDataManager.getInstance().setUserId(userId);
                    GlobalDataManager.getInstance().setUserName(name);
                    GlobalDataManager.getInstance().setAvatarPath(avatar_path);
                    for (Taskmain task : data) {
                        Log.w("ClockActivity", "FetchJobId Task ID: " + task.getId());
                        Log.w("ClockActivity", "FetchJobId Name: " + task.getName());
                        Log.w("ClockActivity", "FetchJobId Category: " + task.getCategory());
                        Log.w("ClockActivity", "FetchJobId Status: " + task.getStatus());
                        Log.w("ClockActivity", "FetchJobId Start Date: " + task.getStart_date());
                        Log.w("ClockActivity", "FetchJobId End Date: " + task.getEnd_date());
                        Log.w("ClockActivity", " <<<< -- SAVED -- >>>>  : " + task.getId());

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


                        IDsManager.fetchTaskIdDataPaginated(String.valueOf(task.getId()), 1, 10, accessToken, new IDsManager.ApiResponseCallback<SubTask>() {
                            @Override
                            public void onDataFetched(List<SubTask> data) {
                                if (data != null && !data.isEmpty()) {
                                    GlobalDataManager.getInstance().setSubTasks(data);

                                    UserTaskIdData.getInstance().setSubTasks(data);
                                    UserTaskIdData.getInstance().setAccessToken(accessToken);
                                    UserTaskIdData.getInstance().setUserId(userId);
                                    UserTaskIdData.getInstance().setUserName(name);
                                    UserTaskIdData.getInstance().setAvatarPath(avatar_path);

                                    for (SubTask subTask : data) {
                                        Log.d("ClockActivity", "SubTask ID: " + subTask.getId());
                                        Log.d("ClockActivity", "Title: " + subTask.getTitle());
                                        Log.w("ClockActivity", "ticketsIDClockINManager    ------------->>>>>>>>>>>>>>: " + accessToken+" -- "+ token);


                                        checkIN(accessToken, task.getId(), subTask.getId(), userId);
                                        progressBar.setVisibility(View.GONE);

                                    }
                                } else {
                                    Log.d("ClockActivity", "No sub-tasks fetched.");
                                    checkIN(accessToken, task.getId(), -1, userId);
                                    progressBar.setVisibility(View.GONE);
                                }


                            }

                            @Override
                            public void onError(String error) {
                                Log.e("ClockActivity", "Error: <<--- " + error);


                            }
                        });


                    }
                }
            }


            @Override
            public void onError(String error) {
                Log.w("ClockActivity", "Error  -->>: " + error);
            }
        });
    }



    private void checkIN(String accessToken, int jobId, Integer taskid, int userId) {

        if (checkInButton != null) {
            checkInButton.setEnabled(true);

            checkInButton.setOnClickListener(v -> {
                if (accessToken != null && !accessToken.isEmpty()) {

                    // Apply click animation safely
                    if (v != null) {
                        ClickAnimationManager.applyClickAnimation(v);
                    }


                    // Ensure progressBar is not null before passing

                    // Call the ClockIN API
                    ClockINApiManager.clockIN(jobId, taskid, accessToken, userId, new ClockINApiManager.ApiCallback() {
                        @Override
                        public void onSuccess() {
                            Log.d("ClockActivity", "Clock IN Successful");
                            // Safely store the success flag
                            SharedPreferences sharedPreferences = getSharedPreferences("ClockPrefs", MODE_PRIVATE);
                            if (sharedPreferences != null) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("ClockInSuccess", true);
                                editor.apply();
                                progressBar.setVisibility(View.GONE);
                            }

                            Intent intent = new Intent(ClockActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }


                        @Override
                        public void onFailure(String error) {
                            Log.d("ClockActivity", "Clock in Error: " + (error != null ? error : "Unknown error"));
                            Toast.makeText(ClockActivity.this, "Clock In Failed: " + (error != null ? error : "Unknown error"), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Log.e("ClockActivity", "Access token is missing.");
                    Toast.makeText(ClockActivity.this, "Access token is required to check in.", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Log.e("ClockActivity", "Check-In button is null.");
        }
    }



}









/*



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
                        progressBar.setVisibility(View.VISIBLE);

                    }

                } else {
                    progressBar.setVisibility(View.GONE);
                    UserTaskID(-1, name, userId, avatar_path, accessToken, avatarUrl, ticketsIDClockINManager);

                    Log.d("ClockActivity", "No tasks fetched.");
                }
            }


            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Log.w("ClockActivity", "Error  -->>: " + error);
                Toast.makeText(ClockActivity.this, error +"\n"+ "Press the Clock in to Continue", Toast.LENGTH_SHORT).show();
                checkIN(accessToken, -1, -1, userId, -1);
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
                        Log.d("ClockActivity", "SubTask ID: " + subTask.getId());
                        Log.d("ClockActivity", "Title: " + subTask.getTitle());



                        ticketsIDClockINManager.loadTicketsWithToken(accessToken, 1, 10, new TicketsIDClockINManager.TicketsCallback() {
                            @Override
                            public void onTicketsLoaded(List<TicketAPICategoryItems> tickets) {
                                Log.d("ClockActivity", "Successfully loaded " + tickets.size() + " category tickets.");

                                for (TicketAPICategoryItems ticketAPICategoryItem : tickets) {
                                    Log.d("ClockActivity", "Ticket ID: " + ticketAPICategoryItem.getId());
                                    Log.d("ClockActivity", "Ticket Name: " + ticketAPICategoryItem.getName());
                                }
                            }

                            @Override
                            public void onTicketsWithTokenLoaded(List<TicketAPIItem> ticketsID) {
                                Log.d("ClockActivity", "Successfully loaded " + ticketsID.size() + " tickets with token.");

                                for (TicketAPIItem ticketAPIItem : ticketsID) {
                                    int ticketMessageID = ticketAPIItem.getId();
                                    String name = ticketAPIItem.getCategory().getName();
                                    String subject = ticketAPIItem.getSubject();
                                    String status = ticketAPIItem.getStatus();

                                    checkIN(accessToken, jobId, subTask.getId(), userId, ticketMessageID);
                                    progressBar.setVisibility(View.GONE);
                                    Log.w("ClockActivity", "Ticket Message ID: --->>> " + ticketMessageID);
                                    Log.w("ClockActivity", "Ticket Name: ---->>> " + name);
                                    Log.w("ClockActivity", "Ticket Subject: ---->>> " + subject);
                                    Log.w("ClockActivity", "Ticket Status: ---->>> " + status);
                                }
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Log.e("ClockActivity", "Error:  -->> " + errorMessage);
                                checkIN(accessToken, jobId, subTask.getId(), userId, -1);
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                } else {
                    Log.d("ClockActivity", "No sub-tasks fetched.");
                    checkIN(accessToken, jobId, -1, userId, -1);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String error) {
                Log.e("ClockActivity", "Error: <<--- " + error);
                progressBar.setVisibility(View.GONE);
                checkIN(accessToken, jobId, -1, userId, -1);
            }
        });

    }







----------------------->>>>>>>>>>>>>>






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

 */
