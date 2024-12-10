package com.example.cq_mobile.HelperManagers.CustomBottomNavFolder;


import android.view.View;

import androidx.fragment.app.Fragment;

import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.NewBuild;
import com.example.cq_mobile.ui.home.HomeFragment;
import com.example.cq_mobile.ui.map.MapFragment;
import com.example.cq_mobile.ui.myJob.myJobFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NavigationManagerForNewBuild {

    private final NewBuild activity;
    private final View bottomSheet;
    private final BottomSheetBehavior<View> bottomSheetBehavior;

    public NavigationManagerForNewBuild(NewBuild activity, View bottomSheet, BottomSheetBehavior<View> bottomSheetBehavior) {
        this.activity = activity;
        this.bottomSheet = bottomSheet;
        this.bottomSheetBehavior = bottomSheetBehavior;

    }

    public void setUpNavigation(CustomBottomNavView bottomNavView ) {
        bottomNavView.setOnNavigationItemSelectedListener(new CustomBottomNavView.OnNavigationItemSelectedListener() {
            @Override
            public void onHomeSelected() {
                switchFragment(new HomeFragment());
            }

            @Override
            public void onMapSelected() {
                switchFragment(new MapFragment());
            }

            @Override
            public void onMyJobsSelected() {
                switchFragment(new myJobFragment());
            }
        });
    }

    private void switchFragment(Fragment fragment) {
        activity.switchFragment(fragment);
        collapseBottomSheet();
    }

    private void collapseBottomSheet() {
        bottomSheet.post(() -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            bottomSheetBehavior.setPeekHeight(bottomSheet.getHeight() / 6);
            bottomSheetBehavior.setHideable(true);

        });
    }
}

