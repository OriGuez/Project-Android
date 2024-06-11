package com.example.project_android;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;

// public class MainActivity extends Activity {
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.Console;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends AppCompatActivity {
    public static List<UserData> userDataList;
    public static List<Video> videoList;
    public static UserData currentUser;
    public static boolean isLoggedUser = false;
    private Button registerButton;
    private Button loginButton;
    private Button logoutButton;
    private Button videoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoList = new ArrayList<>();

        // Load the JSON from assets
        String jsonString = JsonUtils.loadJSONFromAsset(this, "vidDB.json");

        // Parse the JSON using Gson
        Gson gson = new Gson();
        Type videoListType = new TypeToken<List<Video>>() {}.getType();
        videoList = gson.fromJson(jsonString, videoListType);
        for (Video video : videoList) {
            System.out.println(video.getDescription());
        }
        currentUser = null;
        setContentView(R.layout.activity_main);
        // Start RegistrationActivity
        registerButton = findViewById(R.id.registerMe);
        loginButton = findViewById(R.id.LoginMe);
        logoutButton = findViewById(R.id.LogOutButton);
        videoButton = findViewById(R.id.playVideoButton);
        loggedVisibilityLogic();
        // Set OnClickListener for registerButton
        registerButton.setOnClickListener(v -> {
            // This code will execute when registerButton is clicked
            // Start RegistrationActivity
            Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(v -> {
            // This code will execute when registerButton is clicked
            // Start RegistrationActivity
            Intent intent = new Intent(MainActivity.this, LoginActivityOri.class);
            startActivity(intent);

        });
        logoutButton.setOnClickListener(v -> {
            isLoggedUser = false;
            loggedVisibilityLogic();
        });

        videoButton.setOnClickListener(v -> {
            // This code will execute when registerButton is clicked
            // Start RegistrationActivity
            Intent intent = new Intent(MainActivity.this, VideoActivity.class);
            intent.putExtra("videoID",videoList.get(0).getVidID());
            startActivity(intent);
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        loggedVisibilityLogic();
    }


    private void loggedVisibilityLogic(){
        // Check if the user is logged in and adjust visibility of buttons
        if (isLoggedUser) {
            registerButton.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            if (currentUser != null) {
                TextView userTextView = findViewById(R.id.usernameTextView);
                userTextView.setText("welcome, " + currentUser.getUsername());
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
                if (sessionData.getImages() != null) {
                    sessionData.getImages().clear();
                }
                if (sessionData.getVideos() != null) {
                    sessionData.getVideos().clear();
                }
            }
            userDataList.clear();
        }
    }
}