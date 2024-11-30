package com.example.cq_mobile.ui.home.HomeFolder;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.cq_mobile.ui.home.HomeFolder.API_home.SkippedFragment;

public class HomePagerAdapter extends FragmentStateAdapter {

    public HomePagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return the correct fragment based on the position
        if (position == 0) {
            return new ToDoFragment(); // "To Do" fragment
        } else if (position == 1) {
            return new SkippedFragment(); // "Skipped" fragment

        } else {
            return new DoneFragment(); // "Done" fragment

        }
    }

    @Override
    public int getItemCount() {
        return 3; // We now have 3 pages: To Do, Done, and Skipped
    }
}
