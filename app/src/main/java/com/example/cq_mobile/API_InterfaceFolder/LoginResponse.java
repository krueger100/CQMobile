package com.example.cq_mobile.API_InterfaceFolder;

// LoginResponse model (for the response)
// LoginResponse.java
public class LoginResponse {
    private String token; // Example field, change based on your actual response
    private String userId; // Example field

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
