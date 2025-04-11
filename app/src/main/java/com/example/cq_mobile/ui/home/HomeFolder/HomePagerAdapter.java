package com.example.cq_mobile.ui.home.HomeFolder;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
public class HomePagerAdapter extends FragmentStateAdapter {

    public HomePagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Ensure that fragment creation is done on the main thread (though it is by default)
        final Fragment fragment;

        // Wrapping fragment creation in runOnUiThread just to be sure
        fragment = getFragmentForPosition(position);

        return fragment;
    }

    @Override
    public int getItemCount() {
        return 3; // We now have 3 pages: To Do, Done, and Skipped
    }

    private Fragment getFragmentForPosition(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new ToDoFragment(); // "To Do" fragment
        } else if (position == 1) {
            fragment = new SkippedFragment(); // "Skipped" fragment
        } else {
            fragment = new DoneFragment(); // "Done" fragment
        }
        return fragment;
    }
}
