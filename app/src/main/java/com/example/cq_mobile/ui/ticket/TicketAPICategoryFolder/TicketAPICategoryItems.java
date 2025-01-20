package com.example.cq_mobile.ui.ticket.TicketAPICategoryFolder;

import java.util.List;

public class TicketAPICategoryItems {
    private int id;
    private String name;
    private String description;
    private String color;

    public TicketAPICategoryItems() {
        this.id = id;
        this.name = name;
        this.color = color;
    }
    // Getters and Setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
