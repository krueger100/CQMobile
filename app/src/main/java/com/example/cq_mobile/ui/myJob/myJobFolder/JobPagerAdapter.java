package com.example.cq_mobile.ui.myJob.myJobFolder;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class JobPagerAdapter extends FragmentStateAdapter {

    public JobPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return either ToDoFragment or DoneFragment depending on position
        if (position == 0) {
            return new ToDoFragment(); // "To Do" fragment
        } else {
            return new DoneFragment(); // "Done" fragment
        }
    }

    @Override
    public int getItemCount() {
        return 2; // We have 2 pages: To Do and Done
    }
}
