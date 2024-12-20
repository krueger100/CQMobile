package com.example.cq_mobile;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.example.cq_mobile.HelperManagers.NavigationManager;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.HelperManagers.StatusBarManager;
import com.example.cq_mobile.databinding.ActivityMainBinding;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenApiService;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenResponse;
import com.example.cq_mobile.HelperManagers.getAccessToken.RetrofitClientAccessToken;
import com.google.firebase.FirebaseApp;

import androidx.drawerlayout.widget.DrawerLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationManager navigationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseApp.initializeApp(this);
        StatusBarManager.setStatusBarLight(this);

        drawerLayout = binding.drawerLayout;
        navigationManager = new NavigationManager(this, binding.navView, binding.navViewDrawer, drawerLayout);


        navigationManager.setupNavigation();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navigationManager.onSupportNavigateUp();
    }
}
