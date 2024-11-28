package com.example.cq_mobile.API_InterfaceFolder;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginManager {

    private ApiService apiService;
    private Context context;

    public LoginManager(Context context) {
        this.context = context;
        this.apiService = ApiClient.getClient().create(ApiService.class); // Initialize Retrofit service
    }

    public void loginUser(String email, String password, LoginCallback callback) {
        LoginRequest loginRequest = new LoginRequest(email, password);

        Call<String> call = apiService.login(loginRequest);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body();
                    Log.d("API Response", "Response: " + responseBody);

                    try {
                        // Attempt to parse the JSON response
                        Gson gson = new Gson();
                        LoginResponse loginResponse = gson.fromJson(responseBody, LoginResponse.class);
                        callback.onSuccess(loginResponse);
                    } catch (Exception e) {
                        Log.e("Login Error", "Invalid JSON response: " + responseBody);
                        callback.onError("Invalid JSON response.");
                    }
                } else {
                    Log.e("API Error", "Error: " + response.message());
                    callback.onError(response.message());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("API Failure", "Request failed: " + t.getMessage());
                callback.onFailure(t);
            }
        });
    }

    // Callback interface for login responses
    public interface LoginCallback {
        void onSuccess(LoginResponse loginResponse);

        void onError(String errorMessage);

        void onFailure(Throwable throwable);
    }
}
