package com.example.cq_mobile.Clock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.Clock.ViewListFolder.ViewListActivity;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenApiService;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenResponse;
import com.example.cq_mobile.HelperManagers.getAccessToken.RetrofitClientAccessToken;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;
import com.example.cq_mobile.Clock.ClockFolder.ClockView;
import com.example.cq_mobile.Clock.ClockFolder.DigitalClockManager;


import android.widget.ImageView;
import android.widget.TextView;

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
            String saved_accessToken = sharedPreferences.getString("accessToken", null);
            String saved_userId = sharedPreferences.getString("userId", null);
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



        initializeViews();
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
                if (response.isSuccessful() && response.body() != null) {
                    AccessTokenResponse accessTokenResponse = response.body();

                    String accessToken = accessTokenResponse.getAccessToken() != null
                            ? accessTokenResponse.getAccessToken()
                            : "N/A";

                    if (accessTokenResponse.getUser() != null) {
                        int userId = accessTokenResponse.getUser().getId();
                        String firstName = accessTokenResponse.getUser().getFirstName() != null
                                ? accessTokenResponse.getUser().getFirstName()
                                : "N/A";
                        String lastName = accessTokenResponse.getUser().getLastName() != null
                                ? accessTokenResponse.getUser().getLastName()
                                : "N/A";
                        String email = accessTokenResponse.getUser().getEmail() != null
                                ? accessTokenResponse.getUser().getEmail()
                                : "N/A";
                        String avatarUrl = accessTokenResponse.getUser().getAvatar();

                        if (userId > 0) {
                            Log.d("ClockActivity", "Access Token: " + accessToken);
                            Log.d("ClockActivity", "User ID: " + userId);
                            Log.d("ClockActivity", "User First Name: " + firstName);
                            Log.d("ClockActivity", "User Last Name: " + lastName);
                            Log.d("ClockActivity", "User Email: " + email);
                            Log.d("ClockActivity", "avatarUrl: " + avatarUrl);

                            // Save user data to SharedPreferences
                            SharedPrefManager sharedPrefManager = new SharedPrefManager(ClockActivity.this);
                            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                sharedPrefManager.saveUserData(accessToken, String.valueOf(userId), firstName, lastName, email, avatarUrl, password);
                            } else {
                                String defaultAvatarUrl = "2/uploads/contacts/avatars/colleague_avatar_31734941421.png";
                                sharedPrefManager.saveUserData(accessToken, String.valueOf(userId), firstName, lastName, email, defaultAvatarUrl, password);
                            }


                        } else {
                            Log.e("ClockActivity", "Invalid user ID: " + userId);
                        }
                    } else {
                        Log.e("ClockActivity", "User data is null");
                    }
                } else {
                    Log.e("ClockActivity", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<AccessTokenResponse> call, Throwable t) {
                // Log the failure (e.g., network error)
                Log.e("ClockActivity", "Failure: " + t.getMessage());
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

//        if (recyclerView != null) {
//            recyclerView.setHasFixedSize(true);
//            recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        } else {
//            Log.e("ClockActivity", "RecyclerView initialization failed. Check activity_clock.xml.");
//        }

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
}

