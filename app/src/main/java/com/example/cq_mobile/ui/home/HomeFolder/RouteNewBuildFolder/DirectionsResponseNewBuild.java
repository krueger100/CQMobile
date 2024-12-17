package com.example.cq_mobile.ui.home.HomeFolder.RouteNewBuildFolder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DirectionsResponseNewBuild {
    @SerializedName("routes")
    public List<Route> routes;

    // Get all route polylines
    public List<String> getAllRoutePolylines() {
        List<String> polylines = new ArrayList<>();
        if (routes != null && !routes.isEmpty()) {
            for (Route route : routes) {
                if (route.overviewPolyline != null) {
                    polylines.add(route.overviewPolyline.points);
                }
            }
        }
        Log.d("DirectionsAPI", "All route polylines: " + polylines.size());  // Log the total number of polylines
        return polylines;
    }

    // Get the polyline for the shortest route
    public String getShortestRoutePolyline() {
        Route shortestRoute = getShortestRoute();
        if (shortestRoute != null && shortestRoute.overviewPolyline != null) {
            return shortestRoute.overviewPolyline.points;
        }
        return null;
    }

    // Get the destination locations for all routes
    public List<LatLng> getAllDestinations() {
        List<LatLng> destinations = new ArrayList<>();
        if (routes != null && !routes.isEmpty()) {
            for (Route route : routes) {
                LatLng destination = getDestinationLocation(route);
                if (destination != null) {
                    destinations.add(destination);
                }
            }
        }
        return destinations;
    }

    // Get the destination location for a specific route (last leg's end location)
    private LatLng getDestinationLocation(Route route) {
        if (route != null && route.legs != null && !route.legs.isEmpty()) {
            Route.Leg lastLeg = route.legs.get(route.legs.size() - 1);
            if (lastLeg != null && lastLeg.endLocation != null) {
                return new LatLng(lastLeg.endLocation.lat, lastLeg.endLocation.lng);
            }
        }
        return null;
    }

    private Route getShortestRoute() {
        return getRouteBasedOnDistance(true);
    }

    private Route getRouteBasedOnDistance(boolean shortest) {
        if (routes == null || routes.isEmpty()) {
            return null;
        }

        Route selectedRoute = routes.get(0);
        int selectedDistance = getTotalDistance(selectedRoute);

        for (Route route : routes) {
            int currentDistance = getTotalDistance(route);
            if ((shortest && currentDistance < selectedDistance) || (!shortest && currentDistance > selectedDistance)) {
                selectedRoute = route;
                selectedDistance = currentDistance;
            }
        }

        return selectedRoute;
    }

    private int getTotalDistance(Route route) {
        if (route == null || route.legs == null || route.legs.isEmpty()) {
            return 0;
        }

        int totalDistance = 0;
        for (Route.Leg leg : route.legs) {
            if (leg.distance != null) {
                totalDistance += leg.distance.value;
            }
        }
        return totalDistance;
    }

    // Get route info for each route
    public List<RouteInfo> getRouteInfo() {
        List<RouteInfo> routeInfoList = new ArrayList<>();
        if (routes != null && !routes.isEmpty()) {
            for (Route route : routes) {
                RouteInfo routeInfo = new RouteInfo();

                // Add total distance
                int totalDistance = 0;
                int totalDuration = 0;
                if (route.legs != null && !route.legs.isEmpty()) {
                    for (Route.Leg leg : route.legs) {
                        if (leg.distance != null) {
                            totalDistance += leg.distance.value;
                        }
                        if (leg.duration != null) {
                            totalDuration += leg.duration.value;
                        }
                    }
                }
                routeInfo.setDistance(totalDistance);
                routeInfo.setDuration(totalDuration);

                routeInfoList.add(routeInfo);
            }
        }
        return routeInfoList;
    }

    // Nested classes representing the response structure
    public static class Route {
        @SerializedName("overview_polyline")
        public OverviewPolyline overviewPolyline;

        @SerializedName("legs")
        public List<Leg> legs;

        public static class OverviewPolyline {
            @SerializedName("points")
            public String points;
        }

        public static class Leg {
            @SerializedName("distance")
            public Distance distance;

            @SerializedName("duration")
            public Duration duration;  // Add duration field

            @SerializedName("end_location")
            public Location endLocation;

            public static class Distance {
                @SerializedName("value")
                public int value;  // Distance in meters
            }

            public static class Duration {
                @SerializedName("value")
                public int value;  // Duration in seconds
            }

            public static class Location {
                @SerializedName("lat")
                public double lat;

                @SerializedName("lng")
                public double lng;
            }
        }
    }

    public static class RouteInfo {
        private int distance;  // distance in meters
        private int duration;  // duration in seconds

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public String getFormattedDistance() {
            return distance + " meters";
        }

        public String getFormattedDuration() {
            int minutes = duration / 60;
            int seconds = duration % 60;
            return minutes + " min " + seconds + " sec";
        }
    }
}
