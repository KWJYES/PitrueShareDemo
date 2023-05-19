package com.example.pictureshare.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UpLoadDao {

    @SerializedName("imageCode")
    private String imageCode;
    @SerializedName("imageUrlList")
    private List<String> imageUrlList;

    public String getImageCode() {
        return imageCode;
    }

    public void setImageCode(String imageCode) {
        this.imageCode = imageCode;
    }

    public List<String> getImageUrlList() {
        return imageUrlList;
    }

    public void setImageUrlList(List<String> imageUrlList) {
        this.imageUrlList = imageUrlList;
    }
}
