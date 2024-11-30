package com.example.cq_mobile.ui.myJob;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cq_mobile.databinding.FragmentMyJobsBinding;

public class myJobFragment extends Fragment {
    private FragmentMyJobsBinding binding;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myJobViewModel myJobViewModel = new ViewModelProvider(this).get(myJobViewModel.class);
        binding = FragmentMyJobsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
      //  FullscreenManager.enableFullScreen(requireActivity().getWindow());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
