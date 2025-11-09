package com.example.clothesshopproject.model.admin;

public class UserUpdateRoleRequest {
    private String roleName;

    public UserUpdateRoleRequest(String roleName) {
        this.roleName = roleName;
    }

    public UserUpdateRoleRequest() {}

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}