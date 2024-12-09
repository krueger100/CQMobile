package com.example.cq_mobile.HelperManagers.CustomBottomNavFolder;


import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.NewBuild;
import com.example.cq_mobile.ui.home.HomeFragment;
import com.example.cq_mobile.ui.map.MapFragment;
import com.example.cq_mobile.ui.myJob.myJobFragment;

public class NavigationManagerForNewBuild {

    private final NewBuild activity;

    public NavigationManagerForNewBuild(NewBuild activity) {
        this.activity = activity;
    }

    public void setUpNavigation(CustomBottomNavView bottomNavView) {
        bottomNavView.setOnNavigationItemSelectedListener(new CustomBottomNavView.OnNavigationItemSelectedListener() {
            @Override
            public void onHomeSelected() {
                activity.switchFragment(new HomeFragment());
            }

            @Override
            public void onSearchSelected() {
                activity.switchFragment(new MapFragment());
            }

            @Override
            public void onProfileSelected() {
                activity.switchFragment(new myJobFragment());
            }
        });
    }
}
