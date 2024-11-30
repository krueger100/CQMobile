package com.example.cq_mobile.ui.home.HomeFolder.API_home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.cq_mobile.R;

public class SkippedFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate and setup the "Done" layout here
        View root = inflater.inflate(R.layout.fragment_skipped, container, false);
        return root;
    }
}
