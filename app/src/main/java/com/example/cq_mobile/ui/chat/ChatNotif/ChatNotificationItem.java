package com.example.cq_mobile.ui.chat.ChatNotif;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChatNotificationItem {
    private String sender;
    private String text;
    private String avatar;
    private String initials;
    private int channel;
    @SerializedName("channelname")
    private String channelName;
    private String date;
    private String time;
    private String files;

    // Constructor
    public ChatNotificationItem(String sender, String text, String avatar, String initials, int channel,
                                String channelName, String date, String time, String files) {
        this.sender = sender;
        this.text = text;
        this.avatar = avatar;
        this.initials = initials;
        this.channel = channel;
        this.channelName = channelName;
        this.date = date;
        this.time = time;
        this.files = files;
    }

    // Getters
    public String getSender() { return sender; }
    public String getText() { return text; }
    public String getAvatar() { return avatar; }
    public String getInitials() { return initials; }
    public int getChannel() { return channel; }
    public String getChannelName() { return channelName; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getFiles() { return files; }
}
