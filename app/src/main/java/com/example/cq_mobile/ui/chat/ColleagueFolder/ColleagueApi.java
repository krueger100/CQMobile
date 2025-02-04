package com.example.cq_mobile.ui.chat.ColleagueFolder;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ColleagueApi {
    @GET("api/m/chats")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2"  // Static API key remains as is
    })
    Call<ColleagueAPIResponse> getColleagues(
            @Query("page") int page,
            @Query("per_page") int perPage,
            @Query("token") String token,  // Token as query parameter
            @Header("Authorization") String authorization);  // Authorization header dynamically
}
