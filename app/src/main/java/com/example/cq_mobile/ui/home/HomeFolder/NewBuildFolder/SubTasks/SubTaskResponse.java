package com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.SubTasks;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class SubTaskResponse {
    @SerializedName("data")
    private List<SubTask> data;

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    // Getter for 'data'
    public List<SubTask> getData() {
        return data != null ? data : new ArrayList<>(); // Ensure 'data' is never null
    }

    // Setter for 'data'
    public void setData(List<SubTask> data) {
        this.data = data;
    }

    // Getter for 'status'
    public String getStatus() {
        return status;
    }

    // Setter for 'status'
    public void setStatus(String status) {
        this.status = status;
    }

    // Getter for 'message'
    public String getMessage() {
        return message;
    }

    // Setter for 'message'
    public void setMessage(String message) {
        this.message = message;
    }
}
