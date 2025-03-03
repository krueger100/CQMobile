package com.example.cq_mobile.ui.home.HomeFolder.TaskFolder;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class TaskActivityManager {
    private static final String BASE_URL = "https://aws.customquoter.co.uk";
    private final TaskApi taskApi;

    public interface TaskFetchCallback {
        void onTaskFetched(String title, String description, String priority, String status, String startDate, String endDate, String assigneeInfo, String assigneeName, boolean isChecked, String checklistsName, String checklistsInfo);
        void onTaskFetchError(String errorMessage);
    }

    public TaskActivityManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        taskApi = retrofit.create(TaskApi.class);
    }

    public void fetchTask(int jobScheduleId, int taskId, String token, String apiKey, TaskFetchCallback callback) {
        String authToken = "Bearer " + token;
        taskApi.getTask(jobScheduleId, taskId, authToken, apiKey).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Task task = response.body();
                    if (task.getData() != null) {
                        Task.Data taskData = task.getData();

                        String title = taskData.getTitle() != null ? taskData.getTitle() : "No title available";
                        String description = taskData.getDescription() != null ? taskData.getDescription() : "No description available";
                        String priority = taskData.getPriority() != null ? taskData.getPriority() : "No priority available";
                        String status = taskData.getStatus() != null ? taskData.getStatus() : "No status available";
                        String startDate = taskData.getStartDate() != null ? taskData.getStartDate().trim() : "N/A";
                        String endDate = taskData.getEndDate() != null ? taskData.getEndDate().trim() : "N/A";


                        // Extract assignee data with null check
                        Task.Data.Assignee[] assignees = taskData.getAssignees();
                        StringBuilder assigneeInfo = new StringBuilder();
                        StringBuilder assigneeName = new StringBuilder();

                        if (assignees != null && assignees.length > 0) {
                            for (Task.Data.Assignee assignee : assignees) {
                                if (assignee != null) {
                                    assigneeInfo.append("Assignee ID: ").append(assignee.getId()).append(", ")
                                            .append("Name: ").append(assignee.getName() != null ? assignee.getName() : "Unknown").append(", ")
                                            .append("Avatar: ").append(assignee.getAvatar() != null ? assignee.getAvatar() : "No avatar").append("\n");

                                    assigneeName.append(assignee.getName() != null ? assignee.getName() : "Unknown");
                                }
                            }
                        } else {
                            assigneeInfo.append("No assignees found.\n");
                        }

                        // Extract checklist data with null check
                    Task.Data.Checklist[] checklists = taskData.getChecklist();
                        StringBuilder checklistsInfo = new StringBuilder();
                        StringBuilder checklistsName = new StringBuilder();

                        if (checklists != null && checklists.length > 0) {
                            for (Task.Data.Checklist checklist : checklists) {
                                if (checklist != null) {
                                    checklistsInfo.append("Id: ").append(checklist.getId()).append(", ")
                                            .append("Name: ").append(checklist.getName() != null ? checklist.getName() : "Unknown").append(", ")
                                            .append("Checked: ").append(checklist.getChecked()).append("\n");
                                    checklistsName.append(checklist.getName() != null ? checklist.getName() : "Unknown");
                                }
                            }
                        } else {
                            checklistsInfo.append("No checklists found.\n");
                        }

                        boolean isChecked = taskData.isChecked();
                        Log.d("TaskActivityManager", "isChecked: " + isChecked);

                        // Notify via callback
                        callback.onTaskFetched(title, description, priority, status, startDate, endDate, assigneeInfo.toString(), assigneeName.toString(), isChecked, checklistsName.toString(), checklistsInfo.toString());
                    } else {
                        Log.e("TaskActivityManager", "Error: Task data is null.");
                        callback.onTaskFetchError("Error: Task data is null.");
                    }
                } else {
                    Log.e("TaskActivityManager", "Error: " + response.code());
                    callback.onTaskFetchError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Log.e("TaskActivityManager", "API call failed: " + t.getMessage(), t);
                callback.onTaskFetchError("API call failed: " + t.getMessage());
            }
        });
    }
}
