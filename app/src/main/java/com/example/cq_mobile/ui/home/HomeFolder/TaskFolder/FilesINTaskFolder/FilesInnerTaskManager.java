package com.example.cq_mobile.ui.home.HomeFolder.TaskFolder.FilesINTaskFolder;

import android.content.Context;
import android.util.Log;

import com.example.cq_mobile.ui.home.HomeFolder.NotesNFilesAPI_folder.FilesFoler.FileItem;
import com.example.cq_mobile.ui.home.HomeFolder.NotesNFilesAPI_folder.FilesFoler.FilesApi;
import com.example.cq_mobile.ui.home.HomeFolder.NotesNFilesAPI_folder.FilesFoler.FilesResponse;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FilesInnerTaskManager {
    private final String baseUrl;
    private final String apiKey;
    private final Context context;
    public FilesInnerTaskManager(Context context, String baseUrl, String apiKey) {
        this.context = context;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public interface FilesCallback {
        void onFilesLoaded(List<FileItem> files);
        void onError(String errorMessage);
    }

    public void loadFiles(int jobId, int taskIds, int page, int pageSize, String accessToken, FilesCallback callback) {



        String url = baseUrl + "/api/m/jobs/schedules/" + jobId + "/tasks/" + taskIds + "/files?page=" + page + "&per_page=" + pageSize;

        Log.d("FilesInnerTaskManager", "Loading files from URL: " + url);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FilesApi filesApi = retrofit.create(FilesApi.class);
        Call<FilesResponse> call = filesApi.getFiles(url, "Bearer " + accessToken, apiKey);

        call.enqueue(new Callback<FilesResponse>() {
            @Override
            public void onResponse(Call<FilesResponse> call, Response<FilesResponse> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        Gson gson = new Gson();
                        String jsonResponse = gson.toJson(response.body());

                        Log.d("FilesInnerTaskManager", "Response Code: " + response.code());
                        Log.d("FilesInnerTaskManager", "Response Message: " + response.message());
                        Log.d("FilesInnerTaskManager", "Full Response: " + jsonResponse);

                        List<FileItem> files = response.body().getData();
                        callback.onFilesLoaded(files);
                    } else {
                        String errorMessage = response.message() != null ? response.message() : "Unknown error";
                        Log.e("FilesInnerTaskManager", "Error: " + response.code() + " - " + errorMessage);
                        callback.onError(errorMessage);
                    }
                } catch (Exception e) {
                    Log.e("FilesInnerTaskManager", "Parsing Error: " + e.getMessage());
                    callback.onError("Parsing error: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<FilesResponse> call, Throwable t) {
                Log.e("FilesInnerTaskManager", "Request Failed: " + t.getMessage());
                callback.onError(t.getMessage());
            }
        });
    }
}
/*
curl -X GET "https://aws.customquoter.co.uk/api/m/jobs/schedules/5675/tasks/805/files?page=1&per_page=10" \
-H "Authorization: Bearer 5470|rewRTEGNCnsi7Bt8DQkyKRQQ0oi8TFDgriv74BBE" \
-H "Postman-Token: <calculated_when_request_is_sent>" \
-H "Host: aws.customquoter.co.uk" \
-H "User-Agent: PostmanRuntime/7.43.0" \
-H "Accept: " \  <--- get data rom postman
-H "Accept-Encoding: gzip, deflate, br" \
-H "Connection: keep-alive" \
-H "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2" \
-H "Accept: application/json" \
-H "Content-Type: application/json"
 */