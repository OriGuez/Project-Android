package com.example.project_android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_android.adapters.VideoAdapter;
import com.example.project_android.model.UserData;
import com.example.project_android.model.Video;
import com.example.project_android.utils.FileUtils;
import com.example.project_android.utils.ImageLoader;
import com.example.project_android.viewModel.UsersViewModel;
import com.example.project_android.viewModel.VideosViewModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserPageActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_CAMERA_REQUEST = 2;
    private static final int REQUEST_PERMISSION = 100;
    private UsersViewModel usersViewModel;
    private VideosViewModel videosViewModel;
    private RecyclerView videoRecyclerView;
    private VideoAdapter videoAdapter;
    private ImageView profileImageView;
    private TextView usernameTextView;
    private TextView channelNameTextView;
    private EditText editUsernameEditText;
    private EditText editChannelNameEditText;
    private Button editProfileButton;
    private Button saveProfileButton;
    private ImageButton uploadFromGalleryButton;
    private ImageButton takePictureCameraButton;
    private ImageButton toggleImageButtons;
    private UserData pageUser;
    private Uri imgURI;
    private Bitmap selectedProfilePicture;

    private List<Video> userVideos = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        videosViewModel = new ViewModelProvider(this).get(VideosViewModel.class);
        initViews();

        // Retrieve the current user data
        String userID = getIntent().getStringExtra("userID");
        usersViewModel.get(userID).observe(this, user -> {
            if (user != null) {
                pageUser = user;
                updatePageUser();

                videosViewModel.getUserVideos(userID).observe(this, videos -> {
                    if (videos != null) {
                        videoAdapter.updateVideoList(videos);
                    }
                });
            }
        });

        videoRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        videoAdapter = new VideoAdapter(this, userVideos, "User");
        videoRecyclerView.setAdapter(videoAdapter);

        editProfileButton.setOnClickListener(v -> enterEditMode());
        saveProfileButton.setOnClickListener(v -> saveChanges());
        uploadFromGalleryButton.setOnClickListener(v -> openImagePicker());
        takePictureCameraButton.setOnClickListener(v -> openCamera());
        toggleImageButtons.setOnClickListener(v -> toggleButtonsVisibility());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgURI = data.getData();
            try {
                selectedProfilePicture = MediaStore.Images.Media.getBitmap(getContentResolver(), imgURI);
                profileImageView.setVisibility(View.VISIBLE);
                profileImageView.setImageBitmap(selectedProfilePicture);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedProfilePicture = (Bitmap) data.getExtras().get("data");
            profileImageView.setVisibility(View.VISIBLE);
            profileImageView.setImageBitmap(selectedProfilePicture);
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        // Check for camera permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, PICK_CAMERA_REQUEST);
        }
    }

    private void enterEditMode() {
        editUsernameEditText.setVisibility(View.VISIBLE);
        editChannelNameEditText.setVisibility(View.VISIBLE);
        saveProfileButton.setVisibility(View.VISIBLE);
        editUsernameEditText.setText(usernameTextView.getText());
        editChannelNameEditText.setText(channelNameTextView.getText());
        usernameTextView.setVisibility(View.GONE);
        channelNameTextView.setVisibility(View.GONE);
        editProfileButton.setVisibility(View.GONE);
        toggleImageButtons.setVisibility(View.VISIBLE);
    }

    private void toggleButtonsVisibility() {
        if (uploadFromGalleryButton.getVisibility() == View.VISIBLE && takePictureCameraButton.getVisibility() == View.VISIBLE) {
            uploadFromGalleryButton.setVisibility(View.GONE);
            takePictureCameraButton.setVisibility(View.GONE);
        } else {
            uploadFromGalleryButton.setVisibility(View.VISIBLE);
            takePictureCameraButton.setVisibility(View.VISIBLE);
        }
    }

    private void updatePageUser() {
        if (pageUser != null) {
            channelNameTextView.setText(pageUser.getChannelName());
            usernameTextView.setText(pageUser.getUsername());
            if (profileImageView != null) {
                String baseUrl = MyApplication.getContext().getString(R.string.BaseUrl);
                String profilePicPath = pageUser.getImageURI();
                if (profilePicPath != null)
                    profilePicPath = profilePicPath.substring(1);
                String profileImageUrl = baseUrl + profilePicPath;
                ImageLoader.loadImage(profileImageUrl, profileImageView);
            }
        }
    }


    private void initViews() {
        profileImageView = findViewById(R.id.publisherProfilePic);
        usernameTextView = findViewById(R.id.usernameTextView);
        channelNameTextView = findViewById(R.id.channelNameTextView);
        editChannelNameEditText = findViewById(R.id.editChannelNameEditText);
        editUsernameEditText = findViewById(R.id.editUsernameEditText);
        editProfileButton = findViewById(R.id.editProfileButton);
        saveProfileButton = findViewById(R.id.saveProfileButton);
        videoRecyclerView = findViewById(R.id.recyclerView);
        uploadFromGalleryButton = findViewById(R.id.uploadProfilePicImageButton);
        takePictureCameraButton = findViewById(R.id.takePicture);
        toggleImageButtons = findViewById(R.id.toggleImageButtons);
    }

    private void saveChanges() {
        String newUsername = editUsernameEditText.getText().toString().trim();
        String newChannelName = editChannelNameEditText.getText().toString().trim();

        if (newUsername.isEmpty() || newChannelName.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        pageUser.setUsername(newUsername);
        pageUser.setChannelName(newChannelName);

        if (selectedProfilePicture != null) {
            try {
                File imageFile = FileUtils.bitmapToFile(this, selectedProfilePicture);
                pageUser.setImageFile(imageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        usersViewModel.update(pageUser).observe(this, response -> {
            if (response != null && response.isSuccessful()) {
                usernameTextView.setText(newUsername);
                channelNameTextView.setText(newChannelName);
                updatePageUser();

                Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });

        uploadFromGalleryButton.setVisibility(View.GONE);
        takePictureCameraButton.setVisibility(View.GONE);
        editUsernameEditText.setVisibility(View.GONE);
        editChannelNameEditText.setVisibility(View.GONE);
        saveProfileButton.setVisibility(View.GONE);
        usernameTextView.setVisibility(View.VISIBLE);
        channelNameTextView.setVisibility(View.VISIBLE);
        editProfileButton.setVisibility(View.VISIBLE);
        toggleImageButtons.setVisibility(View.GONE);
    }

}
