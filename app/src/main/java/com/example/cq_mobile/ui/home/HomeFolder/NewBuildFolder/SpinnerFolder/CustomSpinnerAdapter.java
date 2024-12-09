package com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.SpinnerFolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cq_mobile.R;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final List<String> items;
    private final int iconResId; // Icon for each item

    public CustomSpinnerAdapter(@NonNull Context context, int resource, List<String> objects, int iconResId) {
        super(context, resource, objects);
        this.context = context;
        this.items = objects;
        this.iconResId = iconResId;
    }

    @NonNull
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_spinner_item, parent, false);

        TextView text = view.findViewById(R.id.spinner_text);
        text.setText(items.get(position));
        return view;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_spinner_selected_item, parent, false);

        TextView text = view.findViewById(R.id.selected_text);
        text.setText(items.get(position));


        return view;
    }
}
