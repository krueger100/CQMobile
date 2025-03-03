package com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.TaskUserDataFolder;
import com.example.cq_mobile.ui.home.HomeFolder.NotesNFilesAPI_folder.UserInfoFolderForFiles.AllFilesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface AllFilesApi {
    @GET("api/m/jobs/schedules/5675/tasks/805/files")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2"  // Static API key remains as is
    })
    Call<AllFilesResponse> getFiles(@Query("page") int page,
                                    @Query("per_page") int perPage,
                                    @Query("token") String token,  // Token as query parameter
                                    @Header("Authorization") String authorization);  // Authorization header dynamically
}



