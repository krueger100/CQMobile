package com.example.cq_mobile.ui.home.HomeFolder.TaskFolder;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.cq_mobile.R;

public class TaskAvatarManager {

    public static void loadAvatars(Context context, String assigneeInfo, LinearLayout assigneeAvatarLayout) {
        String[] assigneeData = assigneeInfo.split("\n"); // Split assigneeInfo into lines

        for (int i = 0; i < assigneeData.length; i++) {
            // Extract the avatar URL directly from the assignee data
            String avatarUrl = getAvatarUrlFromAssigneeData(assigneeData[i]);
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                Log.d("TaskAvatarManager", "Loading avatar URL: " + avatarUrl);
                loadImage(context, avatarUrl, assigneeAvatarLayout);
            } else {
                // If the URL is invalid, load a default image
                Log.d("TaskAvatarManager", "Loading default avatar image");
                loadImage(context, null, assigneeAvatarLayout); // Use default image
            }
        }
    }

    private static String getAvatarUrlFromAssigneeData(String assigneeData) {
        // Example: "Assignee ID: 1, Name: John Doe, Avatar: http://example.com/avatar.jpg"
        String avatarUrlPrefix = "Avatar: ";
        if (assigneeData.contains(avatarUrlPrefix)) {
            int startIndex = assigneeData.indexOf(avatarUrlPrefix) + avatarUrlPrefix.length();
            int endIndex = assigneeData.indexOf("\n", startIndex);
            if (endIndex == -1) {
                endIndex = assigneeData.length();
            }
            String avatarUrl = assigneeData.substring(startIndex, endIndex).trim();
            Log.d("TaskAvatarManager", "Extracted Avatar URL: " + avatarUrl);
            return avatarUrl;
        }
        return null;
    }

    private static void loadImage(Context context, String avatarUrl, LinearLayout assigneeAvatarLayout) {
        ImageView imageView = new ImageView(context);
        int width = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 60, context.getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 60, context.getResources().getDisplayMetrics());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        layoutParams.setMargins(2, 0, 2, 0);
        imageView.setLayoutParams(layoutParams);

        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(context)
                    .load(avatarUrl)
                    .circleCrop()
                    .placeholder(R.drawable.baseline_circle)
                    .error(R.drawable.emptyglide)
                    .into(imageView); // Target ImageView
        } else {
            Glide.with(context)
                    .load(R.drawable.emptyglide)  // Default image
                    .circleCrop()
                    .into(imageView);
        }

        // Add the ImageView to your LinearLayout container
        assigneeAvatarLayout.addView(imageView);
    }
}
