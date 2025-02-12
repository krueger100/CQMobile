package com.example.cq_mobile.ui.ticket.TickeNotifFolder;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;

public interface TicketNotifApi {
    @GET("api/m/notifications")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2",
            "User-Agent: PostmanRuntime/7.43.0"
    })
    Call<TicketNotifAPIResponse> getTicketsNotifications(@Header("Authorization") String authToken);
}
