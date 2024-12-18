package com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.SpinnerFolder;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.UpdateJobsFolder.UpdateSubTaskApiManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SubTaskSpinnerAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final List<String> items;
    private final String jobId;
    private final String initialStatus;
    private final ImageView statusImageView;
    private String taskId;  // Now taskId is a dynamic field

    public SubTaskSpinnerAdapter(@NonNull Context context, int resource, List<String> objects, String jobId,
                                 ImageView statusImageView, String initialStatus, String taskId) {
        super(context, resource, objects);
        this.context = context;

        // Ensure initialStatus is the first item if not already in the list
        this.items = new ArrayList<>();
        Set<String> lowerCaseSet = new HashSet<>();

        if (initialStatus != null && !initialStatus.isEmpty() && !lowerCaseSet.contains(initialStatus.toLowerCase())) {
            this.items.add(initialStatus);
            lowerCaseSet.add(initialStatus.toLowerCase());
        }

        for (String item : objects) {
            if (!lowerCaseSet.contains(item.toLowerCase())) {
                lowerCaseSet.add(item.toLowerCase());
                this.items.add(item);
            }
        }

        this.jobId = jobId;
        this.statusImageView = statusImageView;
        this.initialStatus = initialStatus;
        this.taskId = taskId;  // Assign taskId to the instance variable

        Log.d("SubTaskSpinnerAdapter", "Original objects: " + objects);
        Log.d("SubTaskSpinnerAdapter", "Final items: " + this.items);
    }

    @NonNull
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_spinner_item, parent, false);

        TextView text = view.findViewById(R.id.spinner_text);
        String currentItem = items.get(position);
        text.setText(capitalizeFirstLetter(currentItem));

        int color = getColorForStatus(currentItem);
        text.setTextColor(color);

        return view;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_spinner_selected_item, parent, false);

        TextView text = view.findViewById(R.id.selected_text);
        String currentItem = items.get(position);

        text.setEllipsize(TextUtils.TruncateAt.END);
        text.setSingleLine(true);
        text.setGravity(Gravity.CENTER);

        int color = getColorAndUpdateStatus(currentItem);
        text.setText(capitalizeFirstLetter(currentItem));
        text.setTextColor(color);

        return view;
    }

    private int getColorAndUpdateStatus(String status) {
        if (status == null) {
            return ContextCompat.getColor(context, R.color.textBtnGrey); // Default color for null status
        }

        String lowercaseStatus = status.toLowerCase();

        int color;
        switch (lowercaseStatus) {
            case "in progress":
                color = ContextCompat.getColor(context, R.color.cq_secondary_color);
                if (statusImageView != null) {
                    // Use dynamic taskId here for the update call
                    UpdateSubTaskApiManager.updateSubTaskApiManager(jobId, taskId, "in_progress");
                    statusImageView.setImageResource(R.drawable.button_orange);
                }
                break;
            case "pending":
                color = ContextCompat.getColor(context, R.color.textBtnRed);
                if (statusImageView != null) {
                    // Use dynamic taskId here for the update call
                    UpdateSubTaskApiManager.updateSubTaskApiManager(jobId, taskId, "pending");
                    statusImageView.setImageResource(R.drawable.button_red);
                }
                break;
            case "under inspection":
                color = ContextCompat.getColor(context, R.color.color_inspection);
                if (statusImageView != null) {
                    // Use dynamic taskId here for the update call
                    UpdateSubTaskApiManager.updateSubTaskApiManager(jobId, taskId, "under_inspection");
                    statusImageView.setImageResource(R.drawable.button_blue);
                }
                break;
            case "done":
                color = ContextCompat.getColor(context, R.color.color_done);
                if (statusImageView != null) {
                    // Use dynamic taskId here for the update call
                    UpdateSubTaskApiManager.updateSubTaskApiManager(jobId, taskId, "done");
                    statusImageView.setImageResource(R.drawable.button_green);
                }
                break;
            default:
                color = ContextCompat.getColor(context, R.color.textBtnGrey);
                if (statusImageView != null) {
                    statusImageView.setImageResource(R.drawable.button_grey);
                }
                break;
        }
        return color;
    }

    private int getColorForStatus(String status) {
        if (status == null) {
            return ContextCompat.getColor(context, R.color.textBtnGrey); // Default color for null status
        }

        switch (status.toLowerCase()) {
            case "in progress":
                return ContextCompat.getColor(context, R.color.cq_secondary_color);
            case "pending":
                return ContextCompat.getColor(context, R.color.textBtnRed);
            case "under inspection":
                return ContextCompat.getColor(context, R.color.color_inspection);
            case "done":
                return ContextCompat.getColor(context, R.color.color_done);
            default:
                return ContextCompat.getColor(context, R.color.textBtnGrey); // Default fallback color
        }
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}
