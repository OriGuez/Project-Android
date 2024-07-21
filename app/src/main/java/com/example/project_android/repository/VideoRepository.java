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

    public LiveData<ApiResponse>add(String userID,Video video){
        return api.createVideo(userID,video);
    }
}
