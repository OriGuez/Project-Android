package com.example.project_android;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.example.project_android.model.Video;
import com.example.project_android.utils.FileUtils;
import com.example.project_android.viewModel.VideosViewModel;

public class EditVideo extends AppCompatActivity {
    private VideosViewModel videosViewModel;
    private EditText editVideoTitle;
    private EditText editVideoDescription;
    private ImageView thumbnailView;
    private Button buttonUploadThumbnail;
    private Button buttonSaveChanges;
    private Button deleteVideoButton;
    private Uri thumbnailUri;
    private Bitmap thumbPic;
    private Video currentVideo;

    private static final int REQUEST_CODE_PICK_VIDEO = 10;
    private static final int REQUEST_CODE_PICK_PICTURE = 11;
    private static final int PICK_CAMERA_REQUEST = 12;

    private static final int REQUEST_PERMISSION = 100;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_video);
        videosViewModel = new ViewModelProvider(this).get(VideosViewModel.class);
        editVideoTitle = findViewById(R.id.editVideoTitle);
        editVideoDescription = findViewById(R.id.editVideoDescription);
        buttonUploadThumbnail = findViewById(R.id.buttonUploadThumbnail);
        buttonSaveChanges = findViewById(R.id.saveEditVidButton);
        deleteVideoButton = findViewById(R.id.deleteVideoButton);
        thumbnailView = findViewById(R.id.thumbnailPrev);
        // Retrieve the video data from intent
        String videoID = getIntent().getStringExtra("videoID");
        if (videoID == null) {
            Toast.makeText(this, getString(R.string.MissingVideoID), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        videosViewModel.get(videoID).observe(this, video -> {
            // Update the UI with the new video list
            if (video != null) {
                currentVideo = video;
                updateFields();
            } else {
                currentVideo = null;
            }
        });

        updateFields();

        buttonUploadThumbnail.setOnClickListener(v -> openThumbnailPicker());
        buttonSaveChanges.setOnClickListener(v -> saveChanges());
        deleteVideoButton.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        return BitmapFactory.decodeStream(inputStream);
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
        videosViewModel.delete(currentVideo.getPublisher(), currentVideo.getVidID()).observe(this, resp -> {
            if (resp.isSuccessful()) {
                Toast.makeText(this, getString(R.string.videoDeleted), Toast.LENGTH_SHORT).show();
                UserPageActivity.shouldRefresh = true;
                MainActivity.shouldRefresh = true;
                finish();
            } else {
                Toast.makeText(this, getString(R.string.videoDeletedFailed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openThumbnailPicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_CODE_PICK_PICTURE);
    }

    private void saveChanges() {
        String title = editVideoTitle.getText().toString().trim();
        String description = editVideoDescription.getText().toString().trim();
        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, getString(R.string.fillAll), Toast.LENGTH_LONG).show();
            return;
        }

        File imageFile = null;
        if (thumbPic != null) {
            try {
                imageFile = FileUtils.bitmapToFile(this, thumbPic);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Video updatedVidData = new Video(title, description, imageFile);

        videosViewModel.update(currentVideo.getPublisher(), currentVideo.getVidID(), updatedVidData).observe(this, resp -> {
            if (resp.isSuccessful()) {
                Toast.makeText(this, getString(R.string.videoUpdated), Toast.LENGTH_SHORT).show();
                UserPageActivity.shouldRefresh = true;
                MainActivity.shouldRefresh = true;
                finish();
            } else {
                Toast.makeText(this, getString(R.string.videoUpdateFailed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            thumbnailUri = data.getData();
            try {
                thumbPic = MediaStore.Images.Media.getBitmap(getContentResolver(), thumbnailUri);
                thumbnailView.setImageBitmap(thumbPic);
                thumbnailView.setVisibility(View.VISIBLE); // Ensure the thumbnail is visible
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission approved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void updateFields() {
        if (currentVideo != null) {
            editVideoTitle.setText(currentVideo.getTitle());
            editVideoDescription.setText(currentVideo.getDescription());
            String thumbnailUrl = currentVideo.getThumbnail();
            if (thumbnailUrl != null) {
                thumbnailUri = Uri.parse(thumbnailUrl);
                try {
                    thumbPic = getBitmapFromUri(thumbnailUri);
                    thumbnailView.setImageBitmap(thumbPic);
                    thumbnailView.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
