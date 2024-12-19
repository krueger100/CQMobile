package com.example.cq_mobile.HelperManagers.getAccessToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AccessTokenApiService {

    // Define the login endpoint
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
            "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2" // Replace with your actual API key
    })
    @POST("login") // Make sure the path is correct
    Call<AccessTokenResponse> AccessTokenUser(@Body AccessTokenRequest AccessTokenUser); // Pass the login request object
}
