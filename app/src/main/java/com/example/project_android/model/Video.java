package com.example.project_android.model;


import com.google.gson.annotations.SerializedName;
import android.graphics.Bitmap;
import androidx.room.Entity;
import java.io.File;
import java.util.Date;
import java.util.List;
@Entity
public class Video {
    private File videoFile;
    private File imageFile;
    private Bitmap thumbnailPicture;

    private String title;
    private String description;

    @SerializedName("userId")
    private String publisher;

    @SerializedName("_id")
    private String vidID;
    private String url;
    private String thumbnailUrl;

    private String thumbnail;

    @SerializedName("createdAt")
    private Date createdAt;

    @SerializedName("likes")
    private List<String> whoLikedList;
    private List<Comment> comments;
    private int views;
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

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public List<String> getWhoLikedList() {
        return whoLikedList;
    }

    public void setWhoLikedList(List<String> whoLikedList) {
        this.whoLikedList = whoLikedList;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
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

    // Nested Comment class
    public static class Comment {
        private String id;
        private String publisher;
        private String text;

        public Comment(String id, String publisher, String text) {
            this.id = id;
            this.publisher = publisher;
            this.text = text;
        }

        // Add getters and setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPublisher() {
            return publisher;
        }

        public void setPublisher(String publisher) {
            this.publisher = publisher;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
        @Override
        public String toString() {
            return publisher + ": " + text;
        }

    }
    public int RemoveComment(String commentID)
    {
        if (commentID==null || this.comments == null)
            return 0;
        for (Comment comment :this.comments)
        {
            if (comment.getId().equals(commentID))
                this.comments.remove(comment);
            return 1;
        }
        return 0;
    }

}