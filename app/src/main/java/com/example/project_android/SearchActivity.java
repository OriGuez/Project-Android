package com.example.project_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;

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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private VideosViewModel viewModel;
    private UsersViewModel usersViewModel;
    public static List<Video> videoList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SearchView searchView;
    private ImageView youtubeLogo;
    private VideoAdapter adapter;
    private TextView noVideosText;

    private androidx.coordinatorlayout.widget.CoordinatorLayout mainLayout;
    private boolean isDarkMode = MainActivity.isDarkMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ActivityLifecycle", "onCreate: SearchActivity");
        super.onCreate(savedInstanceState);
        // Get the search query from the intent
        String initialQuery = getIntent().getStringExtra("search_query");
        if (initialQuery == null) {
            Log.e("SearchActivity", "No search query provided");
        }
        setContentView(R.layout.activity_search);
        viewModel = new ViewModelProvider(this).get(VideosViewModel.class);
        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        sharedPreferences = getApplicationContext().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        // Initialize views
        searchView = findViewById(R.id.searchView);
        searchView.setQuery(initialQuery, false); // Set the initial query text
        noVideosText = findViewById(R.id.noVideosText);
        searchView.clearFocus();
        youtubeLogo = findViewById(R.id.youtubeLogo);
        applySearchViewColors(searchView, isDarkMode);
        performSearch(initialQuery);
        // Set SearchView listeners to hide/show the logo
        searchView.setOnSearchClickListener(v -> youtubeLogo.setVisibility(View.GONE));
        searchView.setOnCloseListener(() -> {
            youtubeLogo.setVisibility(View.VISIBLE);
            return false;
        });
        mainLayout = findViewById(R.id.mainSearch);
        recyclerView = findViewById(R.id.recyclerViewVideos);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        // Observe LiveData from ViewModel
        adapter = new VideoAdapter(this, videoList, "Main");
        // Observe LiveData from ViewModel
        recyclerView.setAdapter(adapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //if i want to make another search just update the items.no need to reload page.
            ////////////////////////////////////////////
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //performSearch(newText);
                return false;
            }
        });

        if (isDarkMode) {
            mainLayout.setBackgroundColor(Color.DKGRAY);
        } else {
            mainLayout.setBackgroundColor(Color.WHITE);
        }
    }

    private void performSearch(String query) {
        viewModel.search(query).observe(this, videos -> {
            // Update the UI with the new video list
            if (videos != null) {
                recyclerView.setVisibility(View.VISIBLE);
                adapter.updateVideoList(videos);
                noVideosText.setVisibility(View.GONE);
                searchView.clearFocus();
            } else {
                //print "no video found/ error" on screen
                recyclerView.setVisibility(View.GONE);
                noVideosText.setVisibility(View.VISIBLE);
                adapter.clearList();
                searchView.clearFocus();
                Log.e("search", "Video list is null");
            }
        });
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
