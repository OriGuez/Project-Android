package com.example.project_android.repository;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.project_android.LocalAppDB;
import com.example.project_android.MyApplication;
import com.example.project_android.api.VideoActions;
import com.example.project_android.dao.VideosDao;
import com.example.project_android.model.ApiResponse;
import com.example.project_android.model.Video;

import java.util.LinkedList;
import java.util.List;

public class VideoRepository {
    private VideosDao dao;
    private VideoListData videoListData;
    private LocalAppDB localAppDB;
    private VideoActions api;
//    private LiveData<List<Video>> allVideos;
    private static volatile VideoRepository instance;


    public VideoRepository() {
        localAppDB = LocalAppDB.getDatabase(MyApplication.getContext());
        dao = localAppDB.VideoDao();
        videoListData = new VideoListData();
        api = new VideoActions(videoListData, dao);
        //allVideos = dao.getAll();
    }
    // Static method to provide the singleton instance
    public static VideoRepository getInstance() {
        if (instance == null) {
            synchronized (VideoRepository.class) {
                if (instance == null) {
                    instance = new VideoRepository();
                }
            }
        }
        return instance;
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
        return videoListData;
//        // Check local database first
//        LiveData<List<Video>> localData = dao.index();
//        //if no data in localDB try bringing from api.
//        if (localData.getValue() == null || localData.getValue().isEmpty()) {
//            List<Video> apiData = api.fetch20Videos();
//            localData = api.fetch20Videos();
//            dao.insert();
//            //return api.fetch20Videos();
//        }
//        return localData;
    }

    public LiveData<List<Video>> getUserVideos(String userID) {
        return api.fetchUserVideos(userID);
    }

    //    public LiveData<Video> get(String videoId) {
//        MutableLiveData<Video> videoData = new MutableLiveData<>();
//        // First, look for the video in the local database
//        //LiveData<Video> localData = dao.get(videoId);
//        Video video = dao.get(videoId);
//        if (video != null)
//        {
//            videoData.postValue(video);
//            return videoData;
//        }
//        return api.fetchVideo(videoId);
//        //MutableLiveData<Video> videoData = new MutableLiveData<>();
//// First, look for the video in the local database
////        new Thread(() -> {
////            Video video = dao.get(videoId);
////            if (video != null) {
////                videoData.postValue(video); // If found, post the value to LiveData
////            } else {
////                // If not found, fetch it from the server
////                return api.fetchVideo(videoId);
//////                api.fetchVideo(videoId).observeForever(videoFromServer -> {
//////                    if (videoFromServer != null) {
//////                        videoData.postValue(videoFromServer);
//////                        // Save the video to the local database
//////                        new Thread(() -> {
//////                            dao.insert(videoFromServer);
//////                        }).start();
//////                    } else {
//////                        videoData.postValue(null); // Handle the case where the video is not found on the server
//////                    }
//////                });
////            }
////        }).start();
//
//        //return videoData;
//        //return api.fetchVideo(videoId);
//    }
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

    public LiveData<ApiResponse> update(String UserID, String videoID, Video updatedVideo) {
        return api.updateVideo(UserID, videoID, updatedVideo);
    }

    public LiveData<ApiResponse> delete(String UserID, String videoID) {
        return api.deleteVideo(UserID, videoID);
    }

    public void reload() {
        api.get();
    }

    //    public LiveData<List<Video>> getAll() {
//        return videoListData;
//    }
    class VideoListData extends MutableLiveData<List<Video>> {

        public VideoListData() {
            super();
            setValue(new LinkedList<>());
        }

        @Override
        protected void onActive() {
            super.onActive();
            new Thread(() -> {
                videoListData.postValue(dao.index());
            }).start();
        }
    }
}
