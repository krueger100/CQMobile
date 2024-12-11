package com.example.cq_mobile.HelperManagers.mapFolder;

import android.location.Location;
import android.util.Log;

import com.example.cq_mobile.ui.home.HomeFolder.RouteNewBuildFolder.RouteNewBuildManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapCameraManager {

    private GoogleMap googleMap;
    private LatLng userLocation;
    private LatLng taskLatLng;
    private RouteNewBuildManager routeNewBuildManager;
    private BitmapDescriptor customMarkerIcon;
    private Marker marker;

    public MapCameraManager(GoogleMap googleMap, RouteNewBuildManager routeNewBuildManager, BitmapDescriptor customMarkerIcon) {
        this.googleMap = googleMap;
        this.routeNewBuildManager = routeNewBuildManager;
        this.customMarkerIcon = customMarkerIcon;
    }

    // Set user location and destination, and update the camera view
    public void setDestination(LatLng userLocation, LatLng taskLatLng) {
        this.userLocation = userLocation;
        this.taskLatLng = taskLatLng;

        if (userLocation != null && taskLatLng != null) {
            // Add markers for user location and destination
            googleMap.addMarker(new MarkerOptions().position(userLocation).title("You are here"));
            marker = googleMap.addMarker(new MarkerOptions()
                    .position(taskLatLng)
                    .icon(customMarkerIcon)
                    .anchor(0.6f, 0.6f)
                    .zIndex(5.0f));

            // Draw the route between user location and destination
            routeNewBuildManager.drawRoute(userLocation, taskLatLng);

            // Calculate the distance between userLocation and destination
            float[] results = new float[1];
            Location.distanceBetween(userLocation.latitude, userLocation.longitude,
                    taskLatLng.latitude, taskLatLng.longitude, results);
            float distance = results[0]; // Distance in meters
            Log.d("Distance", "Distance between user and destination: " + distance + " meters");
            int zoomLevel = calculateZoomLevel(distance);

            // Calculate the center point between userLocation and destination
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(userLocation);
            builder.include(taskLatLng);
            LatLngBounds bounds = builder.build();

            // Set zoom level and animate the camera to include both locations
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100)); // 100px padding
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), zoomLevel)); // Zoom based on the calculated zoomLevel
        } else {
            Log.e("setDestination", "User location or destination is null.");
        }
    }

    // Calculate zoom level based on distance
    private int calculateZoomLevel(float distance) {
        if (distance < 500) {
            return 18; // Close range, use high zoom
        } else if (distance < 5000) {
            return 13; // Medium range, moderate zoom
        } else {
            return 10; // Long range, low zoom
        }
    }
}
