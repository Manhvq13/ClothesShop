package com.example.clothesshopproject.model.admin;

import com.google.gson.annotations.SerializedName;

public class ProductImageResponse {
    @SerializedName("id")
    private Long id;
    @SerializedName("url")
    private String url;
    @SerializedName("altText")
    private String altText;

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