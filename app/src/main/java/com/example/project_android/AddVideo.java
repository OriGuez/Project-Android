package com.example.project_android;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import android.Manifest;

public class AddVideo extends AppCompatActivity {
    private EditText editVideoTitle;
    private EditText editVideoDescription;
    private Button buttonUploadVideo;
    private Button buttonUploadThumbnail;
    private Button buttonSubmitVideo;
    private Uri videoUri;
    private Uri thumbnailUri;
    private VideoView videoView;
    private Bitmap thumbPic;
    private static final int REQUEST_CODE_PICK_VIDEO = 10;
    private static final int REQUEST_CODE_PICK_PICTURE = 11;

    private static final int REQUEST_CODE_PERMISSIONS = 2;

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
                // Check for permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSIONS);
        } else {
            Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
        }
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


    // Activity result launcher for thumbnail picking
    private final ActivityResultLauncher<Intent> thumbnailPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    thumbnailUri = result.getData().getData();
                    try {
                        thumbPic = MediaStore.Images.Media.getBitmap(getContentResolver(), thumbnailUri);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    //selectedProfilePicture = MediaStore.Images.Media.getBitmap(getContentResolver(), imgURI);
                    Toast.makeText(this, "Thumbnail Selected", Toast.LENGTH_SHORT).show();
                }
            });

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

    private void submitVideo() {
        String title = editVideoTitle.getText().toString().trim();
        String description = editVideoDescription.getText().toString().trim();
        if (title.isEmpty() || description.isEmpty() || videoUri == null || thumbnailUri == null) {
            Toast.makeText(this, "Please fill all fields and upload video and thumbnail.", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            // Encode video to Base64
            String base64Video = encodeVideoToBase64(videoUri);

            // Create a new Video object
            Video newVideo = new Video();
            newVideo.setWhoLikedList(new ArrayList<>());
            newVideo.setComments(new ArrayList<>());
            newVideo.setTitle(title);
            newVideo.setVidID(generateUniqueID());
            newVideo.setDescription(description);
            newVideo.setBase64Video(base64Video);
            newVideo.setThumbnailUrl(thumbnailUri.toString());
            newVideo.setPublisher(MainActivity.currentUser.getUsername()); // Replace with actual user
            newVideo.setUpload_date("Now"); // Replace with current date
            // Add new video to video list
            MainActivity.videoList.add(newVideo);
            if (thumbPic !=null)
                newVideo.setThumbnailPicture(thumbPic);
            // Inform user and finish activity
            Toast.makeText(this, "Video Added Successfully", Toast.LENGTH_SHORT).show();

            finish();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to encode video", Toast.LENGTH_SHORT).show();
        }
    }

    private void playVideo() {
        videoView.setVideoURI(videoUri);
        videoView.start();
    }

    private String encodeVideoToBase64(Uri videoUri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(videoUri);
        byte[] byteArray = getBytes(inputStream);
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public String generateUniqueID() {
        Set<String> existingIDs = new HashSet<>();
        for (Video video : MainActivity.videoList) {
            existingIDs.add(video.getVidID());
        }
        String newID;
        do {
            newID = UUID.randomUUID().toString();
        } while (existingIDs.contains(newID));
        return newID;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_VIDEO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videoUri = data.getData();
            videoView.setVideoURI(videoUri);
            videoView.start();
        }
        else if (requestCode == REQUEST_CODE_PICK_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            thumbnailUri = data.getData();
            try {
                thumbPic = MediaStore.Images.Media.getBitmap(getContentResolver(), thumbnailUri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}