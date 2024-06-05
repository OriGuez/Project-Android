package com.example.project_android;

import android.os.Bundle;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static List<UserData> userDataList;
    public static UserData currentUser;
    public static boolean isLoggedUser = false;
    private Button registerButton;
    private Button loginButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        userDataList = new ArrayList<>();
        currentUser = null;
        setContentView(R.layout.activity_main);
        // Start RegistrationActivity
        registerButton = findViewById(R.id.registerMe);
        loginButton = findViewById(R.id.LoginMe);
        logoutButton = findViewById(R.id.LogOutButton);
        loggedVisibilityLogic();
        // Set OnClickListener for registerButton
        registerButton.setOnClickListener(v -> {
            // This code will execute when registerButton is clicked
            // Start RegistrationActivity
            Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
            //intent.putParcelableArrayListExtra("userDataList", new ArrayList<>(userDataList));
            startActivity(intent);
        });
        loginButton.setOnClickListener(v -> {
            // This code will execute when registerButton is clicked
            // Start RegistrationActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            //intent.putParcelableArrayListExtra("userDataList", new ArrayList<>(userDataList));
            startActivity(intent);

        });
        logoutButton.setOnClickListener(v -> {
            isLoggedUser = false;
            loggedVisibilityLogic();
        });
// Find the TextView by its ID
        //TextView myTextView = findViewById(R.id.myTextView);
        //Log.d("aaa",userDataList.get(0).getChannelName());
        // Modify the text
        //myTextView.setText(userDataList.get(0).getChannelName());
        //setContentView(R.layout.activity_main);

        //intent.putExtra("userDataArray", userDataArray);
        // Optional: Finish MainActivity if you don't want to keep it in the back stack
        //finish();
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