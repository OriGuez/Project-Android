package com.example.project_android.repository;

import androidx.lifecycle.LiveData;

import com.example.project_android.api.UserActions;
import com.example.project_android.model.ApiResponse;
import com.example.project_android.model.UserData;


public class UserRepository {
    ///private VideosDao dao;
    //private VideosListData videosListData;
    private UserActions api;
    public UserRepository() {
        api = new UserActions();
    }
    public LiveData<UserData>get(String userId) {
        return api.getUserById(userId);
    }
    public LiveData<ApiResponse> add(UserData user) {
        return api.addUser(user);
    }
}
