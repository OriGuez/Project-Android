package com.example.project_android;

import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import android.widget.MediaController;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class VideoActivity extends AppCompatActivity {
    private CommentAdapter adapter;

    private VideoView videoView;
    private TextView titleTextView;
    private TextView dateTextView;
    private TextView descriptionTextView;
    private TextView publisherTextView;
    private ListView commentsListView;
    private Button addComment;
    private EditText commentAddText;
    private Video currentVideo;
    AssetManager assetManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assetManager = getAssets();
        //Intent intent = getIntent();
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
        dateTextView=findViewById(R.id.dateTextView);
        publisherTextView = findViewById(R.id.publisherTextView);
        commentsListView = findViewById(R.id.commentsListView);
        commentAddText = findViewById(R.id.commentAddText);
        addComment = findViewById(R.id.addCommentButton);


        addComment.setOnClickListener(v -> {
            String commentText = commentAddText.getText().toString().trim();
            if (!commentText.isEmpty()) {
                // Create a new comment object (assuming Video.Comment has appropriate constructor)
                Video.Comment newComment = new Video.Comment(commentText, "User", "Now");
                if (MainActivity.currentUser != null) {
                    // Create a new comment object (assuming Video.Comment has appropriate constructor)
                    currentVideo.getComments().add(new Video.Comment("User", MainActivity.currentUser.getUsername(), commentText));
                }
                else {
                    currentVideo.getComments().add(new Video.Comment("User", "Anon", commentText));

                }
                // Notify the adapter that the data set has changed
                adapter.notifyDataSetChanged();

                // Clear the EditText
                commentAddText.getText().clear();
            }
        });


        // Set up video playback
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.vid6);
        videoView.setVideoURI(videoUri);

        // Add media controller for video controls
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Start the video
        videoView.start();
        // Set video details
        titleTextView.setText(currentVideo.getTitle());
        descriptionTextView.setText(currentVideo.getDescription());
        dateTextView.setText(currentVideo.getUpload_date());
        publisherTextView.setText(currentVideo.getPublisher());
// Set up comments list
        List<Video.Comment> comments = currentVideo.getComments();
        adapter = new CommentAdapter(this, comments);
        commentsListView.setAdapter(adapter);
    }
}