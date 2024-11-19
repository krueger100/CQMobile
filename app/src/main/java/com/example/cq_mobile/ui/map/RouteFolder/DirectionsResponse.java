package com.example.cq_mobile.ui.map.RouteFolder;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DirectionsResponse {
    @SerializedName("routes")
    private List<Route> routes;

    public String getRoutePolyline() {
        if (routes != null && !routes.isEmpty() && routes.get(0).overviewPolyline != null) {
            return routes.get(0).overviewPolyline.points;
        }
        return null;
    }

    public static class Route {
        @SerializedName("overview_polyline")
        private OverviewPolyline overviewPolyline;

        public static class OverviewPolyline {
            @SerializedName("points")
            public String points;
        }
    }
}
