package com.example.project_android;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_android.adapters.VideoAdapter;
import com.example.project_android.model.UserData;
import com.example.project_android.model.Video;

import java.util.ArrayList;
import java.util.List;

public class UserPageActivity extends AppCompatActivity {
    private RecyclerView videoRecyclerView;
    private VideoAdapter videoAdapter;
    private ImageView profileImageView;
    private TextView usernameTextView;
    private TextView channelNameTextView;
    private EditText editUsernameEditText;
    private EditText editChannelNameEditText;
    private Button editProfileButton;
    private Button saveProfileButton;
    private UserData currentUser;
    private List<Video> userVideos;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        profileImageView = findViewById(R.id.publisherProfilePic);
        usernameTextView = findViewById(R.id.usernameTextView);
        channelNameTextView = findViewById(R.id.channelNameTextView);
        editUsernameEditText = findViewById(R.id.editUsernameEditText);
        editChannelNameEditText = findViewById(R.id.editChannelNameEditText);
        editProfileButton = findViewById(R.id.editProfileButton);
        saveProfileButton = findViewById(R.id.saveProfileButton);
        videoRecyclerView = findViewById(R.id.recyclerView);

        // Retrieve the current user data
        String username = getIntent().getStringExtra("username");
        for (UserData user : MainActivity.userDataList) {
            if (user.getUsername().equals(username)) {
                currentUser = user;
                break;
            }
        }

        if (currentUser != null) {
            // Populate the UI with the current user data
            profileImageView.setImageBitmap(currentUser.getImage());
            usernameTextView.setText(currentUser.getUsername());
            channelNameTextView.setText(currentUser.getChannelName());

            // Retrieve user's videos
            userVideos = getUserVideos(currentUser.getUsername());
        }

        videoRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        videoAdapter = new VideoAdapter(this, userVideos, "User");
        videoRecyclerView.setAdapter(videoAdapter);

        editProfileButton.setOnClickListener(v -> enterEditMode());
        saveProfileButton.setOnClickListener(v -> saveChanges());
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
    }

    private void saveChanges() {
        String newUsername = editUsernameEditText.getText().toString().trim();
        String newChannelName = editChannelNameEditText.getText().toString().trim();

        if (newUsername.isEmpty() || newChannelName.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        currentUser.setUsername(newUsername);
        currentUser.setChannelName(newChannelName);

        usernameTextView.setText(newUsername);
        channelNameTextView.setText(newChannelName);

        editUsernameEditText.setVisibility(View.GONE);
        editChannelNameEditText.setVisibility(View.GONE);
        saveProfileButton.setVisibility(View.GONE);
        usernameTextView.setVisibility(View.VISIBLE);
        channelNameTextView.setVisibility(View.VISIBLE);
        editProfileButton.setVisibility(View.VISIBLE);

        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
    }

    private List<Video> getUserVideos(String username) {
        // Retrieve videos uploaded by the user
        List<Video> videos = new ArrayList<>();
        for (Video video : MainActivity.videoList) {
//            if (video.getPublisher().equals(username)) {
                videos.add(video);
//            }
        }
        return videos;
    }
}
