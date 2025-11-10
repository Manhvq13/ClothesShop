package com.example.clothesshopproject.model.admin;

import com.google.gson.annotations.SerializedName;

public class UserUpdateRequest {
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("phone")
    private String phone;
    @SerializedName("avatar")
    private String avatar;
    public UserUpdateRequest(String fullName, String phone ,String avatar) {
        this.fullName = fullName;
        this.phone = phone;
        this.avatar = avatar;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }
    public String getAvatar() { return avatar; }
}