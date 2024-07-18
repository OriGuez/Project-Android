package com.example.project_android.api;

import com.example.project_android.model.UserData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UserApiService {
    @GET("/api/users/{id}")
    Call<UserData> getUserById(@Path("id") String userId);
}
