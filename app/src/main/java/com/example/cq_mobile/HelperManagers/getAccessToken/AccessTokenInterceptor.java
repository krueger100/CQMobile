package com.example.cq_mobile.HelperManagers.getAccessToken;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AccessTokenInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        // Log raw response body
        if (response.body() != null) {
            String responseBody = response.body().string();
            Log.d("ClockActivity", "Raw Response: " + responseBody);

            // Recreate the response with the body to pass further down the chain
            return response.newBuilder()
                    .body(ResponseBody.create(response.body().contentType(), responseBody))
                    .build();
        }
        return response;
    }
}
