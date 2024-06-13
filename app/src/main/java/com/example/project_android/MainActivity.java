package com.example.project_android;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static UserData currentUser;
    public static boolean isLoggedUser=false;
    public static List<UserData> userDataList;
    public static List<Video> videoList;
    private RecyclerView recyclerView;
    private Button registerButton;
    private Button loginButton;
    private Button logoutButton;
    private Button videoButton;
    private ImageView profilePic; // Added ImageView


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        videoList = new ArrayList<>();
        userDataList = new ArrayList<>();
        // Load the JSON from assets
        String jsonString = JsonUtils.loadJSONFromAsset(this, "vidDB.json");

        // Parse the JSON using Gson
        Gson gson = new Gson();
        Type videoListType = new TypeToken<List<Video>>() {}.getType();
        videoList = gson.fromJson(jsonString, videoListType);
        assert videoList != null;
        for (Video video : videoList) {
            System.out.println(video.getDescription());
        }
        currentUser = null;
        setContentView(R.layout.activity_main);

        // Initialize views
        registerButton = findViewById(R.id.registerMe);
        loginButton = findViewById(R.id.LoginMe);
        logoutButton = findViewById(R.id.LogOutButton);
        videoButton = findViewById(R.id.playVideoButton);
        // Load video data
        videoList = loadVideoData();
        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns in grid
        VideoAdapter adapter = new VideoAdapter(this, videoList);
        recyclerView.setAdapter(adapter);
        profilePic=findViewById(R.id.profilePic);
        loggedVisibilityLogic();
        // Set OnClickListener for registerButton
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivityOri.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            // Handle logout logic
            isLoggedUser = false;
            currentUser = null;
        });

        videoButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, VideoActivity.class);
            if (!videoList.isEmpty()) {
                intent.putExtra("videoID", videoList.get(0).getVidID());
                startActivity(intent);
            }
        });
    }

    // Method to load video data
    private List<Video> loadVideoData() {
        List<Video> videos = new ArrayList<>();

        // Load the JSON from assets
        String jsonString = JsonUtils.loadJSONFromAsset(this, "vidDB.json");

        // Parse the JSON using Gson
        Gson gson = new Gson();
        Type videoListType = new TypeToken<List<Video>>() {}.getType();
        videos = gson.fromJson(jsonString, videoListType);

        return videos;
    private void loggedVisibilityLogic(){
        // Check if the user is logged in and adjust visibility of buttons
        if (isLoggedUser) {
            registerButton.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            if (currentUser != null) {
                TextView userTextView = findViewById(R.id.usernameTextView);
                userTextView.setText("welcome, " + currentUser.getUsername());

                profilePic.setImageBitmap(currentUser.getImage());
            }
        } else {
            registerButton.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clear the user session data list
        if (userDataList != null) {
            for (UserData sessionData : userDataList) {
                sessionData.setUsername(null);
                sessionData.setPassword(null);
                sessionData.setChannelName(null);
                if (sessionData.getImage() != null) {
                    sessionData.setImage(null);
                }
            }
            userDataList.clear();
        }
    }
}
