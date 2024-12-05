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

import com.example.cq_mobile.R;
import com.example.cq_mobile.databinding.FragmentMapBinding;
import com.example.cq_mobile.ui.map.RouteFolder.RouteManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private FragmentMapBinding binding;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng userLocationLatLng;
    private LatLng destinationLatLng;

    private RouteManager routeManager;

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
                // Use RouteManager to draw the route
                routeManager.drawRoute(userLocationLatLng, destinationLatLng);
            } else {
                Toast.makeText(getContext(), "Please select a destination by clicking on the map", Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        getUserLocation();

        // Set the GoogleMap instance to the RouteManager
        routeManager.setGoogleMap(googleMap);

        googleMap.setOnMapClickListener(latLng -> {
            if (userLocationLatLng != null) {
                destinationLatLng = latLng;

                // Clear existing markers and polylines
                googleMap.clear();

                // Add markers for user location and destination
                googleMap.addMarker(new MarkerOptions().position(userLocationLatLng).title("Your Location"));
                googleMap.addMarker(new MarkerOptions().position(destinationLatLng).title("Destination"));

                // Inform the user to press the button
                Log.d("MapFragment", "Destination set: Lat: " + destinationLatLng.latitude + ", Lng: " + destinationLatLng.longitude);
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
