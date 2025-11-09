package com.example.clothesshopproject.model;

public class User {
    private Long id;
    private String avatar;
    private String email;
    private String fullName;
    private String phone;
    private Boolean status;
    private Role role;

    public User() {
    }

    public User(Long id, String avatar, String email, String fullName, String phone, Boolean status, Role role) {
        this.id = id;
        this.avatar = avatar;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.status = status;
        this.role = role;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}