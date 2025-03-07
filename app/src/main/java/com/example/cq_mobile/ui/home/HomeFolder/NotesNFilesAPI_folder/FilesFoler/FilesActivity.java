package com.example.cq_mobile.ui.home.HomeFolder.NotesNFilesAPI_folder.FilesFoler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.NavigationManagerForTask;
import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.Notes_Files_Docs_Sheets_nav;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.LoginFolder.Login;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.NewBuild;
import com.example.cq_mobile.ui.home.UpdateJobsFolder.UpdateAPIFolder.UpdateAddFilesApiManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.Manifest;

public class FilesActivity extends AppCompatActivity {
    private TextView files_back, files_back2;
    private NavigationManagerForTask navigationManager;
    private ActivityResultLauncher<String> cameraPermissionLauncher;

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
    LinearLayout emptyTask;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private Uri photoUri;
    private File photoFile;

    ImageView add_photo;
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
        emptyTask = findViewById(R.id.emptyTask);
        add_photo = findViewById(R.id.add_photo);

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


        // Request Camera Permission
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (!isGranted) {
                        Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                    }
                });

        if (Build.VERSION.SDK_INT >= 34) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }

        // ActivityResultLauncher for handling camera capture and uploading image
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (photoFile != null && photoFile.exists()) {
                            Toast.makeText(this, "Uploading Photo", Toast.LENGTH_SHORT).show();
                            showCapturedPic(photoFile);
                        } else {
                            Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Image capture cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
        );


        requestCameraPermission();

        // Set OnClickListener for add_photo button
        add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });



        // RecyclerView setup
        recyclerView = findViewById(R.id.recyclerview_files);
        filesList = new ArrayList<>();
        filesAdapter = new FilesAdapter(this, filesList, baseUrl, accessToken, apiKey ,emptyTask);

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

        add_files.setOnClickListener(v -> openFilePicker());
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile(); // Create a file to store the image
                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(this, "com.example.cq_mobile.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    cameraLauncher.launch(takePictureIntent);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No camera app found!", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            Log.e("FilesActivity", "Failed to create directory");
        }

        File image = new File(storageDir, imageFileName);
        if (image.createNewFile()) {
            Log.d("FilesActivity", "File created: " + image.getAbsolutePath());
        } else {
            Log.e("FilesActivity", "File creation failed");
        }

        return image;
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

        new Thread(() -> {
            boolean success = UpdateAddFilesApiManager.uploadTaskFiles(
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
                    new Handler(Looper.getMainLooper()).postDelayed(this::refreshFileList, 1000);
                } else {
                    Toast.makeText(this, "File upload failed", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }



    @SuppressLint("NotifyDataSetChanged")
    private void showCapturedPic(File file) {
        Dialog progressDialog = new Dialog(this);
        progressDialog.setContentView(R.layout.dialog_progress);
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(() -> {
            boolean success = UpdateAddFilesApiManager.uploadTaskFiles(
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
                    new Handler(Looper.getMainLooper()).postDelayed(this::refreshFileList, 1000);
                } else {
                    Toast.makeText(this, "File upload failed", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
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
                if (response.isSuccessful() && response.body() != null) {
                    List<FileItem> newFiles = response.body().getData();
                    Log.d("FilesActivity", "Files loaded successfully.");
                    filesAdapter.addData(newFiles);
                    filesAdapter.notifyItemRangeInserted(filesAdapter.getItemCount() - newFiles.size(), newFiles.size());
                } else if (response.code() == 401) {
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
    private void refreshFileList() {
        currentPage = 1; // Reset pagination
        filesList.clear(); // Clear the list
        filesAdapter.notifyDataSetChanged(); // Notify adapter
        loadFiles(currentPage, accessToken, String.valueOf(taskId)); // Reload files
    }

}
/*
            filesAdapter.notifyDataSetChanged();
                    Intent filesActivityIntent = new Intent(FilesActivity.this, FilesActivity.class);
                    startActivity(filesActivityIntent);
 */