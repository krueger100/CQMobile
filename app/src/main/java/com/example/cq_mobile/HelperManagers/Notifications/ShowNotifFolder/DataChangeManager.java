package com.example.cq_mobile.HelperManagers.Notifications.ShowNotifFolder;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DataChangeManager {

    private static List<String> previousData = new ArrayList<>();

    public static void hasDataSizeChanged(List<String> newData) {
        if (newData.size() != previousData.size()) {
            Log.d("DataChangeManager", "Data size changed from " + previousData.size() + " to " + newData.size());
            previousData = new ArrayList<>(newData);
        }
    }

    public static List<String> getPreviousData() {
        return new ArrayList<>(previousData);
    }
}
