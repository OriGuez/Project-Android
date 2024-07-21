package com.example.project_android.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Comment {
    @SerializedName("_id")
    private String id;
    private String content;
    private String userId;
    private String videoId;
    @SerializedName("createdAt")
    private Date createdAt;

    public Comment(String id, String content, String userId, String videoId) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.videoId = videoId;
    }
    public Comment(String content, String userId, String videoId) {
        this.content = content;
        this.userId = userId;
        this.videoId = videoId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
