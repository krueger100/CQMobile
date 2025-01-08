package com.example.cq_mobile.NotificationData;

import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ShowNotificationsManager {

    private Context context;
    private RecyclerView recyclerView;
    private TeamAdapter teamAdapter;
    private static final String TAG = "ShowNotificationsManager";

    public ShowNotificationsManager(Context context, RecyclerView recyclerView,
                                    List<String> teamNames, List<String> teamAvatars) {
        if (recyclerView == null) {
            throw new IllegalArgumentException("RecyclerView must not be null");
        }
        this.context = context;
        this.recyclerView = recyclerView;

        // Pass data to TeamAdapter
        this.teamAdapter = new TeamAdapter(teamNames, context, teamAvatars);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        this.recyclerView.setAdapter(teamAdapter);
    }
}
