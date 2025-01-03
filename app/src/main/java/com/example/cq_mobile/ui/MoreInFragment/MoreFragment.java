package com.example.cq_mobile.ui.MoreInFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.LogoutFolder.LogoutManager;
import com.example.cq_mobile.MainActivity;
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

        // Log user data
        Log.d("MoreFragment", "Retrieved User Data: ");
        Log.d("MoreFragment", "Access Token: " + accessToken);
        Log.d("MoreFragment", "User ID: " + userId);
        Log.d("MoreFragment", "First Name: " + firstName);
        Log.d("MoreFragment", "Last Name: " + lastName);
        Log.d("MoreFragment", "Email: " + email);

        // Set user data to UI
        binding.name.setText(firstName + " " + lastName);

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
