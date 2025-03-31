package com.example.cq_mobile.ui.chat.InnerChatsFolder;

public class InnerChatMember {

    private int id;
    private String name;
    private String avatar_path;
    private String email;
    private int lastRead;

    // Constructor
    public InnerChatMember(int id, String name, String avatarPath, String email, int lastRead) {
        this.id = id;
        this.name = name;
        this.avatar_path = avatar_path;
        this.email = email;
        this.lastRead = lastRead;
    }

    // Getter for id
    public int getId() {
        return id;
    }

    // Other getters and setters if needed
    public String getName() {
        return name;
    }

    public String getAvatar_path() {
        return avatar_path;
    }

    public void setAvatar_path(String avatar_path) {
        this.avatar_path = avatar_path;
    }

    public String getEmail() {
        return email;
    }

    public int getLastRead() {
        return lastRead;
    }

    @Override
    public String toString() {
        return "ChatMember{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", avatarPath='" + avatar_path + '\'' +
                ", email='" + email + '\'' +
                ", lastRead=" + lastRead +
                '}';
    }
}

