package com.example.project_android.api;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.project_android.model.ApiResponse;
import com.example.project_android.model.NewVideoModel;
import com.example.project_android.model.Video;

import java.io.Console;
import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoActions {
    //private MutableLiveData<List<Video>> videosListData;
    //private VideosDao dao;
    VideoApiService api;

    public VideoActions() {
        //this.videosListData = list;
        //this.dao=dao;
        this.api = RetrofitClient.getClient().create(VideoApiService.class);
    }
    //methods:

    public MutableLiveData<List<Video>> fetchUserVideos(String userId) {
        MutableLiveData<List<Video>> videosListData = new MutableLiveData<>();
        if (userId == null || userId.isEmpty()) {
            videosListData.postValue(null); // Return empty list if query is null or empty
            return videosListData;
        }
        api.getUserVideos(userId).enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    videosListData.postValue(response.body());
                    // Optionally, save the data to local database using dao (Room)
                } else {
                    int statusCode = response.code();
                    // Handle different status codes
                    switch (statusCode) {
                        case 404:
                            // Not found
                            break;
                        case 500:
                            // Server error
                            break;
                        default:
                            // Other status codes
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {
                // Handle failure
            }
        });
        return videosListData;
    }

    public MutableLiveData<Video> fetchVideo(String videoId) {
        MutableLiveData<Video> video = new MutableLiveData<>();
        if (videoId == null || videoId.isEmpty()) {
            video.postValue(null); // Return empty list if query is null or empty
            return video;
        }
        api.getVideo(videoId).enqueue(new Callback<Video>() {
            @Override
            public void onResponse(Call<Video> call, Response<Video> response) {
                if (response.isSuccessful() && response.body() != null) {
                    video.postValue(response.body());
                    // Handle single video response
                } else {
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
                    Log.d("success", "a");
                    videosData.postValue(response.body());
                    //videosListData.postValue(response.body());
                    // Optionally, save the data to local database using dao
                } else {
                    videosData.postValue(null);
                    Log.d("f", "a");
                }
            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {
                videosData.postValue(null);
                Log.d("f", "a");
                // Handle failure
            }
        });
        return videosData;
    }

    public MutableLiveData<List<Video>> searchVideos(String query) {
        MutableLiveData<List<Video>> videosData = new MutableLiveData<>();
        if (query == null || query.isEmpty()) {
            videosData.postValue(null); // Return empty list if query is null or empty
            return videosData;
        }
        String encodedQuery = null;
        try {
            encodedQuery = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            videosData.postValue(null);
            return videosData;
        }
        api.searchVideos(encodedQuery).enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    videosData.postValue(response.body());
                    // Optionally, save the data to local database using dao
                }
                else {
                    videosData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {
                videosData.postValue(null);
                // Handle failure
            }
        });
        return videosData;
    }


    public MutableLiveData<ApiResponse> createVideo(String userId, Video video) {
        MutableLiveData<ApiResponse> resp = new MutableLiveData<>();
        File imageFile = video.getImageFile();
        RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), imageRequestBody);

        File videoFile = video.getVideoFile();
        RequestBody videoRequestBody = RequestBody.create(MediaType.parse("video/mp4"), videoFile);
        MultipartBody.Part videoPart = MultipartBody.Part.createFormData("video", videoFile.getName(), videoRequestBody);

        RequestBody title = RequestBody.create(MediaType.parse("text/plain"), video.getTitle());
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), video.getDescription());
        api.createVideo(userId, imagePart, videoPart, title, description).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resp.setValue(new ApiResponse(true, response.code()));
                    // Handle video creation response
                } else {
                    // Handle response error
                    resp.setValue(new ApiResponse(false, response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                resp.setValue(new ApiResponse(false, 500));
                // Handle failure
            }
        });
        return resp;
    }


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

    public MutableLiveData<ApiResponse> likeVideo(String likingUserId, String videoId) {
        MutableLiveData<ApiResponse> resp = new MutableLiveData<>();

        api.likeVideo(likingUserId, videoId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resp.setValue(new ApiResponse(true, response.code()));
                } else {
                    resp.setValue(new ApiResponse(false, response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                resp.setValue(new ApiResponse(false, 500));
                // Handle failure
            }
        });
        return resp;
    }

    public MutableLiveData<ApiResponse> unlikeVideo(String unlikingUserId, String videoId) {
        MutableLiveData<ApiResponse> resp = new MutableLiveData<>();

        api.unlikeVideo(unlikingUserId, videoId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resp.setValue(new ApiResponse(true, response.code()));
                } else {
                    resp.setValue(new ApiResponse(false, response.code()));
                }
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                resp.setValue(new ApiResponse(false, 500));
                // Handle failure
            }
        });
        return resp;
    }


}
