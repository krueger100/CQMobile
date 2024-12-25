package com.example.cq_mobile.ui.home.HomeFolder.Notes_Folder_Docs_Sheets_Files.DocsFolder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.NavigationManagerForTask;
import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.Notes_Files_Docs_Sheets_nav;
import com.example.cq_mobile.LoginFolder.Login;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.NewBuild;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DocsActivity  extends AppCompatActivity {
    TextView docs_back,docs_back2;
    private NavigationManagerForTask navigationManager;

    private RecyclerView recyclerView;
    private DocsAdapter docsAdapter;
    private List<DocsItem> docsList;
    private boolean isLoading = false;
    private int currentPage = 1;
    private final int pageSize = 10;
    private String token = "4122|YroTV0BAFJBKp0lHKlqJ4pdRSSozU0FkhKYfjYr7";
    private String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";
    private String baseUrl = "https://aws.customquoter.co.uk";
    private int jobScheduleId;
    private int taskId = 773;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docs);
        docs_back = findViewById(R.id.docs_back);
        docs_back2 = findViewById(R.id.docs_back2);
        Intent intent1 = getIntent();
        String jobId = intent1.getStringExtra("job_id");

        Intent intent = getIntent();
        jobScheduleId = Integer.parseInt(intent.getStringExtra("job_id"));



        Notes_Files_Docs_Sheets_nav bottomNavView = findViewById(R.id.nfds_bottom);
        navigationManager = new NavigationManagerForTask(this);
        navigationManager.setUpNavigation(bottomNavView,docs_back2);


        docs_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DocsActivity.this, NewBuild.class);
                intent.putExtra("job_id", jobId);
                startActivity(intent);
            }
        });
        docs_back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DocsActivity.this, NewBuild.class);
                intent.putExtra("job_id", jobId);
                startActivity(intent);
            }
        });


        recyclerView = findViewById(R.id.recyclerview_docs);
        docsList = new ArrayList<>();
        docsAdapter = new DocsAdapter(this, docsList, baseUrl, token, apiKey);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(docsAdapter);

        loadFiles(currentPage,token);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == docsList.size() - 1) {
                    currentPage++;
                    loadFiles(currentPage,token);
                }
            }
        });
    }

    private void loadFiles(int page, String token) {
        isLoading = true;
        String url = baseUrl + "/api/m/jobs/schedules/" + jobScheduleId + "/tasks/" + taskId + "/files?page=" + page + "&per_page=" + pageSize;

        // Debugging: Log the request URL
        Log.d("FilesActivity", "Loading files from URL: " + url);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DocsApi docsApi = retrofit.create(DocsApi.class);
        Call<DocsResponse> call = docsApi.getDocs(url, "Bearer " + token, apiKey);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<DocsResponse> call, Response<DocsResponse> response) {
                if (response.isSuccessful()) {
                    List<DocsItem> newFiles = response.body().getData();
                    Log.d("FilesActivity", "Files loaded successfully.");
                    docsAdapter.addData(newFiles);
                } else if (response.code() == 401) {  // Unauthorized error
                    Log.e("FilesActivity", "Unauthorized! Please log in again.");
                    // You could redirect to the login activity here
                    Intent loginIntent = new Intent(DocsActivity.this, Login.class);
                    startActivity(loginIntent);
                } else {
                    Log.e("FilesActivity", "Error loading files: " + response.message());
                    Toast.makeText(DocsActivity.this, "Error loading files", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DocsResponse> call, Throwable t) {
                isLoading = false;
                Log.e("FilesActivity", "Error loading files: " + t.getMessage());
                Toast.makeText(DocsActivity.this, "Error loading files: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}