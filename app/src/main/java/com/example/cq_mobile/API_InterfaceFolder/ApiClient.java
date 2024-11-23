package com.example.cq_mobile.API_InterfaceFolder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Create the logging interceptor
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Create an OkHttpClient with the logging interceptor
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)  // Add the logging interceptor to client
                    .build();

            // Initialize Retrofit with Gson and the OkHttpClient
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://aws.customquoter.co.uk/api/")
                    .client(client)  // Set the custom OkHttpClient
                    .addConverterFactory(GsonConverterFactory.create())  // Use Gson for JSON parsing
                    .build();
        }
        return retrofit;
    }
}
