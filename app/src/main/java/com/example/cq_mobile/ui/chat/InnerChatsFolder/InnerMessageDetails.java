package com.example.cq_mobile.ui.chat.InnerChatsFolder;

public class InnerMessageDetails {
    private String date;
    private String avatar;
    private String name;
    private String sender;
    private String text;
    private String time;


    public InnerMessageDetails(String date, String avatar, String name, String sender, String text, String time) {
        this.date = date;
        this.avatar = avatar;
        this.name = name;
        this.sender = sender;
        this.text = text;
        this.time = time;
    }

    @Override
    public String toString() {
        return "Date: " + date + ", Name: " + name + ", Sender: " + sender + ", Text: " + text + ", Time: " + time;
    }

    // Getters
    public String getDate() { return date; }
    public String getAvatar() { return avatar; }
    public String getName() { return name; }
    public String getSender() { return sender; }
    public String getText() { return text; }
    public String getTime() { return time; }
}
