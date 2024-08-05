package com.example.project_android.repository;

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
    public LiveData<List<Video>> getAll() {
        return videoListData;
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

    public LiveData<ApiResponse> update(String UserID, String videoID, Video updatedVideo) {
        return api.updateVideo(UserID, videoID, updatedVideo);
    }

    public LiveData<ApiResponse> delete(String UserID, String videoID) {
        return api.deleteVideo(UserID, videoID);
    }

    public void reload() {
        api.get();
    }

    class VideoListData extends MutableLiveData<List<Video>> {
        private boolean dataLoaded = false;

        public VideoListData() {
            super();
            setValue(new LinkedList<>());
        }

        @Override
        protected void onActive() {
            super.onActive();
            if (!dataLoaded) {
                new Thread(() -> {
                    videoListData.postValue(dao.index());
                    dataLoaded = true;
                }).start();
            }
        }
    }
}
