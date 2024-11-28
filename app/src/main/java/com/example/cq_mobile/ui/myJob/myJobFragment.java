package com.example.cq_mobile.ui.myJob;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cq_mobile.R;
import com.example.cq_mobile.databinding.FragmentMyJobsBinding;
import androidx.viewpager2.widget.ViewPager2;

import com.example.cq_mobile.ui.myJob.myJobFolder.JobPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class myJobFragment extends Fragment {
    private FragmentMyJobsBinding binding;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myJobViewModel myJobViewModel = new ViewModelProvider(this).get(myJobViewModel.class);
        binding = FragmentMyJobsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
      //  FullscreenManager.enableFullScreen(requireActivity().getWindow());

        // Setup ViewPager2 and TabLayout
        ViewPager2 viewPager = root.findViewById(R.id.viewPager);
        TabLayout tabLayout = root.findViewById(R.id.tabLayout);

        // Set up the PagerAdapter for ViewPager2
        JobPagerAdapter jobPagerAdapter = new JobPagerAdapter(this);
        viewPager.setAdapter(jobPagerAdapter);

        // Link TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("To Do");
            } else {
                tab.setText("Done");
            }
        }).attach();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
