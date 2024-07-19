package com.example.project_android.api;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.project_android.model.NewVideoModel;
import com.example.project_android.model.Video;

import java.io.Console;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoActions {
    //private MutableLiveData<List<Video>> videosListData;
    //private VideosDao dao;
    VideoApiService api;

    public VideoActions(){
        //this.videosListData = list;
        //this.dao=dao;
        this.api = RetrofitClient.getClient().create(VideoApiService.class);
    }
    //methods:

//    public LiveData<List<Video>> fetchUserVideos(int userId) {
//        api.getUserVideos(userId).enqueue(new Callback<List<Video>>() {
//            @Override
//            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    videosListData.postValue(response.body());
//                    // Optionally, save the data to local database using dao (Room)
//                } else {
//                    int statusCode = response.code();
//                    // Handle different status codes
//                    switch (statusCode) {
//                        case 404:
//                            // Not found
//                            break;
//                        case 500:
//                            // Server error
//                            break;
//                        default:
//                            // Other status codes
//                            break;
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Video>> call, Throwable t) {
//                // Handle failure
//            }
//        });
//        return videosListData;
//    }


//    public void fetchUserVideosByUsername(String username) {
//        api.getUserVideosByUsername(username).enqueue(new Callback<List<Video>>() {
//            @Override
//            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    videosListData.postValue(response.body());
//                    // Optionally, save the data to local database using dao
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Video>> call, Throwable t) {
//                // Handle failure
//            }
//        });
//    }

//    public MutableLiveData<Video> fetchVideo(String userId, String videoId) {
//        MutableLiveData<Video> videoData = new MutableLiveData<>();
//        api.getVideo(userId, videoId).enqueue(new Callback<Video>() {
//            @Override
//            public void onResponse(Call<Video> call, Response<Video> response) {
//                if (response.isSuccessful()) {
//                    videoData.setValue(response.body());
//                }
//                else {
//                    //handle
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Video> call, Throwable t) {
//                //Log.e(TAG, "Error: " + response.message());
//                // Handle failure
//            }
//        });
//        return videoData;
//    }

    public MutableLiveData<Video> fetchVideo(String videoId) {
        MutableLiveData<Video> video = new MutableLiveData<>();
        api.getVideo(videoId).enqueue(new Callback<Video>() {
            @Override
            public void onResponse(Call<Video> call, Response<Video> response) {
                if (response.isSuccessful() && response.body() != null) {
                    video.postValue(response.body());
                    // Handle single video response
                }
                else {
                    video.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Video> call, Throwable t) {
                // Handle failure
                video.postValue(null);
            }
        });
        return video;
    }

    public MutableLiveData<List<Video>> fetch20Videos() {
        MutableLiveData<List<Video>> videosData = new MutableLiveData<>();
        api.get20Videos().enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("success","a");
                    videosData.postValue(response.body());
                    //videosListData.postValue(response.body());
                    // Optionally, save the data to local database using dao
                }
                else {
                    videosData.postValue(null);
                    Log.d("f","a");
                }
            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {
                videosData.postValue(null);
                Log.d("f","a");
                // Handle failure
            }
        });
        return videosData;
    }

//    public void searchVideos(String query) {
//        api.searchVideos(query).enqueue(new Callback<List<Video>>() {
//            @Override
//            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    videosListData.postValue(response.body());
//                    // Optionally, save the data to local database using dao
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Video>> call, Throwable t) {
//                // Handle failure
//            }
//        });
//    }


//    public void createVideo(String userId, Video video) {
//        api.createVideo(userId, video).enqueue(new Callback<Video>() {
//            @Override
//            public void onResponse(Call<Video> call, Response<Video> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    // Handle video creation response
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Video> call, Throwable t) {
//                // Handle failure
//            }
//        });
//    }


//    public void updateVideo(String userId, String videoId, Video video) {
//        api.updateVideo(userId, videoId, video).enqueue(new Callback<Video>() {
//            @Override
//            public void onResponse(Call<Video> call, Response<Video> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    // Handle video update response
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Video> call, Throwable t) {
//                // Handle failure
//            }
//        });
//    }


//    public void updateVideoPartially(String userId, String videoId, Video video) {
//        api.updateVideoPartially(userId, videoId, video).enqueue(new Callback<Video>() {
//            @Override
//            public void onResponse(Call<Video> call, Response<Video> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    // Handle partial video update response
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Video> call, Throwable t) {
//                // Handle failure
//            }
//        });
//    }

//    public void deleteVideo(String userId, String videoId) {
//        api.deleteVideo(userId, videoId).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//                    // Handle video deletion response
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                // Handle failure
//            }
//        });
//    }

//    public void likeVideo(String userId, String videoId) {
//        api.likeVideo(userId, videoId).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//                    // Handle like video response
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                // Handle failure
//            }
//        });
//    }

//    public void unlikeVideo(String userId, String videoId) {
//        api.unlikeVideo(userId, videoId).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//                    // Handle unlike video response
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                // Handle failure
//            }
//        });
//    }


}
