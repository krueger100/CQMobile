package com.example.cq_mobile.ui.home.HomeFolder.TaskFolder.FilesINTaskFolder;

import android.content.Context;
import android.util.Log;

import com.example.cq_mobile.ui.home.HomeFolder.NotesNFilesAPI_folder.FilesFoler.FileItem;
import com.example.cq_mobile.ui.home.HomeFolder.NotesNFilesAPI_folder.FilesFoler.FilesApi;
import com.example.cq_mobile.ui.home.HomeFolder.NotesNFilesAPI_folder.FilesFoler.FilesResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FilesManager {
    private final String baseUrl;
    private final String apiKey;
    private final Context context;

    public FilesManager(Context context, String baseUrl, String apiKey) {
        this.context = context;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public interface FilesCallback {
        void onFilesLoaded(List<FileItem> files);
        void onError(String errorMessage);
    }

    public void loadFiles(int jobScheduleId, String taskId, int page, int pageSize, String accessToken, FilesCallback callback) {
        String url = baseUrl + "/api/m/jobs/schedules/" + jobScheduleId + "/tasks/" + taskId + "/files?page=" + page + "&per_page=" + pageSize;

        // Debugging: Log the request URL
        Log.d("FilesManager", "Loading files from URL: " + url);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FilesApi filesApi = retrofit.create(FilesApi.class);
        Call<FilesResponse> call = filesApi.getFiles(url, "Bearer " + accessToken, apiKey);

        call.enqueue(new Callback<FilesResponse>() {
            @Override
            public void onResponse(Call<FilesResponse> call, Response<FilesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FileItem> files = response.body().getData();
                    callback.onFilesLoaded(files);
                } else {
                    String errorMessage = response.message() != null ? response.message() : "Unknown error";
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<FilesResponse> call, Throwable t) {
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