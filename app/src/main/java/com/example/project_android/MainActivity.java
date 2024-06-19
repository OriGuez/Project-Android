package com.example.project_android;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.graphics.PorterDuff;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static UserData currentUser;
    public static List<UserData> userDataList;
    public static List<Video> videoList;

    private RecyclerView recyclerView;
    private SearchView searchView;
    private VideoAdapter adapter;
    private ImageButton loginButton;
    private ImageButton addVideoButton;
    private ImageButton btnToggleDark;
    private ShapeableImageView profilePic;
    private ImageView youtubeLogo;

    private androidx.appcompat.widget.Toolbar topMenu;
    private androidx.appcompat.widget.Toolbar bottomToolbar;
    private androidx.coordinatorlayout.widget.CoordinatorLayout mainLayout;
    public static boolean isDarkMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoList = loadVideoData();
        setContentView(R.layout.activity_main);

        // Initialize views
        searchView = findViewById(R.id.searchView);
        youtubeLogo = findViewById(R.id.youtubeLogo);
        applySearchViewColors(searchView, isDarkMode);

        // Set SearchView listeners to hide/show the logo
        searchView.setOnSearchClickListener(v -> youtubeLogo.setVisibility(View.GONE));
        searchView.setOnCloseListener(() -> {
            youtubeLogo.setVisibility(View.VISIBLE);
            return false;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });

        // Request permission to read external storage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        btnToggleDark = findViewById(R.id.btnToggleDark);
        loginButton = findViewById(R.id.LoginMe);
        addVideoButton = findViewById(R.id.buttonAddVideo);
        mainLayout = findViewById(R.id.main);
        topMenu = findViewById(R.id.topMenu);
        bottomToolbar = findViewById(R.id.bottomToolbar);
        recyclerView = findViewById(R.id.recyclerViewVideos);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new VideoAdapter(this, videoList, "Main");
        recyclerView.setAdapter(adapter);

        // Set up profile picture
        profilePic = findViewById(R.id.publisherProfilePic);
        loggedVisibilityLogic();

        // Set OnClickListener for login button
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivityOri.class);
            startActivity(intent);
        });

        btnToggleDark.setOnClickListener(v -> {
            // Toggle dark mode state
            isDarkMode = !isDarkMode;

            // Change background colors
            if (isDarkMode) {
                mainLayout.setBackgroundColor(Color.DKGRAY);
                topMenu.setBackgroundColor(Color.DKGRAY);
                bottomToolbar.setBackgroundColor(Color.DKGRAY);
                btnToggleDark.setImageResource(R.drawable.light_mode);
            } else {
                mainLayout.setBackgroundColor(Color.WHITE);
                topMenu.setBackgroundColor(Color.WHITE);
                bottomToolbar.setBackgroundColor(Color.WHITE);
                btnToggleDark.setImageResource(R.drawable.dark_mode);
            }

            // Change SearchView colors based on the current mode
            applySearchViewColors(searchView, isDarkMode);

            // Notify adapter if necessary (depends on your adapter logic)
            adapter.notifyDataSetChanged();
        });

        // Set OnClickListener for add video button
        addVideoButton.setOnClickListener(v -> {
            if (currentUser != null) {
                Intent intent = new Intent(MainActivity.this, AddVideo.class);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "You must be logged in to add a video.", Toast.LENGTH_SHORT).show();
            }
        });

        // Example: Click Listener for publisherProfilePic (for demonstration purpose, you might have implemented in xml)
        findViewById(R.id.publisherProfilePic).setOnClickListener(v -> {
            if (currentUser != null) {
                showPopupMenu(v);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loggedVisibilityLogic();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    // Method to load video data
    private List<Video> loadVideoData() {
        List<Video> videos = new ArrayList<>();
        String jsonString = JsonUtils.loadJSONFromAsset(this, "vidDB.json");
        if (jsonString != null) {
            Gson gson = new Gson();
            java.lang.reflect.Type videoListType = new TypeToken<List<Video>>() {}.getType();
            videos = gson.fromJson(jsonString, videoListType);
        }
        return videos;
    }

    private void loggedVisibilityLogic() {
        // Check if the user is logged in and adjust visibility of buttons
        if (currentUser != null) {
            loginButton.setVisibility(View.GONE);
            addVideoButton.setVisibility(View.VISIBLE);
            if (profilePic != null) {
                // Assuming currentUser.getImage() returns Bitmap
                Bitmap userImage = currentUser.getImage();
                if (userImage != null) {
                    profilePic.setImageBitmap(userImage);
                } else {
                    profilePic.setImageResource(R.drawable.ic_def_user);
                }
            }
        } else {
            loginButton.setVisibility(View.VISIBLE);
            addVideoButton.setVisibility(View.GONE);
            if (profilePic != null) {
                profilePic.setImageResource(R.drawable.ic_def_user);
            }
        }
    }

    public void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);

        // Create menu items programmatically
        popupMenu.getMenu().add("Profile"); // Add Profile item
        popupMenu.getMenu().add("Settings"); // Add Settings item
        popupMenu.getMenu().add("Logout"); // Add Logout item

        // Set listener for menu item clicks
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getTitle().toString()) {
                case "Logout":
                    // Handle logout action
                    currentUser = null; // Logout the user
                    loggedVisibilityLogic(); // Update UI visibility
                    return true;
                // Handle other menu items as needed
                default:
                    return false;
            }
        });

        popupMenu.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userDataList != null) {
            userDataList.clear();
        }
    }

    // Method to perform search based on query
    private void performSearch(String query) {
        // Filter your videoList based on the query and update RecyclerView
        List<Video> filteredList = filter(videoList, query);
        adapter.filterList(query);
    }

    // Method to filter videoList based on search query
    private List<Video> filter(List<Video> videos, String query) {
        query = query.toLowerCase().trim();
        List<Video> filteredList = new ArrayList<>();
        for (Video video : videos) {
            // Perform filtering based on your criteria (e.g., title, description)
            if (video.getTitle().toLowerCase().contains(query)) {
                filteredList.add(video);
            }
        }
        return filteredList;
    }

    private void applySearchViewColors(SearchView searchView, boolean isDarkMode) {
        int textColor = ContextCompat.getColor(this, isDarkMode ? R.color.search_text_color_dark : R.color.search_text_color_light);
        int iconColor = ContextCompat.getColor(this, isDarkMode ? R.color.white : R.color.black);

        // Change search icon color
        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        if (searchIcon != null) {
            searchIcon.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
        }

        // Change close icon color
        ImageView closeIcon = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        if (closeIcon != null) {
            closeIcon.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
        }

        // Change search text color
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(textColor);
        searchEditText.setHintTextColor(textColor);
    }
}
