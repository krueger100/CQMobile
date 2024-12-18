package com.example.cq_mobile.ui.map;

import android.Manifest;
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
import com.example.cq_mobile.HelperManagers.mapFolder.MarkerManager;
import com.example.cq_mobile.R;
import com.example.cq_mobile.databinding.FragmentMapBinding;;
import com.example.cq_mobile.ui.home.HomeFolder.RouteNewBuildFolder.RouteAPIFolder.RouteApiManager;
import com.example.cq_mobile.ui.home.HomeFolder.RouteNewBuildFolder.RouteAPIFolder.Routemain;
import com.example.cq_mobile.ui.map.RouteFolder.RouteManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
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

        // Initialize RouteManager
        routeManager = new RouteManager(googleMap, requireContext());

        // Set up the button click listener
        binding.setRouteButton.setOnClickListener(v -> {
            if (userLocationLatLng != null && destinationLatLng != null) {
                // Use RouteManager to draw the route when button is clicked
                routeManager.drawRoute(userLocationLatLng, destinationLatLng);
            } else {
                Toast.makeText(getContext(), "Please select a destination by clicking on the map or marker", Toast.LENGTH_SHORT).show();
            }
        });

        // Retrieve the job_id from arguments
        String jobId = getArguments() != null ? getArguments().getString("job_id") : null;
        Log.d("MapFragment", "Received Job ID in MapFragment: " + jobId);

        if (jobId != null) {
            fetchRouteData(jobId);
        } else {
            Log.e("MapFragment", "job_id is null in MapFragment!");
            fetchRouteData("5654");
        }


        return root;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        getUserLocation();

        // Set the GoogleMap instance to the RouteManager
        routeManager.setGoogleMap(googleMap);

        // Handle map clicks to set the destination
        googleMap.setOnMapClickListener(latLng -> {
            if (userLocationLatLng != null) {
                destinationLatLng = latLng;
            //    googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(userLocationLatLng).title("Your Location"));
                googleMap.addMarker(new MarkerOptions().position(destinationLatLng).title("Destination"));

                // Log the destination coordinates
                Log.d("MapFragment", "Destination set: Lat: " + destinationLatLng.latitude + ", Lng: " + destinationLatLng.longitude);
            }
        });

        // Set up the marker click listener for the entire map
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

    private void addRouteDataMarker() {
        if (routeData != null && routeData.getCoordinates() != null) {
            String latitude = routeData.getCoordinates().getLatitude();
            String longitude = routeData.getCoordinates().getLongitude();
            customMarkerIcon = markerManager.getCustomCircleMarkerIcon(getContext());

            // Convert the string latitude and longitude to doubles
            try {
                double lat = Double.parseDouble(latitude);
                double lng = Double.parseDouble(longitude);

                // Create a LatLng object using the coordinates
                LatLng routeLocation = new LatLng(lat, lng);
                // Add a marker on the map at the coordinates
                Marker routeMarker = googleMap.addMarker(new MarkerOptions()
                        .position(routeLocation)
                        .icon(customMarkerIcon)
                        .title(routeData.getName()));

                // Set a tag to this marker so we can identify it later
                routeMarker.setTag(routeData);

                // Optionally, move the camera to focus on the route location
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(routeLocation, 15));

                Log.d("MapFragment", "Route location marker added: Lat: " + lat + ", Lng: " + lng);
            } catch (NumberFormatException e) {
                Log.e("MapFragment", "Invalid coordinates: " + latitude + ", " + longitude);
            }
        }
    }

    private void fetchRouteData(String jobId) {
        RouteApiManager.fetchRouteApiData(jobId, new RouteApiManager.ApiResponseCallback<Routemain>() {
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
                Toast.makeText(getContext(), "Failed to fetch route data: " + error, Toast.LENGTH_SHORT).show();
                Log.e("MapFragment", "API Error: " + error);
            }
        });
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

                        // Add marker to the user's location
                        googleMap.addMarker(new MarkerOptions()
                                .position(userLocationLatLng)
                                .title("Your Location"));

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
}
