package com.example.cq_mobile.ui.home;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
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
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ViewPager2 viewPager = root.findViewById(R.id.viewPager);
        TabLayout tabLayout = root.findViewById(R.id.tabLayout);

        tabLayout.setBackgroundColor(getResources().getColor(android.R.color.white, null));

        HomePagerAdapter homePagerAdapter = new HomePagerAdapter(this);
        viewPager.setAdapter(homePagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            View tabView = LayoutInflater.from(getContext()).inflate(R.layout.home_tab_item, null);
            TextView tabText = tabView.findViewById(R.id.tabText);
            ImageView tabIcon = tabView.findViewById(R.id.tabIcon);

            if (position == 0) {
                tabText.setText("To do");
                tabIcon.setImageResource(R.drawable.nav_todo);
                tabView.setBackgroundResource(R.drawable.selected_tab_bg);
                tabText.setTextColor(Color.WHITE);
                tabIcon.setColorFilter(Color.WHITE);
            } else if (position == 1) {
                tabText.setText("Skipped");
                tabIcon.setImageResource(R.drawable.nav_todo);
                tabText.setTextColor(getResources().getColorStateList(R.color.tab_text_color, null));
            } else {
                tabText.setText("Done");
                tabIcon.setImageResource(R.drawable.nav_todo);
                tabText.setTextColor(getResources().getColorStateList(R.color.tab_text_color, null));
            }

            // Set the custom tab view
            tab.setCustomView(tabView);


        }).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View customView = tab.getCustomView();
                if (customView != null) {
                    Drawable selectedBackground = ContextCompat.getDrawable(getContext(), R.drawable.selected_tab_bg);
                    customView.setBackground(selectedBackground);
                    TextView tabText = customView.findViewById(R.id.tabText);
                    ImageView tabIcon = customView.findViewById(R.id.tabIcon);
                    tabText.setTextColor(Color.WHITE);
                    tabIcon.setColorFilter(Color.WHITE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View customView = tab.getCustomView();
                if (customView != null) {
                    // Reset background to transparent
                    customView.setBackgroundResource(android.R.color.transparent);

                    // Reset text and icon color to default
                    TextView tabText = customView.findViewById(R.id.tabText);
                    ImageView tabIcon = customView.findViewById(R.id.tabIcon);
                    tabText.setTextColor(ContextCompat.getColor(getContext(), R.color.tab_text_color));
                    tabIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.tab_icon_color));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Optional behavior for reselected tab
            }
        });

        // Set default selection and background when app starts (for the first tab)
        tabLayout.getTabAt(0).select();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}