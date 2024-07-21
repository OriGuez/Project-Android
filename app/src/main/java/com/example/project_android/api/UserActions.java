package com.example.project_android.api;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.project_android.model.ApiResponse;
import com.example.project_android.model.UserData;
import com.example.project_android.model.TokenRequest;
import com.example.project_android.model.TokenResponse;
import com.example.project_android.model.UserID;
import com.example.project_android.model.Video;
import com.google.gson.Gson;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActions {
    UserApiService api;

    public UserActions() {
        //this.videosListData = list;
        //this.dao=dao;
        this.api = RetrofitClient.getClient().create(UserApiService.class);
    }
    public MutableLiveData<UserData> getUserById(String userId) {
        MutableLiveData<UserData> user = new MutableLiveData<>();
        api.getUserById(userId).enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user.postValue(response.body());
                    // Handle single video response
                } else {
                    user.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                // Handle failure
                user.postValue(null);
            }
        });
        return user;
    }


    public MutableLiveData<UserID> getIdByUsername(String username) {
        MutableLiveData<UserID> user = new MutableLiveData<>();
        api.getIdByUsername(username).enqueue(new Callback<UserID>() {
            @Override
            public void onResponse(Call<UserID> call, Response<UserID> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user.postValue(response.body());
                } else {
                    user.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<UserID> call, Throwable t) {
                user.postValue(null);
            }
        });
        return user;
    }









    public LiveData<ApiResponse> addUser(UserData userData) {
        MutableLiveData<ApiResponse> liveData = new MutableLiveData<>();
        File imageFile = userData.getImageFile(); // Assuming getImage() returns the image file
        RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), imageRequestBody);
        //Gson gson = new Gson();
        //String userDataJson = gson.toJson(userData);
        //RequestBody userDataRequestBody = RequestBody.create(MediaType.parse("application/json"), userDataJson);
        RequestBody username = RequestBody.create(MediaType.parse("text/plain"), userData.getUsername());
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), userData.getPassword());
        RequestBody displayName = RequestBody.create(MediaType.parse("text/plain"), userData.getChannelName());
        Call<UserData> call = api.addUser(imagePart, username,password,displayName);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(new ApiResponse(true, response.code()));
                    Log.d("UserActions", "Response: " + response.body().toString());

                    //callback.onResponse(call, response);
                } else {
                    liveData.setValue(new ApiResponse(false, response.code()));
                    Log.e("UserActions", "Error: " + response.errorBody().toString());
                    //callback.onFailure(call, new Throwable("Failed to add user"));
                }
            }
            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                liveData.setValue(new ApiResponse(false, 500));
                Log.e("UserActions", "Failure: " + t.getMessage());
                //callback.onFailure(call, t);
            }
        });
        return liveData;
    }

    public LiveData<TokenResponse> createToken(TokenRequest tokenRequest) {
        MutableLiveData<TokenResponse> liveData = new MutableLiveData<>();
        Call<TokenResponse> call = api.createToken(tokenRequest);

        call.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(null); // or handle the error case as per your need
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                liveData.setValue(null); // or handle the failure as per your need
            }
        });

        return liveData;
    }



}
