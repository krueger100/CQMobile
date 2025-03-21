package com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.TaskUserDataFolder;

import android.content.Context;
import android.util.Log;

import com.example.cq_mobile.ui.home.HomeFolder.NotesNFilesAPI_folder.UserInfoFolderForFiles.AllFileItem;
import com.example.cq_mobile.ui.home.HomeFolder.NotesNFilesAPI_folder.UserInfoFolderForFiles.AllFilesResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AllFilesManager {
    private final String baseUrl ="https://cqbms.app"; //"https://aws.customquoter.co.uk/";  // Base URL
    private final String accessToken;  // Store the access token
    private final Context context;

    // Updated constructor to accept accessToken
    public AllFilesManager(Context context, String accessToken) {
        this.context = context;
        this.accessToken = accessToken;
    }

    public void loadFiles(String cqLocal, int jobScheduleId, int page, int pageSize, final FilesCallback callback) {
        // Construct the proper URL with the provided parameters
        String url = cqLocal + "/api/m/jobs/schedules/" + jobScheduleId + "/tasks/806/files?page=" + page +
                "&per_page=" + pageSize + "&type=docs&token=" + accessToken;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AllFilesApi allFilesApi = retrofit.create(AllFilesApi.class);

        // Making the API call with the accessToken passed in the Authorization header
        Call<AllFilesResponse> call = allFilesApi.getFiles(
                page,
                pageSize,
                accessToken,  // Pass the access token as the query parameter (token=accessToken)
                "Bearer " + accessToken // Pass the Authorization header dynamically
        );

        call.enqueue(new Callback<AllFilesResponse>() {
            @Override
            public void onResponse(Call<AllFilesResponse> call, Response<AllFilesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Modify each file's URL by appending the token as a query parameter
                    List<AllFileItem> files = response.body().getData();
                    for (AllFileItem file : files) {
                        // Append the token to the file's URL
                        String fileUrl = file.getUrl();
                        String fileUrlWithToken = fileUrl + "?token=" + accessToken;
                        file.setUrl(fileUrlWithToken);  // Set the new URL with the token
                        Log.d("AllFilesManager", "FileURL with token: " + fileUrlWithToken);
                    }

                    // Successfully received the files
                    callback.onFilesLoaded(files);
                } else {
                    // Handle the error
                    String errorMessage = response.message() != null ? response.message() : "Unknown error";
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<AllFilesResponse> call, Throwable t) {
                // Handle the failure
                callback.onError(t.getMessage());
            }
        });
    }

    public interface FilesCallback {
        void onFilesLoaded(List<AllFileItem> files);
        void onError(String errorMessage);
    }
}
/*
Call
        // Handle Task INFO -------------------->>
        AllFilesManager allFilesManager = new AllFilesManager(this, accessToken);
        allFilesManager.loadFiles(cqLocal, Integer.parseInt(jobId), 1, 10, new AllFilesManager.FilesCallback() {
            @Override
            public void onFilesLoaded(List<AllFileItem> files) {
                for (AllFileItem file : files) {
                    // Log the file details (URL, ID, and Title)
                    Log.d("AllFilesManager", "File URL-> " + file.getUrl());
                    Log.d("AllFilesManager", "File ID-> " + file.getId());
                    Log.d("AllFilesManager", "File Title-> " + file.getTitle());


                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("AllFilesManager","ERROR: "+errorMessage);
            }
        });

        // Handle Task INFO <<--------------------

 */