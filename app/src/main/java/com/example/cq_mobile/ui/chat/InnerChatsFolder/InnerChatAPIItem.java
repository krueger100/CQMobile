package com.example.cq_mobile.ui.chat.InnerChatsFolder;



import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

public class InnerChatAPIItem {
    private boolean success;
    private String error_code;
    private String message;
    private int id;
    private String sender;
    private String chat_name;
    private String avatar_path;
    private List<InnerChatMember> members;

    List<InnerChatMessage> messages;
    private String message_read;
    private String name;  //



    // Getter for members
    public List<InnerChatMember> getMembers() {
        return members;
    }

    // ✅ New setMembers method to handle JSON string input
    public void setMembers(String membersJson) {
        if (membersJson != null && !membersJson.isEmpty()) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<InnerChatMember>>() {}.getType();
            this.members = gson.fromJson(membersJson, listType);
        } else {
            this.members = null;
        }
    }


    public List<InnerChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<InnerChatMessage> messages) {
        this.messages = messages;
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

}



