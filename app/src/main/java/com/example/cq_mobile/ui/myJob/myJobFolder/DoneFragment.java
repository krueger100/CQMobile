package com.example.cq_mobile.ui.myJob.myJobFolder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.cq_mobile.R;

public class DoneFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate and setup the "Done" layout here
        View root = inflater.inflate(R.layout.fragment_done, container, false);
        return root;
    }
}
