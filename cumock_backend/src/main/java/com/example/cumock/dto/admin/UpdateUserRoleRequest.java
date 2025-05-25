package com.example.cumock.dto.admin;

import com.example.cumock.model.Role;

public class UpdateUserRoleRequest {
    private Role role;

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
