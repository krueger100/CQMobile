package com.example.cq_mobile.ui.ticket.CreateFolder;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.ticket.TicketAPICategoryFolder.TicketAPICategoryItems;

import java.util.List;

public class CategorySpinnerAdapter extends ArrayAdapter<TicketAPICategoryItems> {
    private final Context context;
    private final List<TicketAPICategoryItems> categories;

    public CategorySpinnerAdapter(Context context, List<TicketAPICategoryItems> categories) {
        super(context, android.R.layout.simple_spinner_item, categories);
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return createCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return createCustomView(position, convertView, parent);
    }

    private View createCustomView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item_layout, parent, false);
        }

        ImageView categoryImage = convertView.findViewById(R.id.category_image);
        TextView categoryName = convertView.findViewById(R.id.category_name);

        TicketAPICategoryItems category = categories.get(position);

        // Log details
        Log.d("CategorySpinnerAdapter", "Category ID: " + category.getId());
        Log.d("CategorySpinnerAdapter", "Category Name: " + category.getName());
        Log.d("CategorySpinnerAdapter", "Category Color: " + category.getColor());

        // Set the category name
        categoryName.setText(category.getName());

        // Apply the color as a tint to the image
        if (category.getColor() != null) {
            try {
                int color = Color.parseColor(category.getColor());
                categoryImage.setColorFilter(color, PorterDuff.Mode.SRC_IN); // Set image color
            } catch (IllegalArgumentException e) {
                Log.d("CategorySpinnerAdapter", "Invalid color format: " + category.getColor());
                categoryImage.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN); // Fallback color
            }
        } else {
            categoryImage.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN); // Fallback color
        }

        return convertView;
    }
}

