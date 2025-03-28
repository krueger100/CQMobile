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
import com.example.cq_mobile.HelperManagers.UKDateTime;
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
import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.NewBuild;
import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.SubTasks.SubTask;
import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.TaskMainFolder.Taskmain;
import com.google.firebase.messaging.FirebaseMessaging;


import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.checkerframework.checker.units.qual.C;

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
                        Log.w("ClockActivity", ">>>>>> ID_Job <<<<<<< " + task.getId());
                        Log.w("ClockActivity", ">>>>>> jobID <<<<<<< " + task.getJobId());
                        Log.w("ClockActivity", ">>>>>> Name <<<<<<< " + task.getName());
                        Log.w("ClockActivity", ">>>>>> Category <<<<<<< " + task.getCategory());
                        Log.w("ClockActivity", ">>>>>> Status <<<<<<< " + task.getStatus());
                        Log.w("ClockActivity", ">>>>>> Start_date <<<<<<< " + task.getStart_date());
                        Log.w("ClockActivity", ">>>>>> End_date <<<<<<< " + task.getEnd_date());

                        sharedPrefManager.saveJobId(task.getJobId());

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
                                String ukTimeStart = UKDateTime.getCurrentUKTimeStart(ClockActivity.this);
                                Log.w("ClockActivity", "ukTimeStart "+ ukTimeStart);
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
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        LogoutManager.logoutUser(getApplicationContext());
                    }, 3000);

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



