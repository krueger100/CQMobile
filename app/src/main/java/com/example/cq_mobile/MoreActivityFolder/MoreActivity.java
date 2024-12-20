package com.example.cq_mobile.MoreActivityFolder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.LogoutFolder.LogoutManager;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;

public class MoreActivity extends AppCompatActivity {
TextView back,name,loginButton;
ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        back = findViewById(R.id.back);
        name= findViewById(R.id.name);
        loginButton= findViewById(R.id.loginButton);
        progressBar= findViewById(R.id.progressBar);

        SharedPrefManager sharedPrefManager = new SharedPrefManager(MoreActivity.this);
        String accessToken = sharedPrefManager.getAccessToken();
        String userId = sharedPrefManager.getUserId();
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

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                LogoutManager.logoutUser(getApplicationContext());

            }
        });
    }}
