package com.example.cq_mobile.Clock.StartAndStopJobsFolder;

import android.util.Log;

import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenApiService;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenResponse;
import com.example.cq_mobile.HelperManagers.getAccessToken.RetrofitClientAccessToken;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartJobAPIManager {

    private static final String TAG = "StartJobAPIManager";

    public interface ApiCallback {
        void onSuccess(String response);
        void onFailure(String error);
    }

    public interface AccessTokenCallback {
        void onAccessTokenReceived(String accessToken);
        void onError(String error);
    }

    // Method to get the access token and notify the caller via a callback
    public void getAccessToken(AccessTokenRequest request, final AccessTokenCallback callback) {
        AccessTokenApiService apiService = RetrofitClientAccessToken.getRetrofitInstance().create(AccessTokenApiService.class);

        Call<AccessTokenResponse> call = apiService.AccessTokenUser(request);

        call.enqueue(new Callback<AccessTokenResponse>() {
            @Override
            public void onResponse(Call<AccessTokenResponse> call, Response<AccessTokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AccessTokenResponse accessTokenResponse = response.body();
                    String accessToken = accessTokenResponse.getAccessToken();

                    if (accessToken != null) {
                        Log.d(TAG, "Access Token: " + accessToken);
                        callback.onAccessTokenReceived(accessToken);
                    } else {
                        callback.onError("Access token not received.");
                    }
                } else {
                    callback.onError("Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<AccessTokenResponse> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }

    public void startJobWithToken(int userId, int jobId, Double latOut, Double longOut, AccessTokenRequest tokenRequest, ApiCallback callback) {
        getAccessToken(tokenRequest, new AccessTokenCallback() {
            @Override
            public void onAccessTokenReceived(String accessToken) {
                startJob(userId, jobId, latOut, longOut, accessToken, callback);
            }

            @Override
            public void onError(String error) {
                callback.onFailure("Failed to get access token: " + error);
            }
        });
    }

    public static void startJob(int userId, int jobId, Double latOut, Double longOut, String accessToken, ApiCallback callback) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new ApiStartJobTask(userId, jobId, latOut, longOut, accessToken, callback));
    }

    private static class ApiStartJobTask implements Runnable {

        private final int userId;
        private final int jobId;
        private final Double latOut;
        private final Double longOut;
        private final String accessToken;
        private final ApiCallback callback;

        public ApiStartJobTask(int userId, int jobId, Double latOut, Double longOut, String accessToken, ApiCallback callback) {
            this.userId = userId;
            this.jobId = jobId;
            this.latOut = latOut;
            this.longOut = longOut;
            this.accessToken = accessToken;
            this.callback = callback;
        }

        @Override
        public void run() {
            String baseUrl = "https://aws.customquoter.co.uk";
            String endpoint = String.format("/api/m/jobs/work-status/start/%d", userId);

            String jsonBody = String.format("{\"status\": \"start\", \"job\": %d, \"custom_job\": null, \"lat_out\": %f, \"long_out\": %f}", jobId, latOut, longOut);
            Log.d(TAG, "jsonBody: --->>> " + jsonBody);
            postStartJob(baseUrl, endpoint, accessToken, jsonBody);

            Log.w(TAG, "Job started : " + userId +"  ----------- "+ jobId);
            Log.w(TAG, "Job started : " +" latOut "+ latOut +"  ----------- "+" longOut "+ longOut);

        }

        private void postStartJob(String baseUrl, String endpoint, String accessToken, String jsonBody) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(baseUrl + endpoint)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("x-api-key", "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .put(body)
                    .build();


            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    String responseBody = response.body() != null ? response.body().string() : null;
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Job started successfully. Response: " + responseBody);
                        callback.onSuccess(responseBody);
                    } else {
                        Log.e(TAG, "Request Failed: " + response.code() + " - " + response.message());
                        Log.e(TAG, "Error Body: " + responseBody);
                        callback.onFailure("Failed to start job: " + responseBody);
                    }
                }

                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e(TAG, "Error starting job: " + e.getMessage(), e);
                    callback.onFailure("Error starting job: " + e.getMessage());
                }
            });
        }
    }
}

/*
CALL

 int userId = 379;
        int jobId = 5699;
        Double latOut = 15.1486464;
        Double longOut = 120.6059008;
        String accessToken = "YOUR_ACCESS_TOKEN";

        startJob(userId, jobId, latOut, longOut, accessToken, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d(TAG, "API Success Response: " + response);
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "API Failure Response: " + error);
            }
        });
    }





CURL

curl -X PUT "https://aws.customquoter.co.uk/api/m/jobs/work-status/start/379" \
-H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
-H "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2" \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-H "User-Agent: PostmanRuntime/7.43.0" \
-H "Accept-Encoding: gzip, deflate, br" \
-H "Connection: keep-alive" \
-d '{
  "status": "start",
  "job": 5699,
  "custom_job": null,
  "lat_out": 15.1486464,
  "long_out": 120.6059008
}'


curl -X PUT "https://aws.customquoter.co.uk/api/m/jobs/work-status/start/3" \
-H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
-H "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2" \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-H "User-Agent: PostmanRuntime/7.43.0" \
-H "Accept-Encoding: gzip, deflate, br" \
-H "Connection: keep-alive" \
-d '{
  "status": "start",
  "job": 5699,
  "custom_job": 0,
  "lat_out": 51.600582,
  "long_out": 0.161092
}'




Results:

carlo@kruegerCO MINGW64 ~/AndroidStudioProjects/CQ_mobile (master)
$ curl -X PUT "https://aws.customquoter.co.uk/api/m/jobs/work-status/start/379" \
-H "Authorization: Bearer 11794|rpP2zlpFTfIfSlxt2JbGH6dgW0HJLqD3uwim3k6m" \
-H "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2" \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-H "User-Agent: PostmanRuntime/7.43.0" \
-H "Accept-Encoding: gzip, deflate, br" \
-H "Connection: keep-alive" \
-d '{
  "status": "start",
  "job": 5699,
}'"long_out": 120.6059008
{"success":true,"error_code":null,"message":"You have ongoing work.","data":{"status":"exist","event":"exist","message":"You have ongoing work.","message2":"Do you want to stop Custom Job 1 and start another Job?","work":{"id":5
71,"organization_id":2,"user_id":379,"job_id":null,"start_time":"2025-02-23 05:27:47","end_time":null,"type":0,"remarks":{"lat":15.1486464,"long":120.6059008},"custom_job":"1","job_timesheets":null,"created_at":"2025-02-23T05:27:47.000000Z","updated_at":"2025-02-23T05:27:47.000000Z","job":null}}}

 */