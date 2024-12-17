package com.example.cq_mobile.ui.map.RouteFolder;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.DistanceMarkerManager;
import com.example.cq_mobile.ui.home.HomeFolder.RouteNewBuildFolder.DirectionsResponseNewBuild;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RouteManager {
    private GoogleMap googleMap;
    private Context context;

    public RouteManager(GoogleMap googleMap, Context context) {
        this.context = context;
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
                + "&key=" + apiKey;

        Log.d("DirectionsAPI", "URL: " + url); // Log the URL

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DirectionsService service = retrofit.create(DirectionsService.class);
        Call<DirectionsResponse> call = service.getDirections(url, true);
        call.enqueue(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DirectionsResponse directionsResponse = response.body();

                    List<String> allRoutePolylines = directionsResponse.getAllRoutePolylines();
                    List<DirectionsResponse.RouteInfo> routeInfoList = directionsResponse.getRouteInfo();

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
                            DirectionsResponse.RouteInfo routeInfo = routeInfoList.get(i);
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
                                    .title("Distance: " + distanceText + "\n" + ", Time: " + durationText)
                                    .snippet("Duration: " + durationText)
                                    .icon(customMarkerIcon)
                                    .anchor(0.5f, 0.8f)
                                    .zIndex(5.0f));

                            if (marker != null) {
                                marker.showInfoWindow();
                            }

                        }
                    } else {
                     //   showAddressInputDialog(origin, destination);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destination, 15));
                        Toast.makeText(context, "No routes found", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Log.e("DirectionsAPI", "Response not successful: " + response.errorBody());
                    Toast.makeText(context, "Unable to fetch routes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Log.e("DirectionsAPI", "API call failed: " + throwable.getMessage());
                Toast.makeText(context, "Error fetching directions", Toast.LENGTH_SHORT).show();
            }

        });
    }


    // Calculate the center of the polyline
    public LatLng calculatePolylineCenter(List<LatLng> points) {
        if (points == null || points.isEmpty()) {
            Log.e("calculatePolylineCenter", "Polyline points are empty or null.");
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
            // Decode the polyline into LatLng points
            List<LatLng> points = PolyUtil.decode(encodedPolyline);

            // Add the polyline to the map
            googleMap.addPolyline(new PolylineOptions()
                    .addAll(points)           // Add all points of the decoded polyline
                    .width(width)             // Set the width of the polyline
                    .color(color)             // Set the color of the polyline
                    .geodesic(true)           // Smooth the polyline
                    .zIndex(1.0f));           // Ensure it's visible above other layers

            Log.d("Polyline", label + " drawn with " + points.size() + " points.");
        } else {
            Log.e("Polyline", "Encoded polyline is null for " + label);
        }
    }
}
