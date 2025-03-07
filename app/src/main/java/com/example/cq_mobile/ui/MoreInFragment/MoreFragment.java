package com.example.cq_mobile.ui.MoreInFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.cq_mobile.Clock.ClockFolder.ClockOutFolder.ClockOutManager;
import com.example.cq_mobile.HelperManagers.Animation.ClickAnimationManager;
import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.ClockOutVisibilityHandler;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.LogoutFolder.LogoutManager;
import com.example.cq_mobile.LogoutFolder.LogoutNotificationManager;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.MoreActivityFolder.AccountSettingManager;
import com.example.cq_mobile.R;
import com.example.cq_mobile.databinding.BottomSheetAccountSettingBinding;
import com.example.cq_mobile.databinding.FragmentMoreBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import de.hdodenhof.circleimageview.CircleImageView;


public class MoreFragment extends Fragment {
    private Context context;

    private FragmentMoreBinding binding;
    private SharedPrefManager sharedPrefManager;

    private ClockOutVisibilityHandler visibilityHandler;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment's layout using view binding
        binding = FragmentMoreBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedPrefManager = new SharedPrefManager(requireContext());

        // Retrieve user data
        String accessToken = sharedPrefManager.getAccessToken();
        int userId = sharedPrefManager.getUserId();
        String userName = sharedPrefManager.getUserName();
        String email = sharedPrefManager.getEmail();
        String password = sharedPrefManager.getPassword();
        String avatar = sharedPrefManager.getAvatarUrl();
        String notificationToken = sharedPrefManager.getNotiftoken();



        Log.d("MoreFragment", "Retrieved User Data: ");
        Log.d("MoreFragment", "Access Token: " + accessToken);
        Log.d("MoreFragment", "User ID: " + userId);
        Log.d("MoreFragment", "UserName: " + userName);
        Log.d("MoreFragment", "Email: " + email);
        Log.d("MoreFragment", "Password: " + password);
        Log.d("MoreFragment", "Avatar: " + avatar);
        Log.d("MoreFragment", "notificationToken: " + notificationToken);

        binding.name.setText(userName);
        context = getContext();





        if (avatar != null && !avatar.isEmpty()) {
            String baseUrl = "https://customquoteruk-live-uploads.s3.eu-west-2.amazonaws.com/"; // S3 Base URL
            String fullAvatarUrl = baseUrl + avatar; // Construct full image URL

            Log.d("MoreActivity", "Full Avatar URL: " + fullAvatarUrl); // Debugging

            Glide.with(this)
                    .load(fullAvatarUrl)
                    .placeholder(R.drawable.circular_background) // Placeholder image
                    .error(R.drawable.emptyglide) // Error image
                    .into(binding.circleImageView);
        }
        binding.accountSetting.setOnClickListener(v -> {
            ClickAnimationManager.applyClickAnimation(v);
            AccountSettingManager accountSettingManager = new AccountSettingManager(context);
            accountSettingManager.showAccountSettingBottomSheetFragment();
        });

        // Set up click listeners
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity
                Intent intent = new Intent(requireContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        binding.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickAnimationManager.applyClickAnimation(v);
                LogoutNotificationManager logoutManager = new LogoutNotificationManager();
                logoutManager.deleteNotificationToken(String.valueOf(userId), new LogoutNotificationManager.LogoutCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("MoreFragment", "Notification token deleted successfully.");
                        binding.progressBar.setVisibility(View.VISIBLE);

                 LogoutManager.logoutUser(requireContext());
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e("MoreFragment", "Failed to delete notification token: " + errorMessage);
                    }
                });

            }
        });


        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ClockOutVisibilityHandler) {
            visibilityHandler = (ClockOutVisibilityHandler) context;
        } else {
            Log.e("MoreFragment", "Activity does not implement ClockOutVisibilityHandler");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (visibilityHandler != null) {
            visibilityHandler.setClockOutVisibility(false);
        }
    }
}

/*
{
  "rules": {
    "users": {
      "$user_id": {
        ".read": "true",  // Allow anyone to read, authenticated or not
        ".write": "auth != null && auth.uid == $user_id"  // Only authenticated users can write to their own node
      }
    }
  }
}

 */