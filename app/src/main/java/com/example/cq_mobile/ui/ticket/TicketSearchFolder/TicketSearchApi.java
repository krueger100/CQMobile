package com.example.cq_mobile.ui.ticket.TicketSearchFolder;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface TicketSearchApi {
    @GET("api/m/tickets")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2"  // Static API key remains as is
    })
    Call<TicketSearchAPIResponse> getSearchTickets(
            @Query("page") int page,
            @Query("per_page") int perPage,
            @Query("search") String search,
            @Query("category_id") int categoryId,
            @Query("token") String token,
            @Header("Authorization") String authorization);
}
