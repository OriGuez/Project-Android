package com.example.project_android;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.MediaController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class VideoActivity extends AppCompatActivity {
    //private CommentAdapter adapter;
    private CommentRecyclerViewAdapter recycleAdapter;
    private RecyclerView commentsRecycleView;
    private VideoAdapter videoAdapter;
    private VideoView videoView;
    private TextView titleTextView;
    private TextView dateTextView;
    private TextView descriptionTextView;
    private TextView publisherTextView;
    private Button addComment;
    private EditText commentAddText;
    private ImageButton likeButton;
    private TextView likeText;
    private ImageButton shareButton;
    private ImageButton deleteVideoButton;
    private ImageButton editVideoButton;
    private EditText editTitleEditText;
    private EditText editDescriptionEditText;
    private Button saveButton;
    private boolean isEditMode = false;
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
        checkPermissions();
        // Request permission to read external storage
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        // Initialize views
        videoView = findViewById(R.id.videoView);
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        dateTextView = findViewById(R.id.dateTextView);
        publisherTextView = findViewById(R.id.publisherTextView);
        commentAddText = findViewById(R.id.commentAddText);
        addComment = findViewById(R.id.addCommentButton);
        likeButton = findViewById(R.id.likeButton);
        likeText = findViewById(R.id.likeText);
        shareButton = findViewById(R.id.shareButton);
        deleteVideoButton = findViewById(R.id.deleteVideoButton);
        editVideoButton = findViewById(R.id.editVideoButton);
        editTitleEditText = findViewById(R.id.editTitleEditText);
        editDescriptionEditText = findViewById(R.id.editDescriptionEditText);
        saveButton = findViewById(R.id.saveEditVidButton);
        editTitleEditText.setVisibility(View.GONE);
        editDescriptionEditText.setVisibility(View.GONE);
        saveButton.setVisibility(View.GONE);
        videoRecyclerView = findViewById(R.id.recyclerView);
        commentsRecycleView = findViewById(R.id.commentsRecyclerView);
        if (MainActivity.currentUser == null) {
            addComment.setVisibility(View.GONE);
            commentAddText.setVisibility(View.GONE);
            likeButton.setVisibility(View.GONE);
            likeText.setVisibility(View.GONE);
            deleteVideoButton.setVisibility(View.GONE);
            editVideoButton.setVisibility(View.GONE);
        } else {
            updateLikeButton(likeButton, likeText);
        }
        addComment.setOnClickListener(v -> {
            String commentText = commentAddText.getText().toString().trim();
            if (!commentText.isEmpty()) {
                if (MainActivity.currentUser != null) {
                    currentVideo.getComments().add(new Video.Comment("User", MainActivity.currentUser.getUsername(), commentText));
                } else {
                    currentVideo.getComments().add(new Video.Comment("User", "Anon", commentText));
                }
                //adapter.notifyDataSetChanged();
                recycleAdapter.notifyDataSetChanged();
                RecyclerViewUtils.setRecyclerViewHeightBasedOnItems(commentsRecycleView);
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
        editVideoButton.setOnClickListener(v -> {
            if (!isEditMode) {
                // Enter edit mode
                enterEditMode();
            }
        });
        saveButton.setOnClickListener(v -> {
            // Save changes and exit edit mode
            saveChanges();
            exitEditMode();
        });
        // Set up video playback
        if (currentVideo.getUrl() != null) {
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + getRawResourceIdByName(currentVideo.getUrl()));
            videoView.setVideoURI(videoUri);
        } else {
            // Decode Base64 video and play it
            try {
                Uri vUri = decodeBase64ToVideoUri(currentVideo.getBase64Video());
                videoView.setVideoURI(vUri);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to decode video", Toast.LENGTH_SHORT).show();
            }
        }
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.start();
        // Set video details
        titleTextView.setText(currentVideo.getTitle());
        descriptionTextView.setText(currentVideo.getDescription());
        dateTextView.setText(currentVideo.getUpload_date());
        publisherTextView.setText(currentVideo.getPublisher());
        recycleAdapter = new CommentRecyclerViewAdapter(this, currentVideo.getComments(), commentsRecycleView);
        commentsRecycleView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecycleView.setAdapter(recycleAdapter);
        commentsRecycleView.post(() -> RecyclerViewUtils.setRecyclerViewHeightBasedOnItems(commentsRecycleView));

        // Set up RecyclerView for scrolling videos
        videoRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        videoAdapter = new VideoAdapter(this, MainActivity.videoList, "Video");
        videoRecyclerView.setAdapter(videoAdapter);
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this, R.style.MyDialogTheme)
                .setTitle(getString(R.string.delete_video))
                .setMessage(getString(R.string.sure_delete_video))
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    deleteCurrentVideo();
                })
                .setNegativeButton(R.string.no, null)
                .setIcon(android.R.drawable.ic_menu_delete)
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

    private void enterEditMode() {
        // Show edit fields and save button
        editTitleEditText.setVisibility(View.VISIBLE);
        editDescriptionEditText.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.VISIBLE);
        // Hide non-editable fields
        titleTextView.setVisibility(View.GONE);
        descriptionTextView.setVisibility(View.GONE);
        // Populate edit fields with current video details
        editTitleEditText.setText(currentVideo.getTitle());
        editDescriptionEditText.setText(currentVideo.getDescription());
        // Change edit button text to "Cancel"
        editVideoButton.setVisibility(View.GONE);
        // Set edit mode flag
        isEditMode = true;
    }

    private void exitEditMode() {
        // Hide edit fields and save button
        editTitleEditText.setVisibility(View.GONE);
        editDescriptionEditText.setVisibility(View.GONE);
        saveButton.setVisibility(View.GONE);
        // Show non-editable fields
        titleTextView.setVisibility(View.VISIBLE);
        descriptionTextView.setVisibility(View.VISIBLE);
        // Change edit button text back to "Edit"
        editVideoButton.setVisibility(View.VISIBLE);
        // Reset edit mode flag
        isEditMode = false;
    }

    private void saveChanges() {
        // Save changes to currentVideo object
        currentVideo.setTitle(editTitleEditText.getText().toString());
        currentVideo.setDescription(editDescriptionEditText.getText().toString());
        // Update UI with new details
        titleTextView.setText(currentVideo.getTitle());
        descriptionTextView.setText(currentVideo.getDescription());
    }

    private int getRawResourceIdByName(String resourceName) {
        // Ensure the resource name is correctly formatted
        String formattedResourceName = resourceName.replace("/videos/", "").replace(".mp4", "");
        return getResources().getIdentifier(formattedResourceName, "raw", getPackageName());
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }
    }

    private Uri decodeBase64ToVideoUri(String base64Video) throws IOException {
        byte[] decodedBytes = Base64.decode(base64Video, Base64.DEFAULT);
        File tempFile = File.createTempFile("decoded_video", ".mp4", getCacheDir());
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(decodedBytes);
        }
        return Uri.fromFile(tempFile);
    }
}