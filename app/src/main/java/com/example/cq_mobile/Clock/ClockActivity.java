package com.example.cq_mobile.Clock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.Clock.ViewListFolder.JSONPlaceholder;
import com.example.cq_mobile.Clock.ViewListFolder.RecyclerViewBottomSheetFragment;
import com.example.cq_mobile.HelperManagers.FullscreenManager;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;
import com.example.cq_mobile.Clock.ClockFolder.ClockView;
import com.example.cq_mobile.Clock.ClockFolder.DigitalClockManager;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ClockActivity extends AppCompatActivity {

    private ClockView clockView;
    private DigitalClockManager digitalClockManager;
    private RecyclerView recyclerView;
    private TextView checkInButton, viewListButton;
    private JSONPlaceholder jsonPlaceholder;
    ImageView nav_drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        FullscreenManager.enableFullScreen(getWindow());

        // Hide the ActionBar if present
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        // Initialize your views and other logic
        initializeViews();
    }

    private void initializeViews() {
        // Initialize views using findViewById
        clockView = findViewById(R.id.analogClock);
        TextView digitalClock = findViewById(R.id.digitalClock);
        checkInButton = findViewById(R.id.check_in);
        viewListButton = findViewById(R.id.viewlist);

        // Initialize RecyclerView
        // Initialize DigitalClockManager
        digitalClockManager = new DigitalClockManager(digitalClock);

        // Initialize RecyclerView
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            Log.e("ClockActivity", "RecyclerView initialization failed. Check activity_clock.xml.");
        }

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceholder = retrofit.create(JSONPlaceholder.class);

        // Start the digital clock updates
        digitalClockManager.startClock();

        // Set up Check In button click listener
        checkInButton.setOnClickListener(v -> {
            Log.d("ClockActivity", "Check-in button clicked");
            Intent intent = new Intent(ClockActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Set up View List button click listener
        viewListButton.setOnClickListener(v -> {
            Log.d("ClockActivity", "View-list button clicked");
            RecyclerViewBottomSheetFragment bottomSheetFragment = new RecyclerViewBottomSheetFragment();
            bottomSheetFragment.show(getSupportFragmentManager(), "RecyclerViewBottomSheetFragment");
        });
    }
}