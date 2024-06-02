package com.example.project_android;

import android.os.Bundle;
import android.content.Intent;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.graphics.Bitmap;
import android.net.Uri;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<UserData> userDataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        //setContentView(R.layout.activity_registration);
        //userDataList = new ArrayList<>();
        // Start RegistrationActivity
        Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
        startActivity(intent);
        //intent.putExtra("userDataArray", userDataArray);
        // Optional: Finish MainActivity if you don't want to keep it in the back stack
        finish();
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