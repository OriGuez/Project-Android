package com.example.project_android.api;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.project_android.model.Comment;
import com.example.project_android.model.Video;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActions {
    CommentApiService api;

    public CommentActions(){
        //this.videosListData = list;
        //this.dao=dao;
        this.api = RetrofitClient.getClient().create(CommentApiService.class);
    }

    public MutableLiveData<List<Comment>> fetchComments(String videoId) {
        MutableLiveData<List<Comment>> commentsData = new MutableLiveData<>();
        api.getVideoComments(videoId).enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("success", "Fetched comments successfully");
                    commentsData.postValue(response.body());
                } else {
                    commentsData.postValue(null);
                    Log.d("failure", "Failed to fetch comments");
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                commentsData.postValue(null);
                Log.d("failure", "Error fetching comments", t);
            }
        });
        return commentsData;
    }

}
