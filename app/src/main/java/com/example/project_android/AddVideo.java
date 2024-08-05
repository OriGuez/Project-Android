package com.example.project_android;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import android.media.ThumbnailUtils;
import com.example.project_android.model.Video;
import com.example.project_android.utils.FileUtils;
import com.example.project_android.viewModel.VideosViewModel;

public class AddVideo extends AppCompatActivity {
    private VideosViewModel vidViewModel;
    private ProgressBar progressBar;
    private EditText editVideoTitle;
    private EditText editVideoDescription;
    private TextView uploadVidFromGallery;
    private ImageView thumbnailView;
    private Button buttonUploadThumbnail;
    private Button buttonSubmitVideo;
    private Uri videoUri;
    private Uri thumbnailUri;
    private VideoView videoView;
    private Bitmap thumbPic;
    private static final int REQUEST_CODE_PICK_VIDEO = 10;
    private static final int REQUEST_CODE_PICK_PICTURE = 11;
    private static final int PICK_CAMERA_REQUEST = 12;
    private static final int REQUEST_PERMISSION = 100;

    @Override
    protected void onResume() {
        super.onResume();
        if (videoUri != null) {
            videoView.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video);
        vidViewModel = new ViewModelProvider(this).get(VideosViewModel.class);
        editVideoTitle = findViewById(R.id.editVideoTitle);
        editVideoDescription = findViewById(R.id.editVideoDescription);
        buttonUploadThumbnail = findViewById(R.id.buttonUploadThumbnail);
        buttonSubmitVideo = findViewById(R.id.buttonSubmitVideo);
        videoView = findViewById(R.id.videoView);
        thumbnailView = findViewById(R.id.thumbnailPrev);
        uploadVidFromGallery = findViewById(R.id.uploadVidFromGallery);
        progressBar = findViewById(R.id.progressBar);
        // Set up the upload buttons
        uploadVidFromGallery.setOnClickListener(v -> openVideoPicker());
        buttonUploadThumbnail.setOnClickListener(v -> openThumbnailPicker());
        buttonSubmitVideo.setOnClickListener(v -> submitVideo());
    }

    private void openVideoPicker() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_CODE_PICK_VIDEO);
    }

    private void openThumbnailPicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_CODE_PICK_PICTURE);
    }

    private void openCameraVideo() {
        // Check for camera permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            startActivityForResult(takePictureIntent, PICK_CAMERA_REQUEST);
        }
    }

    private void submitVideo() {
        buttonSubmitVideo.setEnabled(false);
        if (videoView.isPlaying()) {
            videoView.pause();
        }
        progressBar.setVisibility(View.VISIBLE);
        String title = editVideoTitle.getText().toString().trim();
        String description = editVideoDescription.getText().toString().trim();
        if (title.isEmpty() || description.isEmpty() || videoUri == null || thumbnailUri == null) {
            Toast.makeText(this, getString(R.string.fillAll), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            buttonSubmitVideo.setEnabled(true);
            return;
        }
        // Create a new Video object
        Video newVideo = new Video(title, description, null);
        newVideo.setWhoLikedList(new ArrayList<>());
        newVideo.setPublisher(MainActivity.currentUser.getUsername()); // Replace with actual user
        if (videoUri != null) {
            File videoFile = FileUtils.getFileFromUri(this, videoUri, "video.mp4");
            newVideo.setVideoFile(videoFile);
            if (thumbPic != null) {
                try {
                    newVideo.setImageFile(FileUtils.bitmapToFile(this, thumbPic));
                } catch (IOException e) {
                    buttonSubmitVideo.setEnabled(true);
                    throw new RuntimeException(e);
                }
            }
        }
        vidViewModel.add(MainActivity.currentUser.getId(), newVideo).observe(this, apiResponse -> {
            if (apiResponse.isSuccessful()) {
                Toast.makeText(this, getString(R.string.videoAdded), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                buttonSubmitVideo.setEnabled(true);
                finish();
            } else {
                buttonSubmitVideo.setEnabled(true);
                // add here specific data............................................
                switch (apiResponse.getCode()) {
                    case 400:
                        Toast.makeText(this, "Bad Request", Toast.LENGTH_SHORT).show();
                        break;
                    case 401:
                        Toast.makeText(this, "Unauthorized", Toast.LENGTH_SHORT).show();
                        break;
                    case 500:
                        Toast.makeText(this, "Server Error", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(this, "Unknown Error", Toast.LENGTH_SHORT).show();
                        break;
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_VIDEO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videoUri = data.getData();
            thumbPic = createVideoThumbnail(videoUri.getPath());
            videoView.setVideoURI(videoUri);
            videoView.start();
        } else if (requestCode == REQUEST_CODE_PICK_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            thumbnailUri = data.getData();
            try {
                thumbPic = MediaStore.Images.Media.getBitmap(getContentResolver(), thumbnailUri);
                thumbnailView.setVisibility(View.VISIBLE);
                thumbnailView.setImageBitmap(thumbPic);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Bitmap createVideoThumbnail(String filePath) {
        return ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, PICK_CAMERA_REQUEST);
            } else {
                Toast.makeText(this, getString(R.string.CameraPermissionDenied), Toast.LENGTH_SHORT).show();
            }
        }
    }
}