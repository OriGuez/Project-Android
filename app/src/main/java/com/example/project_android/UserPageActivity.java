package com.example.project_android;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_android.adapters.VideoAdapter;
import com.example.project_android.model.UserData;
import com.example.project_android.model.Video;
import com.example.project_android.utils.ImageLoader;
import com.example.project_android.viewModel.UsersViewModel;
import com.example.project_android.viewModel.VideosViewModel;

import java.util.ArrayList;
import java.util.List;

public class UserPageActivity extends AppCompatActivity {
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
    private UserData pageUser;
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

        //updatePageUser();
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


    private void updatePageUser() {
        if (pageUser != null) {
            // Populate the UI with the current user data
            //profileImageView.setImageBitmap(pageUser.getImage());
            channelNameTextView.setText(pageUser.getChannelName());
            String username= pageUser.getUsername();
            channelNameTextView.setText(pageUser.getChannelName());
            usernameTextView.setText("@" + username);
            if (profileImageView != null){
                    String baseUrl = MyApplication.getContext().getString(R.string.BaseUrl);
                    String profilePicPath = pageUser.getImageURI();
                    if (profilePicPath != null)
                        profilePicPath = profilePicPath.substring(1);
                    String profileImageUrl = baseUrl + profilePicPath;
                    ImageLoader.loadImage(profileImageUrl, profileImageView);
            }

            // Retrieve user's videos
            //userVideos = getUserVideos(pageUser.getUsername());
        }


    }

    private void initViews() {
        profileImageView = findViewById(R.id.publisherProfilePic);
        usernameTextView = findViewById(R.id.usernameTextView);
        channelNameTextView = findViewById(R.id.channelNameTextView);
        editUsernameEditText = findViewById(R.id.editUsernameEditText);
        editChannelNameEditText = findViewById(R.id.editChannelNameEditText);
        editProfileButton = findViewById(R.id.editProfileButton);
        saveProfileButton = findViewById(R.id.saveProfileButton);
        videoRecyclerView = findViewById(R.id.recyclerView);
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
}
