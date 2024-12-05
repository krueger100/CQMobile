package com.example.cq_mobile.Clock.ViewListFolder;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.cq_mobile.HelperManagers.FullscreenManager;
import com.example.cq_mobile.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ViewListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewlist);

        FullscreenManager.enableFullScreen(getWindow());

        // Initialize ViewPager2 and TabLayout
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        // Set up the adapter for ViewPager2
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        // Set up TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // Inflate the custom tab layout
            View tabView = LayoutInflater.from(this).inflate(R.layout.home_tab_item, null);
            TextView tabText = tabView.findViewById(R.id.tabText);
            ImageView tabIcon = tabView.findViewById(R.id.tabIcon);

            // Customize tab layout based on position
            if (position == 0) {
                tabText.setText("To Do");
                tabIcon.setImageResource(R.drawable.nav_todo);
                tabView.setBackgroundResource(R.drawable.selected_tab_bg);
                tabText.setTextColor(Color.WHITE);
                tabIcon.setColorFilter(Color.WHITE);
            } else if (position == 1) {
                tabText.setText("Done");
                tabIcon.setImageResource(R.drawable.nav_todo);
                tabText.setTextColor(getResources().getColorStateList(R.color.tab_text_color, null));
            }

            // Set the custom view for the tab
            tab.setCustomView(tabView);

        }).attach();

        // Handle tab selection changes
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View customView = tab.getCustomView();
                if (customView != null) {
                    // Set background to selected state and change colors
                    customView.setBackground(ContextCompat.getDrawable(ViewListActivity.this, R.drawable.selected_tab_bg));
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
                    // Reset background and text/icon colors
                    customView.setBackgroundResource(android.R.color.transparent);
                    TextView tabText = customView.findViewById(R.id.tabText);
                    ImageView tabIcon = customView.findViewById(R.id.tabIcon);
                    tabText.setTextColor(ContextCompat.getColor(ViewListActivity.this, R.color.tab_text_color));
                    tabIcon.setColorFilter(ContextCompat.getColor(ViewListActivity.this, R.color.tab_icon_color));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Optional behavior for reselected tab
            }
        });

        // Select the first tab by default
        tabLayout.getTabAt(0).select();
    }
}
