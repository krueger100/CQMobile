package com.example.cq_mobile.ui.home.HomeFolder.RouteNewBuildFolder;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;
import retrofit2.http.Query;

public interface DirectionsServiceNewBuild {
    @GET
    Call<DirectionsResponseNewBuild> getDirections(@Url String url, @Query("alternatives") boolean alternatives);

}


