package com.example.project_android.api;

import java.util.List;

import com.example.project_android.model.ApiResponse;
import com.example.project_android.model.UserData;
import com.example.project_android.model.Video;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface VideoApiService {
    // Public routes
    @GET("/api/users/{id}/videos")
    Call<List<Video>> getUserVideos(@Path("id") String userId);

    @GET("/api/{username}/videos")
    Call<List<Video>> getUserVideosByUsername(@Path("username") String username);

    @GET("/api/users/{id}/videos/{pid}")
    Call<Video> getVideo(@Path("id") String userId, @Path("pid") String videoId);

    @GET("/api/videos/{pid}")
    Call<Video> getVideo(@Path("pid") String videoId);

    @GET("/api/videos")
    Call<List<Video>> get20Videos();

    @GET("/api/searchvideo")
    Call<List<Video>> searchVideos(@Query("query") String query);

    // Private routes - only for logged User

    @Multipart
    @POST("/api/users/{id}/videos")
    Call<ApiResponse> createVideo(@Path("id") String userId,
                                  @Part MultipartBody.Part image,
                                  @Part MultipartBody.Part video,
                                  @Part("title") RequestBody title,
                                  @Part("description") RequestBody description);

    @PUT("/api/users/{id}/videos/{pid}")
    Call<Video> updateVideo(@Path("id") String userId, @Path("pid") String videoId, @Body Video video);

    @PATCH("/api/users/{id}/videos/{pid}")
    Call<Video> updateVideoPartially(@Path("id") String userId, @Path("pid") String videoId, @Body Video video);

    @DELETE("/api/users/{id}/videos/{pid}")
    Call<Void> deleteVideo(@Path("id") String userId, @Path("pid") String videoId);

    // Likes
    @POST("/api/users/{id}/videos/{pid}/likes")
    Call<Void> likeVideo(@Path("id") String userId, @Path("pid") String videoId);

    @DELETE("/api/users/{id}/videos/{pid}/likes")
    Call<Void> unlikeVideo(@Path("id") String userId, @Path("pid") String videoId);

}