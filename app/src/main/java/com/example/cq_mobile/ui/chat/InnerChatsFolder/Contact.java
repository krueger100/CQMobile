package com.example.cq_mobile.ui.chat.InnerChatsFolder;

import java.util.List;
import java.util.Map;

public class Contact {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String mobile;
    private String address;
    private String avatar;
    private Map<String, List<InnerChatMessage>> messages;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Map<String, List<InnerChatMessage>> getMessages() {
        return messages;
    }

    public void setMessages(Map<String, List<InnerChatMessage>> messages) {
        this.messages = messages;
    }
}
