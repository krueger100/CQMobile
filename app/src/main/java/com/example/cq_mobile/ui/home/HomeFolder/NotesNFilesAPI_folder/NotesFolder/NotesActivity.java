package com.example.cq_mobile.ui.home.HomeFolder.NotesNFilesAPI_folder.NotesFolder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.HelperManagers.Animation.ClickAnimationManager;
import com.example.cq_mobile.HelperManagers.Animation.TransitionAnimationManager;
import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.NavigationManagerForTask;
import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.Notes_Files_Docs_Sheets_nav;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
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
    private static final String TAG = "NotesActivity";

    private RecyclerView recyclerView;
    private NotesAdapter notesAdapter;
    private List<Note> notesList;
    private boolean isLoading = false;
    private int currentPage = 1;
    private final int pageSize = 10; // Matches the API's "per_page" parameter
    private String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";
    private String baseUrl = "https://cqbms.app";//"https://aws.customquoter.co.uk";
    private int jobScheduleId;
    private String taskId ;
    TextView notes_back, notes_back2;
    private NavigationManagerForTask navigationManager;
   String token;
    int userId;
    String jobId;
    String email;
    String password;
    String username,avatar;
    TextView Notes_off,add_notes;
    EditText Notes_body;
   CardView cardViewNotesInput;
   ImageButton addNotesButton;
    LinearLayout emptyTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        notes_back = findViewById(R.id.notes_back);
        notes_back2 = findViewById(R.id.notes_back2);
        add_notes = findViewById(R.id.add_notes);
        addNotesButton = findViewById(R.id.addNotesButton);
        cardViewNotesInput = findViewById(R.id.cardViewNotesInput);
        Notes_off= findViewById(R.id.Notes_off);
        Notes_body  = findViewById(R.id.Notes_body);
        emptyTask  = findViewById(R.id.emptyTask);


        SharedPrefManager sharedPrefManager = new SharedPrefManager(NotesActivity.this);
        token = sharedPrefManager.getAccessToken();
        username = sharedPrefManager.getUserName();
        email = sharedPrefManager.getEmail();
        password = sharedPrefManager.getPassword();
        avatar= sharedPrefManager.getAvatarUrl();
         userId = sharedPrefManager.getUserId();

        Log.d("NotesActivity", "Access Token: " + token);


        Intent intent = getIntent();
        jobScheduleId = Integer.parseInt(intent.getStringExtra("job_id"));
        taskId = getIntent().getStringExtra("task_id");


        Notes_Files_Docs_Sheets_nav bottomNavView = findViewById(R.id.nfds_bottom);
        navigationManager = new NavigationManagerForTask(this);
        navigationManager.setUpNavigation(bottomNavView, notes_back2);


        recyclerView = findViewById(R.id.recyclerview_notes);
        notesList = new ArrayList<>();
        notesAdapter = new NotesAdapter(notesList,NotesActivity.this,emptyTask);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(notesAdapter);
         jobId = String.valueOf((jobScheduleId));

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
            Intent intent1 = new Intent(NotesActivity.this, NewBuild.class);
            intent1.putExtra("job_id", jobId);
            startActivity(intent1);
        });

        notes_back2.setOnClickListener(v -> {
            Intent intent2 = new Intent(NotesActivity.this, NewBuild.class);
            intent2.putExtra("job_id", jobId);
            startActivity(intent2);
        });

        cardViewNotesInput.setOnClickListener(v -> {

        });

        add_notes.setOnClickListener(v -> {
            cardViewNotesInput.setVisibility(View.VISIBLE);
            TransitionAnimationManager.slideOutToRight(v, 150);
            v.postDelayed(() -> {
                v.postDelayed(() -> {
                    TransitionAnimationManager.slideInFromRight(v, 50);
                }, 150);

            }, 150);
        });

        Notes_off.setOnClickListener(v -> {
            ClickAnimationManager.applyClickAnimation(v);
            cardViewNotesInput.setVisibility(View.GONE);

        });

        addNotesButton.setOnClickListener(v -> {
            ClickAnimationManager.applyClickAnimation(v);

            String notesText = Notes_body.getText().toString().trim();
            if (notesText.isEmpty()) {
                Toast.makeText(NotesActivity.this, "Reply cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
             addNOTES(notesText);
        });

    }

    private void addNOTES(String noteText) {
        UpdateNoteApiManager.updateNote(email, password, String.valueOf(jobScheduleId), noteText, new UpdateNoteApiManager.ApiCallback() {
            @Override
            public void onSuccess() {
                Log.d("UpdateNote", "Note updated successfully!");

                runOnUiThread(() -> {
                    Note.User currentUser = new Note.User(userId, username, avatar);
                    Note newNote = new Note(0, noteText, "Just now", currentUser);

                    // Add the new note at the top of the list
                    notesList.add(0, newNote);
                    notesAdapter.notifyItemInserted(0);
                    recyclerView.scrollToPosition(0);

                    // Clear input field and hide the input view
                    Notes_body.setText("");
                    cardViewNotesInput.setVisibility(View.GONE);
                });

            }

            @Override
            public void onFailure(String error) {
                Log.e("UpdateNote", "Failed to update note: " + error);
                Toast.makeText(NotesActivity.this, "Failed to add note", Toast.LENGTH_SHORT).show();
            }
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
                    Log.d(TAG, "loadNotes  ->>: " + newNotes);
                }
                isLoading = false;
            }

            @Override
            public void onFailure(Call<NotesResponse> call, Throwable t) {
                isLoading = false;
             //   Toast.makeText(NotesActivity.this, "Failed to load notes: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Toast.makeText(NotesActivity.this, "Empty notes: ", Toast.LENGTH_SHORT).show();

            }
        });
    }
}

/*

curl -X POST "https://aws.customquoter.co.uk/api/m/jobs/schedules/8715/notes" \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer 5901|x5PY2orJyRNQsNSLf12khj7Pq6NDqGUAKNIAVtQP" \
     -H "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2" \
     -d '{
           "note": "created a test notes"
         }'
 */