package com.example.cq_mobile.ui.home.ViewListFolder;

public class CheckInData {
    private String time;
    private String date;

    public CheckInData() {
        // Default constructor required for Firebase
    }

    public CheckInData(String time, String date) {
        this.time = time;
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
