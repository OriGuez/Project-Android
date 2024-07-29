package com.example.project_android.api;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.project_android.model.ApiResponse;
import com.example.project_android.model.UserData;
import com.example.project_android.model.TokenRequest;
import com.example.project_android.model.TokenResponse;
import com.example.project_android.model.UserID;

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
        this.api = RetrofitClient.getClient().create(UserApiService.class);
    }

    public MutableLiveData<UserData> getUserById(String userId) {
        MutableLiveData<UserData> user = new MutableLiveData<>();
        api.getUserById(userId).enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user.postValue(response.body());
                } else {
                    user.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
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
        File imageFile = userData.getImageFile();
        RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), imageRequestBody);
        RequestBody username = RequestBody.create(MediaType.parse("text/plain"), userData.getUsername() != null ? userData.getUsername() : "");
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), userData.getPassword() != null ? userData.getPassword() : "");
        RequestBody displayName = RequestBody.create(MediaType.parse("text/plain"), userData.getChannelName() != null ? userData.getChannelName() : "");

        Call<UserData> call = api.addUser(imagePart, username, password, displayName);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(new ApiResponse(true, response.code()));
                    Log.d("UserActions", "Response: " + response.body().toString());
                } else {
                    liveData.setValue(new ApiResponse(false, response.code()));
                    Log.e("UserActions", "Error: " + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                liveData.setValue(new ApiResponse(false, 500));
                Log.e("UserActions", "Failure: " + t.getMessage());
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
                    liveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                liveData.setValue(null);
            }
        });

        return liveData;
    }

    public LiveData<ApiResponse> updateUser(UserData user) {
        MutableLiveData<ApiResponse> result = new MutableLiveData<>();
        File imageFile = user.getImageFile();
        String s_username = user.getUsername();
        String s_password = user.getPassword();
        String s_displayName = user.getChannelName();
        MultipartBody.Part imagePart = null;
        RequestBody username = null;
        RequestBody password = null;
        RequestBody displayName = null;
        if (imageFile != null) {
            RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);
            imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), imageRequestBody);
        }
        if (s_username != null){
            username = RequestBody.create(MediaType.parse("text/plain"), s_username);
        }
        if (s_password != null){
            password = RequestBody.create(MediaType.parse("text/plain"), s_password);
        }
        if (s_displayName != null){
            displayName = RequestBody.create(MediaType.parse("text/plain"), s_displayName);
        }
        Call<ApiResponse> call;
        call = api.updateUser(user.getId(), imagePart, username, password, displayName);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    result.setValue(new ApiResponse(true, response.code()));
                    //result.postValue(response.body());
                } else {
                    result.postValue(new ApiResponse(false, response.message()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                result.postValue(new ApiResponse(false, t.getMessage()));
            }
        });
        return result;
    }
}
