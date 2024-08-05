package com.example.project_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import android.graphics.PorterDuff;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project_android.adapters.VideoAdapter;
import com.example.project_android.model.UserData;
import com.example.project_android.model.Video;
import com.example.project_android.utils.ImageLoader;
import com.example.project_android.viewModel.UsersViewModel;
import com.example.project_android.viewModel.VideosViewModel;
import com.google.android.material.imageview.ShapeableImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static MainActivity instance;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static boolean shouldRefresh = false;
    private SharedPreferences sharedPreferences;
    private VideosViewModel viewModel;
    private UsersViewModel usersViewModel;
    public static UserData currentUser;
    public static List<Video> videoList;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private ImageButton loginButton;
    private ImageButton addVideoButton;
    private ImageButton btnToggleDark;
    private ShapeableImageView profilePic;
    private ImageView youtubeLogo;
    private Toolbar topMenu;
    private Toolbar bottomToolbar;
    private VideoAdapter adapter;
    private androidx.coordinatorlayout.widget.CoordinatorLayout mainLayout;
    public static boolean isDarkMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        super.onCreate(savedInstanceState);
        videoList = new ArrayList<>();
        sharedPreferences = getApplicationContext().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        isDarkMode = sharedPreferences.getBoolean("isDarkMode", false); // Retrieve dark mode preference
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(VideosViewModel.class);
        viewModel.reload();
        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        // Initialize views
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        youtubeLogo = findViewById(R.id.youtubeLogo);
        applySearchViewColors(searchView, isDarkMode);
        // Set SearchView listeners to hide/show the logo
        searchView.setOnSearchClickListener(v -> youtubeLogo.setVisibility(View.GONE));
        searchView.setOnCloseListener(() -> {
            youtubeLogo.setVisibility(View.VISIBLE);
            return false;
        });
        currentUser = null;
        btnToggleDark = findViewById(R.id.btnToggleDark);
        loginButton = findViewById(R.id.LoginMe);
        addVideoButton = findViewById(R.id.buttonAddVideo);
        mainLayout = findViewById(R.id.main);
        topMenu = findViewById(R.id.topMenu);
        bottomToolbar = findViewById(R.id.bottomToolbar);
        recyclerView = findViewById(R.id.recyclerViewVideos);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        // Observe LiveData from ViewModel
        adapter = new VideoAdapter(this, videoList, "Main", false);
        // Observe LiveData from ViewModel
        loadVideos();
        recyclerView.setAdapter(adapter);
        // Set up profile picture
        profilePic = findViewById(R.id.publisherProfilePic);
        // Apply dark mode settings
        applyDarkModeSettings(isDarkMode);
        // Set OnClickListener for login button
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        btnToggleDark.setOnClickListener(v -> {
            // Toggle dark mode state
            isDarkMode = !isDarkMode;
            // Save the dark mode preference
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isDarkMode", isDarkMode);
            editor.apply();
            // Apply dark mode settings
            applyDarkModeSettings(isDarkMode);
        });
        // Set OnClickListener for add video button
        addVideoButton.setOnClickListener(v -> {
            if (currentUser != null) {
                Intent intent = new Intent(this, AddVideo.class);
                startActivity(intent);
            } else {
                loadUser();
                if (currentUser == null) {
                    Toast.makeText(this, getString(R.string.cantEnterAddVidPage), Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(this, AddVideo.class);
                    startActivity(intent);
                }
            }
        });
        // Click Listener for publisherProfilePic
        profilePic.setOnClickListener(v -> {
            showPopupMenu(v);
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("search_query", query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Perform your refresh operation here
            viewModel.reload();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loggedVisibilityLogic();
        if (searchView != null) {
            searchView.clearFocus();
            searchView.setQuery("", false);
        }
        if (shouldRefresh) {
            viewModel.reload();
            shouldRefresh = false;
        }
    }

    private void loggedVisibilityLogic() {
        String value = sharedPreferences.getString("token", "none");
        // Check if the user is logged in and adjust visibility of buttons
        if (value.equals("none")) {
            profilePic.setEnabled(false);
            //no logged User
            loginButton.setVisibility(View.VISIBLE);
            addVideoButton.setVisibility(View.GONE);
            if (profilePic != null) {
                if (isDarkMode) {
                    profilePic.setImageResource(R.drawable.user_dark);
                } else {
                    profilePic.setImageResource(R.drawable.user);
                }
            }
        } else {
            profilePic.setEnabled(true);
            loginButton.setVisibility(View.GONE);
            addVideoButton.setVisibility(View.VISIBLE);
            loadUser();
        }
    }

    public void showPopupMenu(View view) {
        final int MENU_MY_CHANNEL = Menu.FIRST;
        final int MENU_LOGOUT = Menu.FIRST + 1;
        Context wrapper = new ContextThemeWrapper(this, isDarkMode ? R.style.DarkPopupMenu : R.style.LightPopupMenu);
        PopupMenu popupMenu = new PopupMenu(wrapper, view);
        popupMenu.getMenu().add(Menu.NONE, MENU_MY_CHANNEL, Menu.NONE, getString(R.string.MyChannel)); // Add "My Channel" menu item with ID
        popupMenu.getMenu().add(Menu.NONE, MENU_LOGOUT, Menu.NONE, getString(R.string.SignOut)); // Add "Sign Out" menu item with ID
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case MENU_LOGOUT:
                    // Handle logout action
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("token");
                    editor.remove("username");
                    editor.putBoolean("isDarkMode", false);
                    editor.apply(); // Use commit() if you need synchronous removal
                    currentUser = null; // Logout the user
                    loggedVisibilityLogic(); // Update UI visibility
                    if (isDarkMode) {
                        applyDarkModeSettings(false);
                        isDarkMode = false;
                    }
                    return true;
                case MENU_MY_CHANNEL:
                    if (currentUser == null) {
                        loadUser();
                        if (currentUser == null) {
                            Toast.makeText(this, getString(R.string.cantEnterUserPage), Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    }
                    // Navigate to UserPageActivity
                    Intent intent = new Intent(MainActivity.this, UserPageActivity.class);
                    intent.putExtra("userID", currentUser.getId());
                    startActivity(intent);
                    return true;
                // Handle other menu items as needed
                default:
                    return false;
            }
        });
        popupMenu.show();
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

    private void loadVideos() {
        viewModel.get().observe(this, videos -> {
            // Update the UI with the new video list
            if (videos != null && !videos.isEmpty()) {
                adapter.updateVideoList(videos);
            } else {
                Log.e("MainActivity", "Video list is null");
            }
        });
    }

    private void loadUser() {
        String username = sharedPreferences.getString("username", "none");
        if (!username.equals("none")) {
            usersViewModel.getUserID(username).observe(this, id -> {
                if (id != null) {
                    usersViewModel.get(id.getUserID()).observe(this, user -> {
                        if (user != null) {
                            currentUser = user;
                            String baseUrl = MyApplication.getContext().getString(R.string.BaseUrl);
                            String profilePicPath = user.getImageURI();
                            if (profilePicPath != null)
                                profilePicPath = profilePicPath.substring(1);
                            String profileImageUrl = baseUrl + profilePicPath;
                            ImageLoader.loadImage(profileImageUrl, profilePic);
                        }
                    });
                } else {
                    Log.e("MainActivity", "id is null");
                }
            });
        }
    }

    public static MainActivity getInstance() {
        return instance;
    }

    private void updateTextColors(boolean isDarkMode) {
        int textColor = ContextCompat.getColor(this, isDarkMode ? android.R.color.white : android.R.color.black);
        int hintColor = ContextCompat.getColor(this, isDarkMode ? android.R.color.darker_gray : android.R.color.darker_gray);

        // Iterate through all the views and update text color if it's a TextView
        View root = mainLayout.getRootView();
        updateTextColorRecursive(root, textColor, hintColor);
    }

    private void updateTextColorRecursive(View view, int textColor, int hintColor) {
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(textColor);
            if (view instanceof EditText) {
                ((EditText) view).setHintTextColor(hintColor);
            }
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                updateTextColorRecursive(child, textColor, hintColor);
            }
        }
    }

    private void applyDarkModeSettings(boolean isDarkMode) {
        int backgroundColor = isDarkMode ? Color.DKGRAY : Color.WHITE;
        int textColor = isDarkMode ? Color.WHITE : Color.BLACK;
        mainLayout.setBackgroundColor(backgroundColor);
        topMenu.setBackgroundColor(backgroundColor);
        bottomToolbar.setBackgroundColor(backgroundColor);
        applySearchViewColors(searchView, isDarkMode);
        updateTextColors(isDarkMode);
        btnToggleDark.setBackgroundColor(Color.TRANSPARENT);
        loginButton.setBackgroundColor(Color.TRANSPARENT);
        addVideoButton.setBackgroundColor(Color.TRANSPARENT);
        if (isDarkMode) {
            addVideoButton.setImageResource(R.drawable.add_video_dark);
            btnToggleDark.setImageResource(R.drawable.light_mode);
            loginButton.setImageResource(R.drawable.log_in_dark);
            if (profilePic != null && currentUser == null) {
                profilePic.setImageResource(R.drawable.user_dark);
            }
        } else {
            addVideoButton.setImageResource(R.drawable.add_video);
            btnToggleDark.setImageResource(R.drawable.dark_mode);
            loginButton.setImageResource(R.drawable.log_in);
            if (profilePic != null && currentUser == null) {
                profilePic.setImageResource(R.drawable.user);
            }
        }
    }
}
