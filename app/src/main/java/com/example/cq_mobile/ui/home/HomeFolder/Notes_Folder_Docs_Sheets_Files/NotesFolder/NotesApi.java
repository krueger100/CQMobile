package com.example.cq_mobile.ui.home.HomeFolder.Notes_Folder_Docs_Sheets_Files.NotesFolder;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface NotesApi {
    @GET
    Call<NotesResponse> getNotes(
            @Url String url, // Dynamic URL for pagination
            @Header("Authorization") String token,
            @Header("x-api-key") String apiKey
    );
}
