package com.example.cq_mobile.ui.home.myJobsFolder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class myJobAdapter extends FragmentStateAdapter {

    public myJobAdapter(@NonNull AppCompatActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new myJobFragment();
            case 1:
                return new myJobDoneFragment();
            default:
                return new myJobFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
