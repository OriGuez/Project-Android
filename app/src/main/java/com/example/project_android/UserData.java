package com.example.project_android;
import android.graphics.Bitmap;
import android.net.Uri;
import java.util.ArrayList;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;

public class UserData implements Parcelable {
    private String username;
    private String password;
    private String channelName;
    private List<Bitmap> images;
    private List<Uri> videos;

    public UserData(String username, String password, String channelName) {
        this.username = username;
        this.password = password;
        this.channelName = channelName;
        this.images = new ArrayList<>();
        this.videos = new ArrayList<>();
    }
    protected UserData(Parcel in) {
        username = in.readString();
        password = in.readString();
        channelName = in.readString();
        images = in.createTypedArrayList(Bitmap.CREATOR);
        videos = in.createTypedArrayList(Uri.CREATOR);
    }
    public static final Creator<UserData> CREATOR = new Creator<UserData>() {
        @Override
        public UserData createFromParcel(Parcel in) {
            return new UserData(in);
        }

        @Override
        public UserData[] newArray(int size) {
            return new UserData[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(channelName);
        dest.writeTypedList(images);
        dest.writeTypedList(videos);
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

    public List<Bitmap> getImages() {
        return images;
    }

    public void setImages(List<Bitmap> images) {
        this.images = images;
    }

    public List<Uri> getVideos() {
        return videos;
    }

    public void setVideos(List<Uri> videos) {
        this.videos = videos;
    }
}