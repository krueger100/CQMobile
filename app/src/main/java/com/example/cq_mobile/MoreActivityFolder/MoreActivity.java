package com.example.cq_mobile.MoreActivityFolder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.cq_mobile.HelperManagers.Animation.ClickAnimationManager;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.LogoutFolder.LogoutManager;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;

public class MoreActivity extends AppCompatActivity {
    TextView back, name, logoutButton;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        back = findViewById(R.id.back);
        name= findViewById(R.id.name);
        logoutButton= findViewById(R.id.logoutButton);
        progressBar= findViewById(R.id.progressBar);


        SharedPrefManager sharedPrefManager = new SharedPrefManager(MoreActivity.this);
        String accessToken = sharedPrefManager.getAccessToken();
        int userId = sharedPrefManager.getUserId();
        String firstName = sharedPrefManager.getFirstName();
        String lastName = sharedPrefManager.getLastName();
        String email = sharedPrefManager.getEmail();


        Log.d("MoreActivity", "Retrieved User Data: ");
        Log.d("MoreActivity", "Access Token: " + accessToken);
        Log.d("MoreActivity", "User ID: " + userId);
        Log.d("MoreActivity", "First Name: " + firstName);
        Log.d("MoreActivity", "Last Name: " + lastName);
        Log.d("MoreActivity", "Email: " + email);


        name.setText(firstName +" "+lastName);



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
                SharedPreferences sharedPreferencesClockout = getSharedPreferences("ClockPrefs", Context.MODE_PRIVATE);
                sharedPreferencesClockout.edit().clear().apply();




            }
        });




    }


}
