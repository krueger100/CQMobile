package com.example.cq_mobile.ui.home.UpdateJobsFolder.UpdateAPIFolder;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class UpdateAddFilesApiManager {
    private static final String TAG = "UpdateAddFilesApi";

    public static boolean uploadTaskFiles(String accessToken, String jobScheduleId, int taskId, File[] files, String apiKey) {
        Log.w(TAG, "jobScheduleId: " + jobScheduleId + "\n" + "taskId: " + taskId);

        String baseUrl = "https://cqbms.app";
        String endpoint = "/api/m/jobs/schedules/" + jobScheduleId + "/tasks/" + taskId + "/files";
        String url = baseUrl + endpoint;

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Log.d(TAG, "URL -> " + url);

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        for (File file : files) {
            if (file == null || !file.exists()) {
                Log.e(TAG, "File is null or does not exist: " + file);
                return false;
            }
            multipartBuilder.addFormDataPart(
                    "task_files[]", // Ensure the parameter matches Postman
                    file.getName(),
                    RequestBody.create(file, MediaType.parse("image/jpeg"))
            );
        }

        RequestBody requestBody = multipartBuilder.build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("x-api-key", apiKey)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (response.isSuccessful()) {
                Log.d(TAG, "Files uploaded successfully: " + responseBody);
                return true;
            } else {
                Log.e(TAG, "Upload failed: " + response.code() + " - " + response.message() + "\nResponse Body: " + responseBody);
                return false;
            }
        } catch (IOException e) {
            Log.e(TAG, "Upload error: " + e.getMessage());
            return false;
        }
    }
}



/*
        private void loadFiles(int page, String token) {
        if (isLoading || page == lastLoadedPage) return;
        isLoading = true;
        lastLoadedPage = page;

        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this);
        List<Integer> savedJobIdsList = sharedPrefManager.getJobIds();
        Map<Integer, List<Integer>> jobTaskMap = sharedPrefManager.getJobTaskMap();

        if (savedJobIdsList == null || savedJobIdsList.isEmpty()) {
            Log.e(TAG, "Error: No saved job IDs found!");
            isLoading = false;
            return; // ❌
        }

        savedJobIds = TextUtils.join(",", savedJobIdsList);
        Set<Integer> allTaskIdsSet = new HashSet<>();
        for (int jobId : savedJobIdsList) {
            List<Integer> taskIds = jobTaskMap.get(jobId);
            if (taskIds != null) {
                allTaskIdsSet.addAll(taskIds);
            }
        }

        if (allTaskIdsSet.isEmpty()) {
            Log.e(TAG, "Error: No task IDs found for jobs: " + savedJobIds);
            isLoading = false;
            return; // ❌
        }
        taskIds = TextUtils.join(",", allTaskIdsSet);

        String url = baseUrl + "/api/m/jobs/schedules/" + jobScheduleIdStr + "/tasks/" + taskIdsStr + "/files?page=" + page + "&per_page=" + pageSize;
        Log.d(TAG, "Loading files from URL: " + url);

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
                    Log.d(TAG, "Files loaded successfully.");
                    if (page == 1) {
                        filesAdapter.setData(newFiles);
                    } else {
                        filesAdapter.addData(newFiles);
                    }
                    filesAdapter.notifyItemRangeInserted(filesAdapter.getItemCount() - newFiles.size(), newFiles.size());
                } else if (response.code() == 401) {
                    Log.e(TAG, "Unauthorized! Please log in again.");
                    Intent loginIntent = new Intent(FilesActivity.this, Login.class);
                    startActivity(loginIntent);
                } else {
                    Log.e(TAG, "Error loading files: " + response.message());
                    Toast.makeText(FilesActivity.this, "Error loading files", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FilesResponse> call, Throwable t) {
                isLoading = false;
                Log.e(TAG, "Error loading files: " + t.getMessage());
                Toast.makeText(FilesActivity.this, "Error loading files: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


 */

    /*
    private void showUploadDialog(File file) {
        Dialog progressDialog = new Dialog(this);
        progressDialog.setContentView(R.layout.dialog_progress);
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(() -> {boolean success = UpdateAddJobFilesApiManager.uploadTaskFiles( accessToken,  jobId,  taskIdStr, file,  apiKey);


            runOnUiThread(() -> {
                progressDialog.dismiss();
                Toast.makeText(this, success ? "File uploaded successfully" : "File upload failed", Toast.LENGTH_SHORT).show();
                if (success) refreshFileList();
            });
        }).start();
    }

     */
