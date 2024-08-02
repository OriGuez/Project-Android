package com.example.project_android.api;

import com.example.project_android.model.ApiResponse;
import com.example.project_android.model.TokenRequest;
import com.example.project_android.model.TokenResponse;
import com.example.project_android.model.UserData;
import com.example.project_android.model.UserID;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserApiService {
    @GET("/api/users/{id}")
    Call<UserData> getUserById(@Path("id") String userId);

    @GET("/api/users/getID/{username}")
    Call<UserID> getIdByUsername(@Path("username") String username);

    @POST("/api/tokens")
    Call<TokenResponse> createToken(@Body TokenRequest tokenRequest);

    @Multipart
    @POST("/api/users")
    Call<UserData> addUser(@Part MultipartBody.Part image,
                           @Part("username") RequestBody username,
                           @Part("password") RequestBody password,
                           @Part("displayName") RequestBody displayName);

    @Multipart
    @PATCH("/api/users/{id}")
    Call<ApiResponse> updateUser(@Path("id") String userId,
                                 @Part MultipartBody.Part image,
                                 @Part("username") RequestBody username,
                                 @Part("password") RequestBody password,
                                 @Part("displayName") RequestBody displayName);

    @DELETE("/api/users/{id}")
    Call<ApiResponse> deleteUser(@Path("id") String userId);
}
