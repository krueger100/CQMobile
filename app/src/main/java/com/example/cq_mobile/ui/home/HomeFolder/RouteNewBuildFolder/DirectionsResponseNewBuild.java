package com.example.cq_mobile.ui.home.HomeFolder.RouteNewBuildFolder;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class DirectionsResponseNewBuild {
    public List<Route> routes;

    public static class Route {
        public List<Leg> legs;
        public OverviewPolyline overview_polyline;

        public static class Leg {
            public String start_address;
            public String end_address;
            public List<Step> steps;

            public static class Step {
                public String html_instructions;
                public LatLng start_location;
                public LatLng end_location;
            }
        }

        public static class OverviewPolyline {
            public String points;
        }
    }
}
