package com.example.cq_mobile.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.example.cq_mobile.R;
import com.example.cq_mobile.databinding.FragmentHomeBinding;
import com.example.cq_mobile.ui.home.HomeFolder.HomePagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize ViewModel and ViewBinding
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Setup ViewPager2 and TabLayout
        ViewPager2 viewPager = root.findViewById(R.id.viewPager);
        TabLayout tabLayout = root.findViewById(R.id.tabLayout);

        // Set up the PagerAdapter for ViewPager2
        HomePagerAdapter homePagerAdapter = new HomePagerAdapter(this);
        viewPager.setAdapter(homePagerAdapter);

        // Link TabLayout with ViewPager2 and customize tab layout
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // Inflate custom tab layout
            View tabView = LayoutInflater.from(getContext()).inflate(R.layout.home_tab_item, null);
            TextView tabText = tabView.findViewById(R.id.tabText);
            ImageView tabIcon = tabView.findViewById(R.id.tabIcon);

            // Set the text and icon for each tab
            if (position == 0) {
                tabText.setText("To do");
                tabIcon.setImageResource(R.drawable.nav_todo);
            } else if (position == 1) {
                tabText.setText("Skipped");
                tabIcon.setImageResource(R.drawable.nav_todo);
            } else {
                tabText.setText("Done");
                tabIcon.setImageResource(R.drawable.nav_todo);

            }

            // Set the custom tab view
            tab.setCustomView(tabView);

            // Set the text color for each tab to change based on selection state
            tabText.setTextColor(getResources().getColorStateList(R.color.tab_text_color, null));
        }).attach();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
