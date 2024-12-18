package com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.SubTasks;

import com.google.gson.annotations.SerializedName;

// Meta class to represent the 'meta' object in the JSON response
public class Meta {
    @SerializedName("current_page")
    private int currentPage;

    @SerializedName("last_page")
    private int lastPage;

    @SerializedName("total")
    private int total;

    // Getter for 'currentPage'
    public int getCurrentPage() {
        return currentPage;
    }

    // Setter for 'currentPage'
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    // Getter for 'lastPage'
    public int getLastPage() {
        return lastPage;
    }

    // Setter for 'lastPage'
    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    // Getter for 'total'
    public int getTotal() {
        return total;
    }

    // Setter for 'total'
    public void setTotal(int total) {
        this.total = total;
    }
}
