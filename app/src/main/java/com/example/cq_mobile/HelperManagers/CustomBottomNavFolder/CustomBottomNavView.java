package com.example.cq_mobile.HelperManagers.CustomBottomNavFolder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ImageView;

import com.example.cq_mobile.R;

public class CustomBottomNavView extends LinearLayout {

    private ImageView nav_home;   //
    private ImageView nav_map;
    private ImageView nav_todo;
    private ImageView nav_ticket;
    private ImageView nav_chat;
    private ImageView nav_more;

    private OnNavigationItemSelectedListener listener;

    public interface OnNavigationItemSelectedListener {
        void onHomeSelected();
        void onMapSelected();
        void onMyJobsSelected();
        void onTicketSelected();
        void onChatSelected();
        void onMoreSelected();
    }

    public CustomBottomNavView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.custom_bottom_nav_view, this, true);

        nav_home = findViewById(R.id.nav_home);
        nav_map = findViewById(R.id.nav_map);
        nav_todo = findViewById(R.id.nav_todo);
        nav_ticket = findViewById(R.id.nav_ticket);
        nav_chat = findViewById(R.id.nav_chat);
        nav_more = findViewById(R.id.nav_more);

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
                listener.onMapSelected();
            }
        });
        nav_todo.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMyJobsSelected();
            }
        });
        nav_ticket.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTicketSelected();
            }
        });
        nav_chat.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChatSelected();
            }
        });
        nav_more.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMoreSelected();
            }
        });
    }

    public void setOnNavigationItemSelectedListener(OnNavigationItemSelectedListener listener) {
        this.listener = listener;
    }
}
