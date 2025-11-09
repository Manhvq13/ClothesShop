package com.example.clothesshopproject.model.admin;


import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.util.List;

public class AdminProduct {
    @SerializedName("id")
    private Long id;
    @SerializedName("sku")
    private String sku;
    @SerializedName("name")
    private String name;
    @SerializedName("price")
    private BigDecimal price;
    @SerializedName("salePrice")
    private BigDecimal salePrice;
    @SerializedName("isActive")
    private Boolean isActive;
    @SerializedName("images")
    private List<ProductImage> images;

    public static class ProductImage {
        @SerializedName("url")
        private String url;
        public String getUrl() {
            return url;
        }
    }

    // --- Getters ---
    public Long getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public Boolean isActive() {
        return isActive;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }

    // Trả về URL của ảnh đầu tiên để hiển thị thumbnail
    public String getImageUrl() {
        if (images != null && !images.isEmpty()) {
            return images.get(0).getUrl();
        }
        return null;
    }
}