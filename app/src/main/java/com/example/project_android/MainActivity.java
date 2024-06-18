package com.example.project_android;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.UiModeManager;

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
import android.widget.LinearLayout;
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
    //private Button registerButton;
    private Button loginButton;
    private Button logoutButton;
    private Button addVideoButton;
    private Button btnToggleDark;

    private ImageView profilePic;
    private VideoAdapter adapter;
    private LinearLayout mainLayout;
    public static boolean isDarkMode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoList = loadVideoData();
        setContentView(R.layout.activity_main);
        // Request permission to read external storage
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        // Check the current mode and set the button text accordingly
        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)
            isDarkMode = true;
        else
            isDarkMode = false;

        userDataList = new ArrayList<>();
        currentUser = null;
        btnToggleDark = findViewById(R.id.btnToggleDark);
        //registerButton = findViewById(R.id.registerMe);
        loginButton = findViewById(R.id.LoginMe);
        logoutButton = findViewById(R.id.LogOutButton);
        addVideoButton = findViewById(R.id.buttonAddVideo);
        mainLayout=findViewById(R.id.main);
        addVideoButton.setContentDescription("Add Video");
        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerViewVideos);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new VideoAdapter(this, videoList, "Main");
        recyclerView.setAdapter(adapter);
        // Set up profile picture
        // profilePic = findViewById(R.id.profilePic);
        loggedVisibilityLogic();
        // Set OnClickListener for register button
//        registerButton.setOnClickListener(v -> {
//            Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
//            startActivity(intent);
//        });

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
        btnToggleDark.setOnClickListener(v -> {
            if (isDarkMode) {
                mainLayout.setBackgroundColor(Color.WHITE);
                adapter.notifyDataSetChanged();
            } else {
                mainLayout.setBackgroundColor(Color.DKGRAY);
                adapter.notifyDataSetChanged();
            }
            isDarkMode = !isDarkMode;
        });

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
        loggedVisibilityLogic();
    }

    // Method to load video data
    private List<Video> loadVideoData() {
        List<Video> videos = new ArrayList<>();
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
//            registerButton.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            TextView userTextView = findViewById(R.id.usernameTextView);
            userTextView.setText("welcome, " + currentUser.getUsername());
            if (profilePic != null)
                profilePic.setImageBitmap(currentUser.getImage());
        } else {
//            registerButton.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
        }
    }


//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        final int nightMode = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;
//        if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
//            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        } else {
//            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        }
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userDataList != null) {
            userDataList.clear();
        }
    }
}
