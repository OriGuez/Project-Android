package com.example.project_android.api;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.project_android.model.ApiResponse;
import com.example.project_android.model.Comment;
import com.example.project_android.model.Video;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActions {
    CommentApiService api;

    public CommentActions(){
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


    public MutableLiveData<ApiResponse> addComment(String videoId, Comment comment) {
        MutableLiveData<ApiResponse> resp = new MutableLiveData<>();
        api.addComment(videoId, comment).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("success", "Added comment successfully");
                    ApiResponse resSuccess = response.body();
                    resSuccess.setSuccessful(true);
                    resSuccess.setCode(response.code());
                    resp.setValue(resSuccess);
                } else {
                    resp.setValue(new ApiResponse(false, response.code()));
                    Log.d("failure", "Failed to add comment");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                resp.setValue(new ApiResponse(false, -1));
                Log.d("failure", "Error adding comment", t);
            }
        });
        return resp;
    }


    public MutableLiveData<ApiResponse> deleteComment(String commentID) {
        MutableLiveData<ApiResponse> resp = new MutableLiveData<>();
        api.deleteComment(commentID).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("success", "deleted comment successfully");
                    resp.setValue(new ApiResponse(true, response.code()));
                } else {
                    resp.setValue(new ApiResponse(false, response.code()));
                    Log.d("failure", "Failed to deleted comment");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                resp.setValue(new ApiResponse(false, -1));
                Log.d("failure", "Error deleting comment", t);
            }
        });
        return resp;
    }


    public MutableLiveData<ApiResponse> updateComment(String commentID,Comment updatedComment) {
        MutableLiveData<ApiResponse> resp = new MutableLiveData<>();
        api.updateComment(commentID,updatedComment).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("success", "edited comment successfully");
                    resp.setValue(new ApiResponse(true, response.code()));
                } else {
                    resp.setValue(new ApiResponse(false, response.code()));
                    Log.d("failure", "Failed to edited comment");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                resp.setValue(new ApiResponse(false, -1));
                Log.d("failure", "Error editing comment", t);
            }
        });
        return resp;
    }

}
