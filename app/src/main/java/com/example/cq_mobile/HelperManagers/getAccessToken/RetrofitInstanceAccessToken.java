package com.example.cq_mobile.HelperManagers.getAccessToken;

import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstanceAccessToken {

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://cqbms.app/api/m/"; //"https://aws.customquoter.co.uk/api/m/";

    // Singleton pattern to ensure only one instance of Retrofit is created
    public static Retrofit getInstance() {
        if (retrofit == null) {
            synchronized (RetrofitInstanceAccessToken.class) {
                if (retrofit == null) {
                    // Set up logging for the network requests (optional)
                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                    // Set up OkHttpClient with logging interceptor
                    OkHttpClient client = new OkHttpClient.Builder()
                            .addInterceptor(loggingInterceptor) // Logging network requests
                            .build();

                    // Create Retrofit instance
                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL) // Base URL for the API
                            .client(client) // Add OkHttpClient to Retrofit
                            .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())) // Gson converter for parsing JSON
                            .build();
                }
            }
        }
        return retrofit;
    }

    // Provide API service instance
    public static AccessTokenApiService getApiService() {
        return getInstance().create(AccessTokenApiService.class); // Return the API service interface
    }
}
