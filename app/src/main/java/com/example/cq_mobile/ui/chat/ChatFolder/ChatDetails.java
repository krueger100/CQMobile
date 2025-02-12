package com.example.cq_mobile.ui.chat.ChatFolder;

import java.util.List;


public class ChatDetails {
    String chatName;
    String id;
    String name;
    String avatarPath;
    List<ChatMessage> messages;
    String online;
    int channel;
    int status;
    int channelStatus;
    List<ChatMember> members;
    int message_read;

    public ChatDetails(String chatName, String id, String name, String avatarPath, List<ChatMessage> messages, String online, int channel, int status, int channelStatus, List<ChatMember> members, int message_read) {
        this.chatName = chatName;
        this.id = id;
        this.name = name;
        this.avatarPath = avatarPath;
        this.messages = messages;
        this.online = online;
        this.channel = channel;
        this.status = status;
        this.channelStatus = channelStatus;
        this.members = members;
        this.message_read = message_read;
    }

    public int getMessage_read() {
        return message_read;
    }

    public void setMessage_read(int message_read) {
        this.message_read = message_read;
    }

    public String getChatName() {
        return chatName;
    }

    public String getName() {
        return name;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public List<ChatMember> getMembers() {
        return members;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "ChatDetails{" +
                "chatName='" + chatName + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", avatarPath='" + avatarPath + '\'' +
                ", messages=" + messages +
                ", online='" + online + '\'' +
                ", channel=" + channel +
                ", status=" + status +
                ", channelStatus=" + channelStatus +
                ", members=" + members +
                '}';
    }
}
