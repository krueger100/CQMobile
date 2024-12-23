package com.example.cq_mobile.ui.home.HomeFolder.Notes_Folder_Docs_Sheets_Files.NotesFolder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.NavigationManagerForTask;
import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.Notes_Files_Docs_Sheets_nav;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.NewBuild;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NotesAdapter notesAdapter;
    private List<Note> notesList;
    private boolean isLoading = false;
    private int currentPage = 1;
    private final int pageSize = 10; // Matches the API's "per_page" parameter
    private String token = "4118|UkUAHYqaFsqPoHtlEosF1ocYbuSpT57AvfRnj0TF";
    private String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";
    private String baseUrl = "https://aws.customquoter.co.uk";
    private int jobScheduleId;

    TextView notes_back, notes_back2;
    private NavigationManagerForTask navigationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        notes_back = findViewById(R.id.notes_back);
        notes_back2 = findViewById(R.id.notes_back2);
        Intent intent1 = getIntent();
        String jobId = intent1.getStringExtra("job_id");
        jobScheduleId = Integer.parseInt(jobId);

        Notes_Files_Docs_Sheets_nav bottomNavView = findViewById(R.id.nfds_bottom);
        navigationManager = new NavigationManagerForTask(this);
        navigationManager.setUpNavigation(bottomNavView, notes_back2);

        recyclerView = findViewById(R.id.recyclerview_notes);
        notesList = new ArrayList<>();
        notesAdapter = new NotesAdapter(notesList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(notesAdapter);

        loadNotes(currentPage);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == notesList.size() - 1) {
                    currentPage++;
                    loadNotes(currentPage);
                }
            }
        });

        notes_back.setOnClickListener(v -> {
            Intent intent = new Intent(NotesActivity.this, NewBuild.class);
            intent.putExtra("job_id", jobId);
            startActivity(intent);
        });

        notes_back2.setOnClickListener(v -> {
            Intent intent = new Intent(NotesActivity.this, NewBuild.class);
            intent.putExtra("job_id", jobId);
            startActivity(intent);
        });
    }

    private void loadNotes(int page) {
        isLoading = true;
        String url = baseUrl + "/api/m/jobs/schedules/" + jobScheduleId + "/notes?page=" + page + "&per_page=" + pageSize;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NotesApi notesApi = retrofit.create(NotesApi.class);
        Call<NotesResponse> call = notesApi.getNotes(url, "Bearer " + token, apiKey);

        call.enqueue(new Callback<NotesResponse>() {
            @Override
            public void onResponse(Call<NotesResponse> call, Response<NotesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Note> newNotes = response.body().getData();
                    notesAdapter.addData(newNotes);
                }
                isLoading = false;
            }

            @Override
            public void onFailure(Call<NotesResponse> call, Throwable t) {
                isLoading = false;
                Toast.makeText(NotesActivity.this, "Failed to load notes: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
