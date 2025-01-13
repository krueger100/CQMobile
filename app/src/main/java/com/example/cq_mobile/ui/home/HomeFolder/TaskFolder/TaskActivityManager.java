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
        void onTaskFetched(String title, String description, String priority, String status, String startDate, String endDate, String assigneeInfo, String assigneeName, boolean isChecked
        ,  String checklistsName, String checklistsInfo);
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

                    // Check if task.getData() is not null before accessing it
                    if (task.getData() != null) {
                        String title = task.getData().getTitle();
                        String description = task.getData().getDescription();
                        String priority = task.getData().getPriority();
                        String status = task.getData().getStatus();
                        String startDate = task.getData().getStartDate();
                        String endDate = task.getData().getEndDate();

                        // Extract assignee data with null check for assignees
                        Task.Data.Assignee[] assignees = task.getData().getAssignees();
                        StringBuilder assigneeInfo = new StringBuilder();
                        StringBuilder assigneeName = new StringBuilder();

                        Task.Data.Checklist[] checklists = task.getData().getChecklist();
                        StringBuilder checklistsInfo = new StringBuilder();
                        StringBuilder checklistsName = new StringBuilder();


                        if (checklists != null && checklists.length > 0) {
                            for (Task.Data.Checklist checklist : checklists) {
                                checklistsInfo.append("Id: ").append(checklist.getId()).append(", ")
                                        .append("Name: ").append(checklist.getName()).append(", ")
                                        .append("Checked: ").append(checklist.getChecked()).append("\n");
                                checklistsName.append(checklist.getName());

                            }
                        } else {
                            checklistsInfo.append("No checklistsInfo found.\n");
                        }



                        boolean isChecked = task.getData().isChecked();
                        Log.d("TaskActivityManager", "isChecked: " + isChecked);

                        if (assignees != null && assignees.length > 0) {
                            for (Task.Data.Assignee assignee : assignees) {
                                assigneeInfo.append("Assignee ID: ").append(assignee.getId()).append(", ")
                                        .append("Name: ").append(assignee.getName()).append(", ")
                                        .append("Avatar: ").append(assignee.getAvatar()).append("\n");

                                assigneeName.append(assignee.getName());

                            }
                        } else {
                            assigneeInfo.append("No assignees found.\n");
                        }

                        // Notify via callback with assignee info
                        callback.onTaskFetched(title, description, priority, status, startDate, endDate, assigneeInfo.toString()
                        ,assigneeName.toString(), isChecked,checklistsName.toString(),checklistsInfo.toString());

                    } else {
                        callback.onTaskFetchError("Error: Task data is null.");
                    }
                } else {
                    // Handle error in response, e.g., show error code
                    callback.onTaskFetchError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                // Handle failure scenario, e.g., network issues
                callback.onTaskFetchError("API call failed: " + t.getMessage());
            }
        });
    }


}
