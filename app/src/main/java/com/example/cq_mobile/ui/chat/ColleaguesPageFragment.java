package com.example.cq_mobile.ui.chat;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cq_mobile.R;

public class ColleaguesPageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment (Colleagues Page)
        return inflater.inflate(R.layout.fragment_colleagues_page, container, false);
    }
}
