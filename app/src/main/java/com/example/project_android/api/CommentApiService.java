package com.example.project_android.api;

import java.util.List;

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
    @GET("/videos/{pid}/comments")
    Call<List<Comment>> getVideoComments(@Path("pid") String videoId);

    // Add a comment to a video
    @POST("/videos/{pid}/comments")
    Call<Comment> addComment(@Path("pid") String videoId, @Body Comment comment);

    // Update a comment
    @PATCH("/comments/{cid}")
    Call<Comment> updateComment(@Path("cid") String commentId, @Body Comment comment);

    // Delete a comment
    @DELETE("/comments/{cid}")
    Call<Void> deleteComment(@Path("cid") String commentId);
}
