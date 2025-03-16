package com.example.cq_mobile.Clock.ClockFolder.ClockInAPIFolder;

import android.content.Context;
import android.util.Log;


import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ClockINApiManager {

    private static final String TAG = "ClockINApiManager";

    public interface ApiCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public static void clockIN(int jobScheduleId, int taskId, String accessToken, int userId, Context context, ApiCallback callback) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new ApiClockINTask(jobScheduleId, taskId, accessToken, userId,context, callback));
    }

    private static class ApiClockINTask implements Runnable {

        private final int jobScheduleId;
        private final int taskId;
        private final String accessToken;
        private final ApiCallback callback;
        private final int userId; // ✅ Ensure userId is correctly assigned
        Context context;
        public ApiClockINTask(int jobScheduleId, int taskId, String accessToken, int userId, Context context, ApiCallback callback) {
            this.jobScheduleId = jobScheduleId;
            this.taskId = taskId;
            this.accessToken = accessToken;
            this.userId = userId;
            this.context = context;
            this.callback = callback;
        }
        @Override
        public void run() {
            String baseUrl = "https://cqbms.app";

            // ✅ Ensure userId is formatted correctly
            if (userId <= 0) {
                Log.e(TAG, "Invalid userId: " + userId);
                callback.onFailure("Invalid user ID");
                return;
            }

            String endpoint = "/api/m/start-working/timed_in/" + userId;
            String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";

            postStartClockIn(baseUrl, endpoint, accessToken, apiKey);
        }

        private void postStartClockIn(String baseUrl, String endpoint, String accessToken, String apiKey) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            String jsonBody = "{}"; // ✅ Ensure jsonBody is properly defined
            RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(baseUrl + endpoint)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("x-api-key", apiKey)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    String responseBody = response.body() != null ? response.body().string() : null;
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Work started successfully. Response: " + responseBody);

                        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);

                        // Parse response using Gson
                        Gson gson = new Gson();
                        try {
                            JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
                            JsonObject dataObject = jsonObject.getAsJsonObject("data");

                            if (dataObject != null && dataObject.has("start_time")) {
                                String startTime = dataObject.get("start_time").getAsString();
                                sharedPrefManager.saveClockinStartDate(startTime); // Save start time


                            } else {
                                Log.e(TAG, "start_time not found in response");
                            }
                        } catch (JsonSyntaxException e) {
                            Log.e(TAG, "Failed to parse JSON response", e);
                        }


                        callback.onSuccess();
                    } else {
                        Log.e(TAG, "Request Failed: " + response.code() + " - " + response.message());
                        Log.e(TAG, "Error Body: " + responseBody);

                        callback.onFailure("Failed to start work: " + responseBody);
                    }
                }

                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e(TAG, "Error starting work: " + e.getMessage(), e);
                    callback.onFailure("Error starting work: " + e.getMessage());
                }
            });
        }
    }
}


/*
curl -X POST "https://aws.customquoter.co.uk/api/v1/start-working/timedIn/3" \
-H "Authorization: Bearer 7324|ydfJPOHwE5TymiX5vzSKOfbUglApfgw3sI9Y6bcG" \
-H "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2" \
-H "Accept: application/json" \
-H "Content-Type: application/json" \
-H "User-Agent: PostmanRuntime/7.43.0" \
-H "Accept-Encoding: gzip, deflate, br" \
-H "Connection: keep-alive" \
-d '{
  "job_schedule_id": 5682,
  "task_id": 774,
  "job_schedule_ids": [5682],
  "note_id": 125,
  "ticket_category_id": 1,
  "ticket_id": 178,
  "checklist_id": 497,
  "ticket_message_id": 767
}'



Clockout

curl -X POST "https://aws.customquoter.co.uk/api/v1/start-working/timed_out/3" \
-H "Authorization: Bearer 7324|ydfJPOHwE5TymiX5vzSKOfbUglApfgw3sI9Y6bcG" \
-H "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2" \
-H "Accept: application/json" \
-H "Content-Type: application/json" \
-H "User-Agent: PostmanRuntime/7.43.0" \
-H "Accept-Encoding: gzip, deflate, br" \
-H "Connection: keep-alive" \
-d '{
  "job_schedule_id": 5699,
  "task_id": 828,
  "job_schedule_ids": [5682],
  "note_id": 125,
  "ticket_category_id": 1,
  "ticket_id": 192,
  "checklist_id": 497,
  "ticket_message_id": 767
}'


$ curl -X PUT "https://aws.customquoter.co.uk/api/m/start-working/timed_out/3" \
-H "Authorization: Bearer 7324|ydfJPOHwE5TymiX5vzSKOfbUglApfgw3sI9Y6bcG" \
-H "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2" \
-H "Accept: application/json" \
-H "Content-Type: application/json" \
-H "User-Agent: PostmanRuntime/7.43.0" \
-H "Accept-Encoding: gzip, deflate, br" \
-H "Connection: keep-alive" \
-d '{
  "job_schedule_id": 123,
  "task_id": 456,
  "ticket_message_id": 789
}'
{"message":"Unauthenticated."}

ClockOut
curl -X PUT "https://aws.customquoter.co.uk/api/m/start-working/timed_out/379" \
-H "Authorization: Bearer 7324|ydfJPOHwE5TymiX5vzSKOfbUglApfgw3sI9Y6bcG" \
-H "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2" \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-H "User-Agent: PostmanRuntime/7.43.0" \
-H "Accept-Encoding: gzip, deflate, br" \
-H "Connection: keep-alive" \
-d '{
  "job_schedule_id": 123,
  "task_id": 456,
  "ticket_message_id": 789
}'


 */