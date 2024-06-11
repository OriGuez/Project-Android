package com.example.project_android;

import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class VideoActivity extends AppCompatActivity {
    private VideoView videoView;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView publisherTextView;
    private ListView commentsListView;
    private Video currentVideo;
    AssetManager assetManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assetManager = getAssets();
        Intent intent = getIntent();
        // Retrieve the ID from the intent's extras
        String videoID = getIntent().getStringExtra("videoID");
        //EdgeToEdge.enable(this);
        for (Video video : MainActivity.videoList) {
            // Check if the video's ID matches the target ID
            if (video.getVidID().equals(videoID)) {
                // Found the video, you can now work with it
                // For example, you can store it in a variable or perform any action you need
                currentVideo = video;
                // If you want to stop searching after finding the first match, you can break out of the loop
                break;
            }
        }
        setContentView(R.layout.activity_video);
        // Initialize views
        videoView = findViewById(R.id.videoView);
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        publisherTextView = findViewById(R.id.publisherTextView);
        commentsListView = findViewById(R.id.commentsListView);
        // Set up video playback
        //Uri videoUri = Uri.parse(currentVideo.getUrl());
        videoView.setVideoPath("file:///android_asset/videos/vid1.mp4");
        videoView.start();
        //videoView.setVideoURI(videoUri);
        // Set video details
        titleTextView.setText(currentVideo.getTitle());
        descriptionTextView.setText(currentVideo.getDescription());
        publisherTextView.setText(currentVideo.getPublisher());
// Set up comments list
        List<Video.Comment> comments = currentVideo.getComments();
        ArrayAdapter<Video.Comment> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, comments);
        commentsListView.setAdapter(adapter);
    }
}