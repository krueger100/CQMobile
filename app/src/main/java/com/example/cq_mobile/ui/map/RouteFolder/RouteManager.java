package com.example.cq_mobile.ui.map.RouteFolder;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.example.cq_mobile.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
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
        Call<DirectionsResponse> call = service.getDirections(url);
        call.enqueue(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String polyline = response.body().getShortestRoutePolyline();
                    Log.d("DirectionsAPI", "Polyline: " + polyline); // Log the polyline
                    if (polyline != null) {
                        drawPolyline(polyline);
                    } else {
                        Log.e("DirectionsAPI", "No routes found in response");
                        Toast.makeText(context, "No route found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("DirectionsAPI", "Response not successful: " + response.errorBody());
                    Toast.makeText(context, "Unable to fetch route", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Log.e("DirectionsAPI", "API call failed: " + t.getMessage());
                Toast.makeText(context, "Error fetching directions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void drawPolyline(String encodedPolyline) {
        if (googleMap == null) {
            Log.e("RouteManager", "GoogleMap is null, unable to draw polyline.");
            return;
        }

        if (encodedPolyline != null) {
            List<LatLng> points = PolyUtil.decode(encodedPolyline);
            googleMap.addPolyline(new PolylineOptions()
                    .addAll(points)
                    .width(10)
                    .color(Color.BLUE)
                    .geodesic(true));
            Log.d("Polyline", "Polyline drawn with points: " + points.size());
        } else {
            Log.e("Polyline", "Encoded polyline is null");
        }
    }
}
