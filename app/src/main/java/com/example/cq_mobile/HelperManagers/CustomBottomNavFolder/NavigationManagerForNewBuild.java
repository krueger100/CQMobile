package com.example.cq_mobile.HelperManagers.CustomBottomNavFolder;


import android.content.Intent;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.example.cq_mobile.MoreActivityFolder.MoreActivity;
import com.example.cq_mobile.ui.chat.ChatFragment;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.NewBuild;
import com.example.cq_mobile.ui.home.HomeFragment;
import com.example.cq_mobile.ui.home.myJobsFolder.myJobs;
import com.example.cq_mobile.ui.map.MapFragment;
import com.example.cq_mobile.ui.MoreInFragment.MoreFragment;
import com.example.cq_mobile.ui.ticket.TicketFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

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
            //    switchFragment(new MoreFragment());
                Intent intent = new Intent(activity, myJobs.class);
                activity.startActivity(intent);
                activity.finish();
            }
            @Override
            public void onTicketSelected() {
                switchFragment(new TicketFragment());
            }

            @Override
            public void onChatSelected() {
                switchFragment(new ChatFragment());
            }

            @Override
            public void onMoreSelected() {
                Intent intent = new Intent(activity, MoreActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }

        });

    }

    private void switchFragment(Fragment fragment) {
        activity.switchFragment(fragment);

        if (fragment instanceof HomeFragment || fragment instanceof MapFragment) {
            showBottomSheet();
        } else {
            collapseBottomSheet();
        }
    }

    private void showBottomSheet() {
        bottomSheet.post(() -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });
    }


    private void collapseBottomSheet() {
        bottomSheet.post(() -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });
    }

}

    /*
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

     */
