package com.example.pictureshare.pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PhotoItem {

    @SerializedName("records")
    private List<RecordsDTO> records;
    @SerializedName("total")
    private Integer total;
    @SerializedName("size")
    private Integer size;
    @SerializedName("current")
    private Integer current;

    public List<RecordsDTO> getRecords() {
        return records;
    }

    public void setRecords(List<RecordsDTO> records) {
        this.records = records;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public static class RecordsDTO implements Serializable {
        @SerializedName("id")
        private String id;
        @SerializedName("pUserId")
        private String pUserId;
        @SerializedName("imageCode")
        private String imageCode;
        @SerializedName("title")
        private String title;
        @SerializedName("content")
        private String content;
        @SerializedName("createTime")
        private String createTime;
        @SerializedName("imageUrlList")
        private List<String> imageUrlList;
        @SerializedName("likeId")
        private Object likeId;
        @SerializedName("likeNum")
        private Integer likeNum;
        @SerializedName("hasLike")
        private Boolean hasLike;
        @SerializedName("collectId")
        private Object collectId;
        @SerializedName("collectNum")
        private Integer collectNum;
        @SerializedName("hasCollect")
        private Boolean hasCollect;
        @SerializedName("hasFocus")
        private Boolean hasFocus;
        @SerializedName("username")
        private String username;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPUserId() {
            return pUserId;
        }

        public void setPUserId(String pUserId) {
            this.pUserId = pUserId;
        }

        public String getImageCode() {
            return imageCode;
        }

        public void setImageCode(String imageCode) {
            this.imageCode = imageCode;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public List<String> getImageUrlList() {
            return imageUrlList;
        }

        public void setImageUrlList(List<String> imageUrlList) {
            this.imageUrlList = imageUrlList;
        }

        public Object getLikeId() {
            return likeId;
        }

        public void setLikeId(Object likeId) {
            this.likeId = likeId;
        }

        public Integer getLikeNum() {
            return likeNum;
        }

        public void setLikeNum(Integer likeNum) {
            this.likeNum = likeNum;
        }

        public Boolean getHasLike() {
            return hasLike;
        }

        public void setHasLike(Boolean hasLike) {
            this.hasLike = hasLike;
        }

        public Object getCollectId() {
            return collectId;
        }

        public void setCollectId(Object collectId) {
            this.collectId = collectId;
        }

        public Integer getCollectNum() {
            return collectNum;
        }

        public void setCollectNum(Integer collectNum) {
            this.collectNum = collectNum;
        }

        public Boolean getHasCollect() {
            return hasCollect;
        }

        public void setHasCollect(Boolean hasCollect) {
            this.hasCollect = hasCollect;
        }

        public Boolean getHasFocus() {
            return hasFocus;
        }

        public void setHasFocus(Boolean hasFocus) {
            this.hasFocus = hasFocus;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
