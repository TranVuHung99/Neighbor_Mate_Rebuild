package com.example.neighbormaterebuild.model;

import com.google.gson.annotations.SerializedName;

public class AvatarUrl {
    @SerializedName("image_index")
    private int image_index;

    @SerializedName("path")
    private String path;

    public AvatarUrl(int image_index, String path) {
        this.image_index = image_index;
        this.path = path;
    }

    public AvatarUrl() {

    }

    @Override
    public String toString() {
        return "AvatarUrl{" +
                "image_index=" + image_index +
                ", path='" + path + '\'' +
                '}';
    }

    public int getImage_index() {
        return image_index;
    }

    public void setImage_index(int image_index) {
        this.image_index = image_index;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}