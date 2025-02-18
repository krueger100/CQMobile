package com.example.cq_mobile.TestFolder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cq_mobile.Clock.ClockActivity;
import com.example.cq_mobile.LoginFolder.ApiService;
import com.example.cq_mobile.LoginFolder.Login;
import com.example.cq_mobile.LoginFolder.LoginRequest;
import com.example.cq_mobile.R;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Test extends AppCompatActivity {

    private EditText emailField, passwordField;
    private TextView loginButton;
    private ProgressBar progressBar;

    private static final String BASE_URL = "https://aws.customquoter.co.uk/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Hide the ActionBar if present
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

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
             //   performLogin(email, password);
            }
        });
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

    /*
    private void performLogin(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);

        // Set up logging interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Add logging interceptor to OkHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        // Set up Retrofit with logging
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)  // Use the OkHttpClient with logging
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        LoginRequest loginRequest = new LoginRequest(email, password);
        Log.d("Login_act", "email%PASS " + email + password);

        Call<Void> call = apiService.login(loginRequest);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);

                // Log the response for debugging
                if (response.isSuccessful()) {
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", true);  // Store the login status
                    editor.apply();
                    Log.d("Login", "Login successful, Response: " + response.message());
                    Log.d("Login", "Credentials are correct: Email: " + email + " and Password: " + password);
                    navigateToHome();
                } else {
                    Log.e("Login", "Login failed, Response code: " + response.code() + ", Message: " + response.message());
                    handleErrorResponse(response);
                    Log.d("Login", "Credentials are incorrect: Email: " + email + " and Password: " + password);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("Login", "Login failed, Error: " + t.getMessage());
                Toast.makeText(Test.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

     */

    private void handleErrorResponse(Response<Void> response) {
        // Handle error response, show error message
        Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
    }

    private void navigateToHome() {
        // Navigate to the home screen or dashboard
        Intent intent = new Intent(this, ClockActivity.class);
        startActivity(intent);
        finish();
    }
}