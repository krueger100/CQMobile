package com.example.cq_mobile.LoginFolder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cq_mobile.Clock.ClockActivity;
import com.example.cq_mobile.HelperManagers.Animation.ClickAnimationManager;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenApiService;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenResponse;
import com.example.cq_mobile.HelperManagers.getAccessToken.RetrofitClientAccessToken;
import com.example.cq_mobile.OfflineDataFolder.NetworkManager;
import com.example.cq_mobile.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private EditText emailField, passwordField;
    private TextView loginButton;
    private ProgressBar progressBar;
    String email;
    String password;
    private static final String BASE_URL = "https://aws.customquoter.co.uk/";
    private NetworkManager networkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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


        // Initialize UI components for the login screen
        emailField = findViewById(R.id.emailInput);
        passwordField = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);

        EditText passwordInput = findViewById(R.id.passwordInput);
        ImageView showPasswordToggle = findViewById(R.id.showPasswordToggle);
        showPasswordToggle.setOnClickListener(v -> {
            if (passwordInput.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                showPasswordToggle.setImageResource(R.drawable.baseline_visibility_24); // Eye open icon
            } else {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                showPasswordToggle.setImageResource(R.drawable.baseline_visibility_off_24);
            }
            passwordInput.setSelection(passwordInput.getText().length());
        });



        // Set login button click listener
        loginButton.setOnClickListener(v -> {
            ClickAnimationManager.applyClickAnimation(v);
            email = emailField.getText().toString().trim();
             password = passwordField.getText().toString().trim();
            AccessTokenRequest request = new AccessTokenRequest(email, password);
            progressBar.setVisibility(View.VISIBLE);
            if (validateInputs(email, password)) {
                getAccessToken(request);

            }
        });



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

                        String avatar = accessTokenResponse.getUser().getAvatar() != null
                                ? accessTokenResponse.getUser().getAvatar()
                                : "N/A";

                        if (userId > 0) {
                            Log.d("Login", "Access Token: " + accessToken);
                            Log.d("Login", "User ID: " + userId);
                            Log.d("Login", "User First Name: " + firstName);
                            Log.d("Login", "User Last Name: " + lastName);
                            Log.d("Login", "User Email: " + email);
                            Log.d("Login", "User Password: " + password);
                            Log.d("Login", "Avatar: " + avatar);


                            // Save user data to SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("accessToken", accessToken);
                            editor.putString("userId", String.valueOf(userId));
                            editor.putString("firstName", firstName);
                            editor.putString("lastName", lastName);
                            editor.putString("email", email);
                            editor.putString("password", password);
                            editor.putString("avatar", avatar);
                            editor.putBoolean("isLoggedIn", true);
                            editor.apply();

                            navigateToHome(accessToken, userId, firstName, lastName, email,password,progressBar,avatar);  // Pass data here
                        } else {
                            Log.e("Login", "Invalid user ID: --->>> " + userId);
                            networkManager.loginAPINullUserDialog();
                            progressBar.setVisibility(View.GONE);
                        }
                    } else {
                        Log.e("Login", "User data is null");
                       networkManager.loginAPINullUserDialog();
                        progressBar.setVisibility(View.GONE);
                    }
                } else {
                    Log.e("Login", "Error:  --->>> " + response.message());
                    networkManager.loginAPINoConnectionDialog(response.message());
                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<AccessTokenResponse> call, Throwable t) {
                // Log the failure (e.g., network error)
                Log.e("Login", "Failure: --->>> " + t.getMessage());
                networkManager.loginAPIFailedDialog(t.getMessage());
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

    private void navigateToHome(String accessToken, int userId, String firstName, String lastName, String email, String password, ProgressBar progressBar, String avatar) {
        progressBar.setVisibility(View.GONE);
        Intent intent = new Intent(this, ClockActivity.class);
        intent.putExtra("accessToken", accessToken);
        intent.putExtra("userId", userId);
        intent.putExtra("firstName", firstName);
        intent.putExtra("lastName", lastName);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        intent.putExtra("avatar", avatar);
        startActivity(intent);
        finish();
    }
}
