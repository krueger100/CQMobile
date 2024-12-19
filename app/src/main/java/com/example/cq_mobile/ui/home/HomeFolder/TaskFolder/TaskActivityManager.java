package com.example.cq_mobile.ui.home.HomeFolder.TaskFolder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TaskActivityManager {
    private static final String BASE_URL = "https://aws.customquoter.co.uk";
    private final TaskApi taskApi;

    public interface TaskFetchCallback {
        void onTaskFetched(String title, String description, String priority, String status, String startDate, String endDate, String assigneeInfo, String assigneeName);
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
                        List<String> assigneeInfoList = new ArrayList<>();
                        List<String> assigneeNamesList = new ArrayList<>();
                        List<String> assigneeAvatarsList = new ArrayList<>();

                        if (assignees != null && assignees.length > 0) {
                            for (Task.Data.Assignee assignee : assignees) {
                                // Add assignee info to list
                                assigneeInfoList.add("Assignee ID: " + assignee.getId() + ", Name: " + assignee.getName() + ", Avatar: " + assignee.getAvatar());
                                assigneeNamesList.add(assignee.getName());  // Store names for later use
                                assigneeAvatarsList.add(assignee.getAvatar());  // Store avatars for later use
                            }
                        } else {
                            assigneeInfoList.add("No assignees found.");
                        }

                        // Combine assignee names for the callback
                        StringBuilder assigneeNames = new StringBuilder();
                        for (String name : assigneeNamesList) {
                            assigneeNames.append(name).append(", ");
                        }
                        if (assigneeNames.length() > 0) {
                            assigneeNames.setLength(assigneeNames.length() - 2);  // Remove the trailing comma and space
                        }

                        // Combine assignee info for the callback
                        StringBuilder assigneeInfo = new StringBuilder();
                        for (String info : assigneeInfoList) {
                            assigneeInfo.append(info).append("\n");
                        }

                        // Notify via callback with task details and assignee data
                        callback.onTaskFetched(title, description, priority, status, startDate, endDate, assigneeInfo.toString(), assigneeNames.toString());
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
