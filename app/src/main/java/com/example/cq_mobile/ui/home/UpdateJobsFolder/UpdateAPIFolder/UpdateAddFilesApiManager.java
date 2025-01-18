package com.example.cq_mobile.ui.home.UpdateJobsFolder.UpdateAPIFolder;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdateAddFilesApiManager {
    private static final String TAG = "UpdateAddFilesApi";

    public static boolean uploadTaskFiles(String accessToken, String jobScheduleId, String taskId, File[] files, String apiKey) {
        String baseUrl = "https://aws.customquoter.co.uk";
        String endpoint = "/api/m/jobs/schedules/" + jobScheduleId + "/tasks/" + taskId + "/files";
        String url = baseUrl + endpoint;

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (File file : files) {
            multipartBuilder.addFormDataPart(
                    "task_files[]",
                    file.getName(),
                    RequestBody.create(file, MediaType.parse("image/jpeg"))
            );
        }

        RequestBody requestBody = multipartBuilder.build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("x-api-key", apiKey)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                Log.d(TAG, "File uploaded successfully: " + response.body().string());
                return true;
            } else {
                Log.e(TAG, "Upload failed: " + response.code() + " - " + response.message());
                return false;
            }
        } catch (IOException e) {
            Log.e(TAG, "Upload error: " + e.getMessage());
            return false;
        }
    }
}
