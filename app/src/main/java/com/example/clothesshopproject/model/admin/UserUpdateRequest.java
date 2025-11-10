package com.example.clothesshopproject.model.admin;

import com.google.gson.annotations.SerializedName;

public class UserUpdateRequest {
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("phone")
    private String phone;

    public UserUpdateRequest(String fullName, String phone) {
        this.fullName = fullName;
        this.phone = phone;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }
}