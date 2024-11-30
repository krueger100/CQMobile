package com.example.cq_mobile.LoginFolder;

// LoginResponse model (for the response)
// LoginResponse.java
public class LoginResponse {
    private String message;
    private String token; // Example field
    private String userId; // Example field

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
