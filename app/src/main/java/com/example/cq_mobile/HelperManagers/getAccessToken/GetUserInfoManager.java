package com.example.cq_mobile.HelperManagers.getAccessToken;

import android.content.Context;
import android.util.Log;



import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class GetUserInfoManager {

    private Context context;

    public GetUserInfoManager(Context context) {
        this.context = context;
    }

    public interface AccessTokenCallback {
        void onSuccess(AccessTokenResponse response);
        void onFailure(String errorMessage);
    }

    public void getAccessToken(AccessTokenRequest request, AccessTokenCallback callback) {
        // Create an instance of the API service
        AccessTokenApiService apiService = RetrofitClientAccessToken.getRetrofitInstance().create(AccessTokenApiService.class);

        // Call the API
        Call<AccessTokenResponse> call = apiService.AccessTokenUser(request);

        // Enqueue the call to execute asynchronously
        call.enqueue(new Callback<AccessTokenResponse>() {
            @Override
            public void onResponse(Call<AccessTokenResponse> call, Response<AccessTokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<AccessTokenResponse> call, Throwable t) {
                // Log the failure (e.g., network error)
                callback.onFailure("Failure: " + t.getMessage());
            }
        });
    }
}
/*
        getUserData(email,password,profile);

    private void getUserData(String email, String password, ImageView image_profile) {

        GetUserInfoManager getUserInfoManager = new GetUserInfoManager(this);
        // Use the retrieved email and password
        AccessTokenRequest accessTokenRequest = new AccessTokenRequest(email, password);

        // Step 3: Call the method with a callback implementation
        getUserInfoManager.getAccessToken(accessTokenRequest, new GetUserInfoManager.AccessTokenCallback() {
            @Override
            public void onSuccess(AccessTokenResponse response) {
                // Handle the successful response
                String firstName = response.getUser().getFirstName() ;
                String lastName = response.getUser().getLastName() ;
                String avatar = response.getUser().getAvatar() ;


                String accessToken = response.getAccessToken();
                Log.d("MoreAct", "Access Token: " + accessToken);
                Log.d("MoreAct", "User Email: " + response.getUser().getEmail());
                name.setText(firstName +" "+lastName);


                if (avatar != null && !avatar.isEmpty()) {
                    // Use Glide to load the avatar URL into image1
                    Glide.with(MoreActivity.this)
                            .load(avatar)
                            .placeholder(R.drawable.baseline_circle)
                            .error(R.drawable.emptyglide)
                            .into(image_profile);
                } else {
                    Log.e("MoreActivity", "Avatar URL is null or empty");
                    // Optionally, set a default image if avatar URL is missing
                    image_profile.setImageResource(R.drawable.emptyglide);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle the failure
                Log.e("MainActivity", "Error: " + errorMessage);
            }
        });
    }

 */
