package com.example.cq_mobile.ui.MoreInFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.cq_mobile.Clock.ClockFolder.ClockInAPIFolder.TimerManager;
import com.example.cq_mobile.Clock.ClockFolder.ClockInAPIFolder.TimerService;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.LogoutFolder.LogoutManager;
import com.example.cq_mobile.LogoutFolder.LogoutNotificationManager;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;
import com.example.cq_mobile.databinding.FragmentMoreBinding;
import com.example.cq_mobile.ui.ticket.TicketAPICategoryFolder.TicketCategoryManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MoreFragment extends Fragment {
    private Context context;

    private FragmentMoreBinding binding;
    private SharedPrefManager sharedPrefManager;



    private BroadcastReceiver timerReceiver;
    private TextView timerText;
    private FloatingActionButton fab;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment's layout using view binding
        binding = FragmentMoreBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize SharedPrefManager
        sharedPrefManager = new SharedPrefManager(requireContext());

        // Retrieve user data
        String accessToken = sharedPrefManager.getAccessToken();
        String userId = sharedPrefManager.getUserId();
        String firstName = sharedPrefManager.getFirstName();
        String lastName = sharedPrefManager.getLastName();
        String email = sharedPrefManager.getEmail();
        String password = sharedPrefManager.getPassword();
        String avatar = sharedPrefManager.getAvatarUrl();

        String notificationToken = sharedPrefManager.getNotiftoken();


        // Log user data
        Log.d("MoreFragment", "Retrieved User Data: ");
        Log.d("MoreFragment", "Access Token: " + accessToken);
        Log.d("MoreFragment", "User ID: " + userId);
        Log.d("MoreFragment", "First Name: " + firstName);
        Log.d("MoreFragment", "Last Name: " + lastName);
        Log.d("MoreFragment", "Email: " + email);
        Log.d("MoreFragment", "Password: " + password);
        Log.d("MoreFragment", "Avatar: " + avatar);
        Log.d("MoreFragment", "notificationToken: " + notificationToken);
        // Set user data to UI
        binding.name.setText(firstName + " " + lastName);
        context = getContext();


        timerText = binding.timerText;
        fab = root.findViewById(R.id.fab_timer);




        // Load avatar image using Glide
        if (avatar != null && !avatar.isEmpty()) {
            Glide.with(this)
                    .load(avatar)  // The URL of the avatar
                    .placeholder(R.drawable.baseline_circle)  // Optional placeholder image while loading
                    .error(R.drawable.emptyglide)  // Optional error image if something goes wrong
                    .into(binding.imageProfile);  // The ImageView to load the avatar into
        }

        // Set up click listeners
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity
                Intent intent = new Intent(requireContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutNotificationManager logoutManager = new LogoutNotificationManager();
                logoutManager.deleteNotificationToken(userId, new LogoutNotificationManager.LogoutCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("MoreFragment", "Notification token deleted successfully.");
                        binding.progressBar.setVisibility(View.VISIBLE);

                        Intent stopIntent = new Intent(getContext(), TimerService.class);
                        getContext().stopService(stopIntent);
                        LogoutManager.logoutUser(requireContext());
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e("MoreFragment", "Failed to delete notification token: " + errorMessage);
                    }
                });

            }
        });
        fab.setOnClickListener(v -> {
            MainActivity activity = (MainActivity) getActivity();
            if (activity != null) {
                // Stop the TimerManager
                requireActivity().unregisterReceiver(timerReceiver);
                activity.getTimerManager().stopTimer(); // Using the correct getter

                // Stop the TimerService
                Intent stopIntent = new Intent(getContext(), TimerService.class);
                getContext().stopService(stopIntent);

                // Show a toast
                Toast.makeText(getContext(), "Timer Stopped", Toast.LENGTH_SHORT).show();
            }
        });

        registerTimerReceiver();

        return root;
    }

    private void registerTimerReceiver() {
        timerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (TimerService.TIMER_UPDATE_ACTION.equals(intent.getAction())) {
                    String time = intent.getStringExtra("time");
                    timerText.setText(time);  // Update Timer TextView
                }
            }
        };

        IntentFilter filter = new IntentFilter(TimerService.TIMER_UPDATE_ACTION);
        ContextCompat.registerReceiver(requireActivity(), timerReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timerReceiver != null) {
            requireActivity().unregisterReceiver(timerReceiver);
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