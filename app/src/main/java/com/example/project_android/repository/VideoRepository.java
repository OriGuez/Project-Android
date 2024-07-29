package com.example.project_android.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.project_android.api.VideoActions;
import com.example.project_android.dao.VideosDao;
import com.example.project_android.model.ApiResponse;
import com.example.project_android.model.NewVideoModel;
import com.example.project_android.model.Video;

import java.util.LinkedList;
import java.util.List;

public class VideoRepository {
    ///private VideosDao dao;
    //private VideosListData videosListData;
    private VideoActions api;

    public VideoRepository() {
        api = new VideoActions();
    }


//    public MutableLiveData<Video> getAVideo(String uploaderID,String videoID) {
//        return api.fetchVideo(uploaderID,videoID);
//    }


    //    static class VideosListData extends MutableLiveData<List<Video>> {
//        public VideosListData() {
//            super();
//            setValue(new LinkedList<Video>());
//        }
//
//        @Override
//        protected void onActive() {
//            super.onActive();
//
////            new Thread(() ->
////            {
////                postListData.postValue(dao.get());
////            }).start();
//        }
//
//    }
    public LiveData<List<Video>> getAll() {
        return api.fetch20Videos();
    }

    public LiveData<List<Video>> getUserVideos(String userID) {
        return api.fetchUserVideos(userID);
    }

    public LiveData<Video> get(String videoId) {
        return api.fetchVideo(videoId);
    }

    public LiveData<List<Video>> search(String query) {
        return api.searchVideos(query);
    }
    public LiveData<ApiResponse> add(String userID, Video video) {
        return api.createVideo(userID, video);
    }

    public LiveData<ApiResponse> like(String likingUserID, String videoID) {
        return api.likeVideo(likingUserID, videoID);
    }

    public LiveData<ApiResponse> unlike(String unlikingUserID, String videoID) {
        return api.unlikeVideo(unlikingUserID, videoID);
    }

    public LiveData<ApiResponse> update(String UserID, String videoID,Video updatedVideo) {
        return api.updateVideo(UserID, videoID,updatedVideo);
    }
    public LiveData<ApiResponse> delete(String UserID, String videoID) {
        return api.deleteVideo(UserID, videoID);
    }
}
