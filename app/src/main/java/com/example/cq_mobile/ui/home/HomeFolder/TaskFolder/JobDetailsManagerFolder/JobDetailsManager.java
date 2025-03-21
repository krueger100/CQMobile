package com.example.cq_mobile.ui.home.HomeFolder.TaskFolder.JobDetailsManagerFolder;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.cq_mobile.HelperManagers.IDSfolder.IDsManager;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.SubTasks.SubTask;
import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.TaskMainFolder.Taskmain;

import java.util.ArrayList;
import java.util.List;


public class JobDetailsManager {

    public interface JobDetailsCallback {
        void onJobDetailsFetched();
        void onError(String error);
    }

    public static void fetchJob_Details(String accessToken, ProgressBar progressBar, Context context, JobDetailsCallback callback) {
        IDsManager.fetchJobIdPaginated(accessToken, new IDsManager.ApiResponseCallback<Taskmain>() {
            @Override
            public void onDataFetched(List<Taskmain> data) {
                SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(context);
                List<Integer> newJobIds = new ArrayList<>();

                if (data != null && !data.isEmpty()) {
                    for (Taskmain task : data) {
                        newJobIds.add(task.getId());
                        fetchSub_Tasks(context, accessToken, task, progressBar, callback);
                    }

                    // ✅ Store all jobs at once
                    sharedPrefManager.saveJobIds(newJobIds);
                    Log.w("JobDetailsManager", "Saved Job IDs: " + newJobIds);
                } else {
                    handleFetchError("No job data found", accessToken, progressBar, callback);
                }
            }

            @Override
            public void onError(String error) {
                handleFetchError(error, accessToken, progressBar, callback);
            }
        });
    }

    private static void fetchSub_Tasks(Context context, String accessToken, Taskmain task, ProgressBar progressBar, JobDetailsCallback callback) {
        IDsManager.fetchTaskIdDataPaginated(String.valueOf(task.getId()), 1, 10, accessToken, new IDsManager.ApiResponseCallback<SubTask>() {
            @Override
            public void onDataFetched(List<SubTask> data) {
                SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(context);
                List<Integer> newTaskIds = new ArrayList<>();

                if (data != null && !data.isEmpty()) {
                    for (SubTask subTask : data) {
                        newTaskIds.add(subTask.getId());
                    }

                    // ✅ Store tasks correctly under their job ID
                    sharedPrefManager.saveTaskIds(task.getId(), newTaskIds);
                    Log.w("JobDetailsManager", "Saved Task IDs for Job ID " + task.getId() + ": " + newTaskIds);
                }

                progressBar.setVisibility(View.GONE);
                callback.onJobDetailsFetched();
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                callback.onError(error);
            }
        });
    }

    private static void handleFetchError(String error, String accessToken, ProgressBar progressBar, JobDetailsCallback callback) {
        Log.e("JobDetailsManager", "Error fetching job details: " + error);
        callback.onError(error);
    }
}


/*

             JobDetailsManager.fetchJob_Details(accessToken, progressBar, this, new JobDetailsManager.JobDetailsCallback() {
            @Override
            public void onJobDetailsFetched() {
                Log.d(TAG, "Job details successfully fetched!");

                // ✅
                SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
                List<Integer> savedJobIdsList = sharedPrefManager.getJobIds();
                Map<Integer, List<Integer>> jobTaskData = sharedPrefManager.getJobTaskMap();

                if (savedJobIdsList == null || savedJobIdsList.isEmpty()) {
                    Log.e(TAG, "Error: No saved job IDs found!");
                    return; // ❌
                }

                String savedJobIds = TextUtils.join(",", savedJobIdsList);

                // ✅
                Set<String> jobScheduleIdSet = new HashSet<>(Arrays.asList(jobScheduleIdStr.split(",")));
                List<String> savedJobIdList = Arrays.asList(savedJobIds.split(","));

                String matchedJobId = savedJobIdList.stream()
                        .filter(jobScheduleIdSet::contains)
                        .findFirst()
                        .orElse(null);

                if (matchedJobId != null) {
                    Log.d(TAG, "✅ Matched Job ID: " + matchedJobId);
                } else {
                    Log.e(TAG, "❌ No matching Job ID found!");
                    return; // ❌
                }

                // ✅
                int matchedJobIdInt = Integer.parseInt(matchedJobId);
                List<Integer> matchedTaskIds  = jobTaskData.getOrDefault(matchedJobIdInt, new ArrayList<>());

                Set<Integer> uniqueTaskIds = new LinkedHashSet<>(matchedTaskIds);
                 taskIdsStr = TextUtils.join(",", uniqueTaskIds);
                Log.d(TAG, "Matched Job ID: " + matchedJobIdInt + " -> Task IDs: " + taskIdsStr);

                 currentID_url = baseUrl + "/api/m/jobs/schedules/" + jobScheduleIdStr + "/tasks/" + taskIdsStr + "/files?page=" + 1 + "&per_page=" + pageSize;

                jobToTaskMap.clear();
                jobToTaskMap.put(matchedJobIdInt, new HashSet<>(matchedTaskIds));



            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error fetching job details: " + error);



            }
        });


 */