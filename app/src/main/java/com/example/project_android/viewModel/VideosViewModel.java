package com.example.project_android.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.project_android.model.NewVideoModel;
import com.example.project_android.model.Video;
import com.example.project_android.repository.VideoRepository;

import java.util.List;

public class VideosViewModel extends ViewModel {
    private VideoRepository repository;
    private LiveData<List<Video>> videos;

    private LiveData<Video> video;

    public VideosViewModel() {
        repository = new VideoRepository();
        videos = repository.getAll();
    }

    public LiveData<List<Video>> get() {
        return videos;
    }
    public LiveData<Video> get(String videoID) {
        return repository.get(videoID);
    }

//    public void add(Video video) { repository.add(video); }
//
//    public void delete(Video video) { repository.delete(video); }
//
//    public void reload() { repository.reload(); }


}
