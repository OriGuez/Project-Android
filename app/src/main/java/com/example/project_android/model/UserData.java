package com.example.project_android.model;

import android.graphics.Bitmap;
import com.google.gson.annotations.SerializedName;
import java.io.File;

public class UserData {
    @SerializedName("_id")
    private String id;
    private String username;
    private String password;
    @SerializedName("displayName")
    private String channelName;
    private Bitmap image;
    @SerializedName("profilePic")
    private String imageURI;

    private File imageFile;

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

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}