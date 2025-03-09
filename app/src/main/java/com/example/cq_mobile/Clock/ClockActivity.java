package com.example.cq_mobile.Clock;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
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

import com.example.cq_mobile.Clock.ClockFolder.ClockInAPIFolder.ClockINApiManager;
import com.example.cq_mobile.Clock.ClockFolder.TicketIDManager;
import com.example.cq_mobile.HelperManagers.Animation.ClickAnimationManager;
import com.example.cq_mobile.HelperManagers.IDSfolder.IDsManager;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenApiService;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenResponse;
import com.example.cq_mobile.HelperManagers.getAccessToken.RetrofitClientAccessToken;
import com.example.cq_mobile.LogoutFolder.LogoutManager;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.OfflineDataFolder.NetworkManager;
import com.example.cq_mobile.R;
import com.example.cq_mobile.Clock.ClockFolder.ClockView;
import com.example.cq_mobile.Clock.ClockFolder.DigitalClockManager;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.SubTasks.SubTask;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.TaskMainFolder.Taskmain;
import com.google.firebase.messaging.FirebaseMessaging;


import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClockActivity extends AppCompatActivity {
    private NetworkManager networkManager;

    private ClockView clockView;
    private DigitalClockManager digitalClockManager;
    private TextView checkInButton, viewListButton;
    String email;
    String password;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;
    private static final String TAG = "ClockActivity";
    ProgressBar progressBar;
    String avatar;
    String token;
    TicketIDManager ticketIDManager;
    SharedPrefManager sharedPrefManager;
    String firstName;
    String lastName;
    private AlertDialog sessionExpiredDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        checkInButton = findViewById(R.id.check_in);
        checkInButton.setEnabled(false);
        checkInButton.setAlpha(0.5f);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // -->>> Check Network Status
        networkManager = new NetworkManager(this);
        if (!networkManager.isConnected()) {
            networkManager.showNoConnectionDialog();
        } else {
            Log.d("ClockActivity", " Device Online" );

        }



         sharedPrefManager = new SharedPrefManager(ClockActivity.this);
            Intent intent = getIntent();

            String accessToken = (intent != null && intent.hasExtra("accessToken")) ? intent.getStringExtra("accessToken") : sharedPrefManager.getAccessToken();
            int userId = (intent != null && intent.hasExtra("userId")) ? intent.getIntExtra("userId", -1) : sharedPrefManager.getUserId();
            firstName = (intent != null && intent.hasExtra("firstName")) ? intent.getStringExtra("firstName") : sharedPrefManager.getFirstName();
            lastName = (intent != null && intent.hasExtra("lastName")) ? intent.getStringExtra("lastName") : sharedPrefManager.getLastName();
            email = (intent != null && intent.hasExtra("email")) ? intent.getStringExtra("email") : sharedPrefManager.getEmail();
            password = (intent != null && intent.hasExtra("password")) ? intent.getStringExtra("password") : sharedPrefManager.getPassword();
            avatar = (intent != null && intent.hasExtra("avatar")) ? intent.getStringExtra("avatar") : sharedPrefManager.getAvatarUrl();

            progressBar = findViewById(R.id.progressBar);
            checkInButton = findViewById(R.id.check_in);

            sharedPrefManager.saveCredentials(email, password);
            sharedPrefManager.saveAvatar(avatar);


            AccessTokenRequest request = new AccessTokenRequest(email, password);
            getAccessToken(request, sharedPrefManager);

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



    private void getAccessToken(AccessTokenRequest request, SharedPrefManager sharedPrefManager) {
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
                               String userName =  firstName + " " + lastName;
                                sharedPrefManager.saveAccessToken(accessToken);
                                sharedPrefManager.saveUserId(userId);

                                sharedPrefManager.saveUserName(userName);
                                sharedPrefManager.saveFirstName(firstName);
                                sharedPrefManager.saveLastName(lastName);
                                sharedPrefManager.saveCredentials(email, password);


                                new Handler(Looper.getMainLooper()).postDelayed(() -> {

                                    UseDetails_JobID(accessToken, userId);
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

                                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                                    if (!task.isSuccessful()) {
                                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                        return;
                                    }

                                    token = task.getResult();
                                    sharedPrefManager.saveNewNotificationToken(token);
                                    //   NotifFilter(accessToken, userId);

                                    Log.d(TAG, "FCM Token: " + token +": saved -->");
                                });

                            } else {
                                Log.e("ClockActivity", "Invalid user ID: " + userId);
                                alertError(progressBar);

                            }
                        } else {
                            Log.e("ClockActivity", "User data is null");
                            progressBar.setVisibility(View.GONE);
                        }


                    } else {
                        if (response.errorBody() != null) {
                            progressBar.setVisibility(View.VISIBLE);
                            alertError(progressBar);
                            Log.e("ClockActivity", "Error body: getAccessToken" + response.errorBody().string());

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

        digitalClockManager = new DigitalClockManager(digitalClock);
        digitalClockManager.startClock();


    }


    private void UseDetails_JobID(String accessToken, int userId) {
        IDsManager.fetchJobIdPaginated(accessToken, new IDsManager.ApiResponseCallback<Taskmain>() {
            @Override
            public void onDataFetched(List<Taskmain> data) {
                if (data != null && !data.isEmpty()) {

                    for (Taskmain task : data) {
                        Log.w("ClockActivity", ">>>>>> UseDetails_JobID <<<<<<< " );
                        Log.w("ClockActivity", ">>>>>> JobID <<<<<<< " + task.getId());
                        Log.w("ClockActivity", ">>>>>> Name <<<<<<< " + task.getName());
                        Log.w("ClockActivity", ">>>>>> Category <<<<<<< " + task.getCategory());
                        Log.w("ClockActivity", ">>>>>> Status <<<<<<< " + task.getStatus());
                        Log.w("ClockActivity", ">>>>>> Start_date <<<<<<< " + task.getStart_date());
                        Log.w("ClockActivity", ">>>>>> End_date <<<<<<< " + task.getEnd_date());





                        sharedPrefManager.saveJobId(task.getId());
                        // Address details
                        if (task.getAddress() != null) {
                            Taskmain.Address addr = task.getAddress();
                            Log.w("ClockActivity", "  >>>>>>  Address  <<<<<<< " + addr.getAddress() + ", " + addr.getCity() + ", " + addr.getCountry());
                           String AddressData = addr.getAddress() + ", " + addr.getCity() + ", " + addr.getCountry();
                            sharedPrefManager.saveAddress(AddressData);


                        }

                        // Client details
                        if (task.getClient_details() != null) {
                            Taskmain.ClientDetails client = task.getClient_details();
                            Log.w("ClockActivity", "  >>>>>>  User Name  <<<<<<<  " + client.getFirst_name() + " " + client.getLast_name() + " (" + client.getCompany() + ")");
                        }

                        // Coordinates
                        List<Taskmain.Coordinates> coordinatesList = new ArrayList<>();

                        for (Taskmain taskmain : data) {
                            if (taskmain.getCoordinates() != null) {
                                Taskmain.Coordinates coords = taskmain.getCoordinates();

                                Log.w("ClockActivity", "  >>>>>>  Coordinates object  <<<<<<<  " + coords);
                                Log.w("ClockActivity", "  >>>>>>  Coordinates Latitude  <<<<<<< " + coords.getLatitude());
                                Log.w("ClockActivity", "  >>>>>>  Coordinates Longitude  <<<<<<< " + coords.getLongitude());

                                double latitude = coords.getLatitude();
                                double longitude = coords.getLongitude();

                                if (!Double.isNaN(latitude) && !Double.isNaN(longitude)) {
                                    Log.d("ClockActivity", "Coordinates: Lat=" + latitude + ", Lon=" + longitude);

                                    // Add to the list
                                    coordinatesList.add(new Taskmain.Coordinates(latitude, longitude));
                                } else {
                                    Log.e("ClockActivity", "Error: Latitude or Longitude is NaN. Skipping this entry.");
                                }
                            } else {
                                Log.e("ClockActivity", "Error: task.getCoordinates() is null! Skipping this entry.");
                            }
                        }

// Save all coordinates at once
                        sharedPrefManager.saveCoordinatesList(coordinatesList);



                        IDsManager.fetchTaskIdDataPaginated(String.valueOf(task.getId()), 1, 10, accessToken, new IDsManager.ApiResponseCallback<SubTask>() {
                            @Override
                            public void onDataFetched(List<SubTask> data) {
                                if (data != null && !data.isEmpty()) {

                                    for (SubTask subTask : data) {

                                        sharedPrefManager.saveTaskId(subTask.getId());

                                        // Enable the button again after success
                                        checkInButton.setEnabled(true);
                                        checkInButton.setAlpha(1.0f);
                                        checkIN(accessToken,userId, task.getId() ,firstName,lastName,email,avatar,password,token,subTask.getId());


                                    }
                                    progressBar.setVisibility(View.GONE);

                                } else {
                                    checkInButton.setEnabled(true);
                                    checkInButton.setAlpha(1.0f);
                                    checkIN(accessToken,userId, task.getId() ,firstName,lastName,email,avatar,password,token,-1);
                                    Log.e("ClockActivity", "Error: >> IDsManager.fetchTaskIdDataPaginated <<  -> " + userId);
                                    progressBar.setVisibility(View.GONE);
                                }


                            }

                            @Override
                            public void onError(String error) {
                                // Enable the button again after success
                                checkInButton.setEnabled(true);
                                checkInButton.setAlpha(1.0f);
                                checkIN(accessToken,userId,  task.getId() ,firstName,lastName,email,avatar,password,token,-1);
                                Log.e("ClockActivity", "Error: -> " + userId +" <-  "+  error);
                                progressBar.setVisibility(View.GONE);

                            }
                        });


                    }
                }
            }


            @Override
            public void onError(String error) {
                Log.d("ClockActivity", "Handling Null data: " + error);
                // Enable the button again after success
                checkInButton.setEnabled(true);
                checkInButton.setAlpha(1.0f);
                checkIN(accessToken, userId,-1,firstName,lastName,email,avatar,password,token,-1);
                progressBar.setVisibility(View.GONE);
            }
        });
    }



    private void checkIN(String accessToken, int userId, int jobId, String firstName, String lastName, String email, String avatar, String password, String token, Integer subTaskId) {
        progressBar.setVisibility(View.GONE);
        if (checkInButton != null) {
            checkInButton.setOnClickListener(v -> {
                if (accessToken != null && !accessToken.isEmpty()) {

                    checkInButton.setEnabled(false);
                    checkInButton.setAlpha(0.5f);
                    if (v != null) {
                        ClickAnimationManager.applyClickAnimation(v);
                    }

                    ClockINApiManager.clockIN(jobId, subTaskId, accessToken, userId, getApplicationContext(), new ClockINApiManager.ApiCallback() {
                        @Override
                        public void onSuccess() {
                            Log.d("ClockActivity", "Clock IN Successful");
                            progressBar.setVisibility(View.GONE);
                            SharedPreferences sharedPreferences = getSharedPreferences("ClockPrefs", MODE_PRIVATE);
                            if (sharedPreferences != null) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("ClockInSuccess", true);
                                editor.apply();
                            }

                            Log.w("ClockActivity", "ticketsIDClockINManager   <<<<<<<<< CHECK IN PRESSED >>>>>>>> ");
                            Log.w("ClockActivity", "ticketsIDClockINManager   <<<<<<<<< ACCESS TOKEN >>>>>>>> "+ "\n "+" ->  "+ accessToken);
                            Log.w("ClockActivity", "ticketsIDClockINManager   <<<<<<<< NOTIFICATION TOKEN >>>>>>>>> "+ "\n "+" ->  "+ token);
                            Log.w("ClockActivity", "ticketsIDClockINManager   <<<<<<<<< USER ID >>>>>>>> "+ "\n "+" ->  "+ userId);
                            Log.w("ClockActivity", "ticketsIDClockINManager   <<<<<<<<<< JOB ID >>>>>>>>> "+ "\n "+" ->  "+ jobId);
                            Log.w("ClockActivity", "ticketsIDClockINManager   <<<<<<<<< SUBTASK  >>>>>>>>>> "+ "\n "+" ->  "+  subTaskId);
                            Log.w("ClockActivity", "ticketsIDClockINManager   <<<<<<<<< AVATAR >>>>>>>>>> "+ "\n "+" ->  "+  avatar);
                            Log.w("ClockActivity", "ticketsIDClockINManager   <<<<<<<<< FIRST NAME >>>>>>>>>> "+ "\n "+" ->  "+  firstName);
                            Log.w("ClockActivity", "ticketsIDClockINManager   <<<<<<<<< LAST NAME >>>>>>>>>> "+ "\n "+" ->  "+  lastName);
                            Log.w("ClockActivity", "ticketsIDClockINManager   <<<<<<<<< EMAIL >>>>>>>>>> "+ "\n "+" ->  "+  email);

                            Intent intent = new Intent(ClockActivity.this, MainActivity.class);
                            intent.putExtra("accessToken", accessToken);
                            intent.putExtra("userId", userId);
                            intent.putExtra("jobId", jobId);
                            intent.putExtra("taskId", subTaskId != null ? subTaskId : -1);
                            intent.putExtra("firstName", firstName);
                            intent.putExtra("lastName", lastName);
                            intent.putExtra("email", email);
                            intent.putExtra("password", password);
                            intent.putExtra("token", token);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onFailure(String error) {
                            Log.d("ClockActivity", "Clock in Error: " + (error != null ? error : "Unknown error"));
                            alertError(progressBar);

                            // Re-enable button after failure
                            checkInButton.setEnabled(true);
                            checkInButton.setAlpha(1.0f);
                        }
                    });

                } else {
                    Log.e("ClockActivity", "Access token is missing.");
                    Toast.makeText(ClockActivity.this, "Access token is required to check in.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });

        } else {
            Log.e("ClockActivity", "Check-In button is null.");
            progressBar.setVisibility(View.GONE);
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


    private void alertError(ProgressBar progressBar) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClockActivity.this);
        builder.setTitle("Session Expired")
                .setMessage("Your session has expired. Please log in again to continue.")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Hide progress bar first
                    progressBar.setVisibility(View.GONE);

                    // Logout user
                    LogoutManager.logoutUser(getApplicationContext());


                    // Delay dismissing the dialog
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        if (sessionExpiredDialog != null && sessionExpiredDialog.isShowing()) {
                            sessionExpiredDialog.dismiss();
                        }
                    }, 2000);
                });

        sessionExpiredDialog = builder.create();
        sessionExpiredDialog.show();
    }


}





/*
    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        Log.d("SplashActivity", "isLoggedIn: " + isLoggedIn);

        SharedPreferences ClockIN = getSharedPreferences("ClockPrefs", MODE_PRIVATE);
        boolean isClockedIn = ClockIN.getBoolean("ClockInSuccess", false);
        if (isClockedIn) {
            Log.d("SplashActivity", "Navigate to Login");
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent;
                if (isLoggedIn) {
                    intent = new Intent(ClockActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(ClockActivity.this, Login.class);
                }
                startActivity(intent);
                finish();
            }, 500);

        } else {
            Log.d("SplashActivity", "User is not clocked in.");
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent;
                if (isLoggedIn) {
                    intent = new Intent(ClockActivity.this, ClockActivity.class);
                } else {
                    intent = new Intent(ClockActivity.this, Login.class);
                }
                startActivity(intent);
                finish();
            }, 500);

        }

 */



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
