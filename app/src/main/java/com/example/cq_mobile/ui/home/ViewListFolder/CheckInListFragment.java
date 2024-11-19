package com.example.cq_mobile.ui.home.ViewListFolder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.DataSnapshot;

public class CheckInListFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<CheckInData, CheckInViewHolder> adapter;
    private FirebaseRecyclerOptions<CheckInData> options;

    private static final int PAGE_SIZE = 10; // Set your desired page size
    private DataSnapshot lastVisible; // To keep track of the last item (DataSnapshot for Realtime Database)

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkin_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("CheckIn_Data");

        // Initial query to load the first page of data
        Query query = databaseReference.limitToFirst(PAGE_SIZE);

        options = new FirebaseRecyclerOptions.Builder<CheckInData>()
                .setQuery(query, CheckInData.class)
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
            }

            @Override
            public void onDataChanged() {
                // Update lastVisible when data changes
                if (getItemCount() > 0) {
                    lastVisible = getSnapshots().getSnapshot(getItemCount() - 1);
                }
            }
        };

        recyclerView.setAdapter(adapter);

        // Handle pagination when reaching the end of the list
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(1)) {
                    loadNextPage();
                }
            }
        });

        return view;
    }

    private void loadNextPage() {
        if (lastVisible != null) {
            // Create a query starting from the last visible snapshot to load the next set of items
            Query nextPageQuery = databaseReference
                    .orderByKey() // Order by key to ensure proper pagination
                    .startAfter(lastVisible.getKey()) // Start after the last visible item
                    .limitToFirst(PAGE_SIZE);

            FirebaseRecyclerOptions<CheckInData> newOptions = new FirebaseRecyclerOptions.Builder<CheckInData>()
                    .setQuery(nextPageQuery, CheckInData.class)
                    .build();

            adapter.updateOptions(newOptions);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening(); // Start listening for database changes
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening(); // Stop listening to avoid memory leaks
    }

    // ViewHolder for Check-in items
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
