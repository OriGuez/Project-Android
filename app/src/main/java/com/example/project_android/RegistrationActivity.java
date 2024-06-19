package com.example.project_android;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

public class RegistrationActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_CAMERA_REQUEST = 2;
    private static final int REQUEST_PERMISSION = 100;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText channelNameEditText;
    private ImageButton uploadFromGalleryButton;
    private Button takePhotoCameraButton;
    private ImageButton takePictureCameraButton;
    private ImageView profilePictureImageView;
    private Button registerButton;
    private Bitmap selectedProfilePicture;
    private Uri imgURI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        // Bind views
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirmPassword);
        channelNameEditText = findViewById(R.id.channelName);
        profilePictureImageView = findViewById(R.id.profilePicture);
        registerButton = findViewById(R.id.registerButton);
        uploadFromGalleryButton = findViewById(R.id.uploadProfilePicImageButton);
        takePictureCameraButton=findViewById(R.id.takePicture);
        // Set up listeners
        uploadFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });
        takePictureCameraButton.setOnClickListener(v -> {
            openCamera();
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
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
        }
        else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, PICK_CAMERA_REQUEST);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgURI = data.getData();
            try {
                selectedProfilePicture = MediaStore.Images.Media.getBitmap(getContentResolver(), imgURI);
                profilePictureImageView.setVisibility(View.VISIBLE);
                profilePictureImageView.setImageBitmap(selectedProfilePicture);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == PICK_CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedProfilePicture = (Bitmap) data.getExtras().get("data");
            profilePictureImageView.setVisibility(View.VISIBLE);
            profilePictureImageView.setImageBitmap(selectedProfilePicture);
        }
    }

    private boolean isValidPassword(String password) {
        // Password must be at least 8 characters long and contain at least one non-digit character
        return password.length() >= 8 && password.matches(".*\\D.*");
    }

    private void registerUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String channelName = channelNameEditText.getText().toString().trim();
        if (!isValidPassword(password)) {
            // Handle password validation error
            passwordEditText.setError(getString(R.string.invalidPassword));
            return;
        }
        // Check if password and confirm password match
        if (!password.equals(confirmPassword)) {
            // Handle password mismatch error
            confirmPasswordEditText.setError(getString(R.string.password_dont_match));
            return;
        }
        if (channelName.isEmpty()) {
            channelNameEditText.setError(getString(R.string.channelNameRequired));
            return;
        }
        if (selectedProfilePicture == null) {
            //  show a Toast message
            Toast.makeText(this, getString(R.string.profilePicRequired), Toast.LENGTH_SHORT).show();
            return;
        }
        if (username.isEmpty()) {
            usernameEditText.setError(getString(R.string.usernameRequired));
            return;
        }

        UserData user = new UserData(username, password, channelName, selectedProfilePicture);
        user.setImageURI(imgURI);
        MainActivity.userDataList.add(user);
        Intent intent = new Intent(this, LoginActivityOri.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, PICK_CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


}