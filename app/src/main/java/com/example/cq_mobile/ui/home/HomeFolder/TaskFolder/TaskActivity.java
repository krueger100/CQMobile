package com.example.cq_mobile.ui.home.HomeFolder.TaskFolder;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.cq_mobile.HelperManagers.Animation.ClickAnimationManager;
import com.example.cq_mobile.HelperManagers.Animation.TransitionAnimationManager;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.LoginFolder.AuthManager;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.NewBuild;
import com.example.cq_mobile.ui.home.HomeFolder.NotesNFilesAPI_folder.FilesFoler.CameraXActivity;
import com.example.cq_mobile.ui.home.HomeFolder.NotesNFilesAPI_folder.FilesFoler.FileItem;
import com.example.cq_mobile.ui.home.HomeFolder.NotesNFilesAPI_folder.FilesFoler.FileUtils;
import com.example.cq_mobile.ui.home.HomeFolder.TaskFolder.FilesINTaskFolder.FileInnerTaskAdapter;
import com.example.cq_mobile.ui.home.HomeFolder.TaskFolder.FilesINTaskFolder.FilesInnerTaskManager;
import com.example.cq_mobile.ui.home.HomeFolder.TaskFolder.JobDetailsManagerFolder.JobDetailsManager;
import com.example.cq_mobile.ui.home.UpdateJobsFolder.UpdateAPIFolder.UpdateAddFilesApiManager;



import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TaskActivity extends AppCompatActivity {
    private RecyclerView filesRecyclerView;
    private FileInnerTaskAdapter filesAdapter;
    private List<FileItem> filesList;
    private int currentPage = 1;
    private final int pageSize = 10;
    private FilesInnerTaskManager filesManager;

    TextView taskBack;
    LinearLayout emptyChecklist;
    TextView files_text;
    private String baseUrl = "https://cqbms.app";
    String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";
    private static final String TAG = "TaskActivity";
    ProgressBar progressBar;
    String taskIds;
    private Map<Integer, Set<Integer>> jobToTaskMap = new HashMap<>();
    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private ActivityResultLauncher<Intent> filePickerLauncher, cameraLauncher;
    SharedPrefManager sharedPrefManager;
    int taskId;
    String accessToken;
    TextView add_files,add_photo;
    private int lastLoadedPage = -1;
    private boolean isLoading = false;
    private LinearLayout emptyTask;
    String jobId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Intent intent = getIntent();
         jobId = intent.getStringExtra("jobId");
         taskId = intent.getIntExtra("taskId", -1);
        String title_1 = intent.getStringExtra("title");
        String description_1 = intent.getStringExtra("description");

         sharedPrefManager = new SharedPrefManager(TaskActivity.this);
        String accessToken = AuthManager.getInstance(this).getToken();
        int userId = sharedPrefManager.getUserId();
        String firstName = sharedPrefManager.getFirstName();
        String lastName = sharedPrefManager.getLastName();
        String email = sharedPrefManager.getEmail();
        emptyChecklist= findViewById(R.id.emptyChecklist);
        files_text= findViewById(R.id.files_text);
        progressBar= findViewById(R.id.progressBar);
        emptyTask = findViewById(R.id.emptyTask);

        Log.d("TaskActivitySharedPreff", "Retrieved User Data: ");
        Log.d("TaskActivitySharedPreff", "Access Token: " + accessToken);
        Log.d("TaskActivitySharedPreff", "User ID: " + userId);
        Log.d("TaskActivitySharedPreff", "First Name: " + firstName);
        Log.d("TaskActivitySharedPreff", "Last Name: " + lastName);
        Log.d("TaskActivitySharedPreff", "Email: " + email);
        Log.d("TaskActivity", "Received jobId: " + jobId + ", taskId: " + taskId);
        Log.d("TaskActivity", "Received title: " + title_1);
        Log.d("TaskActivity", "Received description: " + description_1);
        Log.d(TAG, "TaskID " + taskId);


        // Initialize UI components
        TextView titleTextView = findViewById(R.id.task_title);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        TextView priorityTextView = findViewById(R.id.priorityTextView);
        TextView statusTextView = findViewById(R.id.statusTextView);
        TextView startDateTextView = findViewById(R.id.startDateTextView);
        TextView endDateTextView = findViewById(R.id.endDateTextView);
        taskBack = findViewById(R.id.task_back);
        RecyclerView recycler_view = findViewById(R.id.recycler_view);
        RecyclerView filesRecyclerView = findViewById(R.id.filesRecyclerView);
        LinearLayout assigneeAvatarLayout = findViewById(R.id.linearLayout2);
        assigneeAvatarLayout.setOrientation(LinearLayout.HORIZONTAL);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        add_photo= findViewById(R.id.add_photo);
        add_files= findViewById(R.id.add_files);





        add_photo.setOnClickListener(v -> {
            ClickAnimationManager.applyClickAnimation(v); // Apply animation
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            } else {
                openCameraX();
            }
        });
        add_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionAnimationManager.slideOutToRight(v, 150);
                v.postDelayed(() -> {
                    v.postDelayed(() -> {
                        TransitionAnimationManager.slideInFromRight(v, 50);
                    }, 150);
                    openFilePicker();
                }, 150);
            }
        });


        taskBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(TaskActivity.this, NewBuild.class);
                backIntent.putExtra("job_id", jobId);
                backIntent.putExtra("task_id", taskId);
                startActivity(backIntent);
            }
        });

        taskIds = sharedPrefManager.getTaskIdsAsString(Integer.parseInt(jobId));

        int numColumns = 2;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numColumns);
        filesRecyclerView.setLayoutManager(gridLayoutManager);
        filesList = new ArrayList<>();
        filesAdapter = new FileInnerTaskAdapter(this, filesList, baseUrl, accessToken, apiKey, emptyTask);
        filesManager = new FilesInnerTaskManager(this, baseUrl, apiKey);

        filesRecyclerView.setAdapter(filesAdapter);
        loadFiles(Integer.parseInt(jobId),taskId, filesAdapter);

        initActivityResultLaunchers(filesAdapter);
    /*    filesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading() && layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == filesList.size() - 1) {
                    currentPage++;
                    loadFiles(filesAdapter);
                }
            }
        });

     */


        filesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        currentPage++;
                        loadFiles(Integer.parseInt(jobId),taskId, filesAdapter);

                    }
                }
            }
        });



        TaskActivityManager manager = new TaskActivityManager();
        manager.fetchTask(Integer.parseInt(jobId), taskId, accessToken, "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2", new TaskActivityManager.TaskFetchCallback() {
                    @Override
                    public void onTaskFetched(String title, String description, String priority, String status, String startDate, String endDate, String assigneeInfo, String assigneeName, boolean isChecked,
                                              String checklistsName, String checklistsInfo) {
                        // Log the task details
                        Log.d("TaskActivity", "Task Fetched Successfully:");
                        Log.d("TaskActivity", "Title: " + title);
                        Log.d("TaskActivity", "Description: " + description);
                        Log.d("TaskActivity", "Priority: " + priority);
                        Log.d("TaskActivity", "Status: " + status);
                        Log.d("TaskActivity", "Start Date: " + startDate);
                        Log.d("TaskActivity", "End Date: " + endDate);

                        // Set Task data in UI components
                        titleTextView.setText(title);
                        if (description == null || description.isEmpty()) {
                            descriptionTextView.setText("Empty Description");
                        } else {
                            descriptionTextView.setText(description);
                        }

                        if ("Low".equalsIgnoreCase(priority)) {
                            priorityTextView.setBackgroundResource(R.drawable.button_blue);
                            priorityTextView.setTextColor(ContextCompat.getColor(TaskActivity.this, R.color.textBtnBlue));
                        } else if ("Medium".equalsIgnoreCase(priority)) {
                            priorityTextView.setBackgroundResource(R.drawable.button_green);
                            priorityTextView.setTextColor(ContextCompat.getColor(TaskActivity.this, R.color.textBtnGreen));
                        } else if ("High".equalsIgnoreCase(priority)) {
                            priorityTextView.setBackgroundResource(R.drawable.button_red);
                            priorityTextView.setTextColor(ContextCompat.getColor(TaskActivity.this, R.color.textBtnRed));
                        }


                        priorityTextView.setText(priority);
                        // statusTextView.setText(status);
                        // startDateTextView.setText(startDate);
                        endDateTextView.setText(endDate);

                        //   List<String> checkDataList = new ArrayList<>(Arrays.asList(checklistsInfo.split("\n")));
                        String[] checkDataList = checklistsInfo.split("\n");
                        checkInfoDATA(checkDataList, recycler_view, accessToken, jobId, taskId, emptyChecklist);

                        // Dynamically create and add ImageViews for each assignee
                        String[] assigneeData = assigneeInfo.split("\n");

                        for (int i = 0; i < assigneeData.length; i++) {
                            // Extract the avatar URL directly from the assignee data
                            String avatarUrl = getAvatarUrlFromAssigneeData(assigneeData[i]);

                            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                Log.d("TaskActivityImage", "Loading avatar URL: " + avatarUrl);

                                // Create a new ImageView for each assignee
                                ImageView imageView = new ImageView(TaskActivity.this);
                                int width = (int) TypedValue.applyDimension(
                                        TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
                                int height = (int) TypedValue.applyDimension(
                                        TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());

                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
                                layoutParams.setMargins(2, 0, 2, 0);
                                imageView.setLayoutParams(layoutParams);

                                try {

                                    URL avatarUrlObject = new URL(avatarUrl);

                                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                        Glide.with(TaskActivity.this)
                                                .load(avatarUrl)
                                                .circleCrop()
                                                .placeholder(R.drawable.baseline_circle)
                                                .error(R.drawable.emptyglide)
                                                .into(imageView); // Target ImageView
                                    } else {

                                        Glide.with(TaskActivity.this)
                                                .load(avatarUrlObject)
                                                .circleCrop()
                                                .placeholder(R.drawable.baseline_circle)
                                                .error(R.drawable.emptyglide)
                                                .into(imageView); // Target ImageView

                                    }

                                } catch (Exception e) {
                                    // Handle any potential error if the URL is invalid
                                    Log.e("TaskActivityImage", "Error loading avatar image: " + e.getMessage());

                                }

                                // Add the ImageView to your LinearLayout container
                                assigneeAvatarLayout.addView(imageView);
                            } else {
                                // If the URL is invalid, load a default image
                                Log.d("TaskActivityImage", "Loading default avatar image");

                                // Use a default image for invalid URLs
                                ImageView imageView = new ImageView(TaskActivity.this);
                                int width = (int) TypedValue.applyDimension(
                                        TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
                                int height = (int) TypedValue.applyDimension(
                                        TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());

                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
                                layoutParams.setMargins(8, 0, 8, 0);
                                imageView.setLayoutParams(layoutParams);


                                Glide.with(TaskActivity.this)
                                        .load(R.drawable.baseline_people_24)  // Default image
                                        .circleCrop()
                                        .into(imageView);

                                // Add the ImageView to your LinearLayout container
                                assigneeAvatarLayout.addView(imageView);


                            }
                        }

                        // Log each assignee's detailed data
                        Log.d("TaskActivity", "Assignees Details:");
                        for (String assignee : assigneeData) {
                            Log.d("TaskActivity", assignee); // Log each assignee's info
                        }

                        Log.d("TaskActivity", "Assignee Name (Concatenated): " + assigneeName);
                    }


                    @Override
                    public void onTaskFetchError(String errorMessage) {
                        Log.e("TaskActivity", "Error fetching task: " + errorMessage);

                    }
                }
        );

        JobDetailsManager.fetchJobDetails(this, progressBar, new JobDetailsManager.JobDetailsCallback() {
            @Override
            public void onJobDetailsFetched() {
                Log.d(TAG, "Job details successfully fetched!");

                SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
                List<Integer> savedJobIdsList = sharedPrefManager.getJobIds();
                Map<Integer, List<Integer>> jobTaskData = sharedPrefManager.getJobTaskMap();

                if (savedJobIdsList == null || savedJobIdsList.isEmpty()) {
                    Log.e(TAG, "Error: No saved job IDs found!");
                    return;
                }

                String savedJobIds = TextUtils.join(",", savedJobIdsList);
                Set<String> jobScheduleIdSet = new HashSet<>(Arrays.asList(jobId.split(",")));
                List<String> savedJobIdList = Arrays.asList(savedJobIds.split(","));

                String matchedJobId = savedJobIdList.stream()
                        .filter(jobScheduleIdSet::contains)
                        .findFirst()
                        .orElse(null);

                if (matchedJobId != null) {
                    Log.d(TAG, "Matched Job ID: " + matchedJobId);
                } else {
                    Log.e(TAG, "No matching Job ID found!");
                    return; // Exit if no matching job ID found
                }

                int matchedJobIdInt = Integer.parseInt(matchedJobId);
                List<Integer> matchedTaskIds = jobTaskData.getOrDefault(matchedJobIdInt, new ArrayList<>());
                Set<Integer> uniqueTaskIds = new LinkedHashSet<>(matchedTaskIds);

                String taskIdsStr = TextUtils.join(",", uniqueTaskIds);
                Log.d(TAG, "Matched Job ID: " + matchedJobIdInt + " -> Task IDs: " + taskIdsStr);

                String currentIDUrl = baseUrl + "/api/m/jobs/schedules/" + matchedJobId + "/tasks/" + taskIdsStr + "/files?page=1&per_page=" + pageSize;

                // Clear and update job-to-task mapping
                jobToTaskMap.clear();
                jobToTaskMap.put(matchedJobIdInt, new HashSet<>(matchedTaskIds));
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error fetching job details: " + error);
            }
        });


    }





    private void checkInfoDATA(String[] checkData, RecyclerView recycler_view, String accessToken, String jobId, int taskId, LinearLayout emptyChecklist) {
        List<String> checked_Id = new ArrayList<>();
        List<String> namesList = new ArrayList<>();
        List<String> checked_List = new ArrayList<>();

        // Extract names from the input data
        for (String data : checkData) {
            String name = getNamesFromCheckedData(data);
            String checkStats = getCheckedStatusFromData(data); //
            String checkID = getCheckedIDFromData(data);
            if (name != null && !name.isEmpty()) {
                if (checkStats != null && !checkStats.isEmpty()) {
                    if (checkID != null && !checkID.isEmpty()) {
                        namesList.add(name);
                        checked_List.add(checkStats);
                        checked_Id.add(checkID);
                        Log.d("checkInfoDATA", "Loading name: " + name +"  Checked: "+checked_List);
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }else {
                emptyChecklist.setVisibility(View.VISIBLE);
                recycler_view.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);

            }
        }

        // Create an adapter with the populated names list
        TaskAdapter taskAdapter = new TaskAdapter(this, namesList,checked_List,checked_Id,accessToken,jobId,taskId);
        recycler_view.setAdapter(taskAdapter);

        // Set a layout manager for the RecyclerView
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initActivityResultLaunchers(FileInnerTaskAdapter filesAdapter) {
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCameraX();
                    } else {
                        Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String photoPath = result.getData().getStringExtra("photo_path");
                        if (photoPath != null) {
                            File file = new File(photoPath);
                            showCapturedPic(file);
                        }
                    } else {
                        Toast.makeText(this, "Image capture cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            String filePath = FileUtils.getPath(this, uri);
                            if (filePath != null) {
                                showUploadDialog(new File[]{new File(filePath)});

                            } else {
                                Toast.makeText(this, "Unable to get file path", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );
    }
    private void openCameraX() {
        Intent intent = new Intent(this, CameraXActivity.class);
        cameraLauncher.launch(intent);
    }
    private void showCapturedPic(File file) {
        showUploadDialog(new File[]{file});
    }
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        filePickerLauncher.launch(intent);
    }
    private void showUploadDialog(File[] files) {
        Dialog progressDialog = new Dialog(this);
        progressDialog.setContentView(R.layout.dialog_progress);
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(() -> {
            boolean success = UpdateAddFilesApiManager.uploadTaskFiles(accessToken, jobId, taskId, files, apiKey);

            runOnUiThread(() -> {
                progressDialog.dismiss();
                Toast.makeText(this, success ? "Files uploaded successfully" : "File upload failed", Toast.LENGTH_SHORT).show();
                if (success) refreshFileList();
            });
        }).start();
    }
    private void refreshFileList() {
        currentPage = 1;
        filesList.clear();
        filesAdapter.notifyDataSetChanged();
        loadFiles(Integer.parseInt(jobId),taskId, filesAdapter);

    }


    private void loadFiles(int jobId , int taskId, FileInnerTaskAdapter filesAdapter) {

        filesManager.loadFiles(jobId, taskId, currentPage, pageSize, accessToken, new FilesInnerTaskManager.FilesCallback() {
            @Override
            public void onFilesLoaded(List<FileItem> files) {
                filesAdapter.addData(files);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("TaskActivity", "Error loading files: " + errorMessage);

            }
        });
    }




    private String getAvatarUrlFromAssigneeData(String assigneeData) {
        String avatarUrlPrefix = "Avatar: ";
        if (assigneeData.contains(avatarUrlPrefix)) {
            int startIndex = assigneeData.indexOf(avatarUrlPrefix) + avatarUrlPrefix.length();
            int endIndex = assigneeData.indexOf("\n", startIndex);
            if (endIndex == -1) {
                endIndex = assigneeData.length();
            }
            String avatarUrl = assigneeData.substring(startIndex, endIndex).trim();
            Log.d("TaskActivity", "Extracted Avatar URL: " + avatarUrl);
            return avatarUrl;
        }
        return null;
    }


    private String getCheckedIDFromData(String checkedData) {
        // Example: "Assignee ID: 1, Name: checkList sample 3, Checked: 0"
        String CheckedIDPrefix = "Id: ";
        if (checkedData.contains(CheckedIDPrefix)) {
            int startIndex = checkedData.indexOf(CheckedIDPrefix) + CheckedIDPrefix.length();
            int endIndex = checkedData.indexOf(",", startIndex); // Find the end of "Checked:" status
            if (endIndex == -1) {
                endIndex = checkedData.length(); // In case there's no comma after Checked:
            }
            String checkedStatus = checkedData.substring(startIndex, endIndex).trim();
            Log.d("TaskActivity", "Extracted Checked Status: " + checkedStatus);
            return checkedStatus;
        }
        return null;
    }

    private String getCheckedStatusFromData(String checkedData) {
        String CheckedPrefix = "Checked: ";
        if (checkedData.contains(CheckedPrefix)) {
            int startIndex = checkedData.indexOf(CheckedPrefix) + CheckedPrefix.length();
            int endIndex = checkedData.indexOf(",", startIndex); // Find the end of "Checked:" status
            if (endIndex == -1) {
                endIndex = checkedData.length(); // In case there's no comma after Checked:
            }
            String checkedStatus = checkedData.substring(startIndex, endIndex).trim();
            Log.d("TaskActivity", "Extracted Checked Status: " + checkedStatus);
            return checkedStatus;
        }
        return null;
    }

    private String getNamesFromCheckedData(String checkedData) {
        String NamePrefix = "Name: ";
        if (checkedData.contains(NamePrefix)) {
            int startIndex = checkedData.indexOf(NamePrefix) + NamePrefix.length();
            int endIndex = checkedData.indexOf(", Checked:", startIndex);
            if (endIndex == -1) {
                endIndex = checkedData.length();
            }
            String name = checkedData.substring(startIndex, endIndex).trim();
            Log.d("TaskActivity", "Extracted Name: " + name);
            return name;
        }
        return null;
    }


}
