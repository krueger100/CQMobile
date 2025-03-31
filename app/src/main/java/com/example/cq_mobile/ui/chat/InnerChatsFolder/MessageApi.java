package com.example.cq_mobile.ui.chat.InnerChatsFolder;
import androidx.annotation.Nullable;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface MessageApi {
    @GET("api/m/chats/chat")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2",
            "User-Agent: PostmanRuntime/7.43.0"
    })
    Call<MessageAPIResponse> getChats(
            @Query("id") int userId,
            @Query("channel") int channel,
            @Query("unread") @Nullable String unread,
            @Header("Authorization") String authToken
    );
}
/// /api/m/chats/update_read