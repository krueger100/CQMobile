package com.example.cq_mobile.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.ClockOutVisibilityHandler;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.HelperManagers.mapFolder.MarkerManager;
import com.example.cq_mobile.HelperManagers.mapFolder.UserPositionMarkerManager;
import com.example.cq_mobile.R;
import com.example.cq_mobile.databinding.FragmentMapBinding;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.TaskMainFolder.Taskmain;
import com.example.cq_mobile.ui.home.HomeFolder.RouteNewBuildFolder.RouteAPIFolder.Routemain;
import com.example.cq_mobile.ui.map.RouteFolder.RouteManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private FragmentMapBinding binding;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng userLocationLatLng;
    private LatLng destinationLatLng;
    private RouteManager routeManager;
    private Routemain routeData;
    MarkerManager markerManager = new MarkerManager();
    BitmapDescriptor customMarkerIcon;
    int jobId;
    String accessToken;
    private ClockOutVisibilityHandler visibilityHandler;
    List<Taskmain.Coordinates> coordinatesList;

    private final ActivityResultLauncher<String> locationPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    getUserLocation();
                } else {
                    Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        routeManager = new RouteManager(googleMap, requireContext());

        // Set up the button click listener
//        binding.setRouteButton.setOnClickListener(v -> {
//            if (userLocationLatLng != null && destinationLatLng != null) {
//                // Use RouteManager to draw the route when button is clicked
//                routeManager.drawRoute(userLocationLatLng, destinationLatLng);
//            } else {
//                Toast.makeText(getContext(), "Please select a destination by clicking on the map or marker", Toast.LENGTH_SHORT).show();
//            }
//        });
//


        SharedPrefManager sharedPrefManager = new SharedPrefManager(getContext());
         accessToken = sharedPrefManager.getAccessToken();
        int userId = sharedPrefManager.getUserId();
        jobId = sharedPrefManager.getJobId();
        String firstName = sharedPrefManager.getFirstName();
        String lastName = sharedPrefManager.getLastName();
        String email = sharedPrefManager.getEmail();
        coordinatesList = sharedPrefManager.getCoordinatesList();

        Log.d("MapFragmentSharedPreff", "Retrieved coordinates list: " + coordinatesList.size() + " entries found.");
        Log.d("MapFragmentSharedPreff", "Retrieved User Data: ");
        Log.d("MapFragmentSharedPreff", "Access Token: " + accessToken);
        Log.d("MapFragmentSharedPreff", "User ID: " + userId);
        Log.d("MapFragmentSharedPreff", "First Name: " + firstName);
        Log.d("MapFragmentSharedPreff", "Last Name: " + lastName);
        Log.d("MapFragmentSharedPreff", "Email: " + email);

        // Retrieve the job_id from arguments
        Log.w("MapFragment", "Received Job ID in MapFragment: -> " + jobId);

        // Set up the button click listener
        binding.setRouteButton.setOnClickListener(v -> {
            if (userLocationLatLng != null && destinationLatLng != null) {
                routeManager.drawRoute(userLocationLatLng, destinationLatLng);
            } else {
                Toast.makeText(getContext(), "Please select a destination by clicking on the map or marker", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    /*
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        getUserLocation();

        routeManager.setGoogleMap(googleMap);
        addRouteDataMarker(googleMap);



        googleMap.setOnMapClickListener(latLng -> {
            if (userLocationLatLng != null) {
                destinationLatLng = latLng;

                // googleMap.clear();  // Uncomment this if you want to clear previous markers
                googleMap.addMarker(new MarkerOptions().position(userLocationLatLng).title("Your Location"));
                googleMap.addMarker(new MarkerOptions().position(destinationLatLng).title("Destination"));

                // Log the destination coordinates
                Log.d("MapFragment", "Destination set: Lat: " + destinationLatLng.latitude + ", Lng: " + destinationLatLng.longitude);
            }
        });

        googleMap.setOnMarkerClickListener(marker -> {
            if (marker.getTag() != null && marker.getTag() instanceof Routemain) {
                Routemain route = (Routemain) marker.getTag();
                destinationLatLng = marker.getPosition();



                // Clear only specific markers related to the route
                // googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(userLocationLatLng).title("Your Location"));
                googleMap.addMarker(new MarkerOptions().position(destinationLatLng).title(route.getName()));
            }
            return false;
        });
    }

     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        getUserLocation();

        routeManager.setGoogleMap(googleMap);
        addRouteDataMarker(googleMap);

        googleMap.setOnMapClickListener(latLng -> {
            if (userLocationLatLng != null) {
                destinationLatLng = latLng;
                googleMap.addMarker(new MarkerOptions().position(destinationLatLng).title("Destination"));
                Log.d("MapFragment", "Destination set: " + destinationLatLng.latitude + ", " + destinationLatLng.longitude);
            }
        });

        googleMap.setOnMarkerClickListener(marker -> {
            Object tag = marker.getTag();
            if (tag instanceof LatLng) {
                destinationLatLng = (LatLng) tag;
                Log.d("MapFragment", "Marker clicked at: " + destinationLatLng.latitude + ", " + destinationLatLng.longitude);
                Toast.makeText(getContext(), "Press to Set Route", Toast.LENGTH_SHORT).show();
            }
            return false;
        });
    }

private void addRouteDataMarker(GoogleMap googleMap) {
    if (coordinatesList != null && !coordinatesList.isEmpty()) {
        customMarkerIcon = markerManager.getCustomCircleMarkerIcon(getContext());

        for (Taskmain.Coordinates coord : coordinatesList) {
            try {
                LatLng routeLocation = new LatLng(coord.getLatitude(), coord.getLongitude());
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(routeLocation)
                        .title("Job Location")
                        .anchor(0.5f, 0.8f)
                        .zIndex(8.0f)
                        .icon(customMarkerIcon));

                if (marker != null) {
                    marker.setTag(routeLocation); // Store location in marker tag
                }
            } catch (Exception e) {
                Log.e("MapFragment", "Error adding marker: " + e.getMessage());
            }
        }
    } else {
        Log.e("MapFragment", "coordinatesList is empty or null.");
    }
}


    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Location location = task.getResult();
                        userLocationLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                        // Display the user's coordinates
                        Log.d("MapFragment", "Latitude: " + userLocationLatLng.latitude + ", Longitude: " + userLocationLatLng.longitude);


                        UserPositionMarkerManager userPositionMarkerManager = new UserPositionMarkerManager();
                        BitmapDescriptor customMarkerIcon = userPositionMarkerManager.getCustomCircleMarkerIcon(getContext());
                        Log.d("enableUserLocation", "Custom marker icon created.");

                        googleMap.addMarker(new MarkerOptions()
                                .position(userLocationLatLng)
                                .title("You are here")
                                .anchor(0.5f, 0.8f)
                                .zIndex(8.0f)
                                .icon(customMarkerIcon));

                        // Move the camera to the user's location and zoom in
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocationLatLng, 15));
                    } else {
                        Toast.makeText(getContext(), "Unable to retrieve location", Toast.LENGTH_SHORT).show();

                    }
                });



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ClockOutVisibilityHandler) {
            visibilityHandler = (ClockOutVisibilityHandler) context;
        } else {
            Log.d("MapFragment", "Activity does not implement ClockOutVisibilityHandler");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (visibilityHandler != null) {
            visibilityHandler.setClockOutVisibility(false);
        }
    }
}




/*


        googleMap.setOnMapLoadedCallback(() -> {
            Log.d("MapFragment", "Google Map has fully loaded");

            if (jobId == -1) {
                fetchRouteData(jobId,accessToken);
            } else {
                Log.e("MapFragment", "job_id is null in MapFragment!");
                fetchRouteData(-1, accessToken);
            }
        });

    private void fetchRouteData(int jobId, String accessToken) {
        RouteApiManager.fetchRouteApiData(String.valueOf(jobId),accessToken, new RouteApiManager.ApiResponseCallback<Routemain>() {
            @Override
            public void onDataFetched(List<Routemain> data) {
                if (!data.isEmpty()) {
                    routeData = data.get(0);
                    if (googleMap != null) {
                        Log.d("MapFragment", "Fetching route data for Job ID: " + jobId);

                        addRouteDataMarker();
                    }
                }
            }

            @Override
            public void onError(String error) {
                Log.e("MapFragment", "API Error: " + error);
            }
        });
    }

 */

/*
    private void addRouteDataMarker(GoogleMap googleMap) {
        if (coordinatesList != null && !coordinatesList.isEmpty()) {
            // Ensure MarkerManager instance is used correctly
            customMarkerIcon = markerManager.getCustomCircleMarkerIcon(getContext());

            for (Taskmain.Coordinates coord : coordinatesList) {
                try {
                    double lat = coord.getLatitude();
                    double lng = coord.getLongitude();

                    // Debug Log
                    Log.d("MapFragment", "Attempting to add marker at: Lat: " + lat + ", Lng: " + lng);

                    LatLng routeLocation = new LatLng(lat, lng);

                    // Create the marker with the correct custom icon
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .position(routeLocation)
                            .title("Job Location")
                            .anchor(0.5f, 0.8f)
                            .zIndex(8.0f)
                            .icon(customMarkerIcon));  // Use the already created custom icon

                    if (marker == null) {
                        Log.e("MapFragment", "Failed to add marker for Lat: " + lat + ", Lng: " + lng);
                    } else {
                        Log.d("MapFragment", "Route marker added successfully!");
                    }

                    // Optional: Move the camera to focus on the marker
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(routeLocation, 15));

                } catch (Exception e) {
                    Log.e("MapFragment", "Error adding marker: " + e.getMessage());
                }
            }
        } else {
            Log.e("MapFragment", "coordinatesList is empty or null.");
        }
    }

 */