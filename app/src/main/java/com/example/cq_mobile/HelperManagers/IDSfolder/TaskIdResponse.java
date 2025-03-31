package com.example.cq_mobile.HelperManagers.IDSfolder;

import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.TaskMainFolder.Taskmain;

import java.util.List;

public class TaskIdResponse {
    private List<Taskmain> data; // Changed from Taskmain to List<Taskmain>

    public List<Taskmain> getData() {
        return data;
    }

    public void setData(List<Taskmain> data) {
        this.data = data;
    }
}
