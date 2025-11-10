package com.example.clothesshopproject.model.admin;

import com.google.gson.annotations.SerializedName;

public class StockUpdateRequest {
    @SerializedName("quantity")
    private int quantity;

    public StockUpdateRequest(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}