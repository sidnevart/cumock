package com.example.cumock.dto.auth;


public class UserResponse {
    private Long id;
    private String email;
    private String username;

    public UserResponse(Long id, String email, String username) {
        this.id = id;
        this.email = email;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }


}


