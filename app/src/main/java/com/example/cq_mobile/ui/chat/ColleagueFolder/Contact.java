package com.example.cq_mobile.ui.chat.ColleagueFolder;

public class Contact {
    private int id;
    private String name;
    private String avatar_path;
    private String initials;
    private String online;
    private String color;
    private String email;
    private String channel;

    public Contact(int id, String name, String avatar_path, String initials, String online, String color, String email, String channel) {
        this.id = id;
        this.name = name;
        this.avatar_path = avatar_path;
        this.initials = initials;
        this.online = online;
        this.color = color;
        this.email = email;
        this.channel = channel;
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

    public String getAvatar_path() {
        return avatar_path;
    }

    public void setAvatar_path(String avatar_path) {
        this.avatar_path = avatar_path;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
