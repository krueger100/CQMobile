package com.example.cq_mobile.Clock;

import com.example.cq_mobile.ui.home.HomeFolder.API_todo.Todo;

public class JobDetails {
    private int id;
    private int jobId;
    private String jobTitle;
    private String taskName;
    private String description;
    private String status;
    private String category;
    private String startDate;
    private String endDate;
    private boolean checked;
    private Todo.ClientDetails clientDetails;
    private Todo.Address address;
    private Todo.Coordinates coordinates;

    // Constructor, getters, and setters

    public JobDetails(int id, int jobId, String jobTitle, String taskName, String description, String status,
                      String category, String startDate, String endDate, boolean checked,
                      Todo.ClientDetails clientDetails, Todo.Address address, Todo.Coordinates coordinates) {
        this.id = id;
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.taskName = taskName;
        this.description = description;
        this.status = status;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.checked = checked;
        this.clientDetails = clientDetails;
        this.address = address;
        this.coordinates = coordinates;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Todo.ClientDetails getClientDetails() {
        return clientDetails;
    }

    public void setClientDetails(Todo.ClientDetails clientDetails) {
        this.clientDetails = clientDetails;
    }

    public Todo.Address getAddress() {
        return address;
    }

    public void setAddress(Todo.Address address) {
        this.address = address;
    }

    public Todo.Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Todo.Coordinates coordinates) {
        this.coordinates = coordinates;
    }
}
