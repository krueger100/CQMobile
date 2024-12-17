package com.example.cq_mobile.ui.map.RouteFolder;

import com.example.cq_mobile.ui.home.HomeFolder.RouteNewBuildFolder.DirectionsResponseNewBuild;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface DirectionsService {
    @GET
    Call<DirectionsResponse> getDirections(@Url String url, @Query("alternatives") boolean alternatives);

}


