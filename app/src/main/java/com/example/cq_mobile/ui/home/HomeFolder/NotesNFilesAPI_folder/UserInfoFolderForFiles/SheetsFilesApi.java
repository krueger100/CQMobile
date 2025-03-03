package com.example.cq_mobile.ui.home.HomeFolder.NotesNFilesAPI_folder.UserInfoFolderForFiles;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface SheetsFilesApi {
    @GET("api/m/jobs/schedules/{jobScheduleId}/files")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2" // Static API key remains as is
    })
    Call<AllFilesResponse> getFiles(
            @Path("jobScheduleId") int jobScheduleId,         // Path variable for jobScheduleId
            @Query("page") int page,                          // Query parameter for pagination
            @Query("per_page") int perPage,                   // Query parameter for pagination
            @Query("type") String type,                       // Query parameter for file type (sheets, docs, etc.)
            @Header("Authorization") String authorization     // Authorization header dynamically
    );
}
