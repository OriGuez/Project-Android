package com.example.project_android.api;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.project_android.MyApplication;
import com.example.project_android.R;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            String baseUrl = MyApplication.getContext().getString(R.string.BaseUrl);
            SharedPreferences sharedPreferences = MyApplication.getContext().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
            // Create an OkHttpClient with the Authentication interceptor
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new Authentication(sharedPreferences))
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .writeTimeout(3, TimeUnit.SECONDS)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
