package com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.SpinnerFolder;

import android.content.Context;
import android.text.TextUtils;
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

import java.util.List;

public class SubTaskSpinnerAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final List<String> items;
    private final String jobId;
    private final ImageView statusImageView;

    public SubTaskSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<String> objects, String jobId, ImageView statusImageView) {
        super(context, resource, objects);
        this.context = context;
        this.items = objects;
        this.jobId = jobId;
        this.statusImageView = statusImageView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_spinner_item, parent, false);

        TextView text = view.findViewById(R.id.spinner_text);
        if (items != null && position < items.size()) {
            String currentItem = items.get(position);
            text.setText(currentItem);

            int color = getColorForStatus(currentItem);

            // Set status image and job status if necessary
            if (statusImageView != null) {
                switch (currentItem) {
                    case "In Progress":
                        color = ContextCompat.getColor(context, R.color.cq_secondary_color);
                        break;
                    case "Pending":
                        color = ContextCompat.getColor(context, R.color.textBtnRed);
                        break;
                    case "Under inspection":
                        color = ContextCompat.getColor(context, R.color.color_inspection);
                        break;
                    case "Done":
                        color = ContextCompat.getColor(context, R.color.color_done);
                        break;
                    default:
                        color = ContextCompat.getColor(context, R.color.textBtnGrey); // Default fallback color
                        break;

            }
            }

            text.setTextColor(color);
        }

        return view;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_spinner_selected_item, parent, false);

        TextView text = view.findViewById(R.id.selected_text);
        if (items != null && position < items.size()) {
            String selectedItem = items.get(position);
            text.setText(selectedItem);
            text.setEllipsize(TextUtils.TruncateAt.END);
            text.setSingleLine(true);
            text.setGravity(Gravity.CENTER);
            int color = getColorForStatus(selectedItem);
            text.setTextColor(color);
        }

        return view;
    }

    /**
     * Utility method to get the color associated with a specific task status.
     */
    private int getColorForStatus(String status) {
        if (status == null) {
            return ContextCompat.getColor(context, R.color.textBtnGrey); // Default color for null status
        }

        switch (status) {
            case "In Progress":
                return ContextCompat.getColor(context, R.color.cq_secondary_color);
            case "Pending":
                return ContextCompat.getColor(context, R.color.textBtnRed);
            case "Under inspection":
                return ContextCompat.getColor(context, R.color.color_inspection);
            case "Done":
                return ContextCompat.getColor(context, R.color.color_done);
            default:
                return ContextCompat.getColor(context, R.color.textBtnGrey); // Default fallback color
        }
    }
}

