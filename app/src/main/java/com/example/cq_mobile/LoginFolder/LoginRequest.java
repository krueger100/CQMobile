package com.example.cq_mobile.LoginFolder;

public class LoginRequest {
    private String email;
    private String password;
   String  avatar;
    public LoginRequest(String email, String password,String avatar) {
        this.email = email;
        this.password = password;
        this.avatar = avatar;
    }

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
