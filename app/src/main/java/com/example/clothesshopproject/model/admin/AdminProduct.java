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
    @SerializedName("description")
    private String description;
    @SerializedName("short_description")
    private String shortDescription;

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
        @SerializedName("alt_text") // Ánh xạ cột alt_text
        private String altText;

        // Constructor mới
        public ProductImage(String url, String altText) {
            this.url = url;
            this.altText = altText;
        }

        public String getUrl() {
            return url;
        }

        public String getAltText() {
            return altText;
        }
    }

    // --- Getters ---
    public Long getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getShortDescription() { return shortDescription; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getSalePrice() { return salePrice; }
    public Boolean isActive() { return isActive; }
    public List<ProductImage> getImages() { return images; }


    // --- Setters ---
    public void setId(Long id) { this.id = id; }
    public void setSku(String sku) { this.sku = sku; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }
    public void setActive(Boolean active) { isActive = active; }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }

    public String getImageUrl() {
        if (images != null && !images.isEmpty()) {
            return images.get(0).getUrl();
        }
        return null;
    }
}