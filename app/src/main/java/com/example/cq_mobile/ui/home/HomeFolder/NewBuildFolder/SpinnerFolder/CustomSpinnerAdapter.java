package com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.SpinnerFolder;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.cq_mobile.R;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final List<String> items;

    public CustomSpinnerAdapter(@NonNull Context context, int resource, List<String> objects ) {
        super(context, resource, objects);
        this.context = context;
        this.items = objects;
    }
    @NonNull
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_spinner_item, parent, false);

        TextView text = view.findViewById(R.id.spinner_text);
        String currentItem = items.get(position);
        text.setText(currentItem);

        // Determine the color based on the task status
        int color;
        switch (currentItem) {
            case "In progress":
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
                color = ContextCompat.getColor(context, R.color.textBtnRed); // Default fallback color
                break;
        }

        text.setTextColor(color);

        return view;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_spinner_selected_item, parent, false);

        TextView text = view.findViewById(R.id.selected_text);
        String selectedItem = items.get(position);
        text.setText(selectedItem);
        text.setEllipsize(TextUtils.TruncateAt.END);
        text.setSingleLine(true);
        text.setGravity(Gravity.CENTER);
        int color;
        switch (selectedItem) {
            case "In progress":
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
                color = ContextCompat.getColor(context, R.color.textBtnRed); // Default fallback color
                break;
        }

        // Apply the color to the text
        text.setTextColor(color);

        return view;
    }

}


