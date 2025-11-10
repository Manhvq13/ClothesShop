package com.example.clothesshopproject.model.admin;

import com.google.gson.annotations.SerializedName;

public class StockResponse {
    @SerializedName("productId")
    private Long productId;
    @SerializedName("productName")
    private String productName;
    @SerializedName("quantity")
    private int quantity;
    @SerializedName("reserved")
    private int reserved;
    @SerializedName("available")
    private int available;

    public StockResponse(Long productId, String productName, int quantity, int reserved, int available) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.reserved = reserved;
        this.available = available;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getReserved() {
        return reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }
}