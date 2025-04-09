package com.example.cq_mobile.ui.home.HomeFolder.API_todo;

import java.util.List;

public class TodoResponse {
    private List<Todo> data;

    public List<Todo> getData() {
        return data;
    }

    public void setData(List<Todo> data) {
        this.data = data;
    }
}
