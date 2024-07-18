package com.example.project_android.model;

import android.graphics.Bitmap;
import android.net.Uri;

public class UserData {
    private String _id;
    private String username;
    private String password;
    private String channelName;
    private Bitmap image;
    private Uri imageURI;

    public UserData(String username, String password, String channelName,Bitmap image) {
        this.username = username;
        this.password = password;
        this.channelName = channelName;
        this.image=image;
    }

    // Getters and setters for the fields
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Uri getImageURI() {
        return imageURI;
    }

    public void setImageURI(Uri imageURI) {
        this.imageURI = imageURI;
    }
}