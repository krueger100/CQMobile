package com.example.cq_mobile.ui.home.HomeFolder.NotesNFilesAPI_folder.FilesFoler;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Url;

public interface FilesApi {
    @GET
    Call<FilesResponse> getFiles(
            @Url String url, // Dynamic URL for pagination
            @Header("Authorization") String token,
            @Header("x-api-key") String apiKey
    );
}
