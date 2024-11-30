package com.example.cq_mobile.LoginFolder;

import retrofit2.Call;
import retrofit2.http.POST;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;

public interface ApiService {
    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> loginUser(
            @Field("email") String email,
            @Field("password") String password
    );
}
