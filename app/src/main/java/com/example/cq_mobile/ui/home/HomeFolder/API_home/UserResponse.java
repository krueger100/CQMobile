package com.example.cq_mobile.ui.home.HomeFolder.API_home;

import java.util.List;

public class UserResponse {
    private int page;
    private int per_page;
    private int total;
    private int total_pages;
    private List<User> data;

    // Getters and Setters
    public List<User> getData() {
        return data;
    }

    public void setData(List<User> data) {
        this.data = data;
    }

    public int getTotalPages() {
        return total_pages;
    }
}

