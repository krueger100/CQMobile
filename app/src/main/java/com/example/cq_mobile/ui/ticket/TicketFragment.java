package com.example.cq_mobile.ui.ticket;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.databinding.FragmentTicketBinding;

import java.util.ArrayList;
import java.util.List;

public class TicketFragment extends Fragment {

    private FragmentTicketBinding binding;
    private ItemAdapter adapter;
    private List<Item> itemList;
    private TicketViewModel ticketViewModel;
    private boolean isLoading = false;
    private static final int ITEM_VIEW_TYPE = 0;
    private static final int LOADING_VIEW_TYPE = 1;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTicketBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize ViewModel
        ticketViewModel = new ViewModelProvider(this).get(TicketViewModel.class);

        // Initialize RecyclerView and Adapter
        itemList = new ArrayList<>();
        adapter = new ItemAdapter(itemList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        // Observe items LiveData from ViewModel
        ticketViewModel.getItems().observe(getViewLifecycleOwner(), updatedItems -> {
            itemList.clear();
            itemList.addAll(updatedItems);
            adapter.notifyDataSetChanged();
        });

        // Observe loading state from ViewModel
        ticketViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            this.isLoading = isLoading;
            // Optional: Show a loading indicator based on isLoading state
        });

        // Add Pagination Listener to RecyclerView
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= ticketViewModel.PAGE_SIZE) {
                        // Request more items from ViewModel when the end of the list is reached
                        ticketViewModel.loadMoreItems();
                    }
                }
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
