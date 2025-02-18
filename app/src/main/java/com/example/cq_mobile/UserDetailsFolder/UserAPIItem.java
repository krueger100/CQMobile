package com.example.cq_mobile.UserDetailsFolder;

public class UserAPIItem {
    private boolean success;  // To indicate if the request was successful or not
    private String error_code;  // For storing any error code in case of failure
    private String message;  // To store the message (if needed)
    private int id;  // The unique ID of the user
    private String sender;  // This could be the sender's information
    private String chat_name;  // User's full name or chat name
    private String avatar_path;  // Path to the user's avatar image
    private String members;  // If there are any related members to be added
    private String message_read;  // Indicates if the message has been read
    private String name;  // User's first and last name
    private int channel;  // Channel information (not clear from the data, so can be left as a placeholder or unused)

    // Constructor
    public UserAPIItem(int id, String name, String avatar_path, String message, boolean success) {
        this.id = id;
        this.name = name;
        this.avatar_path = avatar_path;
        this.message = message;
        this.success = success;
        // You can set default values for other fields if necessary
    }

    // Getters and setters for the fields
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
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

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public String getMessage_read() {
        return message_read;
    }

    public void setMessage_read(String message_read) {
        this.message_read = message_read;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }
}
