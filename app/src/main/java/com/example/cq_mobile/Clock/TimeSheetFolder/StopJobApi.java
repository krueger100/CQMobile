package com.example.cq_mobile.Clock.TimeSheetFolder;

import android.content.Context;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class StopJobApi {
    private static final String TAG = "StopJobApi";
    private static final String BASE_URL = "https://cqbms.app";
    private static final String API_ENDPOINT = "/api/m/jobs/work-status/stop/%d";
    private static final String API_KEY = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";

    public static void stopJobWithTimesheet(String accessToken, Integer userId, ProgressBar progressBar, Context context, ApiTSCallback callback) {
        executeStopJobRequest(accessToken, userId, progressBar, context, callback, false);
    }

    public static void stopJobWithTimesheetColleague(String accessToken, Integer userId, ProgressBar progressBar, Context context, ApiTSCallback callback) {
        executeStopJobRequest(accessToken, userId, progressBar, context, callback, true);
    }

    private static void executeStopJobRequest(String accessToken, Integer userId, ProgressBar progressBar, Context context, ApiTSCallback callback, boolean isColleague) {
        if (validateInputs(accessToken, userId, progressBar, callback)) return;

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("status", "stop");
            jsonBody.put("user_id", userId);
        } catch (JSONException e) {
            callback.onFailure("Error creating JSON body: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));

        String endpoint = isColleague ? "/api/m/time-sheet/store/colleague/" : "/api/m/time-sheet/store/";

        Request request = new Request.Builder()
                .url(BASE_URL + String.format(API_ENDPOINT, userId))
                .put(body)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("x-api-key", API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(new ApiResponseHandler(progressBar, context, callback, isColleague));
    }

    private static boolean validateInputs(String accessToken, Integer userId, ProgressBar progressBar, ApiTSCallback callback) {
        if (accessToken == null || accessToken.trim().isEmpty()) {
            callback.onFailure("Invalid access token.");
            return true;
        }
        if (userId == null || userId <= 0) {
            callback.onFailure("Invalid user ID.");
            return true;
        }
        if (progressBar == null) {
            callback.onFailure("Progress bar is required.");
            return true;
        }
        return false;
    }

    public static class StopJobTimeSheetResponse {
        boolean success;
        String error_code;
        String message;
        Data data;

        public static class Data {
            String status;
            String message;
            List<Integer> shifts;
            String url;
        }
    }
}





/*
            public class StopJobApi {
    private static final String TAG = "StopJobApi";
    private static final String BASE_URL = "https://cqbms.app";
    private static final String API_ENDPOINT = "/api/m/jobs/work-status/stop/%d";
    private static final String API_KEY = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";

    public interface ApiTSCallback {
        void onSuccess(String message);
        void onFailure(String error);
    }


    public static void stopJobWithTimesheet(String accessToken, Integer userId, ProgressBar progressBar, Context context, ApiTSCallback callback) {
        if (accessToken == null || accessToken.trim().isEmpty()) {
            Log.e(TAG, "Access token is null or empty.");
            callback.onFailure("Invalid access token.");
            return;
        }

        if (userId == null || userId <= 0) {
            Log.e(TAG, "Invalid user ID.");
            callback.onFailure("Invalid user ID.");
            return;
        }

        if (progressBar == null) {
            Log.e(TAG, "Progress bar is null.");
            callback.onFailure("Progress bar is required.");
            return;
        }

        // Proceed with API request since all parameters are valid
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("status", "stop");
            jsonBody.put("user_id", userId);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON body", e);
            callback.onFailure("Error creating JSON body: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));

        Log.d(TAG, "Request JSON: " + jsonBody.toString());

        Request request = new Request.Builder()
                .url(BASE_URL + String.format(API_ENDPOINT, userId))
                .put(body)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("x-api-key", API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d(TAG, "API Response: " + responseBody);

                if (response.isSuccessful()) {
                    try {
                        StopJobTimeSheetResponse stopJobResponse = new Gson().fromJson(responseBody, StopJobTimeSheetResponse.class);
                        if (stopJobResponse != null && stopJobResponse.success) {
                            String message = stopJobResponse.data.message;
                            if (progressBar != null) {
                                progressBar.post(() -> progressBar.setVisibility(View.GONE));
                            }
                            Log.w(TAG, "StopJobApi ->  https://cqbms.app/api/m/time-sheet/store/ ->> " + "\n" + responseBody);

                            new Handler(Looper.getMainLooper()).postDelayed(() -> {

                            }, 3000);

                            callback.onSuccess(message);
                        } else {
                            callback.onFailure("Failed to stop the job.");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response", e);
                        if (progressBar != null) {
                            progressBar.post(() -> progressBar.setVisibility(View.GONE));
                        }
                        callback.onFailure("Error parsing response: " + e.getMessage());
                    }
                } else {
                    if (progressBar != null) {
                        progressBar.post(() -> progressBar.setVisibility(View.GONE));
                    }
                    Log.e(TAG, "API Error: " + response.code() + " - " + response.message());
                    callback.onFailure("API Error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                if (progressBar != null) {
                    progressBar.post(() -> progressBar.setVisibility(View.GONE));
                }

                Log.e(TAG, "API call failed", e);
                callback.onFailure("API call failed: " + e.getMessage());

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (progressBar != null) {
                        Toast.makeText(progressBar.getContext(), "API call failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public static void stopJobWithTimesheetColleague(String accessToken, Integer userId, ProgressBar progressBar, Context context, ApiTSCallback callback) {
        if (accessToken == null || accessToken.trim().isEmpty()) {
            Log.e(TAG, "Access token is null or empty.");
            callback.onFailure("Invalid access token.");
            return;
        }

        if (userId == null || userId <= 0) {
            Log.e(TAG, "Invalid user ID.");
            callback.onFailure("Invalid user ID.");
            return;
        }

        if (progressBar == null) {
            Log.e(TAG, "Progress bar is null.");
            callback.onFailure("Progress bar is required.");
            return;
        }

        // Proceed with API request since all parameters are valid
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("status", "stop");
            jsonBody.put("user_id", userId);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON body", e);
            callback.onFailure("Error creating JSON body: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));

        Log.d(TAG, "Request JSON: " + jsonBody.toString());

        Request request = new Request.Builder()
                .url(BASE_URL + String.format(API_ENDPOINT, userId))
                .put(body)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("x-api-key", API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d(TAG, "API Response: " + responseBody);

                if (response.isSuccessful()) {
                    try {
                        StopJobTimeSheetResponse stopJobResponse = new Gson().fromJson(responseBody, StopJobTimeSheetResponse.class);
                        if (stopJobResponse != null && stopJobResponse.success) {
                            String message = stopJobResponse.data.message;
                            if (progressBar != null) {
                                progressBar.post(() -> progressBar.setVisibility(View.GONE));
                            }
                            Log.w(TAG, "StopJobApi -> https://cqbms.app/api/m/time-sheet/store/colleague/  -->" + "\n" + responseBody);

                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                LogoutManager.logoutUser(context);
                            }, 3000);

                            callback.onSuccess(message);
                        } else {
                            callback.onFailure("Failed to stop the job.");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response", e);
                        if (progressBar != null) {
                            progressBar.post(() -> progressBar.setVisibility(View.GONE));
                        }
                        callback.onFailure("Error parsing response: " + e.getMessage());
                    }
                } else {
                    if (progressBar != null) {
                        progressBar.post(() -> progressBar.setVisibility(View.GONE));
                    }
                    Log.e(TAG, "API Error: " + response.code() + " - " + response.message());
                    callback.onFailure("API Error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                if (progressBar != null) {
                    progressBar.post(() -> progressBar.setVisibility(View.GONE));
                }

                Log.e(TAG, "API call failed", e);
                callback.onFailure("API call failed: " + e.getMessage());

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (progressBar != null) {
                        Toast.makeText(progressBar.getContext(), "API call failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    public static class StopJobTimeSheetResponse {
        boolean success;
        String error_code;
        String message;
        Data data;

        public static class Data {
            String status;
            String message;
            List<Integer> shifts;
            String url;
        }
    }

}


 */

/*   StopJobApi.stopJobWithTimesheet(accessToken,userId,progress_circular,this,new StopJobApi.ApiTSCallback() {
                @Override
                public void onSuccess(String message) {
                    runOnUiThread(() -> {
                        new AlertDialog.Builder(newBuild)
                                .setTitle(serverMessage)
                                .setMessage("Job stopped successfully.")
                                .setPositiveButton("OK", (dialog2, which2) -> {
                                    SharedPrefManager sharedPrefManager = new SharedPrefManager(newBuild);
                                    clockOutManager.AutoClockOutandLogout(accessToken, jobId_int, taskId_int, startedDate);
                                    sharedPrefManager.clearStartJobMessage();
                                    dialog2.dismiss();
                                })
                                .show();
                        dialog.dismiss();
                    });
                }

                @Override
                public void onFailure(String error) {
                    Log.e("StartJob", "Failed to stop job: " + error);
                    runOnUiThread(() -> {
                        Toast.makeText(newBuild, "Failed to stop job: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            });


            //___----->>> New
                        StopJobApi.stopJobWithTimesheet(accessToken, userID, progressBarTimer,context, new StopJobApi.ApiTSCallback() {
                        @Override
                        public void onSuccess(String message) {
                            new Handler(Looper.getMainLooper()).post(() -> {

                                Toast.makeText(context, "Timer Stopped", Toast.LENGTH_SHORT).show();

                            });
                        }

                        @Override
                        public void onFailure(String error) {
                            Log.e("StartJob", "Failed to stop job: " + error);
                            new Handler(Looper.getMainLooper()).post(() ->
                                    Toast.makeText(context, "Failed to stop job: " + error, Toast.LENGTH_SHORT).show()
                            );
                        }
                    });


          */


  /*

            SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
                            Double userStartLat = sharedPrefManager.getUserStartJobLatitude();
                            Double userStartLon = sharedPrefManager.getUserStartJobLongitude();
                            Double latitude = sharedPrefManager.getStartJobLatitude();
                            Double longitude = sharedPrefManager.getUserStartJobLongitude();
                            String ukDate = UKDateTime.getCurrentUKDate();
                            String jobId = sharedPrefManager.getStartJobID();
                            String startedDate = sharedPrefManager.getKeyStartDate();
                            String stopDate = sharedPrefManager.getKeyStopDate();
                            sharedPrefManager.clearStartJob();
// ✅

                            new Handler(Looper.getMainLooper()).post(() -> {
                                if (accessToken != null && !accessToken.isEmpty()) {
                                    new Thread(() -> {
                                        TimeSheetAPI.sendTimeSheetData(accessToken, userId, userStartLat, userStartLon, latitude, longitude,
                                                Integer.parseInt(jobId), ukDate, startedDate, stopDate,
                                                new ApiTimeSheetCallback() {
                                                    @Override
                                                    public void onSuccess(String serverMessage) {
                                                        Log.d("TimeSheetManager", "sendTimeSheetData: " + serverMessage);
                                                        new Handler(Looper.getMainLooper()).post(() -> {
                                                            Log.w("StartJob", "TimeSheetManager: sendTimeSheetData -- >> " + serverMessage);
                                                            progressBar.setVisibility(View.GONE);
                                                            sharedPrefManager.saveStartedJobMessage(serverMessage);
                                                            Toast.makeText(context, serverMessage, Toast.LENGTH_SHORT).show();

                                                        });
                                                    }

                                                    @Override
                                                    public void onFailure(String error) {
                                                        Log.e("ClockOutManager", "Failed to send TimeSheet Data: " + error);
                                                        new Handler(Looper.getMainLooper()).post(() ->
                                                                Toast.makeText(context, "Unable to Create Time Sheet", Toast.LENGTH_SHORT).show()
                                                        );
                                                    }
                                                }
                                        );
                                    }).start();
                                } else {
                                    Log.d("ClockOutManager", "Access token is missing!");
                                    Toast.makeText(context, "Unable to Create Time Sheet", Toast.LENGTH_SHORT).show();
                                }
                            });




   */


