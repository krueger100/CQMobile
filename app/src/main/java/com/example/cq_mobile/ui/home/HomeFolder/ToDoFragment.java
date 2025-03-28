package com.example.cq_mobile.ui.home.HomeFolder;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.ClockOutVisibilityHandler;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.API_todo.Todo;
import com.example.cq_mobile.ui.home.HomeFolder.API_todo.TodoAdapter;
import com.example.cq_mobile.ui.home.HomeFolder.API_todo.TodoApiManager;


import java.util.ArrayList;
import java.util.List;


public class ToDoFragment extends Fragment {

    private RecyclerView recyclerView;
    private TodoAdapter todoAdapter;
    private List<Todo> joblist = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView clockout_btn;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = 1;
    private final int PAGE_SIZE = 15;
    private ClockOutVisibilityHandler visibilityHandler;
    private static final String TAG = "Todo Data";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        todoAdapter = new TodoAdapter(getContext(), joblist);
        recyclerView.setAdapter(todoAdapter);

        SharedPrefManager sharedPrefManager = new SharedPrefManager(getContext());
        String accessToken = sharedPrefManager.getAccessToken();
        int userID = sharedPrefManager.getUserId();
        int savedJobId = sharedPrefManager.getJobId();
        int savedTaskId = sharedPrefManager.getTaskId();
        String startDate = sharedPrefManager.getKeyStartDate();

        loadMessages(accessToken);


        Log.d("ToDoFragment", "Retrieved Job ID: " + savedJobId + "Retrieved Task ID: " + savedTaskId);
        Log.d("ToDoFragment", "Retrieved user ID: " + userID + "Retrieved startDate: " + startDate);



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

        return view;
    }


    private void loadMessages(String accessToken) {
        if (isLoading) return;
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
                        for (Todo todo : data) {
                            Log.w(TAG, "**<- ToDoFragment ->**");
                            Log.d(TAG, "ID: " + todo.getId());
                            Log.d(TAG, "Job ID: " + todo.getJob_id());
                            Log.d(TAG, "Title: " + todo.getName());
                            Log.d(TAG, "Description: " + todo.getDescription());
                            Log.d(TAG, "Status: " + todo.getStatus());
                            Log.d(TAG, "Start Date: " + todo.getStart_date());
                            Log.d(TAG, "End Date: " + todo.getEnd_date());
                            Log.d(TAG, "Category: " + todo.getCategory());
                            Log.d(TAG, "Category Color: " + todo.getCategory_color());
                            Log.d(TAG, "Is Checked: " + todo.isChecked());

                            // Logging Client Details
                            if (todo.getClient_details() != null) {
                                Todo.ClientDetails client = todo.getClient_details();
                                Log.d(TAG + "Client Details", "Company: " + client.getCompany());
                                Log.d("Client Details", "Email: " + client.getEmail());
                                Log.d(TAG +"Client Details", "Title: " + client.getTitle());
                                Log.d(TAG +"Client Details", "First Name: " + client.getFirst_name());
                                Log.d(TAG +"Client Details", "Last Name: " + client.getLast_name());
                                Log.d(TAG +"Client Details", "Suffix: " + client.getSuffix());
                                Log.d(TAG +"Client Details", "Phone: " + client.getPhone());
                                Log.d(TAG +"Client Details", "Mobile: " + client.getMobile());
                            }

                            // Logging Address Details
                            if (todo.getAddress() != null) {
                                Todo.Address address = todo.getAddress();
                                Log.d(TAG +"Address", "Address: " + address.getAddress());
                                Log.d(TAG +"Address", "Address1: " + address.getAddress1());
                                Log.d(TAG +"Address", "City: " + address.getCity());
                                Log.d(TAG +"Address", "County: " + address.getCounty());
                                Log.d(TAG +"Address", "Postal Code: " + address.getPostal_code());
                                Log.d(TAG +"Address", "Country: " + address.getCountry());
                            }

                            // Logging Coordinates
                            if (todo.getCoordinates() != null) {
                                Todo.Coordinates coordinates = todo.getCoordinates();
                                Log.d(TAG +"Coordinates", "Latitude: " + coordinates.getLatitude());
                                Log.d(TAG +"Coordinates", "Longitude: " + coordinates.getLongitude());
                            }
                        }

                        joblist.addAll(data);
                        todoAdapter.notifyDataSetChanged();
                        currentPage++;

                        // Pagination logic
                        if (joblist.size() >= 100 && currentPage == 1) {
                            currentPage = 2;
                            loadMessages(accessToken);
                        } else {
                            if (data.size() < PAGE_SIZE) {
                                isLastPage = true;
                            }
                        }
                    } else {
                        isLastPage = true;
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ClockOutVisibilityHandler) {
            visibilityHandler = (ClockOutVisibilityHandler) context;
        } else {
            Log.d("TodoFragment", "Activity does not implement ClockOutVisibilityHandler");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (visibilityHandler != null) {
            visibilityHandler.setClockOutVisibility(true);
        }
    }




}

/*
 implements TimerManager.TimerListener


    private TimerManager timerManager;
    private TextView timerText;
    private FloatingActionButton fab;
    private BroadcastReceiver timerReceiver;
    ImageButton clockOutController;
    private boolean isHidden = false;


    timerText = view.findViewById(R.id.timer_text);
        fab = view.findViewById(R.id.fab_timer);
        clockOutController = view.findViewById(R.id.clockOutController);

    clockOutController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleVisibilityWithAnimation();
            }
        });


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

    timerManager = TimerManager.getInstance();
        timerManager.setListener(this);


    private void toggleVisibilityWithAnimation() {
        if (!isHidden) {
            TransitionAnimationManager.slideOutToRight(timerText, 150);
            TransitionAnimationManager.slideOutToRight(fab, 150);
            timerText.postDelayed(() -> {
                timerText.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
            }, 150);

        } else {
            timerText.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
            timerText.postDelayed(() -> {
                TransitionAnimationManager.slideInFromRight(timerText, 50);
                TransitionAnimationManager.slideInFromRight(fab, 50);
            }, 150);
        }

        isHidden = !isHidden; // Toggle the state
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




    <LinearLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginEnd="25dp"
        android:background="@drawable/timerbckground"
        android:backgroundTint="@color/black"
        android:layout_marginBottom="56dp"
        android:elevation="15dp"
        android:src="@drawable/ic_launcher_foreground"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent">

        <ImageButton
            android:id="@+id/clockOutController"
            tools:ignore="TouchTargetSizeCheck,SpeakableTextPresentCheck"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:background="@android:color/transparent"
            android:src="@drawable/baseline_arrow_forward_ios_24" />

        <TextView
            android:id="@+id/timer_text"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_alignParentStart="true"
            android:layout_alignParentBottom="true"
            android:layout_gravity="center"
            android:layout_marginStart="15dp"
            android:layout_marginEnd="10dp"
            android:text="00:00:00"
            android:textColor="@color/white"
            android:visibility="gone"
            android:textSize="18sp" />

        <com.google.android.material.floatingactionbutton.FloatingActionButton
            android:id="@+id/fab_timer"
            tools:ignore="SpeakableTextPresentCheck"
            android:layout_width="40dp"
            android:layout_height="40dp"
            android:layout_alignParentEnd="true"
            android:layout_alignParentBottom="true"
            android:backgroundTint="@color/textBtnRed"
            android:scaleType="center"
            android:src="@drawable/baseline_stop_circle_24"
            app:borderWidth="0dp"
            android:visibility="gone"
            app:fabCustomSize="40dp"
            app:shapeAppearanceOverlay="@style/CustomFABShape"
            app:tint="@android:color/white" />

    </LinearLayout>


 */


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