package com.example.project_android;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project_android.adapters.VideoAdapter;
import com.example.project_android.model.Video;
import com.example.project_android.viewModel.UsersViewModel;
import com.example.project_android.viewModel.VideosViewModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.imageview.ShapeableImageView;

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
    private TextView searchResultsText;
    private androidx.coordinatorlayout.widget.CoordinatorLayout mainLayout;
    private AppBarLayout appBarLayout;
    private Toolbar topMenu;

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
        mainLayout = findViewById(R.id.mainSearch);
        appBarLayout = findViewById(R.id.appBarLayout);
        searchView = findViewById(R.id.searchView);
        searchView.setQuery(initialQuery, false); // Set the initial query text
        noVideosText = findViewById(R.id.noVideosText);
        searchResultsText = findViewById(R.id.searchResultsText);
        searchView.clearFocus();
        topMenu = findViewById(R.id.topMenu);
        youtubeLogo = findViewById(R.id.youtubeLogo);
        applySearchViewColors(searchView, isDarkMode);
        updateTextColors(isDarkMode);
        updateTopBarColors(isDarkMode);
        performSearch(initialQuery);
        // Set SearchView listeners to hide/show the logo
        searchView.setOnSearchClickListener(v -> youtubeLogo.setVisibility(View.GONE));
        searchView.setOnCloseListener(() -> {
            youtubeLogo.setVisibility(View.VISIBLE);
            return false;
        });
        recyclerView = findViewById(R.id.recyclerViewVideos);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        // Observe LiveData from ViewModel
        adapter = new VideoAdapter(this, videoList, "Main", false);
        recyclerView.setAdapter(adapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // If a new search is initiated, update the items without reloading the page.
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        youtubeLogo.setOnClickListener(v -> finish());

        if (isDarkMode) {
            mainLayout.setBackgroundColor(Color.DKGRAY);
        } else {
            mainLayout.setBackgroundColor(Color.WHITE);
        }
    }

    @SuppressLint("StringFormatInvalid")
    private void performSearch(String query) {
        viewModel.search(query).observe(this, videos -> {
            String searchResultsMessage;
            searchResultsMessage = getString(R.string.searchResults, query);
            searchResultsText.setText(searchResultsMessage);
            // Update the UI with the new video list
            if (videos != null && !videos.isEmpty()) {
                recyclerView.setVisibility(View.VISIBLE);
                noVideosText.setVisibility(View.GONE);
                adapter.updateVideoList(videos);
            } else {
                // Show "Search results for "(input)": No videos found"
                recyclerView.setVisibility(View.GONE);
                noVideosText.setVisibility(View.VISIBLE);
                String noResultsMessage;
                noResultsMessage = getString(R.string.searchResults, query) + getString(R.string.noVideosFound);
                noVideosText.setText(noResultsMessage);
                adapter.clearList();
            }
            searchView.clearFocus();
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

    private void updateTopBarColors(boolean isDarkMode) {
        int backgroundColor = isDarkMode ? Color.DKGRAY : Color.WHITE;
        appBarLayout.setBackgroundColor(backgroundColor);
        topMenu.setBackgroundColor(backgroundColor);

    }
}
