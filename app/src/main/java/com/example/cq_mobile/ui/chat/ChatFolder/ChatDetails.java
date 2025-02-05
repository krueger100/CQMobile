package com.example.cq_mobile.ui.chat.ChatFolder;

public class ChatDetails {
    String chatName;
    String id;
    String name;
    String avatarPath;
    String message;
//    String online;
//    int channel;
//    int status;
//    int channelStatus;
//    String members;

    public ChatDetails(String chatName, String id, String name, String avatarPath, String message){ //, String online, int channel, int status, int channelStatus, String members) {
        this.chatName = chatName;
        this.id = id;
        this.name = name;
        this.avatarPath = avatarPath;
        this.message = message;
//        this.online = online;
//        this.channel = channel;
//        this.status = status;
//        this.channelStatus = channelStatus;
//        this.members = members;
    }

    @Override
    public String toString() {
        return "ChatDetails{" +
                "chatName='" + chatName + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", avatarPath='" + avatarPath + '\'' +
           ", message='" + message  + '\''    +
//                ", online='" + online + '\'' +
//                ", channel=" + channel +
//                ", status=" + status +
//                ", channelStatus=" + channelStatus +
//                ", members='" + members + '\'' +
                '}';
    }
    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
//
//    public String getOnline() {
//        return online;
//    }
//
//    public void setOnline(String online) {
//        this.online = online;
//    }
//
//    public int getChannel() {
//        return channel;
//    }
//
//    public void setChannel(int channel) {
//        this.channel = channel;
//    }
//
//    public int getStatus() {
//        return status;
//    }
//
//    public void setStatus(int status) {
//        this.status = status;
//    }
//
//    public int getChannelStatus() {
//        return channelStatus;
//    }
//
//    public void setChannelStatus(int channelStatus) {
//        this.channelStatus = channelStatus;
//    }
//
//    public String getMembers() {
//        return members;
//    }
//
//    public void setMembers(String members) {
//        this.members = members;
//    }
}