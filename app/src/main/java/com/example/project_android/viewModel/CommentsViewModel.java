package com.example.project_android.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.project_android.model.ApiResponse;
import com.example.project_android.model.Comment;
import com.example.project_android.model.Video;
import com.example.project_android.repository.CommentRepository;
import com.example.project_android.repository.VideoRepository;

import java.util.List;

public class CommentsViewModel extends ViewModel {
    private CommentRepository repository;
    private LiveData<List<Comment>> comments;

    public CommentsViewModel() {
        repository = new CommentRepository();
        comments = null;
    }

    public LiveData<List<Comment>> get(String videoID) {
        return repository.getVideoComments(videoID);
    }
    public LiveData<ApiResponse> add(String videoID, Comment comment){
        return repository.add(videoID,comment);
    }

    public LiveData<ApiResponse> delete(String commentID){
        return repository.delete(commentID);
    }
}
