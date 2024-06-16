package com.example.project_android;

import static java.lang.Integer.MAX_VALUE;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.media.Image;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import androidx.appcompat.app.AlertDialog;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import android.widget.MediaController;

public class VideoActivity extends AppCompatActivity {

    //private CommentAdapter adapter;
    private CommentRecyclerViewAdapter adapter;

    private RecyclerView commentsRecyclerView;

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
    private ImageButton editVideoButton;
    private EditText editTitleEditText;
    private EditText editDescriptionEditText;
    private Button saveButton;
    private boolean isEditMode = false;

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


        //commentsListView = findViewById(R.id.commentsListView);

        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);


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
        //
        if (MainActivity.currentUser == null) {
            addComment.setVisibility(View.GONE);
            commentAddText.setVisibility(View.GONE);
            likeButton.setVisibility(View.GONE);
            likeText.setVisibility(View.GONE);
            deleteVideoButton.setVisibility(View.GONE);
            editVideoButton.setVisibility(View.GONE);
        } else {
            //if user is logged update the Like button
            UpdateLikeButton(likeButton, likeText);
        }
        addComment.setOnClickListener(v -> {
            String commentText = commentAddText.getText().toString().trim();
            if (!commentText.isEmpty()) {
                if (MainActivity.currentUser != null) {
                    Random random = new Random();
                    String newCommentID = Integer.toString(random.nextInt(MAX_VALUE));
                    // Create a new comment object (assuming Video.Comment has appropriate constructor)
                    currentVideo.getComments().add(new Video.Comment(newCommentID, MainActivity.currentUser.getUsername(), commentText));
                } else {
                    //currentVideo.getComments().add(new Video.Comment("Anon", "Anon", commentText));
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
        int resID = getResources().getIdentifier("vid" + videoID, "raw", getPackageName());

        // Set up video playback
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + resID);
        videoView.setVideoURI(videoUri);
        Log.d("AddVideoActivity", "Received URI: " + videoUri.toString());

//         ContentResolver contentResolver = getContentResolver();
//        try (InputStream inputStream = contentResolver.openInputStream(MainActivity.publicURI)) {
//            if (inputStream != null) {
//                Log.d("NewActivity", "Video URI is accessible");
//            } else {
//                Log.e("NewActivity", "Failed to access video URI");
//            }
//        } catch (IOException e) {
//            Log.e("NewActivity", "Error accessing video URI", e);
//        }
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
        //List<Video.Comment> comments = currentVideo.getComments();
        adapter = new CommentRecyclerViewAdapter(this, currentVideo.getComments());
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(adapter);
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this, R.style.MyDialogTheme)
                .setTitle(R.string.delete_video)
                .setMessage(R.string.sure_delete_video)
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

}