package com.example.cq_mobile.NotificationData;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.R;

import java.util.ArrayList;
import java.util.List;
public class ShowNotificationActivity extends AppCompatActivity {
    private ShowNotificationsManager showNotificationsManager;
    private static final String TAG = "ShowNotificationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_notification);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        if (recyclerView == null) {
            Log.e(TAG, "RecyclerView is not initialized");
            return; // Prevent crash
        }
        // Retrieve data from the Intent
        Intent intent = getIntent();
        List<String> teamNames = intent.getStringArrayListExtra("teamNames");
        List<String> teamAvatars = intent.getStringArrayListExtra("teamAvatars");

        SharedPreferences sharedPreferences = getSharedPreferences("NotificationPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notification_displayed", false);
        editor.apply();

        // Null check before passing to the manager
        if (teamNames == null || teamAvatars == null ) {
            Log.e(TAG, "One or more required lists are null");
            // Handle the null case here (e.g., show a message to the user or pass empty lists)
            teamNames = teamNames != null ? teamNames : new ArrayList<>();
            teamAvatars = teamAvatars != null ? teamAvatars : new ArrayList<>();
            Log.e(TAG, "teamNames" + teamNames +"\n"+ teamAvatars);
        }

        // Now it's safe to pass the lists to ShowNotificationsManager
        showNotificationsManager = new ShowNotificationsManager(this, recyclerView, teamNames, teamAvatars);
    }
}
