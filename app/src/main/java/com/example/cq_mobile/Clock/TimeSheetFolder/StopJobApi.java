package com.example.cq_mobile.Clock.TimeSheetFolder;

import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.cq_mobile.LoginFolder.AuthManager;

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

    public static void stopJobWithTimesheet(Integer userId, ProgressBar progressBar, Context context, ApiTSCallback callback) {
        String accessToken = AuthManager.getInstance(context).getToken();
        if (accessToken == null || accessToken.isEmpty()) {
            callback.onFailure("Access token is missing or invalid.");
            return;
        }

        executeStopJobRequest(accessToken, userId, progressBar, context, callback, false);
    }

    public static void stopJobWithTimesheetColleague(Integer userId, ProgressBar progressBar, Context context, ApiTSCallback callback) {
        String accessToken = AuthManager.getInstance(context).getToken();
        if (accessToken == null || accessToken.isEmpty()) {
            callback.onFailure("Access token is missing or invalid.");
            return;
        }

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

        Log.w("StopJobApi", "body" + "\n -> " + body);
        Log.w("StopJobApi", "userId" + "\n -> " + userId);
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




