package com.example.project_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }
}
