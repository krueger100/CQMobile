package com.example.cq_mobile;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.example.cq_mobile.HelperManagers.NavigationManager;
import com.example.cq_mobile.HelperManagers.StatusBarManager;
import com.example.cq_mobile.databinding.ActivityMainBinding;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenApiService;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenResponse;
import com.example.cq_mobile.HelperManagers.getAccessToken.RetrofitClientAccessToken;
import com.google.firebase.FirebaseApp;

import androidx.drawerlayout.widget.DrawerLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationManager navigationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseApp.initializeApp(this);
        StatusBarManager.setStatusBarLight(this);

        drawerLayout = binding.drawerLayout;
        navigationManager = new NavigationManager(this, binding.navView, binding.navViewDrawer, drawerLayout);

// Example email and password
        String email = "richard.anthony.wetherell@gmail.com";
        String password = "123456";

        // Create request object
        AccessTokenRequest request = new AccessTokenRequest(email, password);

        // Call the API to get the access token
        getAccessToken(request);
        // Set up navigation
        navigationManager.setupNavigation();
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
                if (response.isSuccessful()) {
                    AccessTokenResponse accessTokenResponse = response.body();
                    if (accessTokenResponse != null) {
                        Log.d("LoginActivity", "Access Token: " + accessTokenResponse.getAccessToken());
                        if (accessTokenResponse.getUser() != null) {
                            Log.d("LoginActivity", "User ID: " + accessTokenResponse.getUser().getId());
                            Log.d("LoginActivity", "User First Name: " + accessTokenResponse.getUser().getFirstName());
                            Log.d("LoginActivity", "User Last Name: " + accessTokenResponse.getUser().getLastName());
                            Log.d("LoginActivity", "User Email: " + accessTokenResponse.getUser().getEmail());
                        }
                    }
                } else {
                    Log.e("LoginActivity", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<AccessTokenResponse> call, Throwable t) {
                // Log the failure (e.g., network error)
                Log.e("MainActivity", "Failure: " + t.getMessage());
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navigationManager.onSupportNavigateUp();
    }
}
