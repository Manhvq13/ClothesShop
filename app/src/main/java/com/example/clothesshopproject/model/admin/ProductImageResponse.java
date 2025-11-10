package com.example.clothesshopproject.model.admin;

import com.google.gson.annotations.SerializedName;

public class ProductImageResponse {
    @SerializedName("id")
    private Long id; // ID của bản ghi ảnh trên server (tùy chọn)
    @SerializedName("url")
    private String url;
    @SerializedName("altText")
    private String altText;

    // Constructors
    public ProductImageResponse(Long id, String url, String altText) {
        this.id = id;
        this.url = url;
        this.altText = altText;
    }

    public Long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getAltText() {
        return altText;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }
}