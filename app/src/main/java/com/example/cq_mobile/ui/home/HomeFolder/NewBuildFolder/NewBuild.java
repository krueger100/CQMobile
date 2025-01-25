package com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.cq_mobile.HelperManagers.BackPressManager;
import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.CustomBottomNavView;
import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.NavigationManagerForNewBuild;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefTaskADandJobID;
import com.example.cq_mobile.HelperManagers.mapFolder.MapCameraManager;
import com.example.cq_mobile.HelperManagers.mapFolder.MarkerManager;
import com.example.cq_mobile.HelperManagers.mapFolder.UserPositionMarkerManager;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.RetrieveDataFromAPIMangers.SetupMainTaskManager;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.RetrieveDataFromAPIMangers.SetupRecyclerViewManager;
import com.example.cq_mobile.ui.home.HomeFolder.Notes_Folder_Docs_Sheets_Files.DocsFolder.DocsActivity;
import com.example.cq_mobile.ui.home.HomeFolder.Notes_Folder_Docs_Sheets_Files.FilesFoler.FilesActivity;
import com.example.cq_mobile.ui.home.HomeFolder.Notes_Folder_Docs_Sheets_Files.NotesFolder.NotesActivity;
import com.example.cq_mobile.ui.home.HomeFolder.Notes_Folder_Docs_Sheets_Files.SheetsFolder.SheetsAcitivy;
import com.example.cq_mobile.ui.home.HomeFolder.Notes_Folder_Docs_Sheets_Files.UserInfoFolderForFiles.DocsFilesManager;
import com.example.cq_mobile.ui.home.HomeFolder.RouteNewBuildFolder.RouteNewBuildManager;
import com.example.cq_mobile.ui.home.HomeFolder.Notes_Folder_Docs_Sheets_Files.UserInfoFolderForFiles.AllFileItem;
import com.example.cq_mobile.ui.home.HomeFolder.Notes_Folder_Docs_Sheets_Files.UserInfoFolderForFiles.SheetsFilesManager;
import com.example.cq_mobile.ui.home.HomeFolder.TaskFolder.TaskActivityManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.List;

public class NewBuild extends AppCompatActivity implements OnMapReadyCallback, SetupMainTaskManager.OnCoordinatesReceivedListener {
    private int currentPage = 1; // Start from page 1
    private final int pageSize = 15; // Number of items per page
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private BackPressManager backPressManager;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private NavigationManagerForNewBuild navigationManager;
    TextView showBottomSheet;
    private SetupMainTaskManager setupMainTaskManager;
    private SetupRecyclerViewManager setupRecyclerViewManager;
    private Marker marker;
    private RouteNewBuildManager routeNewBuildManager;
    LatLng taskLatLng;
    LatLng userLocation;
    MarkerManager markerManager = new MarkerManager();
    BitmapDescriptor customMarkerIcon;
    MapCameraManager mapCameraManager;
    ProgressBar progress_circular;
    ProgressBar progress_circular_2;
TextView category_todo;
ImageView statusImageView;
LinearLayout notes,folder,docs,sheets;
    private NewBuildButtonManager newBuildButtonManager;
    String taskId;
    String jobId;
    String accessToken;
    String cqLocal = "https://aws.customquoter.co.uk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newbuild);
         jobId = getIntent().getStringExtra("job_id");

        SharedPrefTaskADandJobID sharedPrefTaskADandJobID = new SharedPrefTaskADandJobID(this);
        taskId = sharedPrefTaskADandJobID.getTaskId();
        if (taskId != null) {
            Log.d("TASKID", "Task ID: " + taskId);
            Log.d("TASKID", "Job ID: " + jobId);
        } else {
            taskId = "782";
            jobId = "5703";
            Log.d("TASKID", "No Job or Task ID found in SharedPreferences");
        }


        SharedPrefManager sharedPrefManager = new SharedPrefManager(NewBuild.this);
         accessToken = sharedPrefManager.getAccessToken();
        String userId = sharedPrefManager.getUserId();
        String firstName = sharedPrefManager.getFirstName();
        String lastName = sharedPrefManager.getLastName();
        String email = sharedPrefManager.getEmail();

        Log.d("NewBuild", "Retrieved User Data: ");
        Log.d("NewBuild", "Access Token: " + accessToken);
        Log.d("NewBuild", "User ID: " + userId);

        customMarkerIcon = markerManager.getCustomCircleMarkerIcon(NewBuild.this);

        backPressManager = new BackPressManager(this);
        showBottomSheet = findViewById(R.id.showBottomSheet);
        category_todo = findViewById(R.id.category_todo);
        progress_circular = findViewById(R.id.progress_circular);
        progress_circular_2 = findViewById(R.id.progress_circular_2);
        statusImageView = findViewById(R.id.statusImageView);



        notes  = findViewById(R.id.notes);
        folder = findViewById(R.id.folder);
        docs = findViewById(R.id.docs);
        sheets  = findViewById(R.id.sheets);
        newBuildButtonManager = new NewBuildButtonManager(notes, folder, docs, sheets);

        String sheets_job = "sheets";
        String docs_job = "docs";


        // Handle Job ------------------>>
        SheetsFilesManager sheetsFilesManager = new SheetsFilesManager(this, accessToken);
        sheetsFilesManager.loadFiles(cqLocal, Integer.parseInt(jobId), 1, 10, sheets_job, new SheetsFilesManager.FilesCallback() {
            @Override
            public void onFilesLoaded(List<AllFileItem> files) {
                // Handle the loaded files
                for (AllFileItem file : files) {
                    Log.d("SheetsFilesManager", "File ID: " + file.getId());
                    Log.d("SheetsFilesManager", "File Title: " + file.getTitle());
                    Log.d("SheetsFilesManager", "File Other_title: " + file.getOther_title());
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Handle the error
                Log.e("MainActivity", "Error: " + errorMessage);
            }
        });
        DocsFilesManager docsFilesManager = new DocsFilesManager(this, accessToken);
        docsFilesManager.loadFilesDocs(cqLocal, Integer.parseInt(jobId), 1, 10, docs_job, new DocsFilesManager.FilesDocsCallback() {
            @Override
            public void onFilesDocsLoaded(List<AllFileItem> files) {
                // Handle the loaded files
                for (AllFileItem file : files) {
                    Log.d("DocsFilesManager", "File ID: " + file.getId());
                    Log.d("DocsFilesManager", "File Title: " + file.getTitle());
                    Log.d("DocsFilesManager", "File Other_title: " + file.getOther_title());
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Handle the error
                Log.e("MainActivity", "Error: " + errorMessage);
            }
        });
        // Handle Job <<--------------------


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
            } else if (view == docs) {
                if (jobId != null) {
                    Intent intent = new Intent(NewBuild.this, DocsActivity.class);
                    intent.putExtra("job_id", jobId);
                    intent.putExtra("task_id", taskId);
                    startActivity(intent);
                }

            } else if (view == sheets) {
                Intent intent = new Intent(this, SheetsAcitivy.class);
                intent.putExtra("job_id", jobId);
                intent.putExtra("task_id", taskId);
                startActivity(intent);
            }
        });

        // Initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);  // Will call onMapReady when ready
        }

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

        // Initialize RouteNewBuildManager
        routeNewBuildManager = new RouteNewBuildManager(googleMap, this,userLocation);

    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        routeNewBuildManager.setGoogleMap(googleMap);

        setupRecyclerViewManager = new SetupRecyclerViewManager(NewBuild.this, findViewById(R.id.recycler_view));
        setupMainTaskManager = new SetupMainTaskManager(NewBuild.this, googleMap, findViewById(R.id.task_title), findViewById(R.id.task_description), findViewById(R.id.task_location)
                , findViewById(R.id.task_number), findViewById(R.id.spinner_task), NewBuild.this,category_todo,statusImageView);  // Pass listener for coordinates

        checkBoxData(jobId,accessToken,taskId,progress_circular_2);


        // Check location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission not granted to access location", Toast.LENGTH_SHORT).show();
            return;
        }

        googleMap.setMyLocationEnabled(true);

        // Get the user's current location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                 userLocation = new LatLng(location.getLatitude(), location.getLongitude());
             //  googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                UserPositionMarkerManager userPositionMarkerManager = new UserPositionMarkerManager();
                BitmapDescriptor customMarkerIcon = userPositionMarkerManager.getCustomCircleMarkerIcon(this);

                Marker userMarker;
                userMarker =  googleMap.addMarker(new MarkerOptions()
                        .position(userLocation)
                        .title("You are here")
                        .anchor(0.6f, 0.9f)
                        .zIndex(8.0f)
                        .icon(customMarkerIcon));


                Log.d("UserLocation", userLocation.latitude + " " + userLocation.longitude);

            } else {
                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onCoordinatesReceived(double latitude, double longitude) {
        // Check if coordinates are valid
        if (latitude != 0.0 && longitude != 0.0) {
            taskLatLng = new LatLng(latitude, longitude);

            // Make sure taskLatLng is not null
            if (taskLatLng != null) {
                marker = googleMap.addMarker(new MarkerOptions()
                        .position(taskLatLng)
                       .icon(customMarkerIcon)
                        .anchor(0.5f, 0.8f)
                        .zIndex(5.0f));


                mapCameraManager = new MapCameraManager(googleMap, routeNewBuildManager, customMarkerIcon);
                mapCameraManager.setDestination(userLocation, taskLatLng);
                progress_circular.setVisibility(View.GONE);


            } else {
                Log.e("onCoordinatesReceived", "Invalid LatLng: " + latitude + ", " + longitude);
                progress_circular.setVisibility(View.GONE);

            }
        } else {
            Log.e("onCoordinatesReceived", "Received invalid coordinates: " + latitude + ", " + longitude);
            progress_circular.setVisibility(View.GONE);

        }
    }


    private void checkBoxData(String jobId, String accessToken, String taskId, ProgressBar progress_circular_2) {
        TaskActivityManager manager = new TaskActivityManager();

        if (jobId != null && !jobId.isEmpty() && taskId != null && !taskId.isEmpty()) {
            try {
                int parsedJobId = Integer.parseInt(jobId); // job_schedule_id
                int parsedTaskId = Integer.parseInt(taskId); // task_id

                manager.fetchTask(
                        parsedJobId,
                        parsedTaskId,
                        accessToken, // token
                        "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2", // api_key
                        new TaskActivityManager.TaskFetchCallback() {
                            @Override
                            public void onTaskFetched(String title, String description, String priority, String status, String startDate, String endDate, String assigneeInfo, String assigneeName, boolean isChecked,
                                                      String checklistsName, String checklistsInfo) {
                                Log.d("checkBoxData", "Task Fetched Successfully:");
                                Log.d("checkBoxData", "Is Checked: " + isChecked);

                                if (jobId != null) {
                                    setupMainTaskManager.setupMainTask(jobId);
                                    setupRecyclerViewManager.setupRecyclerView(jobId, isChecked, accessToken, taskId);
                                    progress_circular_2.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onTaskFetchError(String errorMessage) {
                                Log.e("checkBoxData", "Error fetching task: " + errorMessage);

                                if (jobId != null) {
                                    setupMainTaskManager.setupMainTask(jobId);
                                    setupRecyclerViewManager.setupRecyclerView(jobId, false, accessToken, taskId);
                                    progress_circular_2.setVisibility(View.GONE);
                                }
                            }
                        }
                );
            } catch (NumberFormatException e) {
                Log.e("checkBoxData", "Invalid Job ID or Task ID: " + jobId + ", " + taskId);
                progress_circular_2.setVisibility(View.GONE);
                Toast.makeText(this, "Invalid Job or Task ID format", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("checkBoxData", "Job ID or Task ID is null or empty.");
            progress_circular_2.setVisibility(View.GONE);
            Toast.makeText(this, "Job ID or Task ID cannot be null or empty", Toast.LENGTH_SHORT).show();
        }
    }



    public void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}


//             LatLng defaultLoc = new LatLng(51.60357351825253, 0.17148271425495226);