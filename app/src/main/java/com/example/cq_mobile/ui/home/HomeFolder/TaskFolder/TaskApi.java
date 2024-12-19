package com.example.cq_mobile.ui.home.HomeFolder.TaskFolder;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

// Retrofit API interface
interface TaskApi {
    @GET("/api/m/jobs/schedules/{job_schedule_id}/tasks/{task_id}")
    Call<Task> getTask(
            @Path("job_schedule_id") int jobScheduleId,
            @Path("task_id") int taskId,
            @Header("Authorization") String token,
            @Header("x-api-key") String apiKey
    );
}
