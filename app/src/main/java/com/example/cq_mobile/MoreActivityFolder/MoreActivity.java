package com.example.cq_mobile.MoreActivityFolder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cq_mobile.Clock.ClockFolder.ClockOutFolder.ClockOutManager;
import com.example.cq_mobile.HelperManagers.Animation.ClickAnimationManager;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.LoginFolder.AuthManager;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class MoreActivity extends AppCompatActivity {
    TextView back, name, logoutButton;
    ProgressBar progressBar;
    CircleImageView circleImageView2;
    TextView account_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        back = findViewById(R.id.back);
        name= findViewById(R.id.name);
        logoutButton= findViewById(R.id.logoutButton);
        progressBar= findViewById(R.id.progressBar);
        circleImageView2 = findViewById(R.id.circleImageView2);
        account_setting = findViewById(R.id.account_setting);

        SharedPrefManager sharedPrefManager = new SharedPrefManager(MoreActivity.this);
        String accessToken = AuthManager.getInstance(this).getToken();
        int userId = sharedPrefManager.getUserId();
        String firstName = sharedPrefManager.getFirstName();
        String lastName = sharedPrefManager.getLastName();
        String email = sharedPrefManager.getEmail();
        String avatar = sharedPrefManager.getAvatarUrl();

        int jobId = sharedPrefManager.getJobId();
        int taskId = sharedPrefManager.getTaskId();
        String startDate = sharedPrefManager.getKeyStartDate();


        Log.d("MoreActivity", "Retrieved User Data: ");
        Log.d("MoreActivity", "Access Token: " + accessToken);
        Log.d("MoreActivity", "User ID: " + userId);
        Log.d("MoreActivity", "First Name: " + firstName);
        Log.d("MoreActivity", "Last Name: " + lastName);
        Log.d("MoreActivity", "Email: " + email);
        Log.d("MoreActivity", "avatar: " + avatar);
        Log.d("MoreActivity", "jobId: " + jobId);
        Log.d("MoreActivity", "taskId: " + taskId);
        Log.d("MoreActivity", "startDate: " + startDate);


        name.setText(firstName +" "+lastName);

        SharedPreferences sharedPreferencesClockout = getSharedPreferences("ClockPrefs", Context.MODE_PRIVATE);
        ClockOutManager clockOutManager = new ClockOutManager(MoreActivity.this, progressBar, jobId, taskId, userId, sharedPrefManager);
        boolean jobSuccess = sharedPrefManager.isJobSuccessful();

        if (avatar != null && !avatar.isEmpty()) {
            String baseUrl = "https://customquoteruk-live-uploads.s3.eu-west-2.amazonaws.com/"; // S3 Base URL
            String fullAvatarUrl = baseUrl + avatar; // Construct full image URL

            Log.d("MoreActivity", "Full Avatar URL: " + fullAvatarUrl); // Debugging

            Glide.with(this)
                    .load(fullAvatarUrl)
                    .placeholder(R.drawable.circular_background) // Placeholder image
                    .error(R.drawable.emptyglide) // Error image
                    .into(circleImageView2);
        }

        AccountSettingManager accountSettingManager = new AccountSettingManager(this);

        account_setting.setOnClickListener(v -> {
            ClickAnimationManager.applyClickAnimation(v);
            accountSettingManager.showAccountSettingBottomSheetFragment();
        });



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoreActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });



        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                ClickAnimationManager.applyClickAnimation(v);
                sharedPreferencesClockout.edit().clear().apply();

                if (jobSuccess) {
                    clockOutManager.setupClockOutButtonLogout(logoutButton, accessToken, jobId);

                } else {
                    clockOutManager.setupStopJobWithTimeSheetLogout(logoutButton, accessToken, jobId, taskId, sharedPrefManager,progressBar);

                }
            }
        });




    }


}

