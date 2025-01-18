package com.example.cq_mobile.ui.home.UpdateJobsFolder.CheckListFolder;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Path;

import retrofit2.http.Body;
import retrofit2.http.PATCH;

public interface TaskChecklistApi {
    @PATCH("api/m/jobs/schedules/{jobScheduleId}/tasks/{taskId}/{checklistId}")
    Call<TaskChecklistResponse> updateTaskChecklist(
            @Path("jobScheduleId") String jobScheduleId,
            @Path("taskId") int taskId,
            @Path("checklistId") String checklistId,
            @Header("Authorization") String authorization,
            @Header("x-api-key") String apiKey,
            @Body TaskChecklistUpdateRequest updateRequest
    );
}
