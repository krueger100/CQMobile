package com.example.cq_mobile.ui.home.HomeFolder.Notes_Folder_Docs_Sheets_Files.FilesFoler;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.example.cq_mobile.ui.home.UpdateJobsFolder.UpdateAddFilesApiManagerCheckBox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FilesActivity extends AppCompatActivity {
    private TextView files_back, files_back2;
    private NavigationManagerForTask navigationManager;

    private RecyclerView recyclerView;
    private FilesAdapter filesAdapter;
    private List<FileItem> filesList;
    private boolean isLoading = false;
    private int currentPage = 1;
    private final int pageSize = 10;
    private String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";
    private String baseUrl = "https://aws.customquoter.co.uk";
    private int jobScheduleId;
    private int taskId ;
    private String accessToken;
TextView add_files;
    String jobId;
    private ActivityResultLauncher<Intent> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);

        TextView addFiles = findViewById(R.id.add_files);
        Intent intent = getIntent();
        jobScheduleId = Integer.parseInt(intent.getStringExtra("job_id"));
        taskId = Integer.parseInt(getIntent().getStringExtra("task_id"));
        // Initialize UI components
        files_back = findViewById(R.id.files_back);
        files_back2 = findViewById(R.id.files_back2);
        add_files = findViewById(R.id.add_files);

        // Initialize ActivityResultLauncher
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            String filePath = FileUtils.getPath(this, uri);
                            if (filePath != null) {
                                File file = new File(filePath);
                                showUploadDialog(file);
                            } else {
                                Toast.makeText(this, "Unable to get file path", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

        SharedPrefManager sharedPrefManager = new SharedPrefManager(FilesActivity.this);
        accessToken = sharedPrefManager.getAccessToken();
        Log.d("FilesActivity", "Access Token: " + accessToken);

        // Bottom navigation setup
        Notes_Files_Docs_Sheets_nav bottomNavView = findViewById(R.id.nfds_bottom);
        navigationManager = new NavigationManagerForTask(this);
        navigationManager.setUpNavigation(bottomNavView, files_back2);

         jobId = String.valueOf(jobScheduleId);

        // Back button setup
        files_back.setOnClickListener(v -> {
            Intent backIntent = new Intent(FilesActivity.this, NewBuild.class);
            backIntent.putExtra("job_id", jobId);
            backIntent.putExtra("task_id", taskId);
            startActivity(backIntent);
        });
        files_back2.setOnClickListener(v -> {
            Intent backIntent = new Intent(FilesActivity.this, NewBuild.class);
            backIntent.putExtra("job_id", jobId);
            backIntent.putExtra("task_id", taskId);
            startActivity(backIntent);
        });


        // RecyclerView setup
        recyclerView = findViewById(R.id.recyclerview_files);
        filesList = new ArrayList<>();
        filesAdapter = new FilesAdapter(this, filesList, baseUrl, accessToken, apiKey);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(filesAdapter);

        // Load files initially
        loadFiles(currentPage, accessToken, String.valueOf(taskId));

        // Infinite scroll listener
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Make sure LinearLayoutManager is being used and it's not already loading
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading) {
                    // Check if the user has scrolled to the bottom of the RecyclerView
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    // Trigger data loading if we've reached the end of the list
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        currentPage++; // Increment page number for pagination
                        loadFiles(currentPage, accessToken, String.valueOf(taskId)); // Load the next set of files
                    }
                }
            }
        });


        addFiles.setOnClickListener(v -> openFilePicker());
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        filePickerLauncher.launch(intent);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showUploadDialog(File file) {
        Dialog progressDialog = new Dialog(this);
        progressDialog.setContentView(R.layout.dialog_progress);
        progressDialog.setCancelable(false);
        progressDialog.show();



        // Start uploading the file
        new Thread(() -> {
            boolean success = UpdateAddFilesApiManagerCheckBox.uploadTaskFiles(
                    accessToken,
                    String.valueOf(jobScheduleId),
                    String.valueOf(taskId),
                    new File[]{file},
                    apiKey
            );
            runOnUiThread(() -> {
                progressDialog.dismiss();
                if (success) {
                    Toast.makeText(this, "File uploaded successfully", Toast.LENGTH_SHORT).show();
                    try {
                        Thread.sleep(1000);
                        new Handler().postDelayed(() -> showDialog(), 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }    
                
                } else {
                    Toast.makeText(this, "File upload failed", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void showDialog() {
        // Show a new dialog (after upload success)
        AlertDialog.Builder builder = new AlertDialog.Builder(FilesActivity.this);
        builder.setTitle("File Upload Complete")
                .setMessage("The file has been successfully uploaded. Do you want to proceed?")
                .setCancelable(false)  // Prevent dismissal by back button or outside
                .setPositiveButton("Ok", (dialog, which) -> {
                    // Handle OK button click and proceed with the next activity
                    navigateToNextActivity();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Handle Cancel button click (optional, just close the dialog)
                    dialog.dismiss();
                });

        AlertDialog progressDialog = builder.create();
        progressDialog.show();
    }
    private void navigateToNextActivity() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent backIntent = new Intent(FilesActivity.this, NewBuild.class);
        backIntent.putExtra("job_id", jobId);
        backIntent.putExtra("task_id", taskId);
        startActivity(backIntent);
        finish();
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
                    filesAdapter.notifyItemRangeInserted(filesAdapter.getItemCount() - newFiles.size(), newFiles.size());
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
/*
            filesAdapter.notifyDataSetChanged();
                    Intent filesActivityIntent = new Intent(FilesActivity.this, FilesActivity.class);
                    startActivity(filesActivityIntent);
 */