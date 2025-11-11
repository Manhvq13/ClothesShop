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
    @SerializedName("shortDescription")
    private String shortDescription;
    @SerializedName("categories")
    private List<Category> categories;
    @SerializedName("price")
    private BigDecimal price;
    @SerializedName("salePrice")
    private BigDecimal salePrice;
    @SerializedName("isActive")
    private Boolean isActive;
    @SerializedName("images")
    private List<ProductImage> images;

    @SerializedName("quantityInStock")
    private Integer quantityInStock;
    @SerializedName("reservedStock")
    private Integer reservedStock;
    @SerializedName("availableStock")
    private Integer availableStock;


    public static class ProductImage {
        @SerializedName("url")
        private String url;
        @SerializedName("altText")
        private String altText;

        // THÊM: Constructor mặc định
        public ProductImage() {
        }

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

    public String getPrimaryCategoryName() {
        if (categories != null && !categories.isEmpty()) {
            return categories.get(0).getName();
        }
        return "N/A";
    }

    // --- Getters ---
    public List<Category> getCategories() { return categories; }
    public void setCategories(List<Category> categories) { this.categories = categories; }
    public Long getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getShortDescription() { return shortDescription; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getSalePrice() { return salePrice; }

    public Boolean getIsActive() { return isActive; }
    public Boolean isActive() { return isActive; }
    public List<ProductImage> getImages() { return images; }

    // THÊM: Getters cho tồn kho
    public Integer getQuantityInStock() { return quantityInStock; }
    public Integer getReservedStock() { return reservedStock; }
    public Integer getAvailableStock() { return availableStock; }


    // --- Setters ---
    public void setId(Long id) { this.id = id; }
    public void setSku(String sku) { this.sku = sku; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }

    public void setIsActive(Boolean active) { isActive = active; }
    public void setActive(Boolean active) { isActive = active; }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }

    // THÊM: Setters cho tồn kho
    public void setQuantityInStock(Integer quantityInStock) { this.quantityInStock = quantityInStock; }
    public void setReservedStock(Integer reservedStock) { this.reservedStock = reservedStock; }
    public void setAvailableStock(Integer availableStock) { this.availableStock = availableStock; }

    public String getImageUrl() {
        if (images != null && !images.isEmpty()) {
            return images.get(0).getUrl();
        }
        return null;
    }
}