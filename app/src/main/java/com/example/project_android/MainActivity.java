package com.example.project_android;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.provider.MediaStore;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static UserData currentUser;
    public static List<UserData> userDataList;
    public static List<Video> videoList;

    private RecyclerView recyclerView;
    private Button registerButton;
    private Button loginButton;
    private Button logoutButton;
    private Button addVideoButton;
    private ImageView profilePic;
    private VideoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoList = loadVideoData();
        setContentView(R.layout.activity_main);
        userDataList = new ArrayList<>();
        currentUser = null;

        registerButton = findViewById(R.id.registerMe);
        loginButton = findViewById(R.id.LoginMe);
        logoutButton = findViewById(R.id.LogOutButton);
//         videoButton = findViewById(R.id.playVideoButton);

        addVideoButton = findViewById(R.id.buttonAddVideo);
        addVideoButton.setContentDescription("Add Video");
// Initialize new button

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerViewVideos);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new VideoAdapter(this, videoList, "Main");
        recyclerView.setAdapter(adapter);


        // Set up profile picture
        // profilePic = findViewById(R.id.profilePic);

        loggedVisibilityLogic();

        // Set OnClickListener for register button
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

        // Set OnClickListener for login button
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivityOri.class);
            startActivity(intent);
        });

        // Set OnClickListener for logout button
        logoutButton.setOnClickListener(v -> {

            currentUser = null;
            loggedVisibilityLogic();
        });


//            Intent intent = new Intent(MainActivity.this, VideoActivity.class);
//            if (!videoList.isEmpty()) {
//                intent.putExtra("videoID", videoList.get(0).getVidID());
//                startActivity(intent);
//            }

        // Set OnClickListener for add video button
        addVideoButton.setOnClickListener(v -> {
            if (currentUser != null
            ) {
                Intent intent = new Intent(MainActivity.this, AddVideo.class);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "You must be logged in to add a video.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    // Method to load video data
    private List<Video> loadVideoData() {
        List<Video> videos = new ArrayList<>();
        // Load the JSON from assets
//         String jsonString = JsonUtils.loadJSONFromAsset(this, "vidDB.json");
        // Parse the JSON using Gson
//         Gson gson = new Gson();
//         Type videoListType = new TypeToken<List<Video>>() {
//         }.getType();
//         videos = gson.fromJson(jsonString, videoListType);

        String jsonString = JsonUtils.loadJSONFromAsset(this, "vidDB.json");
        if (jsonString != null) {
            Gson gson = new Gson();
            Type videoListType = new TypeToken<List<Video>>() {
            }.getType();
            videos = gson.fromJson(jsonString, videoListType);
        }
        return videos;
    }

    private void loggedVisibilityLogic() {
        // Check if the user is logged in and adjust visibility of buttons
        if (currentUser != null) {
            registerButton.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            TextView userTextView = findViewById(R.id.usernameTextView);
            userTextView.setText("welcome, " + currentUser.getUsername());
            if (profilePic != null)
                profilePic.setImageBitmap(currentUser.getImage());
        } else {
            registerButton.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userDataList != null) {
            userDataList.clear();
        }
    }
}
