package com.example.cq_mobile.ui.home.HomeFolder.API_home;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService_home {
    @GET("api/users")
    Call<UserResponse> getUsers(@Query("page") int page);
}
