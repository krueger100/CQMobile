package com.example.cq_mobile.ui.ticket;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class TicketViewModel extends ViewModel {

    private final MutableLiveData<List<Item>> items;
    private final MutableLiveData<Boolean> isLoading;
    private int page = 1;
    public final int PAGE_SIZE = 20;

    public TicketViewModel() {
        items = new MutableLiveData<>(new ArrayList<>());
        isLoading = new MutableLiveData<>(false);
        loadMoreItems();  // Initial load
    }

    public LiveData<List<Item>> getItems() {
        return items;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadMoreItems() {
        if (Boolean.TRUE.equals(isLoading.getValue())) {
            return; // Already loading, prevent multiple requests
        }

        isLoading.setValue(true);

        // Simulate data loading delay (or replace with actual data fetching)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            List<Item> currentItems = items.getValue();
            List<Item> newItems = getPage(page, PAGE_SIZE);
            if (currentItems != null) {
                currentItems.addAll(newItems);
                items.setValue(currentItems);
            }
            page++;
            isLoading.setValue(false);
        }, 1500);
    }

    private List<Item> getPage(int page, int pageSize) {
        List<Item> newItems = new ArrayList<>();
        int start = (page - 1) * pageSize;
        int end = start + pageSize;

        for (int i = start; i < end; i++) {
            newItems.add(new Item("Item " + (i + 1)));
        }

        return newItems;
    }
}
