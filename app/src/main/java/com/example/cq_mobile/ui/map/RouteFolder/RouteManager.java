package com.example.cq_mobile.ui.map.RouteFolder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cq_mobile.R;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
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
                + "&mode=driving"
                + "&key=" + apiKey;

        Log.d("DirectionsAPI", "URL: " + url);

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
                        int[] routeColors = {
                                Color.parseColor("#804E7CFF"),
                                Color.parseColor("#2D63F8"),
                                Color.parseColor("#80303F9F")
                        };

                        for (int i = 0; i < allRoutePolylines.size(); i++) {
                            String polyline = allRoutePolylines.get(i);
                            DirectionsResponse.RouteInfo routeInfo = routeInfoList.get(i);
                            String distanceText = routeInfo.getFormattedDistance();
                            String durationText = routeInfo.getFormattedDuration();

                            int color = routeColors[i % routeColors.length];
                            drawPolyline(polyline, color, "Route " + (i + 1), 10);

                            List<LatLng> points = PolyUtil.decode(polyline);
                            LatLng midpoint = calculatePolylineCenter(points);

                            // Create custom marker for distance and duration
                            BitmapDescriptor customMarkerIcon = createCustomMarker(context,
                                    "Route " + (i + 1), distanceText, durationText);

                            googleMap.addMarker(new MarkerOptions()
                                    .position(midpoint)
                                    .icon(customMarkerIcon)
                                    .anchor(0.5f, 1.0f) // Align to bottom
                                    .zIndex(5.0f));

                            Log.d("RouteInfo", "Route " + (i + 1) + " - Distance: " + distanceText + ", Duration: " + durationText);
                        }
                    } else {
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
            return null;
        }
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
                    .width(width)
                    .color(color)
                    .geodesic(true)
                    .zIndex(1.0f));
            Log.d("Polyline", label + " drawn with " + points.size() + " points.");
        } else {
            Log.e("Polyline", "Encoded polyline is null for " + label);
        }
    }

    // Create a custom marker with route distance and duration
    private BitmapDescriptor createCustomMarker(Context context, String routeLabel, String distance, String duration) {
        View markerView = LayoutInflater.from(context).inflate(R.layout.custom_marker_layout, null);

        TextView routeLabelView = markerView.findViewById(R.id.routeLabel);
        TextView distanceView = markerView.findViewById(R.id.distance);
        TextView durationView = markerView.findViewById(R.id.duration);

        routeLabelView.setText(routeLabel);
        distanceView.setText("Distance: " + distance);
        durationView.setText("Duration: " + duration);

        markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        markerView.layout(0, 0, markerView.getMeasuredWidth(), markerView.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(markerView.getMeasuredWidth(), markerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerView.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
