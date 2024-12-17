package com.example.cq_mobile.ui.home.HomeFolder.RouteNewBuildFolder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.DistanceMarkerManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import java.io.IOException;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RouteNewBuildManager {
    private GoogleMap googleMap;
    private Context context;
    private LatLng testLoc;  // Define taskLatLng here
    private static final String TAG = "RouteNewBuildManager";

    // Modify the constructor to accept taskLatLng as a parameter
    public RouteNewBuildManager(GoogleMap googleMap, Context context, LatLng testLoc) {
        this.context = context;
        this.testLoc = testLoc;  // Assign taskLatLng
        this.googleMap = googleMap;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }
    public void drawRoute(LatLng origin, LatLng destination) {
        if (googleMap == null) {
            Log.e("RouteManager", "GoogleMap is not initialized.");
            return;
        }

        String apiKey = context.getString(R.string.google_maps_key);
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
                + origin.latitude + "," + origin.longitude
                + "&destination=" + destination.latitude + "," + destination.longitude
                + "&mode=driving" // Add driving mode
                + "&alternatives=true"  // Request alternative routes
                + "&key=" + apiKey;

        Log.d("DirectionsAPI", "URL: " + url); // Log the URL

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DirectionsServiceNewBuild service = retrofit.create(DirectionsServiceNewBuild.class);
        Call<DirectionsResponseNewBuild> call = service.getDirections(url, true); // Requesting alternatives
        call.enqueue(new Callback<DirectionsResponseNewBuild>() {
            @Override
            public void onResponse(Call<DirectionsResponseNewBuild> call, Response<DirectionsResponseNewBuild> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DirectionsResponseNewBuild directionsResponse = response.body();

                    List<String> allRoutePolylines = directionsResponse.getAllRoutePolylines();
                    List<DirectionsResponseNewBuild.RouteInfo> routeInfoList = directionsResponse.getRouteInfo();

                    if (allRoutePolylines != null && !allRoutePolylines.isEmpty()) {
                        // Define the colors for each route
                        int[] routeColors = {
                                Color.parseColor("#804E7CFF"), // Route Color 1  2D63F8
                                Color.parseColor("#2D63F8"), // Route Color 2  804E7CFF
                                Color.parseColor("#80303F9F")  // Route Color 3
                        };
                        // Loop through each route
                        for (int i = 0; i < allRoutePolylines.size(); i++) {
                            String polyline = allRoutePolylines.get(i);
                            DirectionsResponseNewBuild.RouteInfo routeInfo = routeInfoList.get(i);
                            String distanceText = routeInfo.getFormattedDistance();
                            String durationText = routeInfo.getFormattedDuration();

                            // Choose the color for this route
                            int color = routeColors[i % routeColors.length];

                            // Draw polyline
                            drawPolyline(polyline, color, "Route " + (i + 1), 10);

                            // Calculate the center of the polyline (simple midpoint approach)
                            List<LatLng> points = PolyUtil.decode(polyline);
                            LatLng midpoint = calculatePolylineCenter(points);

                            DistanceMarkerManager distanceMarkerManager = new DistanceMarkerManager();
                            BitmapDescriptor customMarkerIcon = distanceMarkerManager.getCustomCircleMarkerIcon(context);

                            Marker marker = googleMap.addMarker(new MarkerOptions()
                                    .position(midpoint)
                                    .title("Distance: " + distanceText +"\n"+", Time: " + durationText)
                                    .snippet("Duration: " + durationText)
                                    .icon(customMarkerIcon)
                                    .anchor(0.5f, 0.8f)
                                    .zIndex(5.0f));

                            if (marker != null) {
                                marker.showInfoWindow();
                            }

                        }
                    } else {
                        showAddressInputDialog(origin, destination);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destination, 15));
                        Toast.makeText(context, "No routes found", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Log.e("DirectionsAPI", "Response not successful: " + response.errorBody());
                    Toast.makeText(context, "Unable to fetch routes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponseNewBuild> call, Throwable t) {
                Log.e("DirectionsAPI", "API call failed: " + t.getMessage());
                Toast.makeText(context, "Error fetching directions", Toast.LENGTH_SHORT).show();
            }
        });

    }


    // Calculate the center of the polyline
    public LatLng calculatePolylineCenter(List<LatLng> points) {
        if (points == null || points.isEmpty()) {
            Log.e(TAG, "Polyline points are empty or null.");
            return null; // Return null or handle this case as needed
        }
        // Find the middle point of the polyline
        int middleIndex = points.size() / 2;
        return points.get(middleIndex);
    }

    public void drawPolyline(String encodedPolyline, int color, String label, float width) {
        if (googleMap == null) {
            Log.e("RouteManager", "GoogleMap is null, unable to draw polyline.");
            return;
        }

        if (encodedPolyline != null) {
            List<LatLng> points = PolyUtil.decode(encodedPolyline);
            googleMap.addPolyline(new PolylineOptions()
                    .addAll(points)
                    .width(width)                // Set thickness of the polyline
                    .color(color)                 // Set color of the polyline
                    .geodesic(true)               // Smooth the polyline to follow the curve of the Earth
                    .zIndex(1.0f));               // Make sure that this polyline is visible above other layers
            Log.d("Polyline", label + " drawn with " + points.size() + " points.");
        } else {
            Log.e("Polyline", "Encoded polyline is null for " + label);
        }
    }


    public void showAddressInputDialog(LatLng userLocation, LatLng taskLatLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter Destination Address");

        // Set up the input field
        final EditText input = new EditText(context);
        input.setHint("Enter address or location");
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userInput = input.getText().toString();
                if (!userInput.isEmpty()) {
                    // Process the user input and geocode the address
                    geocodeAddress(userLocation, userInput, taskLatLng); // Pass taskLatLng to keep the destination
                } else {
                    Toast.makeText(context, "Please enter an address", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void geocodeAddress(LatLng userLocation, String address, LatLng taskLatLng) {
        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                // Get the LatLng from the geocoded address
                Address userAddress = addresses.get(0);
                LatLng addressDestination = new LatLng(userAddress.getLatitude(), userAddress.getLongitude());
                // Log the geocoded destination coordinates
                Log.d("geocodeAddress", "Destination: " + addressDestination.latitude + ", " + addressDestination.longitude);

                // Add markers for user location and the newly geocoded destination
                googleMap.addMarker(new MarkerOptions().position(userLocation).title("You are here"));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));

                // Clear previous markers and polyline -->   googleMap.clear();

                drawRoute(userLocation, addressDestination);

            } else {
                Toast.makeText(context, "Address not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoding failed", e);
            Toast.makeText(context, "Error in geocoding", Toast.LENGTH_SHORT).show();
        }
    }

}
