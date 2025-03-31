package com.example.cq_mobile.ui.chat.ChatFolder;

import org.json.JSONException;
import org.json.JSONObject;


public class MessageItem {
    private String id;
    private String text;
    private String time;
    private String date;
    private int unread;
    private String name;

    // Constructor
    public MessageItem(String id, String text, String time, String date, int unread) {
        this.id = id;
        this.text = text;
        this.time = time;
        this.date = date;
        this.unread = unread;
    }

    // Constructor to parse JSON
    public MessageItem(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getString("id");
        this.text = jsonObject.getString("text");
        this.name = jsonObject.getString("name");
        this.time = jsonObject.getString("time");
        this.date = jsonObject.getString("date");
        this.unread = jsonObject.getInt("unread");
    }

    // Getters
    public String getId() { return id; }
    public String getText() { return text; }
    public String getName() { return name; }
    public String getTime() { return time; }
    public String getDate() { return date; }
    public int getUnread() { return unread; }
}
