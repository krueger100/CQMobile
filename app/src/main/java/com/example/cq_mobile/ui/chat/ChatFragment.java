package com.example.cq_mobile.ui.chat;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.ClockOutVisibilityHandler;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;

import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;public class ChatFragment extends Fragment {

    private ViewPager2 viewPager2;
    private ClockOutVisibilityHandler visibilityHandler;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        viewPager2 = rootView.findViewById(R.id.viewPager2);

        // Set the adapter for the ViewPager2
        FragmentStateAdapter adapter = new ChatPagerAdapter(this);
        viewPager2.setAdapter(adapter);


        // Add tabs to the TabLayout
        TabLayout tabLayout = rootView.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Chat"));
        tabLayout.addTab(tabLayout.newTab().setText("Colleagues"));

        // Handle tab selection (optional)
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Do something when a tab is unselected (optional)
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Do something when a tab is reselected (optional)
            }
        });

        // Update indicator based on swipe events
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        return rootView;
    }

    private static class ChatPagerAdapter extends FragmentStateAdapter {

        public ChatPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new ChatPageFragment();
            } else {
                return new ColleaguesPageFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ClockOutVisibilityHandler) {
            visibilityHandler = (ClockOutVisibilityHandler) context;
        } else {
            Log.d("MoreFragment", "Activity does not implement ClockOutVisibilityHandler");
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