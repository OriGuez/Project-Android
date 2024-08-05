package com.example.project_android.repository;

import androidx.lifecycle.LiveData;

import com.example.project_android.api.CommentActions;
import com.example.project_android.model.ApiResponse;
import com.example.project_android.model.Comment;

import java.util.List;

public class CommentRepository {
    public CommentRepository() {
        api = new CommentActions();
    }
    private CommentActions api;
    public LiveData<List<Comment>> getVideoComments(String videoId) {
        return api.fetchComments(videoId);
    }

    public LiveData<ApiResponse> add(String videoID,Comment comment){
        return api.addComment(videoID,comment);
    }
    public LiveData<ApiResponse> delete(String commentID){
        return api.deleteComment(commentID);
    }

    public LiveData<ApiResponse> update(String commentID,Comment updatedComment){
        return api.updateComment(commentID,updatedComment);
    }
}
