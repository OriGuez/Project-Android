package com.example.project_android.model;


import com.google.gson.annotations.SerializedName;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.File;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Entity
public class Video {
    @Ignore
    private File videoFile;
    @Ignore
    private File imageFile;
    @Ignore
    private Bitmap thumbnailPicture;
    @PrimaryKey
    @NonNull
    @SerializedName("_id")
    private String vidID;
    private String title;
    private String description;

    @SerializedName("userId")
    private String publisher;

    private String url;
//    private String thumbnailUrl;

    private String thumbnail;

    private Date createdAt;

    @Ignore
    @SerializedName("likes")
    private List<String> whoLikedList;
    private int views;
    @Ignore
    private Bitmap encodedImage;

    public Video(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Video(String title, String description, File thumbnail) {
        this.title = title;
        this.description = description;
        this.imageFile = thumbnail;
    }

    // Add getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getVidID() {
        return vidID;
    }

    public void setVidID(String vidID) {
        this.vidID = vidID;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public List<String> getWhoLikedList() {
        return whoLikedList;
    }

    public void setWhoLikedList(List<String> whoLikedList) {
        this.whoLikedList = whoLikedList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Bitmap getThumbnailPicture() {
        return thumbnailPicture;
    }

    public void setThumbnailPicture(Bitmap thumbnailPicture) {
        this.thumbnailPicture = thumbnailPicture;
    }

    public int getViews() {
        return views;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }


    public void setViews(int views) {
        this.views = views;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public File getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(File videoFile) {
        this.videoFile = videoFile;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public Bitmap getEncodedImage() {
        return encodedImage;
    }

    public void setEncodedImage(Bitmap encodedImage) {
        this.encodedImage = encodedImage;
    }
}