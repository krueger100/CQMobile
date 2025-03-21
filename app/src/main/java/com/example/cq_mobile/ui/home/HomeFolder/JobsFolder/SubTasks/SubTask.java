package com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.SubTasks;


public class SubTask {
    private int id;
    private String title;
    private String description;
    private String priority;
    private String status;
    private String start_date;
    private String end_date;
    private boolean is_checked;

    // Getters and Setters for each field
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartDate() {
        return start_date;
    }

    public void setStartDate(String start_date) {
        this.start_date = start_date;
    }

    public String getEndDate() {
        return end_date;
    }

    public void setEndDate(String end_date) {
        this.end_date = end_date;
    }

    public boolean isChecked() {
        return is_checked;
    }

    @Override
    public String toString() {
        return "SubTask{id=" + id + ", title='" + title + "', status='" + status + "'}";
    }

}
/*
    // Checklist inner class
    public class Checklist {
        private int id;
        private String name;
        private boolean checked;

        // Getters and Setters for Checklist fields
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }
    }
 */