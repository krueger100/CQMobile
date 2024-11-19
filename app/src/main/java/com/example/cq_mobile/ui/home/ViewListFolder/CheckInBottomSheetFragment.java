package com.example.cq_mobile.ui.home.ViewListFolder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import android.util.Log;

public class CheckInBottomSheetFragment extends BottomSheetDialogFragment {

    private static final int PAGE_SIZE = 10; // Number of items to load per page
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<CheckInData, CheckInViewHolder> adapter;
    private DatabaseReference databaseReference;
    private Query currentQuery;
    private String lastItemKey = null;
    private boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        setupRecyclerView();

        return view;
    }

    private void setupRecyclerView() {
        databaseReference = FirebaseDatabase.getInstance().getReference("CheckIn_Data");

        // Initial Query to load first page
        currentQuery = databaseReference.orderByKey().limitToFirst(PAGE_SIZE);

        loadData();

        // Attach scroll listener for pagination
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                // Check if we reached the end of the list
                if (!isLoading && layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == adapter.getItemCount() - 1) {
                    Log.d("Pagination", "Loading more data...");
                    loadNextPage();
                }
            }
        });
    }

    private void loadData() {
        FirebaseRecyclerOptions<CheckInData> options = new FirebaseRecyclerOptions.Builder<CheckInData>()
                .setQuery(currentQuery, CheckInData.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<CheckInData, CheckInViewHolder>(options) {
            @NonNull
            @Override
            public CheckInViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_checkin, parent, false);
                return new CheckInViewHolder(itemView);
            }

            @Override
            protected void onBindViewHolder(@NonNull CheckInViewHolder holder, int position, @NonNull CheckInData model) {
                holder.bind(model);

                // Save the key of the last item loaded
                if (position == getItemCount() - 1) {
                    lastItemKey = getRef(position).getKey();
                }
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void loadNextPage() {
        if (lastItemKey == null) return;

        isLoading = true;

        // Create a query to load the next page
        Query nextQuery = databaseReference.orderByKey().startAt(lastItemKey).limitToFirst(PAGE_SIZE + 1);

        nextQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                int itemCount = (int) task.getResult().getChildrenCount();

                if (itemCount > 1) { // Exclude the duplicate key
                    FirebaseRecyclerOptions<CheckInData> newOptions = new FirebaseRecyclerOptions.Builder<CheckInData>()
                            .setQuery(nextQuery, CheckInData.class)
                            .build();

                    adapter.updateOptions(newOptions);
                } else {
                    Log.d("Pagination", "No more data to load.");
                }
            }
            isLoading = false;
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public static class CheckInViewHolder extends RecyclerView.ViewHolder {
        private final TextView timeTextView;
        private final TextView dateTextView;

        public CheckInViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }

        public void bind(CheckInData checkInData) {
            timeTextView.setText("Time: " + checkInData.getTime());
            dateTextView.setText("Date: " + checkInData.getDate());
        }
    }
}
