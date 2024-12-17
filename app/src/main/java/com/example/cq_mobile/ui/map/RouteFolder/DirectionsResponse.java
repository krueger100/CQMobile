package com.example.cq_mobile.ui.map.RouteFolder;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DirectionsResponse {
    @SerializedName("routes")
    public List<Route> routes;

    public String getShortestRoutePolyline() {
        if (routes != null && !routes.isEmpty()) {
            Route shortestRoute = getShortestRoute();
            if (shortestRoute != null && shortestRoute.overviewPolyline != null) {
                return shortestRoute.overviewPolyline.points;
            }
        }
        return null;
    }

    public String getLongestRoutePolyline() {
        if (routes != null && !routes.isEmpty()) {
            Route longestRoute = getLongestRoute();
            if (longestRoute != null && longestRoute.overviewPolyline != null) {
                return longestRoute.overviewPolyline.points;
            }
        }
        return null;
    }

    private Route getShortestRoute() {
        return getRouteBasedOnSteps(true);
    }

    private Route getLongestRoute() {
        return getRouteBasedOnSteps(false);
    }

    private Route getRouteBasedOnSteps(boolean shortest) {
        if (routes == null || routes.isEmpty()) {
            return null;
        }

        Route selectedRoute = routes.get(0);
        int selectedRouteSteps = getTotalSteps(selectedRoute);

        for (Route route : routes) {
            int currentRouteSteps = getTotalSteps(route);
            if ((shortest && currentRouteSteps < selectedRouteSteps) || (!shortest && currentRouteSteps > selectedRouteSteps)) {
                selectedRoute = route;
                selectedRouteSteps = currentRouteSteps;
            }
        }

        return selectedRoute;
    }

    private int getTotalSteps(Route route) {
        int totalSteps = 0;
        if (route != null && route.legs != null) {
            for (Route.Leg leg : route.legs) {
                totalSteps += leg.steps.size();
            }
        }
        return totalSteps;
    }

    public static class Route {
        @SerializedName("legs")
        public List<Leg> legs;

        @SerializedName("overview_polyline")
        private OverviewPolyline overviewPolyline;

        public static class Leg {
            @SerializedName("steps")
            public List<Step> steps;

            public static class Step {
                @SerializedName("html_instructions")
                private String htmlInstructions;

                @SerializedName("start_location")
                private LatLng startLocation;

                @SerializedName("end_location")
                private LatLng endLocation;
            }
        }

        public static class OverviewPolyline {
            @SerializedName("points")
            public String points;
        }
    }
}
