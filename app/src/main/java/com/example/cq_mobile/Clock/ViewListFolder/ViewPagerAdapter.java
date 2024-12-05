package com.example.cq_mobile.Clock.ViewListFolder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.cq_mobile.ui.home.HomeFolder.DoneFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull AppCompatActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ViewListTodoFragment();
            case 1:
                return new DoneFragment();
            default:
                return new ViewListTodoFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Update this to match the number of tabs/fragments you have
    }
}
