package com.example.cq_mobile.ui.home.HomeFolder.TaskFolder;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.cq_mobile.R;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        // Get the jobId and taskId from the Intent
        Intent intent = getIntent();
        String jobId = intent.getStringExtra("jobId");
        int taskId = intent.getIntExtra("taskId", -1);
        String title_1 = intent.getStringExtra("title");
        String description_1 = intent.getStringExtra("description");

        Log.d("TaskActivity", "Received jobId: " + jobId + ", taskId: " + taskId);
        Log.d("TaskActivity", "Received title: " + title_1);
        Log.d("TaskActivity", "Received description: " + description_1);

        // Initialize UI components
        TextView titleTextView = findViewById(R.id.task_title);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        TextView priorityTextView = findViewById(R.id.priorityTextView);
        TextView statusTextView = findViewById(R.id.statusTextView);
        TextView startDateTextView = findViewById(R.id.startDateTextView);
        TextView endDateTextView = findViewById(R.id.endDateTextView);
        TextView assigneeTextView = findViewById(R.id.assigneeTextView);
        RecyclerView recycler_view = findViewById(R.id.recycler_view);
        LinearLayout assigneeAvatarLayout = findViewById(R.id.linearLayout2);  // The container for avatars

        // Initialize RecyclerView
        recycler_view.setLayoutManager(new LinearLayoutManager(this));

        // Dummy task list
        List<String> taskList = new ArrayList<>();
        taskList.add("Task 1");
        taskList.add("Task 2");
        taskList.add("Task 3");

        TaskAdapter taskAdapter = new TaskAdapter(this, taskList);
        recycler_view.setAdapter(taskAdapter);

        // Initialize TaskActivityManager
        TaskActivityManager manager = new TaskActivityManager();

        // Fetch task using TaskActivityManager
        manager.fetchTask(
                Integer.parseInt(jobId), // job_schedule_id
                taskId,                  // task_id
                "3864|1HFH2WjQnby84sFvd0I24UjXPmThdaYhOyV0W7lI", // token
                "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2",             // api_key
                new TaskActivityManager.TaskFetchCallback() {
                    @Override
                    public void onTaskFetched(String title, String description, String priority, String status, String startDate, String endDate, String assigneeInfo, String assigneeName) {
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
                        // priorityTextView.setText(priority);  // Uncomment if required
                        // statusTextView.setText(status);      // Uncomment if required
                        // startDateTextView.setText(startDate); // Uncomment if required
                        endDateTextView.setText(endDate);

                        // Log Assignee info (from assigneeInfo passed in the callback)
                        Log.d("TaskActivity", "Assignee Info: " + assigneeInfo);
                        assigneeTextView.setText(assigneeInfo);  // Display assignee info in the TextView

                        // Dynamically create and add ImageViews for each assignee
                        String[] assigneeData = assigneeInfo.split("\n"); // Split assigneeInfo into lines

                        for (int i = 0; i < assigneeData.length; i++) {
                            // Extract the avatar URL directly from the assignee data
                            String avatarUrl = getAvatarUrlFromAssigneeData(assigneeData[i]);

                            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                Log.d("TaskActivity", "Loading avatar URL: " + avatarUrl);

                                // Create a new ImageView for each assignee
                                ImageView imageView = new ImageView(TaskActivity.this);
                                imageView.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                ));

                                try {

                                     URL avatarUrlObject = new URL(avatarUrl);
                                    Glide.with(TaskActivity.this)
                                            .load(avatarUrlObject)
                                            .circleCrop()  // Ensures the image is displayed as a circle
                                            .into(imageView);

                                } catch (Exception e) {
                                    // Handle any potential error if the URL is invalid
                                    Log.e("TaskActivity", "Error loading avatar image: " + e.getMessage());
                                }

                                // Add the ImageView to your LinearLayout container
                                assigneeAvatarLayout.addView(imageView);
                            } else {
                                // If the URL is invalid, load a default image
                                Log.d("TaskActivity", "Loading default avatar image");

                                // Use a default image for invalid URLs
                                ImageView imageView = new ImageView(TaskActivity.this);
                                imageView.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                ));

                                Glide.with(TaskActivity.this)
                                        .load(R.drawable.round_account_circle_24)  // Default image
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
                        // Handle error (e.g., show a toast or alert)
                    }
                }
        );
    }

    private String getAvatarUrlFromAssigneeData(String assigneeData) {
        // Example: "Assignee ID: 1, Name: John Doe, Avatar: http://example.com/avatar.jpg"
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
}
