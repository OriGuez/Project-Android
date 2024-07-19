package com.example.project_android.repository;

import androidx.lifecycle.LiveData;

import com.example.project_android.api.CommentActions;
import com.example.project_android.api.VideoActions;
import com.example.project_android.model.Comment;
import com.example.project_android.model.Video;

import java.util.List;

public class CommentRepository {
    public CommentRepository() {
        api = new CommentActions();
    }
    private CommentActions api;
    public LiveData<List<Comment>> getVideoComments(String videoId) {
        return api.fetchComments(videoId);
    }
}
