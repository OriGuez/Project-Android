package com.example.project_android;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.lifecycle.ViewModelProvider;
import com.example.project_android.model.UserData;
import com.example.project_android.utils.FileUtils;
import com.example.project_android.viewModel.UsersViewModel;
import java.io.File;
import java.io.IOException;

public class RegistrationActivity extends AppCompatActivity {

    private UsersViewModel viewModel;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_CAMERA_REQUEST = 2;
    private static final int REQUEST_PERMISSION = 100;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText channelNameEditText;
    private ImageButton uploadFromGalleryButton;
    private ImageButton takePictureCameraButton;
    private ImageView profilePictureImageView;
    private Button registerButton;
    private Bitmap selectedProfilePicture;
    private Uri imgURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        setContentView(R.layout.activity_registration);
        // Bind views
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirmPassword);
        channelNameEditText = findViewById(R.id.channelName);
        profilePictureImageView = findViewById(R.id.profilePicture);
        registerButton = findViewById(R.id.registerButton);
        uploadFromGalleryButton = findViewById(R.id.uploadProfilePicImageButton);
        takePictureCameraButton = findViewById(R.id.takePicture);
        // TextWatcher to passwordEditText
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isValidPassword(s.toString())) {
                    passwordEditText.setError(getString(R.string.invalidPassword));
                } else {
                    passwordEditText.setError(null);
                }
            }
        });
        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validatePasswordsMatch();
            }
        });
        // Listeners
        uploadFromGalleryButton.setOnClickListener(v -> openImagePicker());
        takePictureCameraButton.setOnClickListener(v -> openCamera());
        registerButton.setOnClickListener(v -> registerUser());
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
        } else if (requestCode == PICK_CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
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
            // Handle password error
            confirmPasswordEditText.setError(getString(R.string.password_dont_match));
            return;
        }
        if (channelName.isEmpty()) {
            channelNameEditText.setError(getString(R.string.channelNameRequired));
            return;
        }
        if (selectedProfilePicture == null) {
            Toast.makeText(this, getString(R.string.profilePicRequired), Toast.LENGTH_SHORT).show();
            return;
        }
        if (username.isEmpty()) {
            usernameEditText.setError(getString(R.string.usernameRequired));
            return;
        }
        registerButton.setEnabled(false);
        UserData newUser = new UserData(username, password, channelName, selectedProfilePicture);
        try {
            File imageFile = FileUtils.bitmapToFile(this, selectedProfilePicture);
            newUser.setImageFile(imageFile);
        } catch (IOException e) {
            registerButton.setEnabled(true);
            throw new RuntimeException(e);
        }

        viewModel.add(newUser).observe(this, apiResponse -> {
            if (apiResponse.isSuccessful()) {
                registerButton.setEnabled(true);
                Toast.makeText(this, getString(R.string.userAdded), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                registerButton.setEnabled(true);
                switch (apiResponse.getCode()) {
                    case 400: {
                        Toast.makeText(RegistrationActivity.this, "Bad Request", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 401: {
                        Toast.makeText(RegistrationActivity.this, "Unauthorized", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 500: {
                        Toast.makeText(RegistrationActivity.this, getString(R.string.serverError), Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 409: {
                        Toast.makeText(RegistrationActivity.this, getString(R.string.usernameExists), Toast.LENGTH_SHORT).show();
                        break;
                    }
                    default:
                        Toast.makeText(RegistrationActivity.this, R.string.unknownError, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void validatePasswordsMatch() {
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        if (!confirmPassword.equals(password)) {
            confirmPasswordEditText.setError(getString(R.string.password_dont_match));
        } else {
            confirmPasswordEditText.setError(null);
        }
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