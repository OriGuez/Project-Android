package com.example.project_android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class AddVideo extends AppCompatActivity {
    public static Uri here;
    private EditText editVideoTitle;
    private EditText editVideoDescription;
    private Button buttonUploadVideo;
    private Button buttonUploadThumbnail;
    private Button buttonSubmitVideo;
    private Uri videoUri;
    private Uri thumbnailUri;
    private VideoView videoView;
    @Override
    protected void onResume() {
        super.onResume();
        if (videoUri != null) {
            //videoView.seekTo(currentVideoPosition);
            videoView.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView.isPlaying()) {
            //currentVideoPosition = videoView.getCurrentPosition();
            videoView.pause();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video);

        editVideoTitle = findViewById(R.id.editVideoTitle);
        editVideoDescription = findViewById(R.id.editVideoDescription);
        buttonUploadVideo = findViewById(R.id.buttonUploadVideo);
        buttonUploadThumbnail = findViewById(R.id.buttonUploadThumbnail);
        buttonSubmitVideo = findViewById(R.id.buttonSubmitVideo);
        videoView = findViewById(R.id.videoView);

        // Set up the upload buttons
        buttonUploadVideo.setOnClickListener(v -> openVideoPicker());
        buttonUploadThumbnail.setOnClickListener(v -> openThumbnailPicker());
        buttonSubmitVideo.setOnClickListener(v -> submitVideo());
    }

    // Activity result launcher for video picking
    private final ActivityResultLauncher<Intent> videoPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    videoUri = result.getData().getData();
                    Toast.makeText(this, "Video Selected", Toast.LENGTH_SHORT).show();
                    playVideo();
                }
            });

    // Activity result launcher for thumbnail picking
    private final ActivityResultLauncher<Intent> thumbnailPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    thumbnailUri = result.getData().getData();
                    here=thumbnailUri;
                    Toast.makeText(this, "Thumbnail Selected", Toast.LENGTH_SHORT).show();
                }
            });

    private void openVideoPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        videoPickerLauncher.launch(intent);
    }

    private void openThumbnailPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        thumbnailPickerLauncher.launch(intent);
    }

    private void submitVideo() {
        String title = editVideoTitle.getText().toString().trim();
        String description = editVideoDescription.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || videoUri == null || thumbnailUri == null) {
            Toast.makeText(this, "Please fill all fields and upload video and thumbnail.", Toast.LENGTH_LONG).show();
            return;
        }

        // Create a new Video object
        Video newVideo = new Video();
        newVideo.setTitle(title);
        newVideo.setVidID("15");
        newVideo.setDescription(description);
        newVideo.setUrl(videoUri.toString());
        newVideo.setThumbnailUrl(thumbnailUri.toString());
        newVideo.setPublisher("CurrentUser"); // Replace with actual user
        newVideo.setUpload_date("Now"); // Replace with current date

        // Add new video to video list
        MainActivity.videoList.add(newVideo);
        here=videoUri;
        // Inform user and finish activity
        Toast.makeText(this, "Video Added Successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void playVideo() {
        videoView.setVideoURI(videoUri);
        videoView.start();
    }
}