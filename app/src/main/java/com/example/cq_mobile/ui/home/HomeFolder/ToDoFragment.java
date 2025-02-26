package com.example.cq_mobile.ui.home.HomeFolder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.Clock.ClockActivity;
import com.example.cq_mobile.Clock.ClockFolder.ClockInAPIFolder.TimerManager;
import com.example.cq_mobile.Clock.ClockFolder.ClockInAPIFolder.TimerService;
import com.example.cq_mobile.Clock.ClockFolder.ClockOutFolder.ClockOutManager;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.API_todo.Todo;
import com.example.cq_mobile.ui.home.HomeFolder.API_todo.TodoAdapter;
import com.example.cq_mobile.ui.home.HomeFolder.API_todo.TodoApiManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.ArrayList;
import java.util.List;



public class ToDoFragment extends Fragment implements TimerManager.TimerListener {

    private RecyclerView recyclerView;
    private TodoAdapter todoAdapter;
    private List<Todo> joblist = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView clockout_btn;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = 1;
    private final int PAGE_SIZE = 15;
    private TimerManager timerManager;
    private TextView timerText;
    private FloatingActionButton fab;
    private BroadcastReceiver timerReceiver;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        clockout_btn = view.findViewById(R.id.clockout_btn);
        timerText = view.findViewById(R.id.timer_text);
        fab = view.findViewById(R.id.fab_timer);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        todoAdapter = new TodoAdapter(getContext(), joblist);
        recyclerView.setAdapter(todoAdapter);

        SharedPrefManager sharedPrefManager = new SharedPrefManager(getContext());
        String accessToken = sharedPrefManager.getAccessToken();
        loadMessages(accessToken);

        ClockOutManager clockOutManager = new ClockOutManager(getContext(),progressBar);
        clockOutManager.setupClockOutButton(clockout_btn);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!isLoading && !isLastPage) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        loadMessages(accessToken);
                    }
                }
            }
        });

        clockout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ClockActivity.class);
                intent.putExtra("key", "value");
                startActivity(intent);
                getActivity().finish();
            }
        });


        timerManager = TimerManager.getInstance();
        timerManager.setListener(this);


        fab.setOnClickListener(v -> {
            MainActivity activity = (MainActivity) getActivity();
            if (activity != null) {
                // Stop the TimerManager
                activity.getTimerManager().stopTimer(); // Using the correct getter

                // Stop the TimerService
                Intent stopIntent = new Intent(getContext(), TimerService.class);
                getContext().stopService(stopIntent);

                // Show a toast
                Toast.makeText(getContext(), "Timer Stopped", Toast.LENGTH_SHORT).show();
            }
        });



        timerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(TimerService.TIMER_UPDATE_ACTION)) {
                    String time = intent.getStringExtra("time");
                    timerText.setText(time);
                }
            }
        };

        IntentFilter filter = new IntentFilter(TimerService.TIMER_UPDATE_ACTION);
        ContextCompat.registerReceiver(requireActivity(), timerReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);


        return view;
    }



    private void loadMessages(String accessToken) {
        if (isLoading) return; // Prevent fetching while already loading data
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);

        TodoApiManager.fetchApiDataPaginated(accessToken, currentPage, PAGE_SIZE, new TodoApiManager.ApiResponseCallback() {
            @Override
            public void onDataFetched(List<Todo> data) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    isLoading = false;

                    if (data != null && !data.isEmpty()) {
                        joblist.addAll(data);
                        todoAdapter.notifyDataSetChanged();
                        currentPage++;

                        // Check if total data count has reached 100
                        if (joblist.size() >= 100 && currentPage == 1) {
                            // If data reaches 100, skip to page 2 directly, if we are still on page 1
                            currentPage = 2; // Move to page 2
                            loadMessages(accessToken); // Recurse to load data from page 2
                        } else {
                            // Check if this is the last page
                            if (data.size() < PAGE_SIZE) {
                                isLastPage = true;
                            }
                        }
                    } else {
                        isLastPage = true; // No more data to load
                    }
                });
            }

            @Override
            public void onError(String error) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    isLoading = false;
                    Log.d("Paginated Data", "Error loading data: " + error);
                });
            }



        });

    }


    @Override
    public void onTimerUpdate(String time) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> timerText.setText(time));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove listener to avoid memory leaks
        timerManager.setListener(null);
    }

}


/*
    private TimerManager timerManager;
    private TextView timerText;
    private FloatingActionButton fab;
    private BroadcastReceiver timerReceiver;

        timerManager = new TimerManager((TimerManager.TimerListener) this);
        timerManager.startTimer();

        timerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String time = intent.getStringExtra("time");
                if (timerText != null) {
                    timerText.setText(time);
                }
            }
        };

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(timerReceiver, new IntentFilter("TIMER_UPDATE"));

        fab.setOnClickListener(v -> {
            MainActivity activity = (MainActivity) getActivity();
            if (activity != null) {
                activity.getTimerManager().stopTimer(); // Using the correct getter
                Toast.makeText(getContext(), "Timer Stopped", Toast.LENGTH_SHORT).show();
            }
        });

 */