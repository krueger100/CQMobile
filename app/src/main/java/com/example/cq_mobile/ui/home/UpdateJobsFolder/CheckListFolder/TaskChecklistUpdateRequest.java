package com.example.cq_mobile.ui.home.UpdateJobsFolder.CheckListFolder;

public class TaskChecklistUpdateRequest {
    private int checked;

    public TaskChecklistUpdateRequest(int checked) {
        this.checked = checked;
    }

    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }
}
