package com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.Clock.StartAndStopJobsFolder.StartJobAPIManager;
import com.example.cq_mobile.Clock.StartAndStopJobsFolder.StartJobResponse;
import com.example.cq_mobile.HelperManagers.Animation.ClickAnimationManager;
import com.example.cq_mobile.HelperManagers.BackPressManager;
import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.CustomBottomNavView;
import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.NavigationManagerForNewBuild;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefTaskADandJobID;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.HelperManagers.mapFolder.MapCameraManager;
import com.example.cq_mobile.HelperManagers.mapFolder.MarkerManager;
import com.example.cq_mobile.HelperManagers.mapFolder.UserPositionMarkerManager;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.RetrieveDataFromAPIMangers.SetupTaskRecyclerViewManager;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.SpinnerFolder.SetupMainTaskSpinnerAdapter;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.SubTasks.SubTask;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.TaskMainFolder.Taskmain;
import com.example.cq_mobile.ui.home.HomeFolder.NotesNFilesAPI_folder.FilesFoler.FilesActivity;
import com.example.cq_mobile.ui.home.HomeFolder.NotesNFilesAPI_folder.NotesFolder.NotesActivity;
import com.example.cq_mobile.ui.home.HomeFolder.RouteNewBuildFolder.RouteNewBuildManager;
import com.example.cq_mobile.ui.home.UpdateJobsFolder.UpdateAPIFolder.UpdateJobApiManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

public class NewBuild extends AppCompatActivity implements OnMapReadyCallback{
    int page = 1;
    int pageSize = 10;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private BackPressManager backPressManager;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private NavigationManagerForNewBuild navigationManager;
    TextView showBottomSheet,start_job;
    private SetupTaskRecyclerViewManager setupTaskRecyclerViewManager;
    private Marker marker;
    private RouteNewBuildManager routeNewBuildManager;
    LatLng taskLatLng;
    LatLng userLocation;
    MarkerManager markerManager = new MarkerManager();
    BitmapDescriptor customMarkerIcon;
    ProgressBar progress_circular;
    ProgressBar progress_circular_2;
    TextView category_todo;
    ImageView statusImageView;
    LinearLayout notes, folder, docs, sheets;
    private NewBuildButtonManager newBuildButtonManager;
    String taskId;
    String jobId;
    String accessToken;
    int userId;
    String email ;
    String password ;
    SharedPrefManager sharedPrefManager;
    RecyclerView recycler_view;
    TextView task_title ;
    TextView task_location;
    TextView task_number ;
    TextView task_description;
    Spinner spinner_task ;
    String firstName ;
    String lastName;
    private boolean isChecked;
    private static final String TAG = "NewBuild";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newbuild);

        jobId = getIntent().getStringExtra("job_id");
        taskId = getIntent().getStringExtra("task_id");
        sharedPrefManager = new SharedPrefManager(NewBuild.this);

        SharedPrefTaskADandJobID sharedPrefTaskADandJobID = new SharedPrefTaskADandJobID(this);
        taskId = sharedPrefTaskADandJobID.getTaskId();
        if (taskId != null) {
            Log.d(TAG, "Task ID: " + taskId);
            Log.d(TAG, "Job ID: " + jobId);
        } else {
            taskId = String.valueOf(sharedPrefManager.getTaskId());
            jobId = String.valueOf(sharedPrefManager.getJobId());
            Log.d(TAG, "Job ID -> SharedPrefManager  " + taskId);
            Log.d(TAG, "Task ID -> SharedPrefManager  " + jobId);
        }

         accessToken = sharedPrefManager.getAccessToken();
         userId = sharedPrefManager.getUserId();
         firstName = sharedPrefManager.getFirstName();
         lastName = sharedPrefManager.getLastName();
         email = sharedPrefManager.getEmail();
         password = sharedPrefManager.getPassword();

        Log.d("NewBuild", "User ID: " + userId);

        customMarkerIcon = markerManager.getCustomCircleMarkerIcon(NewBuild.this);

        backPressManager = new BackPressManager(this);
        showBottomSheet = findViewById(R.id.showBottomSheet);
        category_todo = findViewById(R.id.category_todo);
        progress_circular = findViewById(R.id.progress_circular);
        progress_circular_2 = findViewById(R.id.progress_circular_2);
        statusImageView = findViewById(R.id.statusImageView);
        start_job = findViewById(R.id.start_job);
         recycler_view = findViewById(R.id.recycler_view);
         task_title = findViewById(R.id.task_title);
         task_location = findViewById(R.id.task_location);
         task_number = findViewById(R.id.task_number);
         task_description = findViewById(R.id.task_description);
         spinner_task = findViewById(R.id.spinner_task);
        notes  = findViewById(R.id.notes);
        folder = findViewById(R.id.folder);

        newBuildButtonManager = new NewBuildButtonManager(notes, folder);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }else {
               recreate();
        }
        routeNewBuildManager = new RouteNewBuildManager(googleMap, this,userLocation);

        fetchData();

    }

    private void fetchData() {
        NewBuildApiManager.fetchNewBuiltApiData(jobId, accessToken, new NewBuildApiManager.ApiResponseCallback<Taskmain>() {
            @Override
            public void onDataFetched(List<Taskmain> data) {
                runOnUiThread(() -> {
                    if (!data.isEmpty()) {
                        Taskmain task = data.get(0); // Assuming we need the first task
                        task_title.setText(task.getName());
                        task_location.setText(task.getAddress().getCity() + ", " + task.getAddress().getCountry());
                        task_number.setText(String.valueOf(task.getId()));
                        task_description.setText(task.getDescription());

                        // Get the status from the API response
                        String status_main = task.getStatus();

                        // Define a list of statuses
                        List<String> statusList = Arrays.asList("Todo", "Skipped", "Done");

                        // Setup Spinner Adapter
                        SetupMainTaskSpinnerAdapter adapter = new SetupMainTaskSpinnerAdapter(
                                NewBuild.this,
                                R.layout.task_spinner_item,
                                statusList,
                                jobId,
                                statusImageView,
                                status_main,
                                accessToken
                        );

                        // Set adapter to the spinner
                        spinner_task.setAdapter(adapter);

                        // Find the position of the current status and set selection
                        int position = statusList.indexOf(status_main);
                        if (position >= 0) {
                            spinner_task.setSelection(position);
                        }

                        // **Set OnItemSelectedListener here**
                        spinner_task.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String selectedStatus = (String) parent.getItemAtPosition(position);

                                // Update the status in API when the user changes the selection
                                UpdateJobApiManager.updateJobStatus(jobId, taskId, selectedStatus, accessToken);

                                // Update the statusImageView based on selection
                                if (selectedStatus.equalsIgnoreCase("Todo")) {
                                    statusImageView.setImageResource(R.drawable.button_orange);
                                } else if (selectedStatus.equalsIgnoreCase("Skipped")) {
                                    statusImageView.setImageResource(R.drawable.button_blue);
                                } else if (selectedStatus.equalsIgnoreCase("Done")) {
                                    statusImageView.setImageResource(R.drawable.button_green);
                                } else {
                                    statusImageView.setImageResource(R.drawable.button_grey);
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // Do nothing
                            }
                        });

                        // Call fetchSubTasks() to get subtasks
                        fetchSubTasks();
                    } else {
                        Toast.makeText(NewBuild.this, "No data found.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(NewBuild.this, "Error: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }
    private void fetchSubTasks() {
        NewBuildApiManager.fetchSecondaryApiData(jobId, page, pageSize, accessToken, new NewBuildApiManager.ApiResponseCallback<SubTask>() {
            @Override
            public void onDataFetched(List<SubTask> data) {
                runOnUiThread(() -> {
                    if (!data.isEmpty()) {
                        Log.d("SubTask", "Subtasks received: " + data.size());

                        for (SubTask subTask : data) {
                            Log.d("SubTask", "-----------------------------");
                            Log.d("SubTask", "ID: " + subTask.getId());
                            Log.d("SubTask", "Title: " + subTask.getTitle());
                            Log.d("SubTask", "Description: " + subTask.getDescription());
                            Log.d("SubTask", "Priority: " + subTask.getPriority());
                            Log.d("SubTask", "Status: " + subTask.getStatus());
                            Log.d("SubTask", "Start Date: " + subTask.getStartDate());
                            Log.d("SubTask", "End Date: " + subTask.getEndDate());
                            Log.d("SubTask", "Is Checked: " + subTask.isChecked());
                            Log.d("SubTask", "-----------------------------");

                            setupTaskRecyclerViewManager = new SetupTaskRecyclerViewManager(NewBuild.this, recycler_view);
                            setupTaskRecyclerViewManager.setupRecyclerView(jobId, isChecked, accessToken, taskId);
                                progress_circular_2.setVisibility(View.GONE);
                        }

                    } else {
                        Log.d("SubTask", "No subtasks found.");
                        Toast.makeText(NewBuild.this, "No tasks found.", Toast.LENGTH_SHORT).show();
                        progress_circular_2.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Log.e("SubTask", "Error fetching subtasks: " + error);
                    Toast.makeText(NewBuild.this, "Error fetching subtasks: " + error, Toast.LENGTH_SHORT).show();
                    progress_circular_2.setVisibility(View.GONE);
                });
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.googleMap = map;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        routeNewBuildManager.setGoogleMap(googleMap);

        googleMap.setOnMapLoadedCallback(() -> {
            Log.d("MapLoad", "Google Map has fully loaded");
            navigationInitialization();
        });
    }
    private void enableUserLocation() {
        if (googleMap == null) {
            Log.e("enableUserLocation", "GoogleMap is null. Cannot enable user location.");
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("enableUserLocation", "Location permission not granted.");
            Toast.makeText(this, "Permission not granted to access location", Toast.LENGTH_SHORT).show();
            return;
        }

        googleMap.setMyLocationEnabled(true);
        Log.d("enableUserLocation", "My location enabled on Google Map.");

        // Get user's current location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                Log.d("UserLocation", "Retrieved user location: Lat=" + userLocation.latitude + ", Lon=" + userLocation.longitude);

                SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
                List<Taskmain.Coordinates> coordinatesList = sharedPrefManager.getCoordinatesList();
                Log.d("enableUserLocation", "Retrieved coordinates list: " + coordinatesList.size() + " entries found.");

                MarkerManager markerManager = new MarkerManager();
                BitmapDescriptor taskMarkerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);

                UserPositionMarkerManager userPositionMarkerManager = new UserPositionMarkerManager();
                BitmapDescriptor customMarkerIcon = userPositionMarkerManager.getCustomCircleMarkerIcon(this);
                Log.d("enableUserLocation", "Custom marker icon created.");


                if (!coordinatesList.isEmpty()) {
                    Taskmain.Coordinates firstCoordinate = coordinatesList.get(0);
                    taskLatLng = new LatLng(firstCoordinate.getLatitude(), firstCoordinate.getLongitude());
                    Log.d("enableUserLocation", "First saved coordinate: Lat=" + firstCoordinate.getLatitude() + ", Lon=" + firstCoordinate.getLongitude());

                    // Update task marker with custom icon
                    taskMarkerIcon = markerManager.getCustomCircleMarkerIcon(this);
                } else {
                    Log.e("enableUserLocation", "No saved coordinates found.");
                }

                // Add user location marker
                googleMap.addMarker(new MarkerOptions()
                        .position(userLocation)
                        .title("You are here")
                        .anchor(0.5f, 0.8f)
                        .zIndex(8.0f)
                        .icon(customMarkerIcon));
                Log.d("enableUserLocation", "User location marker added on map.");

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                Log.d("enableUserLocation", "Camera moved to user location.");

                // Set destination if taskLatLng is available
                if (taskLatLng != null) {
                    new MapCameraManager(googleMap, routeNewBuildManager, taskMarkerIcon)
                            .setDestination(userLocation, taskLatLng);
                    Log.d("enableUserLocation", "Destination set on map with custom icon: Lat=" + taskLatLng.latitude + ", Lon=" + taskLatLng.longitude);
                    progress_circular.setVisibility(View.GONE);
                } else {
                    Log.w("enableUserLocation", "No task destination set. taskLatLng is null.");
                    progress_circular.setVisibility(View.GONE);
                }

            } else {
                Log.e("enableUserLocation", "Unable to retrieve current location.");
                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                progress_circular.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
            } else {
                Toast.makeText(this, "Location permission is required to display your position", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void start_Job(int userId, int jobId, String email, String password, Double latOut, Double longOut) {

        start_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccessTokenRequest request = new AccessTokenRequest(email, password);
                StartJobAPIManager startJobAPIManager = new StartJobAPIManager();
                Gson gson = new Gson();
                ClickAnimationManager.applyClickAnimation(v);

                Log.d("start_Job", "Access Token: " + accessToken);
                Log.d("start_Job", "userId: " + userId);
                Log.d("start_Job", "jobId: " + jobId);
                Log.d("start_Job", "email: " + email);
                Log.d("start_Job", "latOut: " + latOut);
                Log.d("start_Job", "longOut: " + longOut);

                startJobAPIManager.startJobWithToken(userId, jobId, latOut, longOut, request, new StartJobAPIManager.ApiCallback() {
                    @Override
                    public void onSuccess(String response) {
                        StartJobResponse startJobResponse = gson.fromJson(response, StartJobResponse.class);
                        Log.d("StartJob", "Job started successfully: " + response);

                        if (startJobResponse != null && startJobResponse.isSuccess()) {
                            // Get main message
                            String message = startJobResponse.getMessage();
                            Log.d("StartJob", "Server Message: " + message);

                            // Check for additional message (message2)
                            StartJobResponse.Data data = startJobResponse.getData();
                            if (data != null) {
                                String message2 = data.getMessage2();
                                if (message2 != null && !message2.isEmpty()) {
                                    Log.d("StartJob", "Additional Message: " + message2);
                                }

                                // Log Data fields
                                Log.d("StartJob", "Status: " + data.getStatus());
                                Log.d("StartJob", "Event: " + data.getEvent());

                                // Handle Work Object
                                StartJobResponse.Data.Work work = data.getWork();
                                if (work != null) {
                                    Log.d("StartJob", "Work ID: " + work.getId());
                                    Log.d("StartJob", "Organization ID: " + work.getOrganization_id());
                                    Log.d("StartJob", "User ID: " + work.getUser_id());
                                    Log.d("StartJob", "Job ID: " + work.getJob_id());
                                    Log.d("StartJob", "Start Time: " + work.getStart_time());
                                    Log.d("StartJob", "End Time: " + work.getEnd_time());

                                    // Handle Remarks (coordinates)
                                    StartJobResponse.Data.Work.Remarks remarks = work.getRemarks();
                                    if (remarks != null) {
                                        Log.d("StartJob", "Latitude: " + remarks.getLat());
                                        Log.d("StartJob", "Longitude: " + remarks.getLongitude());
                                    } else {
                                        Log.d("StartJob", "No location info available in remarks.");
                                    }

                                    // Handle Job inside Work
                                    StartJobResponse.Data.Work.Job job = work.getJob();
                                    if (job != null) {
                                        Log.d("StartJob", "Job Title: " + job.getTitle());
                                        Log.d("StartJob", "Job Description: " + job.getDescription());
                                        Log.d("StartJob", "Job Status: " + job.getJob_status());
                                    } else {
                                        Log.d("StartJob", "No job info available.");
                                    }
                                } else {
                                    Log.d("StartJob", "No work info available.");
                                }
                            } else {
                                Log.d("StartJob", "No data section in response.");
                            }
                        } else {
                            Log.e("StartJob", "Failed response or success flag is false.");
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e("StartJob", "Failed to start job: " + error);
                    }
                });
            }
        });



    }


    /*
      checkBoxData(accessToken,jobId, taskId, progress_circular_2);

    private void checkBoxData(String accessToken, String jobId, String taskId, ProgressBar progress_circular_2) {
        TaskActivityManager manager = new TaskActivityManager();

        if (this.jobId != null && !this.jobId.isEmpty() && this.taskId != null && !this.taskId.isEmpty()) {
            try {
                int parsedJobId = Integer.parseInt(this.jobId);
                int parsedTaskId = Integer.parseInt(this.taskId);

                manager.fetchTask(parsedJobId, parsedTaskId,accessToken, "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2", new TaskActivityManager.TaskFetchCallback() {
                            @Override
                            public void onTaskFetched(String title, String description, String priority, String status, String startDate, String endDate, String assigneeInfo, String assigneeName, boolean isChecked,
                                                      String checklistsName, String checklistsInfo) {
                                Log.d("checkBoxData", "Task Fetched Successfully:");
                                Log.d("checkBoxData", "Is Checked: " + isChecked);

                                if (jobId != null) {
                                    setupMainTaskManager.setupMainTask(jobId);
                                    setupTaskRecyclerViewManager.setupRecyclerView(jobId, isChecked,accessToken, taskId);
                                    progress_circular_2.setVisibility(View.GONE);
                                }
                            }
                            @Override
                            public void onTaskFetchError(String errorMessage) {
                                Log.e("checkBoxData", "Error fetching task: " + errorMessage);
                                Log.w("checkBoxData", "jobId:  ------------>>>>>>>>>>>>>>>> " + jobId);
                                Log.w("checkBoxData", "jobId:  ------------>>>>>>>>>>>>>>>> " + accessToken);
                                Log.w("checkBoxData", "jobId:  ------------>>>>>>>>>>>>>>>> " + taskId);

                                if (NewBuild.this.jobId != null) {
                                    setupMainTaskManager.setupMainTask(jobId);
                                    setupTaskRecyclerViewManager.setupRecyclerView(jobId, false,accessToken,taskId);
                                    progress_circular_2.setVisibility(View.GONE);
                                }

                            }
                        }
                );
            } catch (NumberFormatException e) {
                Log.e("checkBoxData", "Invalid Job ID or Task ID: " + this.jobId + ", " + this.taskId);
                this.progress_circular_2.setVisibility(View.GONE);
            }
        } else {
            Log.e("checkBoxData", "Job ID or Task ID is null or empty.");
            this.progress_circular_2.setVisibility(View.GONE);
        }
    }

 */



    private void navigationInitialization() {

        newBuildButtonManager.setButtonsVisibility(true);
        newBuildButtonManager.setButtonClickListener(view -> {
            // Handle button clicks here
            if (view == notes) {
                Intent intent = new Intent(this, NotesActivity.class);
                intent.putExtra("job_id", jobId);
                this.startActivity(intent);
            } else if (view == folder) {
                if (jobId != null) {
                    Intent intent = new Intent(NewBuild.this, FilesActivity.class);
                    intent.putExtra("job_id", jobId);
                    intent.putExtra("task_id", taskId);
                    startActivity(intent);
                }


            }
        });



        View bottomSheet = findViewById(R.id.new_built_bottom_sheet);
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheet.post(() -> bottomSheetBehavior.setPeekHeight(bottomSheet.getHeight() / 3));
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);



        CustomBottomNavView bottomNavView = findViewById(R.id.custom_bottom_nav_view);
        navigationManager = new NavigationManagerForNewBuild(this, bottomSheet, bottomSheetBehavior);
        navigationManager.setUpNavigation(bottomNavView);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backPressManager.handleBackPress(MainActivity.class);
                finish();
            }
        });

        showBottomSheet.setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheet.post(() -> bottomSheetBehavior.setPeekHeight(bottomSheet.getHeight() / 3));
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }


    public void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}


//             LatLng defaultLoc = new LatLng(51.60357351825253, 0.17148271425495226);