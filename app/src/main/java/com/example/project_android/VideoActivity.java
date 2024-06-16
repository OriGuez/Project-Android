package com.example.project_android;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.MediaController;
import java.util.List;

public class VideoActivity extends AppCompatActivity {
    private CommentAdapter adapter;
    private VideoAdapter videoAdapter;
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
    private RecyclerView videoRecyclerView;
    private Video currentVideo;
    AssetManager assetManager;


    private static final String TAG = "VideoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assetManager = getAssets();

        // Retrieve the ID from the intent's extras
        String videoID = getIntent().getStringExtra("videoID");
        Log.d(TAG, "Received videoID: " + videoID);

        for (Video video : MainActivity.videoList) {
            if (video.getVidID().equals(videoID)) {
                currentVideo = video;
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
        videoRecyclerView = findViewById(R.id.recyclerView);

        if (MainActivity.currentUser == null) {
            addComment.setVisibility(View.GONE);
            commentAddText.setVisibility(View.GONE);
            likeButton.setVisibility(View.GONE);
            likeText.setVisibility(View.GONE);
        } else {
            updateLikeButton(likeButton, likeText);
        }

        addComment.setOnClickListener(v -> {
            String commentText = commentAddText.getText().toString().trim();
            if (!commentText.isEmpty()) {
                Video.Comment newComment = new Video.Comment(commentText, "User", "Now");
                if (MainActivity.currentUser != null) {
                    currentVideo.getComments().add(new Video.Comment("User", MainActivity.currentUser.getUsername(), commentText));
                } else {
                    currentVideo.getComments().add(new Video.Comment("User", "Anon", commentText));
                }
                adapter.notifyDataSetChanged();
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
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + getRawResourceIdByName(currentVideo.getUrl()));
        videoView.setVideoURI(videoUri);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
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

        // Set up RecyclerView for scrolling videos
        videoRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        videoAdapter = new VideoAdapter(this, MainActivity.videoList, "Video");
        videoRecyclerView.setAdapter(videoAdapter);
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this, R.style.MyDialogTheme)
                .setTitle("Delete Video")
                .setMessage("Are you sure you want to delete this video?")
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    deleteCurrentVideo();
                })
                .setNegativeButton(R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteCurrentVideo() {
        MainActivity.videoList.remove(currentVideo);
        finish();
    }

    private void updateLikeButton(ImageButton likeButton, TextView isLiked) {
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

    private int getRawResourceIdByName(String resourceName) {
        // Ensure the resource name is correctly formatted
        String formattedResourceName = resourceName.replace("/videos/", "").replace(".mp4", "");
        return getResources().getIdentifier(formattedResourceName, "raw", getPackageName());
    }
}