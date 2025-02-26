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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.cq_mobile.Clock.ClockFolder.ClockInAPIFolder.TimerManager;
import com.example.cq_mobile.Clock.ClockFolder.ClockInAPIFolder.TimerService;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.LogoutFolder.LogoutManager;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MoreActivity extends AppCompatActivity {
    TextView back, name, logoutButton;
    ProgressBar progressBar;


    private TextView timerTextView;
    private BroadcastReceiver timerReceiver;
    private FloatingActionButton fab;
    private TimerManager timerManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        back = findViewById(R.id.back);
        name= findViewById(R.id.name);
        logoutButton= findViewById(R.id.loginButton);
        progressBar= findViewById(R.id.progressBar);
        timerTextView = findViewById(R.id.timerTextView);
        fab = findViewById(R.id.fab_timer);
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



        timerManager = TimerManager.getInstance();
        Intent serviceIntent = new Intent(this, TimerService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
        registerTimerReceiver();

        fab.setOnClickListener(v -> {
            timerManager.stopTimer();
            Intent stopIntent = new Intent(this, TimerService.class);
            stopService(stopIntent);
            Toast.makeText(this, "Timer Stopped", Toast.LENGTH_SHORT).show();
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

                SharedPreferences sharedPreferencesClockout = getSharedPreferences("ClockPrefs", Context.MODE_PRIVATE);
                sharedPreferencesClockout.edit().clear().apply();


                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        timerManager.stopTimer();
                        unregisterReceiver(timerReceiver);
                        Intent stopIntent = new Intent(MoreActivity.this, TimerService.class);
                        stopService(stopIntent);
                        LogoutManager.logoutUser(getApplicationContext());
                    }
                }, 2000);


            }
        });
    }

    private void registerTimerReceiver() {
        timerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (TimerService.TIMER_UPDATE_ACTION.equals(intent.getAction())) {
                    String time = intent.getStringExtra("time");
                    timerTextView.setText(time);  // Update the Timer TextView
                }
            }
        };

        IntentFilter filter = new IntentFilter(TimerService.TIMER_UPDATE_ACTION);
        ContextCompat.registerReceiver(MoreActivity.this, timerReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerReceiver != null) {
            unregisterReceiver(timerReceiver);

        }

    }
}
