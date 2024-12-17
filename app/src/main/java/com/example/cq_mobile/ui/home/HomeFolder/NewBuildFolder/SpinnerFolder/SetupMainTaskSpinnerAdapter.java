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
import com.example.cq_mobile.ui.home.UpdateJobsFolder.UpdateJobApiManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetupMainTaskSpinnerAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final List<String> items;
    private final String jobId;
    private final String status_main;
    private final ImageView statusImageView;

    public SetupMainTaskSpinnerAdapter(@NonNull Context context, int resource, List<String> objects, String jobId, ImageView statusImageView, String status_main) {
        super(context, resource, objects);
        this.context = context;

        // Ensure status_main is always the first item if it's not already in the list
        this.items = new ArrayList<>();
        Set<String> lowerCaseSet = new HashSet<>();

        // Add status_main first if it is not already in the list (case-insensitive check)
        if (status_main != null && !status_main.isEmpty() && !lowerCaseSet.contains(status_main.toLowerCase())) {
            this.items.add(status_main); // Add status_main as the first item
            lowerCaseSet.add(status_main.toLowerCase());
        }


        for (String item : objects) {
            if (!lowerCaseSet.contains(item.toLowerCase())) {
                lowerCaseSet.add(item.toLowerCase());
                this.items.add(item);
            }
        }

        this.jobId = jobId;
        this.statusImageView = statusImageView;
        this.status_main = status_main;

        Log.d("SetupMainTaskSpinnerAdapter", "Original objects: " + objects);
        Log.d("SetupMainTaskSpinnerAdapter", "Final items: " + this.items);
    }




    @NonNull
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_spinner_item, parent, false);

        TextView text = view.findViewById(R.id.spinner_text);
        String currentItem = items.get(position);
        text.setText(capitalizeFirstLetter(currentItem));
        int color = getColorAndUpdateStatusDropDown(currentItem);
        text.setTextColor(color);
        return view;
    }

    private int getColorAndUpdateStatusDropDown(String currentItem) {
        int color;
        switch (currentItem) {
            case "Todo":
                color = ContextCompat.getColor(context, R.color.todoColor);
                break;
            case "Skipped":
                color = ContextCompat.getColor(context, R.color.skippedColor);
                break;
            case "Done":
                color = ContextCompat.getColor(context, R.color.color_done);
                break;
            default:
                color = ContextCompat.getColor(context, R.color.textBtnGrey);
                break;
        }
        return color;
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_spinner_selected_item, parent, false);

        TextView text = view.findViewById(R.id.selected_text);
        String currentItem = items.get(position);

        // Capitalize and set text for the selected item
        text.setEllipsize(TextUtils.TruncateAt.END);
        text.setSingleLine(true);
        text.setGravity(Gravity.CENTER);

        // Set the text and text color for the selected item
        int color = getColorAndUpdateStatus(currentItem);
        text.setText(capitalizeFirstLetter(currentItem));
        text.setTextColor(color);

        return view;
    }


    private int getColorAndUpdateStatus(String status) {
        if (status == null) {
            return ContextCompat.getColor(context, R.color.textBtnGrey); // Default color for null status
        }

        // Convert status to lowercase for comparison
        String lowercaseStatus = status.toLowerCase();

        int color;
        switch (lowercaseStatus) {
            case "todo":
                color = ContextCompat.getColor(context, R.color.todoColor);
                if (statusImageView != null) {
                    UpdateJobApiManager.updateJobStatus(jobId, "373", "todo");
                    statusImageView.setImageResource(R.drawable.button_orange);
                }
                break;
            case "skipped":
                color = ContextCompat.getColor(context, R.color.skippedColor);
                if (statusImageView != null) {
                    UpdateJobApiManager.updateJobStatus(jobId, "373", "skipped");
                    statusImageView.setImageResource(R.drawable.button_blue);
                }
                break;
            case "done":
                color = ContextCompat.getColor(context, R.color.color_done);
                if (statusImageView != null) {
                    UpdateJobApiManager.updateJobStatus(jobId, "373", "done");
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



    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}
