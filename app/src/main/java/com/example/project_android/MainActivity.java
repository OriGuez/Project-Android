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
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static List<UserData> userDataList=null;
    private Button registerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        //userDataList = new ArrayList<>();
        // Start RegistrationActivity
        registerButton = findViewById(R.id.registerMe);
        // Set OnClickListener for registerButton
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This code will execute when registerButton is clicked
                // Start RegistrationActivity
                Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }


        });
        if(userDataList!=null)
            Log.d("tochen",userDataList.get(0).getChannelName());
        //intent.putExtra("userDataArray", userDataArray);
        // Optional: Finish MainActivity if you don't want to keep it in the back stack
        //finish();
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