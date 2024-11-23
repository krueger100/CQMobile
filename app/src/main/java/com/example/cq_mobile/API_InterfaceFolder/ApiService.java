package com.example.cq_mobile.API_InterfaceFolder;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

// ApiService.java


public interface ApiService {
    @POST("m/login")
    Call<String> login(@Body LoginRequest loginRequest);
}
