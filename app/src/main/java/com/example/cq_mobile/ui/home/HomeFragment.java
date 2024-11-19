package com.example.cq_mobile.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cq_mobile.R;
import com.example.cq_mobile.databinding.FragmentHomeBinding;
import com.example.cq_mobile.ui.home.CheckInFolder.CheckInManager;
import com.example.cq_mobile.ui.home.ClockFolder.ClockView;

import com.example.cq_mobile.ui.home.ClockFolder.DigitalClockManager;
import com.example.cq_mobile.ui.home.ViewListFolder.CheckInBottomSheetFragment;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ClockView clockView;
    private DigitalClockManager digitalClockManager;
    private CheckInManager checkInManager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize ViewModel and ViewBinding
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize ClockView and digitalClock TextView
        clockView = root.findViewById(R.id.analogClock);
        digitalClockManager = new DigitalClockManager(root.findViewById(R.id.digitalClock));

        // Start the digital clock updates
        digitalClockManager.startClock();

        // Initialize CheckInManager
        checkInManager = new CheckInManager(requireContext());

        // Set up Check In button click listener
        binding.checkIn.setOnClickListener(v -> {
            Log.d("HomeFragment", "Check-in button clicked");
            checkInManager.saveCheckIn();
        });

        // Set up View List button click listener
        binding.viewlist.setOnClickListener(v -> {
            Log.d("HomeFragment", "View List button clicked");

            // Open BottomSheetDialogFragment
            CheckInBottomSheetFragment bottomSheetFragment = new CheckInBottomSheetFragment();
            bottomSheetFragment.show(getParentFragmentManager(), bottomSheetFragment.getTag());
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        digitalClockManager.stopClock(); // Stop updating the clock when the view is destroyed
        binding = null;
    }
}
