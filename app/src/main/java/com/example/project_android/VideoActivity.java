package com.example.project_android;

import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.media.Image;

import androidx.appcompat.app.AlertDialog;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private ImageButton likeButton;
    private TextView likeText;
    private ImageButton shareButton;
    private ImageButton deleteVideoButton;


    private Video currentVideo;
    AssetManager assetManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assetManager = getAssets();
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
        dateTextView = findViewById(R.id.dateTextView);
        publisherTextView = findViewById(R.id.publisherTextView);
        commentsListView = findViewById(R.id.commentsListView);
        commentAddText = findViewById(R.id.commentAddText);
        addComment = findViewById(R.id.addCommentButton);
        likeButton = findViewById(R.id.likeButton);
        likeText = findViewById(R.id.likeText);
        shareButton = findViewById(R.id.shareButton);
        deleteVideoButton = findViewById(R.id.deleteVideoButton);
        if (MainActivity.currentUser == null) {
            addComment.setVisibility(View.GONE);
            commentAddText.setVisibility(View.GONE);
            likeButton.setVisibility(View.GONE);
            likeText.setVisibility(View.GONE);
        } else {
            UpdateLikeButton(likeButton, likeText);
        }
        addComment.setOnClickListener(v -> {
            String commentText = commentAddText.getText().toString().trim();
            if (!commentText.isEmpty()) {
                // Create a new comment object (assuming Video.Comment has appropriate constructor)
                Video.Comment newComment = new Video.Comment(commentText, "User", "Now");
                if (MainActivity.currentUser != null) {
                    // Create a new comment object (assuming Video.Comment has appropriate constructor)
                    currentVideo.getComments().add(new Video.Comment("User", MainActivity.currentUser.getUsername(), commentText));
                } else {
                    currentVideo.getComments().add(new Video.Comment("User", "Anon", commentText));

                }
                // Notify the adapter that the data set has changed
                adapter.notifyDataSetChanged();
                // Clear the EditText
                commentAddText.getText().clear();
            }
        });
        likeButton.setOnClickListener(v -> {
            boolean isFound = false;
            for (String user : currentVideo.getWhoLikedList()) {
                if (user.equals(MainActivity.currentUser.getUsername())) {
                    likeButton.setImageDrawable(getResources().getDrawable(R.drawable.like));
                    likeText.setText(R.string.like);
                    isFound = true;
                    currentVideo.getWhoLikedList().remove(user);
                    break;
                }
            }
            if (!isFound) {
                currentVideo.getWhoLikedList().add(MainActivity.currentUser.getUsername());
                likeButton.setImageDrawable(getResources().getDrawable(R.drawable.likebuttonpressed));
                likeText.setText(R.string.liked);
            }
        });
        shareButton.setOnClickListener(v -> {
            String textToShare = currentVideo.getUrl();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
            String title = getResources().getString(R.string.share_via);
            Intent chooser = Intent.createChooser(shareIntent, title);
            if (shareIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(chooser);
            }
        });
        deleteVideoButton.setOnClickListener(v -> {
            showDeleteConfirmationDialog();

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
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this,R.style.MyDialogTheme)
                .setTitle("Delete Video")
                .setMessage("Are you sure you want to delete this video?")
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    // Perform delete operation here
                    deleteCurrentVideo();
                })
                .setNegativeButton(R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteCurrentVideo() {
        // Implement your delete logic here
        // For example, remove the video from the list and finish the activity
        MainActivity.videoList.remove(currentVideo);
        finish(); // Close the activity after deletion
    }

    void UpdateLikeButton(ImageButton likeButton, TextView isLiked) {
        for (String user : currentVideo.getWhoLikedList()) {
            if (user.equals(MainActivity.currentUser.getUsername())) {
                likeButton.setImageDrawable(getResources().getDrawable(R.drawable.likebuttonpressed));
                isLiked.setText(R.string.liked);
                return;
            }
        }
        likeButton.setImageDrawable(getResources().getDrawable(R.drawable.like));
        isLiked.setText(R.string.like);
    }
}