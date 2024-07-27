package com.example.project_android.repository;

import androidx.lifecycle.LiveData;

import com.example.project_android.api.UserActions;
import com.example.project_android.model.ApiResponse;
import com.example.project_android.model.TokenRequest;
import com.example.project_android.model.TokenResponse;
import com.example.project_android.model.UserData;
import com.example.project_android.model.UserID;


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
    public LiveData<TokenResponse> login(TokenRequest request){
        return api.createToken(request);
    };
    public LiveData<UserID> getUserID(String username){
        return  api.getIdByUsername(username);
    }

    public LiveData<ApiResponse> update(UserData user) {
        return api.updateUser(user);
    }

}
