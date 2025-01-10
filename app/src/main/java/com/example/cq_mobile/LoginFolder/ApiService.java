package com.example.cq_mobile.LoginFolder;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/api/m/login")
    Call<Void> login(@Body LoginRequest loginRequest);
}
