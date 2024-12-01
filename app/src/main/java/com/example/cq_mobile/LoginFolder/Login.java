package com.example.cq_mobile.LoginFolder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cq_mobile.Clock.ClockActivity;
import com.example.cq_mobile.HelperManagers.FullscreenManager;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class Login extends AppCompatActivity {

    private EditText emailField, passwordField;
    private TextView loginButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Hide the ActionBar if present
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        // Check if user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // If user is logged in, navigate to the home screen
            navigateToHome();
        } else {
            // Initialize UI components for the login screen
            emailField = findViewById(R.id.emailInput);
            passwordField = findViewById(R.id.passwordInput);
            loginButton = findViewById(R.id.loginButton);
            progressBar = findViewById(R.id.progressBar);

            // Set login button click listener
            loginButton.setOnClickListener(v -> {
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();

                if (validateInputs(email, password)) {
                    performLogin(email, password);
                }
            });
        }
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty()) {
            emailField.setError("Email is required");
            return false;
        }
        if (password.isEmpty()) {
            passwordField.setError("Password is required");
            return false;
        }
        return true;
    }

    private void performLogin(String email, String password) {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Create Retrofit instance and make the login call
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<ResponseBody> call = apiService.loginUser(email, password);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    // Get cookies and CSRF token from the response headers
                    String cookies = response.headers().get("Set-Cookie");
                    String csrfToken = response.headers().get("X-CSRF-Token");

                    // Save cookies and CSRF token in SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", true);  // Store the login status
                    editor.putString("cookies", cookies);  // Store cookies
                    editor.putString("csrfToken", csrfToken);  // Store CSRF token
                    editor.apply();

                    // Navigate to home screen
                    Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    navigateToHome();
                } else {
                    // Handle server errors
                    String errorBody = null;
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e("LoginActivity", "Error reading error body", e);
                    }
                    Log.e("LoginActivity", "Server error: " + response.code() + " - " + errorBody);
                    Toast.makeText(Login.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                // Log the error
                Log.e("LoginActivity", "Request failed", t);

                // Show failure message
                Toast.makeText(Login.this, "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToHome() {
        // Navigate to the home screen or dashboard
        Intent intent = new Intent(this, ClockActivity.class);
        startActivity(intent);
        finish();
    }
}


