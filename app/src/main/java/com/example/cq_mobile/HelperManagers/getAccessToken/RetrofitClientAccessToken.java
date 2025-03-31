package com.example.cq_mobile.HelperManagers.getAccessToken;

import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientAccessToken {
    private static final String BASE_URL = "https://cqbms.app/api/m/";//"https://aws.customquoter.co.uk/api/m/";
    private static Retrofit retrofit;

    // Singleton pattern with thread safety
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            synchronized (RetrofitClientAccessToken.class) { // Ensure thread-safety
                if (retrofit == null) {
                    // Logging interceptor for debugging
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY); // Log request and response body

                    // Custom AccessTokenInterceptor for raw response logging
                    AccessTokenInterceptor accessTokenInterceptor = new AccessTokenInterceptor();

                    OkHttpClient client = new OkHttpClient.Builder()
                            .addInterceptor(logging) // Standard HTTP logging interceptor
                            .addInterceptor(accessTokenInterceptor) // Your custom logging interceptor
                            .addInterceptor(new Interceptor() {
                                @Override
                                public Response intercept(Chain chain) throws IOException {
                                    Request original = chain.request();

                                    // Dynamically get the API key (e.g., from SharedPreferences)
                                    String apiKey = getApiKeyFromPreferences();  // Modify to fetch from a secure source

                                    // Add dynamic headers to the request
                                    Request request = original.newBuilder()
                                            .header("x-api-key", apiKey) // Set the dynamic API key
                                            .header("Content-Type", "application/json")
                                            .header("Accept", "application/json")
                                            .build();

                                    return chain.proceed(request);
                                }
                            })
                            .build();

                    // Gson with lenient parsing for more flexible JSON parsing
                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                            .build();
                }
            }
        }
        return retrofit;
    }

    // Helper method to get API Key from SharedPreferences (or any secure storage)
    private static String getApiKeyFromPreferences() {
        // This is just an example. Modify to use actual secure storage.
        return "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";  // Replace with dynamic retrieval logic
    }
}

