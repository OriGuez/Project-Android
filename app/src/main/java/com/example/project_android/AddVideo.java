package com.example.project_android;

import static java.lang.Integer.MAX_VALUE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.net.Uri;
import android.Manifest;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.provider.MediaStore;
import android.widget.VideoView;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import java.io.IOException;

import java.util.Random;

public class AddVideo extends AppCompatActivity {
    private Button addVid;
    private VideoView tryVV;
    private static final int REQUEST_CODE_PICK_VIDEO = 1;
    private static final int REQUEST_CODE_PERMISSIONS = 2;
    private Uri videoURI;

    private EditText editVideoTitle;
    private EditText editVideoDescription;
    private Button buttonUploadVideo;
    private Button buttonUploadThumbnail;
    private Button buttonSubmitVideo;
    private Uri videoUri;
    private Uri thumbnailUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video);
        addVid = findViewById(R.id.uploadButtonAddVid);
        tryVV=findViewById(R.id.vidvie);

//        // Check for permissions
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                    REQUEST_CODE_PERMISSIONS);
//        } else {
//            Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
//        }


//         addVid.setOnClickListener(v -> {
//             pickVideo();
//         });


    }

    @Override
//     public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//         super.onRequestPermissionsResult(requestCode, permissions, grantResults); // Call the superclass implementation
//         if (requestCode == REQUEST_CODE_PERMISSIONS) {
//             if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                 //loadLastSavedVideo();
//             } else {
//                 Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
//             }
//         }
//     }

//     public void pickVideo() {
//         Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//         intent.setType("video/*");
//         startActivityForResult(intent, REQUEST_CODE_PICK_VIDEO);
//     }

    @Override
//     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//         super.onActivityResult(requestCode, resultCode, data);
//         if (requestCode == REQUEST_CODE_PICK_VIDEO && resultCode == RESULT_OK && data != null) {
//              videoURI = data.getData();
//             if (videoURI != null) {
//                 MainActivity.publicURI=videoURI;
//                 tryVV.setVideoURI(videoURI);
//                 tryVV.start();
//                //save the URI
            }
        }

        editVideoTitle = findViewById(R.id.editVideoTitle);
        editVideoDescription = findViewById(R.id.editVideoDescription);
        buttonUploadVideo = findViewById(R.id.buttonUploadVideo);
        buttonUploadThumbnail = findViewById(R.id.buttonUploadThumbnail);
        buttonSubmitVideo = findViewById(R.id.buttonSubmitVideo);

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
                }
            });

    // Activity result launcher for thumbnail picking
    private final ActivityResultLauncher<Intent> thumbnailPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    thumbnailUri = result.getData().getData();
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
        newVideo.setDescription(description);
        newVideo.setUrl(videoUri.toString());
        newVideo.setThumbnailUrl(thumbnailUri.toString());
        newVideo.setPublisher("CurrentUser"); // Replace with actual user
        newVideo.setUpload_date("Now"); // Replace with current date

        // Add new video to video list
        MainActivity.videoList.add(newVideo);

        // Inform user and finish activity
        Toast.makeText(this, "Video Added Successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
