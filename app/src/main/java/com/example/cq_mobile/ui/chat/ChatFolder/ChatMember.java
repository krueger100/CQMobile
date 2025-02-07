package com.example.cq_mobile.ui.chat.ChatFolder;

public class ChatMember {
    private int id;
    private String name;
    private String avatarPath;
    private String email;
    private int lastRead;

    // Constructor
    public ChatMember(int id, String name, String avatarPath, String email, int lastRead) {
        this.id = id;
        this.name = name;
        this.avatarPath = avatarPath;
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

    public String getAvatarPath() {
        return avatarPath;
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
                ", avatarPath='" + avatarPath + '\'' +
                ", email='" + email + '\'' +
                ", lastRead=" + lastRead +
                '}';
    }
}
