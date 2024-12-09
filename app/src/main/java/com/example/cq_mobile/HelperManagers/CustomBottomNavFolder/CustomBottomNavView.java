package com.example.cq_mobile.HelperManagers.CustomBottomNavFolder;

// CustomBottomNavView.java
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ImageView;

import com.example.cq_mobile.R;

public class CustomBottomNavView extends LinearLayout {

    private ImageView nav_home;
    private ImageView nav_map;
    private ImageView nav_todo;

    private OnNavigationItemSelectedListener listener;

    public interface OnNavigationItemSelectedListener {
        void onHomeSelected();
        void onSearchSelected();
        void onProfileSelected();
    }

    public CustomBottomNavView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.custom_bottom_nav_view, this, true);

        nav_home = findViewById(R.id.nav_home);
        nav_map = findViewById(R.id.nav_map);
        nav_todo = findViewById(R.id.nav_todo);

        setUpListeners();
    }

    private void setUpListeners() {
        nav_home.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHomeSelected();
            }
        });
        nav_map.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSearchSelected();
            }
        });
        nav_todo.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProfileSelected();
            }
        });
    }

    public void setOnNavigationItemSelectedListener(OnNavigationItemSelectedListener listener) {
        this.listener = listener;
    }
}
