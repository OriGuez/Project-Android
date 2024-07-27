package com.example.project_android.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.project_android.model.ApiResponse;
import com.example.project_android.model.TokenRequest;
import com.example.project_android.model.TokenResponse;
import com.example.project_android.model.UserData;
import com.example.project_android.model.UserID;
import com.example.project_android.repository.UserRepository;
import com.example.project_android.repository.VideoRepository;

public class UsersViewModel extends ViewModel {
    private UserRepository repository;
    public UsersViewModel(){
        repository = new UserRepository();
    }


    public LiveData<ApiResponse> add(UserData user){
        return repository.add(user);
    }
    public LiveData<TokenResponse> login(TokenRequest request){
        return repository.login(request);
    };
    public LiveData<UserData>get(String userID){
        return repository.get(userID);
    }
    public LiveData<UserID>getUserID(String username){
        return repository.getUserID(username);
    }
    public LiveData<ApiResponse> update(UserData user) {
        return repository.update(user);
    }
}
