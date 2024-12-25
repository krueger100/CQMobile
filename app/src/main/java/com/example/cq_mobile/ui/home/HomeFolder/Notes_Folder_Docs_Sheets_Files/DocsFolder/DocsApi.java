package com.example.cq_mobile.ui.home.HomeFolder.Notes_Folder_Docs_Sheets_Files.DocsFolder;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Url;

public interface DocsApi {
    @GET
    Call<DocsResponse> getDocs(
            @Url String url, // Dynamic URL for pagination
            @Header("Authorization") String token,
            @Header("x-api-key") String apiKey
    );
}
