package com.example.cq_mobile.ui.home.HomeFolder.NotesNFilesAPI_folder.UserInfoFolderForFiles;

import android.content.Context;
import android.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SheetsFilesManager {
    private final String baseUrl = "https://aws.customquoter.co.uk/";  // Base URL
    private final String accessToken;  // Store the access token
    private final Context context;

    // Updated constructor to accept accessToken
    public SheetsFilesManager(Context context, String accessToken) {
        this.context = context;
        this.accessToken = accessToken;
    }

    public void loadFiles(String cqLocal, int jobScheduleId, int page, int pageSize, String fileType, final FilesCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SheetsFilesApi sheetsFilesApi = retrofit.create(SheetsFilesApi.class);

        // Making the API call
        Call<AllFilesResponse> call = sheetsFilesApi.getFiles(
                jobScheduleId,
                page,
                pageSize,
                fileType,
                "Bearer " + accessToken  // Pass the Authorization header dynamically
        );

        call.enqueue(new Callback<AllFilesResponse>() {
            @Override
            public void onResponse(Call<AllFilesResponse> call, Response<AllFilesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AllFileItem> files = response.body().getData();

                    for (AllFileItem file : files) {
                        // Log the file details
                        Log.d("SheetsFilesManager", "File Other_title: " + file.getOther_title());
                        Log.d("SheetsFilesManager", "File ID: " + file.getId());
                        Log.d("SheetsFilesManager", "File Title: " + file.getTitle());
                    }

                    callback.onFilesLoaded(files);  // Pass the list of files to the callback
                } else {
                    String errorMessage = response.message() != null ? response.message() : "Unknown error";
                    callback.onError(errorMessage);  // Pass the error message to the callback
                }
            }

            @Override
            public void onFailure(Call<AllFilesResponse> call, Throwable t) {
                callback.onError(t.getMessage());  // Pass the failure message to the callback
            }
        });
    }

    public interface FilesCallback {
        void onFilesLoaded(List<AllFileItem> files);
        void onError(String errorMessage);
    }
}


