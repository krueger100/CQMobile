package com.example.cq_mobile.Clock.ClockFolder;

import com.example.cq_mobile.ui.ticket.TicketsFolder.TicketAPIItem;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface TicketIDApi {
    @GET("tickets/{ticketId}")
    Call<TicketAPIItem> getTicketById(
            @Path("ticketId") int ticketId,
            @Header("Authorization") String authHeader,
            @Header("x-api-key") String apiKey,
            @Header("Accept") String acceptHeader,
            @Header("Content-Type") String contentType,
            @Header("User-Agent") String userAgent,
            @Header("Accept-Encoding") String acceptEncoding,
            @Header("Connection") String connection
    );
}
