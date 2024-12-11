package com.example.cq_mobile.ui.home.HomeFolder.RouteNewBuildFolder;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DirectionsServiceNewBuild {
    private Context context;
    private static final String API_KEY = "YOUR_GOOGLE_API_KEY";
    private static final String DIRECTIONS_API_URL = "https://maps.googleapis.com/maps/api/directions/json";

    public DirectionsServiceNewBuild(Context context) {
        this.context = context;
    }

    public void getDirections(LatLng origin, LatLng destination, DirectionsCallback callback) {
        String originStr = origin.latitude + "," + origin.longitude;
        String destinationStr = destination.latitude + "," + destination.longitude;

        String url = DIRECTIONS_API_URL + "?origin=" + originStr + "&destination=" + destinationStr + "&key=" + API_KEY;

        // Use ExecutorService to handle the background task
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            DirectionsResponseNewBuild response = fetchDirections(url);
            if (response != null) {
                callback.onDirectionsResponse(response);
            } else {
                callback.onDirectionsFailure(new Throwable("Error fetching directions"));
            }
        });
    }

    private DirectionsResponseNewBuild fetchDirections(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
            return new Gson().fromJson(inputStreamReader, DirectionsResponseNewBuild.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public interface DirectionsCallback {
        void onDirectionsResponse(DirectionsResponseNewBuild response);
        void onDirectionsFailure(Throwable t);
    }
}