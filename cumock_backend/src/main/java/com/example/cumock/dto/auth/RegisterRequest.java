package com.example.cumock.dto.auth;

public class RegisterRequest {
    private String email;
    private String password;
    private String username;


    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String newEmail) {
        this.email = newEmail;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String newPassword) {
        this.password = newPassword;
    }
}
