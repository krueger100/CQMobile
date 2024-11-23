package com.example.cq_mobile;

import static retrofit2.converter.gson.GsonConverterFactory.*;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;


import com.example.cq_mobile.API_InterfaceFolder.ApiClient;
import com.example.cq_mobile.API_InterfaceFolder.ApiService;

import com.example.cq_mobile.API_InterfaceFolder.LoginRequest;
import com.example.cq_mobile.API_InterfaceFolder.LoginResponse;
import com.example.cq_mobile.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;
import com.google.gson.Gson;

import androidx.drawerlayout.widget.DrawerLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationManager navigationManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseApp.initializeApp(this);

        drawerLayout = binding.drawerLayout;
        navigationManager = new NavigationManager(this, binding.navView, binding.navViewDrawer, drawerLayout);
        navigationManager.setupNavigation();
        // Initialize Retrofit and make the login API call
        loginUser();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navigationManager.onSupportNavigateUp();
    }

    private void loginUser() {

        // Initialize Retrofit and ApiService
        apiService = ApiClient.getClient().create(ApiService.class);

        // Create a sample login request
        LoginRequest loginRequest = new LoginRequest("sag.gonzales@gmail.com", "P@ssw0rd1234");

        // Make the login API call
        Call<String> call = apiService.login(loginRequest);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    // Log the raw response body
                    String responseBody = response.body();
                    Log.d("API Response", "Response: " + responseBody);

                    // Check if the response is valid JSON or not
                    try {
                        // Attempt to parse the response (assuming it's supposed to be JSON)
                        Gson gson = new Gson();
                        LoginResponse loginResponse = gson.fromJson(responseBody, LoginResponse.class);
                        Log.d("Login Response", "Message: " + loginResponse.getMessage());
                    } catch (Exception e) {
                        // Handle any JSON parsing errors
                        Log.e("Login Error", "Invalid JSON response: " + responseBody);
                    }
                } else {
                    // Log the error response message
                    Log.e("API Error", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log any failure in the network request
                Log.e("API Failure", "Request failed: " + t.getMessage());
            }
        });
    }

}
