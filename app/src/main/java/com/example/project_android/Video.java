package com.example.project_android;

import java.util.List;

public class Video {
    private String title;
    private String description;
    private String publisher;
    private String vidID;
    private String url;
    private String thumbnailUrl;
    private String upload_date;
    private List<String> whoLikedList;
    private List<Comment> comments;

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

    public String getUpload_date() {
        return upload_date;
    }

    public void setUpload_date(String upload_date) {
        this.upload_date = upload_date;
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