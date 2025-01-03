package com.example.cq_mobile;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.cq_mobile.HelperManagers.NavigationManager;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.HelperManagers.StatusBarManager;
import com.example.cq_mobile.NotificationData.NotificationApiManager;
import com.example.cq_mobile.NotificationData.NotificationResponse;
import com.example.cq_mobile.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationManager navigationManager;

    private static final String TAG = "MainActivity";
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase and set status bar style
        FirebaseApp.initializeApp(this);
        StatusBarManager.setStatusBarLight(this);

        // Set up drawer and navigation manager
        drawerLayout = binding.drawerLayout;
        navigationManager = new NavigationManager(this, binding.navView, binding.navViewDrawer, drawerLayout);

        // Request notification permissions if required
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                );
            } else {
                initializeApp();
            }
        } else {
            initializeApp();
        }
    }

    private void initializeApp() {
        // Retrieve access token and user ID from shared preferences
        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
        String accessToken = sharedPrefManager.getAccessToken();
        String userId = sharedPrefManager.getUserId();

        // Log retrieved user data
        Log.d(TAG, "Retrieved User Data: ");
        Log.d(TAG, "Access Token: " + accessToken);
        Log.d(TAG, "User ID: " + userId);

        // Call API and fetch data
        fetchNotificationData(getApplicationContext(), accessToken);

        // Setup navigation
        navigationManager.setupNavigation();
    }

    private void fetchNotificationData(Context context, String accessToken) {
        // Call the method to fetch paginated data, passing the context for notifications
        NotificationApiManager.fetchApiDataPaginated(context, accessToken, 1, 10, new NotificationApiManager.ApiResponseCallback() {
            @Override
            public void onDataFetched(List<NotificationResponse.NotificationData> data) {
                // Handle the fetched data
                for (NotificationResponse.NotificationData notification : data) {
                    // Log the notification data
                    Log.d(TAG, "Notification Data: " + notification.toString());
                }
            }

            @Override
            public void onError(String error) {
                // Handle the error
                Log.e(TAG, "Error fetching data: " + error);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT).show();
                initializeApp();
            } else {
                Toast.makeText(this, "Notification permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navigationManager.onSupportNavigateUp();
    }
}
