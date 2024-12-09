package com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.SpinnerFolder.CustomSpinnerAdapter;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.SubTasks.SubTask;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.SubTasks.SubTaskAdapter;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.TaskMainFolder.Taskmain;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;


public class NewBuild extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newbuild);

        // Get the job ID passed from the previous activity
        String jobId = getIntent().getStringExtra("job_id");
        if (jobId != null) {
            Log.d("job ID ->", "Received Job ID: " + jobId);
        } else {
            Log.e("job ID ->", "No Job ID received!");
        }

        // Set up the BottomSheet
        View bottomSheet = findViewById(R.id.new_built_bottom_sheet);
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheet.post(() -> bottomSheetBehavior.setPeekHeight(bottomSheet.getHeight() / 3));
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        setupRecyclerView(jobId);
        setupMainTask(jobId);


        // Initialize the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize the Fused Location Provider
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

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
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                googleMap.addMarker(new MarkerOptions().position(userLocation).title("You are here"));
            } else {
                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupMainTask(String jobId) {
        TextView task_title = findViewById(R.id.task_title);
        TextView task_description = findViewById(R.id.task_description);
        TextView task_location = findViewById(R.id.task_location);
        TextView task_number = findViewById(R.id.task_number);
        Spinner spinner_task = findViewById(R.id.spinner_task);

        NewBuildApiManager.fetchNewBuiltApiData(jobId, new NewBuildApiManager.ApiResponseCallback<Taskmain>() {
            @Override
            public void onDataFetched(List<Taskmain> data) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    StringBuilder taskTitle = new StringBuilder();
                    StringBuilder taskDescription = new StringBuilder();
                    StringBuilder taskLocation = new StringBuilder();
                    StringBuilder taskNumber = new StringBuilder();

                    for (Taskmain taskmain : data) {
                        // Task title and description
                        taskTitle.append(taskmain.getName());
                        taskDescription.append(taskmain.getDescription());

                        String clientInfo = taskmain.getClient_details() != null
                                ? taskmain.getClient_details().getPhone()
                                : "No client info";
                        taskNumber.append(clientInfo);

                        String address = taskmain.getAddress() != null
                                ? taskmain.getAddress().getAddress()
                                : "No address";
                        taskLocation.append(address);

                        if (googleMap != null) {
                            Taskmain.Coordinates coordinates = taskmain.getCoordinates();
                            if (coordinates != null) {
                                try {
                                    double latitude = Double.parseDouble(coordinates.getLatitude());
                                    double longitude = Double.parseDouble(coordinates.getLongitude());
                                    LatLng taskLatLng = new LatLng(latitude, longitude);

                                    // Add marker to the map
                                    googleMap.addMarker(new MarkerOptions()
                                            .position(taskLatLng)
                                            .title(taskmain.getName())
                                            .snippet(taskmain.getDescription()));

                                    // Optionally, focus camera on the first task
                                    if (data.indexOf(taskmain) == 0) {
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(taskLatLng, 15));
                                    }
                                } catch (NumberFormatException e) {
                                    Log.e("NewBuild", "Invalid coordinates: " + e.getMessage());
                                }
                            }
                        }
                    }

                    // Set up spinner options
                    List<String> options = new ArrayList<>();
                    options.add("In Progress");
                    options.add("Pending");
                    options.add("Under Inspection");
                    options.add("Done");

                    // Set up spinner adapter
                    CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(
                            NewBuild.this,
                            R.layout.task_spinner_item,
                            options,
                            R.drawable.arrow_down_24
                    );

                    spinner_task.setAdapter(adapter);

                    // Set spinner item selection listener
                    spinner_task.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position >= 0 && position < data.size()) { // Ensure position is within bounds
                                Taskmain selectedTask = data.get(position);

                                task_title.setText(selectedTask.getName());
                                task_description.setText(selectedTask.getDescription());

                                String clientInfo = selectedTask.getClient_details() != null
                                        ? selectedTask.getClient_details().getPhone()
                                        : "No client info";
                                task_number.setText(clientInfo);

                                String address = selectedTask.getAddress() != null
                                        ? selectedTask.getAddress().getAddress()
                                        : "No address";
                                task_location.setText(address);
                            } else {
                                Log.e("NewBuild", "Selected spinner position is out of bounds");
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Optional: handle case when no item is selected
                        }
                    });

                    // Update the TextViews with the tasks
                    task_title.setText(taskTitle.toString());
                    task_description.setText(taskDescription.toString());
                    task_location.setText(taskLocation.toString());
                    task_number.setText(taskNumber.toString());
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() ->
                        Toast.makeText(NewBuild.this, "Error fetching data: " + error, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void setupRecyclerView(String jobId) {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Fetch secondary tasks and update RecyclerView adapter using jobId
        NewBuildApiManager.fetchSecondaryApiData(jobId, new NewBuildApiManager.ApiResponseCallback<SubTask>() {
            @Override
            public void onDataFetched(List<SubTask> secondaryData) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    SubTaskAdapter subTaskAdapter = new SubTaskAdapter(secondaryData);
                    recyclerView.setAdapter(subTaskAdapter);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() ->
                        Toast.makeText(NewBuild.this, "Error fetching secondary data: " + error, Toast.LENGTH_SHORT).show()
                );
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
}
