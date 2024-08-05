package com.example.project_android.api;

import java.util.List;
import com.example.project_android.model.ApiResponse;
import com.example.project_android.model.Comment;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
public interface CommentApiService {

    // Get comments for a video
    @GET("/api/videos/{pid}/comments")
    Call<List<Comment>> getVideoComments(@Path("pid") String videoId);

    // Add a comment to a video
    @POST("/api/videos/{pid}/comments")
    Call<ApiResponse> addComment(@Path("pid") String videoId, @Body Comment comment);

    // Update a comment
    @PATCH("/api/comments/{cid}")
    Call<ApiResponse> updateComment(@Path("cid") String commentId, @Body Comment comment);

    // Delete a comment
    @DELETE("/api/comments/{cid}")
    Call<ApiResponse> deleteComment(@Path("cid") String commentId);
}
