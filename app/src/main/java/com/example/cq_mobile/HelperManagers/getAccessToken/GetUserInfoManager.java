package com.example.cq_mobile.HelperManagers.getAccessToken;

import android.content.Context;
import android.util.Log;



import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.cq_mobile.HelperManagers.getAccessToken.GetUserInfoManager;

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
        AccessTokenApiService apiService = RetrofitClientAccessToken.getRetrofitInstance().create(AccessTokenApiService.class);
        Call<AccessTokenResponse> call = apiService.AccessTokenUser(request);

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



        Context context = getApplicationContext();
        GetUserInfoManager getUserInfoManager = new GetUserInfoManager(context);
        AccessTokenRequest request = new AccessTokenRequest(context, email, password);

        getUserInfoManager.getAccessToken(request, new GetUserInfoManager.AccessTokenCallback() {
            @Override
            public void onSuccess(AccessTokenResponse response) {
                if (response != null) {
                    // Log the access token
                    String token = response.getAccessToken();
                    Log.d("AccessToken", "Token: " + token);

                    // Log the user details
                    AccessTokenResponse.User user = response.getUser();
                    if (user != null) {
                        int userId = user.getId();
                        String firstName = user.getFirstName();
                        String lastName = user.getLastName();
                        String email = user.getEmail();
                        String avatar = user.getAvatar();

                        Log.d("User Info", "User ID: " + userId);
                        Log.d("User Info", "First Name: " + firstName);
                        Log.d("User Info", "Last Name: " + lastName);
                        Log.d("User Info", "Email: " + email);
                        Log.d("User Info", "Avatar: " + avatar);
                    } else {
                        Log.e("AccessTokenError", "User data is null.");
                    }
                } else {
                    Log.e("AccessTokenError", "Received null response.");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                if (errorMessage != null && !errorMessage.isEmpty()) {
                    Log.e("AccessTokenError", errorMessage);
                } else {
                    Log.e("AccessTokenError", "Unknown error occurred.");
                }
            }
        });




 */
