package com.example.cq_mobile.LoginFolder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cq_mobile.FirebaseUserData.FirebaseDataManager;
import com.example.cq_mobile.FirebaseUserData.FirebaseDatabaseManager;
import com.example.cq_mobile.HelperManagers.Animation.ClickAnimationManager;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.ServerDataReconnect;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.OfflineDataFolder.NetworkManager;
import com.example.cq_mobile.R;
import com.google.firebase.messaging.FirebaseMessaging;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class Login extends AppCompatActivity {
    static final String TAG = "ClockFragment";
    private static final String LOGIN_URL = "https://cqbms.app/api/m/login";
    private static final String API_KEY = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private EditText emailField, passwordField;
    private TextView loginButton;
    private ProgressBar progressBar;
    private ImageView showPasswordToggle;
    private NetworkManager networkManager;
    String email;
    String password;
    String responseBody;
    String token;
    SharedPrefManager sharedPrefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        networkManager = new NetworkManager(this);
        if (!networkManager.isConnected()) {
            networkManager.showNoConnectionDialog();
            return;
        }

        emailField = findViewById(R.id.emailInput);
        passwordField = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
        showPasswordToggle = findViewById(R.id.showPasswordToggle);

        sharedPrefManager = new SharedPrefManager(Login.this);

        showPasswordToggle.setOnClickListener(v -> {
            if (passwordField.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                showPasswordToggle.setImageResource(R.drawable.baseline_visibility_24);
            } else {
                passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                showPasswordToggle.setImageResource(R.drawable.baseline_visibility_off_24);
            }
            passwordField.setSelection(passwordField.getText().length());
        });

        loginButton.setOnClickListener(v -> {
            ClickAnimationManager.applyClickAnimation(v);
            emailField.setError(null);
            passwordField.setError(null);
            email = emailField.getText().toString().trim();
            password = passwordField.getText().toString().trim();

            if (email.isEmpty()) {
                emailField.setError("Email is required");
                return;
            }
            if (password.isEmpty()) {
                passwordField.setError("Password is required");
                return;
            }

            performLogin(email, password);
        });



    }

    private void performLogin(String email, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            return;  // Return early if JSON creation fails
        }

        Log.d("Login", "Request Payload: " + jsonObject.toString());

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .addHeader("x-api-key", API_KEY)
                .post(body)
                .build();

        Log.d("Login", "Request Headers: " + request.headers());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("Login", "Network Error: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(Login.this, "Network Error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : "No Response Body";
                Log.d("Login", "Response Code: " + response.code());
                Log.d("Login", "Response Headers: " + response.headers());
                Log.d("Login", "Response Body: " + responseBody);

                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        if (validateInputs(email, password)) {
                            try {
                                JSONObject jsonResponse = new JSONObject(responseBody);
                                String accessToken = jsonResponse.optString("access_token");
                                JSONObject userObject = jsonResponse.optJSONObject("user");
                                String userId = userObject != null ? userObject.optString("id") : null;
                                String firstName = userObject != null ? userObject.optString("first_name") : null;
                                String lastName = userObject != null ? userObject.optString("last_name") : null;
                                String avatar = userObject != null ? userObject.optString("avatar") : null;

                                Log.w("Login", "accessToken: " + accessToken);
                                Log.w("Login", "userId: " + userId);
                                Log.w("Login", "firstName: " + firstName);
                                Log.w("Login", "lastName: " + lastName);
                                Log.w("Login", "avatar: " + avatar);

                                // Save credentials using AuthManager
                                AuthManager authManager = AuthManager.getInstance(Login.this);

                                ServerDataReconnect serverDataReconnect = new ServerDataReconnect(getApplicationContext());
                                serverDataReconnect.saveLoginData(email, password);

                                // Set expiration time as part of token save
                                long expirationTime = System.currentTimeMillis() + 3600000;
                                authManager.saveToken(accessToken, Integer.parseInt(userId), firstName, lastName, email, avatar, expirationTime);

                                progressBar.setVisibility(View.VISIBLE);

                                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    sharedPrefManager.saveIsLoggedIn(true);

                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                        Intent intent = new Intent(Login.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }, 1000);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                runOnUiThread(() -> Toast.makeText(Login.this, "Failed to parse response", Toast.LENGTH_SHORT).show());
                            }
                        }
                    });

                } else {
                    String errorBody = response.body() != null ? response.body().string() : "No Response Body";
                    Log.e("Login", "Login Failed: " + response.message());
                    Log.e("Login", "Error Response Body: " + errorBody);

                    if (response.code() == 422) {
                        runOnUiThread(() -> {
                            Toast.makeText(Login.this, "Invalid credentials, please try again.", Toast.LENGTH_SHORT).show();
                            sharedPrefManager.clearUserId();
                            sharedPrefManager.clearUserName();
                            sharedPrefManager.clearAddress();
                            sharedPrefManager.clearAvatarUrl();
                            sharedPrefManager.clearEmail();
                            sharedPrefManager.clearPassword();
                            sharedPrefManager.clearCoordinates();
                            sharedPrefManager.clearStartJob();
                            sharedPrefManager.clearStartJobMessage();
                            sharedPrefManager.clearJobTrackingData();
                            sharedPrefManager.clearStartDate();
                            sharedPrefManager.clearStopDate();
                            networkManager.loginAPIUnauthenticatedDialog(Login.this);
                        });

                    } else {
                        runOnUiThread(() -> networkManager.loginAPINoConnectionDialog(response.message()));
                    }
                }
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



}


/*
      FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                                    if (!task.isSuccessful()) {
                                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                        return;
                                    }
                                     token = task.getResult();
                                    sharedPrefManager.saveNewNotificationToken(token);
                                    Log.d(TAG, "FCM Token: " + token + ": saved -->");
                                });



                                FirebaseDataManager firebaseDataManager = new FirebaseDataManager(userId);
                                    firebaseDataManager.saveUserData(
                                            accessToken != null ? accessToken : "",
                                            userId,
                                            avatar != null ? avatar : "",
                                            firstName != null ? firstName : "",
                                            lastName != null ? lastName : "",
                                            token);


 */

/*
        IDsManager.fetchJobIdPaginated(accessToken, new IDsManager.ApiResponseCallback<Taskmain>() {
            @Override
            public void onDataFetched(List<Taskmain> data) {
                if (data != null && !data.isEmpty()) {

                    for (Taskmain task : data) {
                        Log.w("Login", ">>>>>> UseDetails_JobID <<<<<<< " );
                        Log.w("Login", ">>>>>> ID_Job <<<<<<< " + task.getId());
                        Log.w("Login", ">>>>>> jobID <<<<<<< " + task.getJobId());
                        Log.w("Login", ">>>>>> Name <<<<<<< " + task.getName());
                        Log.w("Login", ">>>>>> Category <<<<<<< " + task.getCategory());
                        Log.w("Login", ">>>>>> Status <<<<<<< " + task.getStatus());
                        Log.w("Login", ">>>>>> Start_date <<<<<<< " + task.getStart_date());
                        Log.w("Login", ">>>>>> End_date <<<<<<< " + task.getEnd_date());

                        String userName =  firstName + " " + lastName;
                        sharedPrefManager.saveAccessToken(accessToken);
                        sharedPrefManager.saveUserId(userId);
                        sharedPrefManager.saveUserName(userName);
                        sharedPrefManager.saveFirstName(firstName);
                        sharedPrefManager.saveLastName(lastName);
                        sharedPrefManager.saveCredentials(email, password);

                        sharedPrefManager.saveJobId(task.getJobId());


                        if (task.getAddress() != null) {
                            Taskmain.Address addr = task.getAddress();
                            Log.w("Login", "  >>>>>>  Address  <<<<<<< " + addr.getAddress() + ", " + addr.getCity() + ", " + addr.getCountry());
                            String AddressData = addr.getAddress() + ", " + addr.getCity() + ", " + addr.getCountry();
                            sharedPrefManager.saveAddress(AddressData);


                        }

                        // Client details
                        if (task.getClient_details() != null) {
                            Taskmain.ClientDetails client = task.getClient_details();

                            Log.w("Login", "  >>>>>>  User Name  <<<<<<<  " + client.getFirst_name() + " " + client.getLast_name() + " (" + client.getCompany() + ")");
                        }

                        // Coordinates
                        List<Taskmain.Coordinates> coordinatesList = new ArrayList<>();

                        for (Taskmain taskmain : data) {
                            if (taskmain.getCoordinates() != null) {
                                Taskmain.Coordinates coords = taskmain.getCoordinates();

                                Log.w("Login", "  >>>>>>  Coordinates object  <<<<<<<  " + coords);
                                Log.w("Login", "  >>>>>>  Coordinates Latitude  <<<<<<< " + coords.getLatitude());
                                Log.w("Login", "  >>>>>>  Coordinates Longitude  <<<<<<< " + coords.getLongitude());

                                double latitude = coords.getLatitude();
                                double longitude = coords.getLongitude();

                                if (!Double.isNaN(latitude) && !Double.isNaN(longitude)) {
                                    Log.d("Login", "Coordinates: Lat=" + latitude + ", Lon=" + longitude);

                                    // Add to the list
                                    coordinatesList.add(new Taskmain.Coordinates(latitude, longitude));
                                } else {
                                    Log.e("Login", "Error: Latitude or Longitude is NaN. Skipping this entry.");
                                }
                            } else {
                                Log.e("Login", "Error: task.getCoordinates() is null! Skipping this entry.");
                            }
                        }

                        sharedPrefManager.saveCoordinatesList(coordinatesList);



                        IDsManager.fetchTaskIdDataPaginated(String.valueOf(task.getId()), 1, 10, accessToken, new IDsManager.ApiResponseCallback<SubTask>() {
                            @Override
                            public void onDataFetched(List<SubTask> data) {
                                if (data != null && !data.isEmpty()) {

                                    for (SubTask subTask : data) {
                                        navigateToHome(accessToken, userId, firstName, lastName, email,password,progressBar,avatar,task.getId(),subTask.getId());
                                        sharedPrefManager.saveTaskId(subTask.getId());

                                    }
                                    progressBar.setVisibility(View.GONE);
                                    Log.d("Login", "Check Data: >> IDsManager <<  -> " + data);
                                } else {

                                    Log.e("Login", "Error: >> IDsManager.fetchTaskIdDataPaginated <<  -> " + userId);
                                    navigateToHome(accessToken, userId, firstName, lastName, email,password,progressBar,avatar,task.getId(),-1);
                                    progressBar.setVisibility(View.GONE);
                                }


                            }

                            @Override
                            public void onError(String error) {
                                Log.w("Login", "Error: -> " + userId +" <-  "+  error);
                                progressBar.setVisibility(View.GONE);
                                navigateToHome(accessToken, userId, firstName, lastName, email,password,progressBar,avatar,task.getId(),-1);


                            }
                        });


                    }
                }
            }


            @Override
            public void onError(String error) {
                Log.d("ClockActivity", "Handling Null data: " + error);

                progressBar.setVisibility(View.GONE);
            }
        });

 */

