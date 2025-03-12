package com.example.cq_mobile.LoginFolder;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("https://cqbms.app/login")
    Call<Void> login(@Body LoginRequest loginRequest);
}



/*

->  /api/m/login
->   /api/m/logout ->  https://cqbms.app/logout



 */