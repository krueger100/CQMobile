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
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefTaskADandJobID;
import com.example.cq_mobile.HelperManagers.mapFolder.MapCameraManager;
import com.example.cq_mobile.HelperManagers.mapFolder.MarkerManager;
import com.example.cq_mobile.HelperManagers.mapFolder.UserPositionMarkerManager;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.RetrieveDataFromAPIMangers.SetupMainTaskManager;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.RetrieveDataFromAPIMangers.SetupRecyclerViewManager;
import com.example.cq_mobile.ui.home.HomeFolder.Notes_Folder_Docs_Sheets_Files.DocsFolder.DocsActivity;
import com.example.cq_mobile.ui.home.HomeFolder.Notes_Folder_Docs_Sheets_Files.FilesFoler.FileItem;
import com.example.cq_mobile.ui.home.HomeFolder.Notes_Folder_Docs_Sheets_Files.FilesFoler.FilesActivity;
import com.example.cq_mobile.ui.home.HomeFolder.Notes_Folder_Docs_Sheets_Files.NotesFolder.NotesActivity;
import com.example.cq_mobile.ui.home.HomeFolder.Notes_Folder_Docs_Sheets_Files.SheetsFolder.SheetsAcitivy;
import com.example.cq_mobile.ui.home.HomeFolder.RouteNewBuildFolder.RouteNewBuildManager;
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
TextView category_todo;
ImageView statusImageView;

LinearLayout notes,folder,docs,sheets;
    private NewBuildButtonManager newBuildButtonManager;

    String taskId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newbuild);

        customMarkerIcon = markerManager.getCustomCircleMarkerIcon(NewBuild.this);
        backPressManager = new BackPressManager(this);
        showBottomSheet = findViewById(R.id.showBottomSheet);
        category_todo = findViewById(R.id.category_todo);
        progress_circular = findViewById(R.id.progress_circular);
        statusImageView = findViewById(R.id.statusImageView);



        String jobId = getIntent().getStringExtra("job_id");
        if (jobId != null) {
            setupRecyclerViewManager = new SetupRecyclerViewManager(this, findViewById(R.id.recycler_view));
            setupRecyclerViewManager.setupRecyclerView(jobId);
        } else {
            Log.e("job ID ->", "No Todo ID received!");
        }
        setupRecyclerViewManager = new SetupRecyclerViewManager(this, findViewById(R.id.recycler_view));
        setupRecyclerViewManager.setupRecyclerView(jobId);

        SharedPrefTaskADandJobID sharedPrefTaskADandJobID = new SharedPrefTaskADandJobID(this);
         taskId = sharedPrefTaskADandJobID.getTaskId();
        if (taskId != null) {
            Log.d("TASKID", "Task ID: " + taskId);
        } else {
            Log.d("TASKID", "No Job or Task ID found in SharedPreferences");
        }

        notes  = findViewById(R.id.notes);
        folder = findViewById(R.id.folder);
        docs = findViewById(R.id.docs);
        sheets  = findViewById(R.id.sheets);
        newBuildButtonManager = new NewBuildButtonManager(notes, folder, docs, sheets);





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
                Intent intent = new Intent(this, DocsActivity.class);
                intent.putExtra("job_id", jobId);
                this.startActivity(intent);
            } else if (view == sheets) {
                Intent intent = new Intent(this, SheetsAcitivy.class);
                intent.putExtra("job_id", jobId);
                this.startActivity(intent);
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

        // Initialize the SetupMainTaskManager only after googleMap is ready
        setupMainTaskManager = new SetupMainTaskManager(this, googleMap, findViewById(R.id.task_title),
                findViewById(R.id.task_description), findViewById(R.id.task_location), findViewById(R.id.task_number),
                findViewById(R.id.spinner_task), this,category_todo,statusImageView);  // Pass listener for coordinates

        String jobId = getIntent().getStringExtra("job_id");
        if (jobId != null) {
            setupMainTaskManager.setupMainTask(jobId);
        }


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


    public void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}


//             LatLng defaultLoc = new LatLng(51.60357351825253, 0.17148271425495226);