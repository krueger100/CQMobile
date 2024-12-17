package com.example.cq_mobile.ui.home.HomeFolder.RouteNewBuildFolder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.cq_mobile.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
    private LatLng testLoc; // Task location
    private static final String TAG = "RouteNewBuildManager";

    // Constructor to accept task location
    public RouteNewBuildManager(GoogleMap googleMap, Context context, LatLng testLoc) {
        this.context = context;
        this.testLoc = testLoc;
        this.googleMap = googleMap;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public void drawRoute(LatLng origin, LatLng destination) {
        if (googleMap == null) {
            Log.e(TAG, "GoogleMap is not initialized.");
            return;
        }

        String apiKey = context.getString(R.string.google_maps_key);
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
                + origin.latitude + "," + origin.longitude
                + "&destination=" + destination.latitude + "," + destination.longitude
                + "&mode=driving"
                + "&alternatives=true"
                + "&key=" + apiKey;

        Log.d(TAG, "URL: " + url);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DirectionsServiceNewBuild service = retrofit.create(DirectionsServiceNewBuild.class);
        Call<DirectionsResponseNewBuild> call = service.getDirections(url, true);
        call.enqueue(new Callback<DirectionsResponseNewBuild>() {
            @Override
            public void onResponse(Call<DirectionsResponseNewBuild> call, Response<DirectionsResponseNewBuild> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DirectionsResponseNewBuild directionsResponse = response.body();

                    List<String> allRoutePolylines = directionsResponse.getAllRoutePolylines();
                    List<DirectionsResponseNewBuild.RouteInfo> routeInfoList = directionsResponse.getRouteInfo();

                    if (allRoutePolylines != null && !allRoutePolylines.isEmpty()) {
                        int[] routeColors = {
                                Color.parseColor("#804E7CFF"),
                                Color.parseColor("#2D63F8"),
                                Color.parseColor("#80303F9F")
                        };

                        for (int i = 0; i < allRoutePolylines.size(); i++) {
                            String polyline = allRoutePolylines.get(i);
                            DirectionsResponseNewBuild.RouteInfo routeInfo = routeInfoList.get(i);

                            String distanceText = routeInfo.getFormattedDistance();
                            String durationText = routeInfo.getFormattedDuration();
                            int color = routeColors[i % routeColors.length];

                            // Draw polyline
                            drawPolyline(polyline, color, "Route " + (i + 1), 10);

                            // Calculate center of polyline for marker placement
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

                        }
                    } else {
                        Log.w(TAG, "No routes found");
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destination, 15));
                        Toast.makeText(context, "No routes found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Response not successful: " + response.errorBody());
                    Toast.makeText(context, "Unable to fetch routes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponseNewBuild> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                Toast.makeText(context, "Error fetching directions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public LatLng calculatePolylineCenter(List<LatLng> points) {
        if (points == null || points.isEmpty()) {
            Log.e(TAG, "Polyline points are empty or null.");
            return null;
        }
        int middleIndex = points.size() / 2;
        return points.get(middleIndex);
    }

    public void drawPolyline(String encodedPolyline, int color, String label, float width) {
        if (googleMap == null) {
            Log.e(TAG, "GoogleMap is null, unable to draw polyline.");
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

            Log.d(TAG, label + " drawn with " + points.size() + " points.");
        } else {
            Log.e(TAG, "Encoded polyline is null for " + label);
        }
    }
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
