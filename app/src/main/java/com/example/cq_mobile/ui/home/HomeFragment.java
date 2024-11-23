package com.example.cq_mobile.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.R;
import com.example.cq_mobile.databinding.FragmentHomeBinding;
import com.example.cq_mobile.ui.home.ClockFolder.ClockView;

import com.example.cq_mobile.ui.home.ClockFolder.DigitalClockManager;
import com.example.cq_mobile.ui.home.ViewListFolder.JSONPlaceholder;
import com.example.cq_mobile.ui.home.ViewListFolder.Post;
import com.example.cq_mobile.ui.home.ViewListFolder.PostAdapter;
import com.example.cq_mobile.ui.home.ViewListFolder.RecyclerViewBottomSheetFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ClockView clockView;
    private DigitalClockManager digitalClockManager;
    private RecyclerView recyclerView;
    private JSONPlaceholder jsonPlaceholder;
    private PostAdapter postAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize ViewModel and ViewBinding
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize ClockView and digitalClock TextView
        clockView = root.findViewById(R.id.analogClock);
        digitalClockManager = new DigitalClockManager(root.findViewById(R.id.digitalClock));

        // Initialize RecyclerView
        recyclerView = root.findViewById(R.id.recyclerView);
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            Log.e("HomeFragment", "RecyclerView initialization failed. Check fragment_home.xml.");
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
        binding.checkIn.setOnClickListener(v -> {
            Log.d("HomeFragment", "Check-in button clicked");
            // You can add functionality for check-in here
        });

        // Set up View List button click listener
        binding.viewlist.setOnClickListener(v -> {
            Log.d("HomeFragment", "View-list button clicked");
            RecyclerViewBottomSheetFragment bottomSheetFragment = new RecyclerViewBottomSheetFragment();
            bottomSheetFragment.show(getParentFragmentManager(), "RecyclerViewBottomSheetFragment");
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
