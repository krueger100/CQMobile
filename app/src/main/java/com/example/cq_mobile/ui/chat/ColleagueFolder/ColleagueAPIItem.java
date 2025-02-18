package com.example.cq_mobile.ui.chat.ColleagueFolder;

import com.example.cq_mobile.ui.chat.ChatFolder.ChatMember;

import java.util.List;

public class ColleagueAPIItem {
    private boolean success;
    private String error_code;
    private String message;
    private int id;
    private String sender;
    private String chat_name;
    private String avatar_path;
    private List<ChatMember> members;  //
    private String message_read;
    private String name;
    private List<Contact> contacts;  //
    private String avatar_path_new;
    String channel;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getAvatar_path_new() {
        return avatar_path_new;
    }

    public void setAvatar_path_new(String avatar_path_new) {
        this.avatar_path_new = avatar_path_new;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage_read() {
        return message_read;
    }

    public void setMessage_read(String message_read) {
        this.message_read = message_read;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChat_name() {
        return chat_name;
    }

    public void setChat_name(String chat_name) {
        this.chat_name = chat_name;
    }

    public String getAvatar_path() {
        return avatar_path;
    }

    public void setAvatar_path(String avatar_path) {
        this.avatar_path = avatar_path;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ChatMember> getMembers() {
        return members;
    }

    public void setMembers(List<ChatMember> members) {
        this.members = members;
    }
}
