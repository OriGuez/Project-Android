package com.example.project_android.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.project_android.model.ApiResponse;
import com.example.project_android.model.NewVideoModel;
import com.example.project_android.model.Video;
import com.example.project_android.repository.VideoRepository;

import java.util.List;

public class VideosViewModel extends ViewModel {
    private VideoRepository repository;
    private LiveData<List<Video>> videos;

    private LiveData<Video> video;

    public VideosViewModel() {
        repository = new VideoRepository();
        //videos = repository.getAll();
    }

    public LiveData<List<Video>> get() {
        return repository.getAll();
        //return videos;
    }
    public LiveData<Video> get(String videoID) {
        return repository.get(videoID);
    }
    public LiveData<ApiResponse>add(String userID, Video video){
        return repository.add(userID,video);
    }

    public LiveData<List<Video>> getUserVideos(String userID) {
        return repository.getUserVideos(userID);
    }
    public LiveData<List<Video>> search(String query) {
        return repository.search(query);
    }

    public LiveData<ApiResponse> like(String likingUserID, String videoID) {
        return repository.like(likingUserID, videoID);
    }

    public LiveData<ApiResponse> unlike(String unlikingUserID, String videoID) {
        return repository.unlike(unlikingUserID, videoID);
    }

    public LiveData<ApiResponse> update(String UserID, String videoID,Video updatedVideo) {
        return repository.update(UserID, videoID,updatedVideo);
    }

    public LiveData<ApiResponse> delete(String UserID, String videoID) {
        return repository.delete(UserID, videoID);
    }
//    public void add(Video video) { repository.add(video); }
//
//    public void delete(Video video) { repository.delete(video); }
//
    //public void reload() { this.videos = repository.getAll(); }


}
