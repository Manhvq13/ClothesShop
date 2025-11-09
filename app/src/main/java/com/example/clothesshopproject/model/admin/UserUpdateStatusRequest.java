package com.example.clothesshopproject.model.admin;

public class UserUpdateStatusRequest {
    private Boolean isActive;

    public UserUpdateStatusRequest(Boolean isActive) {
        this.isActive = isActive;
    }

    public UserUpdateStatusRequest() {}

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}