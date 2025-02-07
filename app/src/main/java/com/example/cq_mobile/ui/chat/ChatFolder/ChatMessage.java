package com.example.cq_mobile.ui.chat.ChatFolder;

public class ChatMessage {
    int id;
    String text;
    String time;
    String date;
    int unread;

    public ChatMessage(int id, String text, String time, String date, int unread) {
        this.id = id;
        this.text = text;
        this.time = time;
        this.date = date;
        this.unread = unread;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", time='" + time + '\'' +
                ", date='" + date + '\'' +
                ", unread=" + unread +
                '}';
    }
}
