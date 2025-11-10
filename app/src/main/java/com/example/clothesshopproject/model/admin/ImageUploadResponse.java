package com.example.clothesshopproject.model.admin;

import com.google.gson.annotations.SerializedName;

public class ImageUploadResponse {
    @SerializedName("fileName")
    private String fileName;
    @SerializedName("url")
    private String url;

    public ImageUploadResponse(String fileName, String url) {
        this.fileName = fileName;
        this.url = url;
    }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}