package com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

import com.example.cq_mobile.Clock.ClockFolder.ClockInAPIFolder.TimerManager;
import com.example.cq_mobile.Clock.ClockFolder.ClockOutFolder.ClockOutManager;
import com.example.cq_mobile.Clock.StartAndStopJobsFolder.StartJobAPIManager;
import com.example.cq_mobile.Clock.StartAndStopJobsFolder.StartJobResponse;
import com.example.cq_mobile.Clock.StartAndStopJobsFolder.StopJobApiManager;
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
    Spinner spinner_task;
    String firstName;
    String lastName;
    String startJob;
    private boolean isChecked;
    private static final String TAG = "NewBuild";
    LinearLayout emptyTask;
    TextView progress_text;
    ProgressBar progressbar;
    Double latOut;
    Double longOut;
String jobTitle;

    private Double lat = null;
    private Double lon = null;

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
         startJob = sharedPrefManager.getKeyStartDate();
          latOut = sharedPrefManager.getLatitude();
           longOut= sharedPrefManager.getLongitude();

        Log.d("NewBuild", "User ID: " + userId);
        Log.d("start_Job", "latOut: " + latOut);
        Log.d("start_Job", "longOut: " + longOut);


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
        emptyTask  = findViewById(R.id.emptyTask);
         progress_text= findViewById(R.id.progress_text);
         progressbar= findViewById(R.id.progressbar);

        newBuildButtonManager = new NewBuildButtonManager(notes, folder);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }else {
               recreate();
        }
        routeNewBuildManager = new RouteNewBuildManager(googleMap, this,userLocation);


        start_job.setEnabled(false);
        start_job.setAlpha(0.5f);

        fetchData();



    }


    private void fetchData() {
        NewBuildApiManager.fetchNewBuiltApiData(jobId, accessToken, new NewBuildApiManager.ApiResponseCallback<Taskmain>() {
            @Override
            public void onDataFetched(List<Taskmain> data) {
                runOnUiThread(() -> {
                    if (data == null || data.isEmpty()) {
                        Toast.makeText(NewBuild.this, "No data found.", Toast.LENGTH_SHORT).show();
                        return; // Exit early to avoid crashes
                    }

                    Taskmain task = data.get(0);
                    if (task == null) {
                        Toast.makeText(NewBuild.this, "Task data is missing.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Safely set values with null checks
                    task_title.setText(task.getName() != null ? task.getName() : "No Title Available");
                    task_number.setText(task.getId() == -1 ? String.valueOf(task.getId()) : "N/A");
                    task_description.setText(task.getDescription() != null ? task.getDescription() : "No Description Available");
                    category_todo.setText(task.getCategory() != null ? task.getCategory() : "No Category");
                    jobTitle= task.getName() != null ? task.getName() : "No Title Available";

                    String city = (task.getAddress() != null && task.getAddress().getCity() != null) ? task.getAddress().getCity() : "";
                    String country = (task.getAddress() != null && task.getAddress().getCountry() != null) ? task.getAddress().getCountry() : "";
                    String postal = (task.getAddress() != null && task.getAddress().getPostal_code() != null) ? task.getAddress().getPostal_code() : "";
                    String address = (task.getAddress() != null && task.getAddress().getAddress() != null) ? task.getAddress().getAddress() : "";
                    String address1 = (task.getAddress() != null && task.getAddress().getAddress1() != null) ? task.getAddress().getAddress1() : "";

                    String location = city + (city.isEmpty() || country.isEmpty() ? "" : ", ") + country;
                    location += (!location.isEmpty() && !postal.isEmpty()) ? ", " + postal : postal;

                   String  fullAddress = address;
                    if (!address1.isEmpty()) {
                        fullAddress += (fullAddress.isEmpty() ? "" : ", ") + address1;
                    }
                    if (!location.isEmpty()) {
                        fullAddress += (fullAddress.isEmpty() ? "" : ", ") + location;
                    }

                    task_location.setText(!fullAddress.isEmpty() ? fullAddress : "No Address is set");

                    lat = (task.getCoordinates() != null) ? task.getCoordinates().getLatitude() : null;
                    lon = (task.getCoordinates() != null) ? task.getCoordinates().getLongitude() : null;

                    Log.d("CoordinatesNewBuild", "LAT: -> " + lat + " LON: -> " + lon);
                    if (lat != null && lon != null) {
                        enableUserLocation();
                    } else {
                        Log.e("CoordinatesNewBuild", "lat or lon is null, cannot enable location.");
                        noCoordinatesFound();

                    }

                    String status_main = (task.getStatus() != null) ? task.getStatus() : "Todo"; // Default to "Todo" if null

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
                            accessToken,progressbar,progress_text
                    );

                    spinner_task.setAdapter(adapter);

                    // Find the position of the current status and set selection
                    int position = statusList.indexOf(status_main);
                    spinner_task.setSelection(position >= 0 ? position : 0); // Default to first item if not found

                    // **Set OnItemSelectedListener**
                    spinner_task.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedStatus = (String) parent.getItemAtPosition(position);

                                // Update the status in API when the user changes the selection
                                UpdateJobApiManager.updateJobStatus(jobId, taskId, selectedStatus, accessToken,progressbar,progress_text);

                            // Update the statusImageView based on selection
                            if ("Todo".equalsIgnoreCase(selectedStatus)) {
                                statusImageView.setImageResource(R.drawable.button_orange);
                            } else if ("Skipped".equalsIgnoreCase(selectedStatus)) {
                                statusImageView.setImageResource(R.drawable.button_blue);
                            } else if ("Done".equalsIgnoreCase(selectedStatus)) {
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

                    fetchSubTasks();
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
                    if (data == null || data.isEmpty()) {
                        Log.d("SubTask", "No subtasks found.");
                        Toast.makeText(NewBuild.this, "No tasks found.", Toast.LENGTH_SHORT).show();
                        emptyTask.setVisibility(View.VISIBLE);
                        progress_circular_2.setVisibility(View.GONE);
                        return;
                    }

                    Log.d("SubTask", "Subtasks received: " + data.size());

                    for (SubTask subTask : data) {
                        if (subTask != null) { // Prevent null pointer issues
                            Log.d("SubTask", "-----------------------------");
                            Log.d("SubTask", "ID: " + (subTask.getId() != null ? subTask.getId() : "N/A"));
                            Log.d("SubTask", "Title: " + (subTask.getTitle() != null ? subTask.getTitle() : "No Title"));
                            Log.d("SubTask", "Description: " + (subTask.getDescription() != null ? subTask.getDescription() : "No Description"));
                            Log.d("SubTask", "Priority: " + (subTask.getPriority() != null ? subTask.getPriority() : "No Priority"));
                            Log.d("SubTask", "Status: " + (subTask.getStatus() != null ? subTask.getStatus() : "No Status"));
                            Log.d("SubTask", "Start Date: " + (subTask.getStartDate() != null ? subTask.getStartDate() : "No Start Date"));
                            Log.d("SubTask", "End Date: " + (subTask.getEndDate() != null ? subTask.getEndDate() : "No End Date"));
                            Log.d("SubTask", "Is Checked: " + subTask.isChecked());
                            Log.d("SubTask", "-----------------------------");
                        }
                    }

                    // Setup RecyclerView **once**, not inside the loop
                    setupTaskRecyclerViewManager = new SetupTaskRecyclerViewManager(NewBuild.this, recycler_view,progressbar,progress_text);
                    setupTaskRecyclerViewManager.setupRecyclerView(jobId, isChecked, accessToken, taskId);

                    progress_circular_2.setVisibility(View.GONE);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Log.e("SubTask", "Error fetching subtasks: " + error);
                    Toast.makeText(NewBuild.this, "Error fetching subtasks: " + error, Toast.LENGTH_SHORT).show();
                    emptyTask.setVisibility(View.VISIBLE);
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


            //    if (!coordinatesList.isEmpty()) {
                if (lat != null && lon != null) {
                    Taskmain.Coordinates firstCoordinate = coordinatesList.get(0);
           //         taskLatLng = new LatLng(firstCoordinate.getLatitude(), firstCoordinate.getLongitude());
                    taskLatLng = new LatLng(lat, lon);

                    Log.d("enableUserLocation", "First saved coordinate: Lat=" + firstCoordinate.getLatitude() + ", Lon=" + firstCoordinate.getLongitude());


                    start_job.setEnabled(true);
                    start_job.setAlpha(1.0f);
         //      start_jobBranch(firstCoordinate.getLatitude(),firstCoordinate.getLongitude());
                    start_jobBranch(lat,lon);


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

    private void start_jobBranch(double latitude, double longitude) {
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
                Log.d("start_Job", "latOut: " + latitude);
                Log.d("start_Job", "longOut: " + longitude);

                startJobAPIManager.startJobWithToken(userId, jobId,  latitude,longitude, request, new StartJobAPIManager.ApiCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Log.d("StartJob", "Job started successfully. Response: " + response);

                        StartJobResponse startJobResponse = gson.fromJson(response, StartJobResponse.class);
                        if (startJobResponse == null) {
                            Log.e("StartJob", "Response parsing failed.");
                            return;
                        }

                        // Log top-level response data
                        boolean success = startJobResponse.isSuccess();
                        String serverMessage = startJobResponse.getMessage();
                        StartJobResponse.Data data = startJobResponse.getData();

                        Log.d("StartJob", "Success: " + success);
                        Log.d("StartJob", "Server Message: " + serverMessage);

                        if (data != null) {
                            String status = data.getStatus();
                            String event = data.getEvent();
                            String message2 = data.getMessage2();

                            Log.d("StartJob", "Status: " + status);
                            Log.d("StartJob", "Event: " + event);
                            Log.d("StartJob", "Additional Message: " + (message2 != null ? message2 : "N/A"));

                            // Extract work details
                            StartJobResponse.Data.Work work = data.getWork();
                            if (work != null) {
                                int workId = work.getId();
                                int organizationId = work.getOrganization_id();
                                int workUserId = work.getUser_id();
                                int jobId = work.getJob_id();
                                String startTime = work.getStart_time();

                                Log.d("StartJob", "Work ID: " + workId);
                                Log.d("StartJob", "Organization ID: " + organizationId);
                                Log.d("StartJob", "User ID: " + workUserId);
                                Log.d("StartJob", "Job ID: " + jobId);
                                Log.d("StartJob", "Start Time: " + startTime);




                                // Extract location details
                                StartJobResponse.Data.Work.Remarks remarks = work.getRemarks();
                                if (remarks != null) {
                                    double latitude = remarks.getLat();
                                    double longitude = remarks.getLongitude();

                                    Log.d("StartJob", "Latitude: " + latitude);
                                    Log.d("StartJob", "Longitude: " + longitude);
                                }

                                // Extract job details
                                StartJobResponse.Data.Work.Job job = work.getJob();
                                if (job != null) {
                                    String jobTitle = job.getTitle();
                                    int jobStatus = job.getJob_status();

                                    Log.d("StartJob", "Job Title: " + jobTitle);
                                    Log.d("StartJob", "Job Status: " + jobStatus);
                                }
                            }
                        }
                        runOnUiThread(() -> {
                            showAlertDialog(NewBuild.this, success, serverMessage, accessToken,  userId, progress_circular, startJob, jobId, taskId);

                        });


                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e("StartJob", "Failed to start job: " + error);

                    }
                });
            }
        });

    }

    private void noCoordinatesFound() {
        new AlertDialog.Builder(this)
                .setTitle("Location Not Found")
                .setMessage("No valid coordinates were found. Client/Site details is Empty Check Jobs in CQBMS APP.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
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

    private void navigationInitialization() {

        newBuildButtonManager.setButtonsVisibility(true);
        newBuildButtonManager.setButtonClickListener(view -> {
            ClickAnimationManager.applyClickAnimation(view);
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
        bottomSheetBehavior.setHideable(true);
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

    private void showAlertDialog(NewBuild newBuild, boolean success, String serverMessage, String accessToken, int userId, ProgressBar progress_circular, String startJob, String jobId, String taskId) {
        if (newBuild == null) {
            Log.e("AlertDialog", "Context is empty, cannot show dialog");
            return;
        }

        TimerManager timerManager = TimerManager.getInstance(NewBuild.this, startJob);
        timerManager.resetTimer(NewBuild.this);
        sharedPrefManager.saveStartedJob(jobTitle);
        new AlertDialog.Builder(newBuild)
                .setTitle(serverMessage)
                .setMessage("Choose from the options")
                .setPositiveButton("Clock out", (dialog, which) -> {
                    // Auto Clock Out on OK
                    ClockOutManager clockOutManager = new ClockOutManager(newBuild, progress_circular, Integer.parseInt(jobId), Integer.parseInt(taskId), userId, startJob);
                    clockOutManager.AutoClockOutandLogout(accessToken, Integer.parseInt(jobId),Integer.parseInt(taskId),startJob);
                })
                .setNegativeButton("Stop job", (dialog, which) -> {
                    StopJobApiManager.stopJob(accessToken, userId, progress_circular, new StopJobApiManager.ApiCallback() {
                        @Override
                        public void onSuccess(String message) {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                new AlertDialog.Builder(newBuild)
                                        .setTitle(serverMessage)
                                        .setMessage("Job stopped successfully.")
                                        .setPositiveButton("OK", (dialog2, which2) -> {
                                            SharedPrefManager sharedPrefManager = new SharedPrefManager(newBuild);
                                            sharedPrefManager.clearStartJob();
                                            dialog2.dismiss();
                                        })
                                        .show();
                                dialog.dismiss();
                            });
                        }

                        @Override
                        public void onFailure(String error) {
                            Log.e("StartJob", "Failed to stop job: " + error);
                            new Handler(Looper.getMainLooper()).post(() ->
                                    Toast.makeText(newBuild, "Failed to stop job: " + error, Toast.LENGTH_SHORT).show()
                            );
                        }
                    });
                })
                .setNeutralButton("Continue", (dialog, which) -> dialog.dismiss()) //
                .show();

    }


    public void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}


//             LatLng defaultLoc = new LatLng(51.60357351825253, 0.17148271425495226);

/*
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

 */