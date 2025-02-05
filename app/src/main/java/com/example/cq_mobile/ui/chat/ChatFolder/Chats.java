package com.example.cq_mobile.ui.chat.ChatFolder;

import org.json.JSONException;
import org.json.JSONObject;

public class Chats {
    private int id;
    private String text;
    private String time;
    private String date;
    private int unread;

    // Constructor
    public Chats(int id, String text, String time, String date, int unread) {
        this.id = id;
        this.text = text;
        this.time = time;
        this.date = date;
        this.unread = unread;
    }

    // Constructor to parse JSON
    public Chats(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getInt("id");
        this.text = jsonObject.getString("text");
        this.time = jsonObject.getString("time");
        this.date = jsonObject.getString("date");
        this.unread = jsonObject.getInt("unread");
    }

    // Getters
    public int getId() { return id; }
    public String getText() { return text; }
    public String getTime() { return time; }
    public String getDate() { return date; }
    public int getUnread() { return unread; }

}
