package com.example.cq_mobile.ui.home.HomeFolder.Notes_Folder_Docs_Sheets_Files.FilesFoler;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.NavigationManagerForTask;
import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.Notes_Files_Docs_Sheets_nav;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.LoginFolder.Login;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.NewBuild;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.SubTasks.SubTask;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FilesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FilesAdapter filesAdapter;
    private List<FileItem> filesList;
    private boolean isLoading = false;
    private int currentPage = 1;
    private final int pageSize = 10;
    private String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";
    private String baseUrl = "https://aws.customquoter.co.uk";
    private int jobScheduleId;
    private String taskId ;
    private String accessToken;
    private TextView files_back, files_back2;
    private NavigationManagerForTask navigationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);

        // Retrieve Intent extras
        Intent intent = getIntent();
        jobScheduleId = Integer.parseInt(intent.getStringExtra("job_id"));
        taskId = getIntent().getStringExtra("task_id");
        // Initialize UI components
        files_back = findViewById(R.id.files_back);
        files_back2 = findViewById(R.id.files_back2);

        // Retrieve the access token from Shared Preferences
        SharedPrefManager sharedPrefManager = new SharedPrefManager(FilesActivity.this);
        accessToken = sharedPrefManager.getAccessToken();
        Log.d("FilesActivity", "Access Token: " + accessToken);

        // Bottom navigation setup
        Notes_Files_Docs_Sheets_nav bottomNavView = findViewById(R.id.nfds_bottom);
        navigationManager = new NavigationManagerForTask(this);
        navigationManager.setUpNavigation(bottomNavView, files_back2);

        String jobId = String.valueOf(jobScheduleId);

        // Back button setup
        files_back.setOnClickListener(v -> {
            Intent backIntent = new Intent(FilesActivity.this, NewBuild.class);
            backIntent.putExtra("job_id", jobId);
            startActivity(backIntent);
        });

        files_back2.setOnClickListener(v -> {
            Intent backIntent = new Intent(FilesActivity.this, NewBuild.class);
            backIntent.putExtra("job_id", jobId);
            startActivity(backIntent);
        });

        // RecyclerView setup
        recyclerView = findViewById(R.id.recyclerview_files);
        filesList = new ArrayList<>();
        filesAdapter = new FilesAdapter(this, filesList, baseUrl, accessToken, apiKey);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(filesAdapter);

        // Load files initially
        loadFiles(currentPage, accessToken, taskId);

        // Infinite scroll listener
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == filesList.size() - 1) {
                    currentPage++;
                    loadFiles(currentPage, accessToken, taskId);
                }
            }
        });
    }

    private void loadFiles(int page, String token, String taskid) {
        isLoading = true;
        String url = baseUrl + "/api/m/jobs/schedules/" + jobScheduleId + "/tasks/" + taskid + "/files?page=" + page + "&per_page=" + pageSize;

        // Debugging: Log the request URL
        Log.d("FilesActivity", "Loading files from URL: " + url);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FilesApi filesApi = retrofit.create(FilesApi.class);
        Call<FilesResponse> call = filesApi.getFiles(url, "Bearer " + token, apiKey);

        call.enqueue(new Callback<FilesResponse>() {
            @Override
            public void onResponse(Call<FilesResponse> call, Response<FilesResponse> response) {
                isLoading = false;
                if (response.isSuccessful()) {
                    List<FileItem> newFiles = response.body().getData();
                    Log.d("FilesActivity", "Files loaded successfully.");
                    filesAdapter.addData(newFiles);
                } else if (response.code() == 401) {  // Unauthorized error
                    Log.e("FilesActivity", "Unauthorized! Please log in again.");
                    Intent loginIntent = new Intent(FilesActivity.this, Login.class);
                    startActivity(loginIntent);
                } else {
                    Log.e("FilesActivity", "Error loading files: " + response.message());
                    Toast.makeText(FilesActivity.this, "Error loading files", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FilesResponse> call, Throwable t) {
                isLoading = false;
                Log.e("FilesActivity", "Error loading files: " + t.getMessage());
                Toast.makeText(FilesActivity.this, "Error loading files: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
