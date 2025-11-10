package com.example.clothesshopproject.model.admin;

import com.google.gson.annotations.SerializedName;

public class StockUpdateRequest {

    @SerializedName("productId")
    private Long productId;

    @SerializedName("quantity")
    private Integer quantity;

    public StockUpdateRequest() {
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }
}