package com.example.clothesshopproject.model.admin;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("id")
    private Long id;
    @SerializedName("email")
    private String email;
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("phone") // Đã thêm
    private String phone;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("status")
    private Boolean status;
    @SerializedName("role")
    private RoleResponse role;

    // Constructors
    public UserResponse(Long id, String email, String fullName, String phone, String avatar, Boolean status, RoleResponse role) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone; // Khởi tạo
        this.avatar = avatar;
        this.status = status;
        this.role = role;
    }

    // Getters and Setters (Đã rút gọn)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; } // Getter mới
    public void setPhone(String phone) { this.phone = phone; } // Setter mới
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }
    public RoleResponse getRole() { return role; }
    public void setRole(RoleResponse role) { this.role = role; }
}