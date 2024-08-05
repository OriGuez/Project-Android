package com.example.project_android.api;

import android.content.SharedPreferences;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
public class Authentication implements Interceptor {
    private SharedPreferences sharedPreferences;

    public Authentication(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = sharedPreferences.getString("token", null);
        Request request = chain.request();

        if (token != null) {
            Request.Builder requestBuilder = request.newBuilder()
                    .header("Authorization", "Bearer " + token);
            request = requestBuilder.build();
        }
        return chain.proceed(request);
    }
}
