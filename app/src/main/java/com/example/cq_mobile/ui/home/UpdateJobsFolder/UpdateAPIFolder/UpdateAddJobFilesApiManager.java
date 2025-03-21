package com.example.cq_mobile.ui.home.UpdateJobsFolder.UpdateAPIFolder;

import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class UpdateAddJobFilesApiManager {
    private static final String TAG = "UpdateAddJobFilesApiManager";

    public static boolean uploadTaskFiles(String accessToken, String jobId, int eventId, File file, String apiKey) {
        if (!file.exists() || file.length() == 0) {
            Log.e(TAG, "File does not exist or is empty.");
            return false;
        }

        String baseUrl = "https://cqbms.app";
        String endpoint = "/api/m/jobs/schedules/store/files";
        String url = baseUrl + endpoint;



        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        Log.d(TAG, "jobID:  --:>> " + jobId);
        Log.d(TAG, "eventId:  --:>> " + eventId);


        MultipartBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("files[]", file.getName(), RequestBody.create(file, MediaType.parse(mimeType)))
                .addFormDataPart("job",  String.valueOf(eventId))
                .addFormDataPart("event",jobId)
                .build();



        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("x-api-key", apiKey)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "multipart/form-data")
                .post(requestBody)
                .build();

        Log.d(TAG, "Request URL: " + url);
        Log.d(TAG, "Job ID: " + jobId);
        Log.d(TAG, "Event ID: " + eventId);
        Log.d(TAG, "File: " + file.getAbsolutePath());
        Log.d(TAG, "MIME Type: " + mimeType);

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            Log.d(TAG, "Response Code: " + response.code());
            Log.d(TAG, "Response Message: " + response.message());
            Log.d(TAG, "Response Body: " + responseBody);

            JSONObject jsonResponse = new JSONObject(responseBody);
            boolean success = jsonResponse.optBoolean("success", false);
            String message = jsonResponse.optString("message", "No message");
            JSONObject fileData = jsonResponse.optJSONObject("data");

            Log.d(TAG, "Success: " + success);
            Log.d(TAG, "Message: " + message);

            if (fileData != null) {
                int fileId = fileData.optInt("id");
                String fileUrl = fileData.optString("url");
                String filename = fileData.optString("filename");
                int fileSize = fileData.optInt("filesize");
                String fileMimeType = fileData.optString("mime_type");

                Log.d(TAG, "Uploaded File ID: " + fileId);
                Log.d(TAG, "File URL: " + fileUrl);
                Log.d(TAG, "Filename: " + filename);
                Log.d(TAG, "File Size: " + fileSize);
                Log.d(TAG, "File MIME Type: " + fileMimeType);
            }

            return success;
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Upload error: " + e.getMessage());
            return false;
        }
    }
}


/*
curl -X POST "https://cqbms.app/api/m/jobs/schedules/store/files" \
  -H "Authorization: Bearer 6211|szPLf07fRg9RMM6bDN1XtbuIXG2jyHMFzuccw3gQ" \
  -H "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2" \
  -H "Accept: application/json" \
  -H "Content-Type: multipart/form-data" \
  -H "User-Agent: PostmanRuntime/7.43.0" \
  -H "Accept-Encoding: gzip, deflate, br" \
  -H "Connection: keep-alive" \
  -F "files[]=@C:\Users\carlo\Downloads\lawnM.jpeg" \
  -F "job=7407" \
  -F "event=9199"

C:\Users\carlo\Downloads\images.jpeg
"C:\Users\carlo\Downloads\lawnM.jpeg"
 */


