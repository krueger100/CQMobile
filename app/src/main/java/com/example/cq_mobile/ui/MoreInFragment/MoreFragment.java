package com.example.cq_mobile.ui.MoreInFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.LogoutFolder.LogoutManager;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;
import com.example.cq_mobile.databinding.FragmentMoreBinding;


public class MoreFragment extends Fragment {
    private FragmentMoreBinding binding;
    private SharedPrefManager sharedPrefManager;

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

        // Log user data
        Log.d("MoreFragment", "Retrieved User Data: ");
        Log.d("MoreFragment", "Access Token: " + accessToken);
        Log.d("MoreFragment", "User ID: " + userId);
        Log.d("MoreFragment", "First Name: " + firstName);
        Log.d("MoreFragment", "Last Name: " + lastName);
        Log.d("MoreFragment", "Email: " + email);
        Log.d("MoreFragment", "Password: " + password);
        Log.d("MoreFragment", "Avatar: " + avatar);

        // Set user data to UI
        binding.name.setText(firstName + " " + lastName);

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
                // Show progress bar and perform logout
                binding.progressBar.setVisibility(View.VISIBLE);
                LogoutManager.logoutUser(requireContext());
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Clean up binding to avoid memory leaks
    }
}