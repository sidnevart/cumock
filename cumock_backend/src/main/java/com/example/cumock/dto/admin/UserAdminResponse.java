package com.example.cumock.dto.admin;

import com.example.cumock.model.Role;

public class UserAdminResponse {
    private Long id;
    private String email;
    private String username;
    private Role role;

    public UserAdminResponse(Long id, String email, String username, Role role) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.role = role;
    }

    public Long getId() {
        return id;
    }
    public String getEmail() {
        return email;
    }
    public String getUsername() {
        return username;
    }
    public Role getRole() {
        return role;
    }


}
