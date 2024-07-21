package com.example.project_android;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.MediaController;

import com.example.project_android.adapters.CommentRecyclerViewAdapter;
import com.example.project_android.adapters.VideoAdapter;
import com.example.project_android.model.Comment;
import com.example.project_android.model.UserData;
import com.example.project_android.model.Video;
import com.example.project_android.viewModel.CommentsViewModel;
import com.example.project_android.viewModel.VideosViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VideoActivity extends AppCompatActivity {
    private VideosViewModel vidViewModel;
    List<Comment> vidCommentList = new ArrayList<>();
    private CommentsViewModel commentsViewModel;

    private CommentRecyclerViewAdapter recycleAdapter;
    private RecyclerView commentsRecycleView;
    private VideoAdapter videoAdapter;
    private VideoView videoView;
    private TextView titleTextView;
    private TextView CommentSectionTitle;
    private TextView dateTextView;
    private TextView viewsTextView;
    private TextView descriptionTextView;
    private TextView publisherTextView;
    private Button addComment;
    private EditText commentAddText;
    private ImageButton likeButton;
    private TextView likeText;
    private ImageButton shareButton;
    private ImageButton deleteVideoButton;
    private ImageButton editVideoButton;
    private EditText editTitleEditText;
    private EditText editDescriptionEditText;
    private Button saveButton;
    private boolean isEditMode = false;
    private RecyclerView videoRecyclerView;
    private Video currentVideo;
    private LinearLayout vidScreenLayout;
    ImageView profileImageView;

    private static final String TAG = "VideoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        InitializeUiComponents();

        vidViewModel = new ViewModelProvider(this).get(VideosViewModel.class);
        commentsViewModel = new ViewModelProvider(this).get(CommentsViewModel.class);

        String videoID = getIntent().getStringExtra("videoID");
        if (currentVideo == null)
            currentVideo = MainActivity.videoList.get(0);
        Log.d(TAG, "Received videoID: " + videoID);
        vidViewModel.get(videoID).observe(this, new Observer<Video>() {
            @Override
            public void onChanged(@Nullable Video video) {
                if (video != null) {
                    currentVideo = video;
                    fetchCommentsForCurrentVideo(video.getVidID());
                    updateVideoDetails();
                    incrementViewCount(video);
                } else {
                    currentVideo = MainActivity.videoList.get(0);
                    Log.e("ac", "Video list is null");
                }
            }
        });

        vidViewModel.get().observe(this, videos -> {
            if (videos != null) {
                videoAdapter.updateVideoList(videos);
            } else {
                Log.e("ac", "Video list is null");
            }
        });

        if (profileImageView != null && MainActivity.userDataList != null) {
            for (UserData user : MainActivity.userDataList) {
                if (user.getUsername().equals(currentVideo.getPublisher())) {
                    profileImageView.setImageBitmap(user.getImage());
                    profileImageView.setOnClickListener(v -> {
                        Intent intent = new Intent(VideoActivity.this, UserPageActivity.class);
                        intent.putExtra("username", user.getUsername());
                        startActivity(intent);
                    });
                    break;
                }
            }
        }
        videoPageDarkMode();

        if (currentVideo.getUrl() != null) {
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + getRawResourceIdByName(currentVideo.getUrl()));
            videoView.setVideoURI(videoUri);
        } else {
            try {
                Uri vUri = decodeBase64ToVideoUri(currentVideo.getBase64Video());
                videoView.setVideoURI(vUri);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to decode video", Toast.LENGTH_SHORT).show();
            }
        }
        videoView.setVideoPath("http://10.0.2.2:8080/uploads/videos/video-1720542407846.mp4");
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.start();

        updateVideoDetails();

        if (commentsRecycleView != null) {
            recycleAdapter = new CommentRecyclerViewAdapter(this, vidCommentList, commentsRecycleView);
            commentsRecycleView.setLayoutManager(new LinearLayoutManager(this));
            commentsRecycleView.setAdapter(recycleAdapter);
        }

        if (videoRecyclerView != null) {
            videoRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
            videoAdapter = new VideoAdapter(this, MainActivity.videoList, "Video");
            videoRecyclerView.setAdapter(videoAdapter);
        }
    }

    private void fetchCommentsForCurrentVideo(String vidId) {
        commentsViewModel.get(vidId).observe(this, new Observer<List<Comment>>() {
            @Override
            public void onChanged(@Nullable List<Comment> comments) {
                if (comments != null) {
                    vidCommentList = comments;
                    updateComments(vidCommentList);
                }
            }
        });
    }

    private void incrementViewCount(Video video) {
        video.setViews( Integer.valueOf (video.getViews() + 1)) ;
        updateVideoDetails();
//        vidViewModel.update(video);
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
        MainActivity.videoList.remove(currentVideo);
        finish();
    }

    private void updateLikeButton(ImageButton likeButton, TextView isLiked) {
        if (likeButton == null || isLiked == null) return;

        for (String user : currentVideo.getWhoLikedList()) {
            if (user.equals(MainActivity.currentUser.getUsername())) {
                likeButton.setImageDrawable(getResources().getDrawable(R.drawable.likebuttonpressed));
                isLiked.setText(R.string.liked);
                return;
            }
        }
        likeButton.setImageDrawable(getResources().getDrawable(R.drawable.like));
        isLiked.setText(R.string.like);
    }

    private void updateVideoDetails() {
        if (titleTextView != null) {
            titleTextView.setText(currentVideo.getTitle());
        }
        if (descriptionTextView != null) {
            descriptionTextView.setText(currentVideo.getDescription());
        }
        if (dateTextView != null) {
            Date d = currentVideo.getCreatedAt();
            if (d != null) {
                dateTextView.setText(d.toString());
            } else {
                dateTextView.setText(currentVideo.getUpload_date());
            }
        }
        if (viewsTextView != null) {
            viewsTextView.setText(formatNum(currentVideo.getViews()) + " views");
        }
        if (publisherTextView != null) {
            publisherTextView.setText(currentVideo.getPublisher());
        }
        String baseUrl = MyApplication.getContext().getString(R.string.BaseUrl);
        String path = currentVideo.getUrl();
        if (path != null)
            path = path.substring(1);
        String vidPath = baseUrl + path;

        videoView.setVideoPath(vidPath);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.start();
    }

    private void updateComments(List<Comment> newList) {
        recycleAdapter.updateCommentsList(newList);
    }

    private void enterEditMode() {
        if (editTitleEditText == null || editDescriptionEditText == null || saveButton == null ||
                titleTextView == null || descriptionTextView == null || editVideoButton == null)
            return;

        editTitleEditText.setVisibility(View.VISIBLE);
        editDescriptionEditText.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.VISIBLE);

        titleTextView.setVisibility(View.GONE);
        descriptionTextView.setVisibility(View.GONE);

        editTitleEditText.setText(currentVideo.getTitle());
        editDescriptionEditText.setText(currentVideo.getDescription());
        editVideoButton.setVisibility(View.GONE);
        isEditMode = true;
    }

    private void exitEditMode() {
        if (editTitleEditText == null || editDescriptionEditText == null || saveButton == null ||
                titleTextView == null || descriptionTextView == null || editVideoButton == null)
            return;

        editTitleEditText.setVisibility(View.GONE);
        editDescriptionEditText.setVisibility(View.GONE);
        saveButton.setVisibility(View.GONE);

        titleTextView.setVisibility(View.VISIBLE);
        descriptionTextView.setVisibility(View.VISIBLE);

        editVideoButton.setVisibility(View.VISIBLE);
        isEditMode = false;
    }

    private void saveChanges() {
        if (editTitleEditText == null || editDescriptionEditText == null) return;

        currentVideo.setTitle(editTitleEditText.getText().toString());
        currentVideo.setDescription(editDescriptionEditText.getText().toString());

        if (titleTextView != null) {
            titleTextView.setText(currentVideo.getTitle());
        }
        if (descriptionTextView != null) {
            descriptionTextView.setText(currentVideo.getDescription());
        }
        updateVideoDetails();
//        vidViewModel.updateVideoDetails(currentVideo);
    }

    private int getRawResourceIdByName(String resourceName) {
        String formattedResourceName = resourceName.replace("/videos/", "").replace(".mp4", "");
        return getResources().getIdentifier(formattedResourceName, "raw", getPackageName());
    }

    private Uri decodeBase64ToVideoUri(String base64Video) throws IOException {
        byte[] decodedBytes = Base64.decode(base64Video, Base64.DEFAULT);
        File tempFile = File.createTempFile("decoded_video", ".mp4", getCacheDir());
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(decodedBytes);
        }
        return Uri.fromFile(tempFile);
    }

    private void InitializeUiComponents() {
        videoView = findViewById(R.id.videoView);
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        dateTextView = findViewById(R.id.dateTextView);
        viewsTextView = findViewById(R.id.viewsTextView);
        publisherTextView = findViewById(R.id.publisherTextView);
        commentAddText = findViewById(R.id.commentAddText);
        addComment = findViewById(R.id.addCommentButton);
        likeButton = findViewById(R.id.likeButton);
        likeText = findViewById(R.id.likeText);
        shareButton = findViewById(R.id.shareButton);
        deleteVideoButton = findViewById(R.id.deleteVideoButton);
        editVideoButton = findViewById(R.id.editVideoButton);
        editTitleEditText = findViewById(R.id.editTitleEditText);
        editDescriptionEditText = findViewById(R.id.editDescriptionEditText);
        saveButton = findViewById(R.id.saveEditVidButton);
        videoRecyclerView = findViewById(R.id.recyclerView);
        commentsRecycleView = findViewById(R.id.commentsRecyclerView);
        profileImageView = findViewById(R.id.publisherProfilePic);
        CommentSectionTitle = findViewById(R.id.commentsTitleTextView);
        vidScreenLayout = findViewById(R.id.vidLO);

        if (commentsRecycleView != null) {
            commentsRecycleView.setNestedScrollingEnabled(false);
        }
        if (videoRecyclerView != null) {
            videoRecyclerView.setNestedScrollingEnabled(false);
        }

        if (editTitleEditText != null) {
            editTitleEditText.setVisibility(View.GONE);
        }
        if (editDescriptionEditText != null) {
            editDescriptionEditText.setVisibility(View.GONE);
        }
        if (saveButton != null) {
            saveButton.setVisibility(View.GONE);
        }

        if (MainActivity.currentUser == null) {
            if (addComment != null) {
                addComment.setVisibility(View.GONE);
            }
            if (commentAddText != null) {
                commentAddText.setVisibility(View.GONE);
            }
            if (likeButton != null) {
                likeButton.setVisibility(View.GONE);
            }
            if (likeText != null) {
                likeText.setVisibility(View.GONE);
            }
            if (deleteVideoButton != null) {
                deleteVideoButton.setVisibility(View.GONE);
            }
            if (editVideoButton != null) {
                editVideoButton.setVisibility(View.GONE);
            }
        } else {
            updateLikeButton(likeButton, likeText);
        }

        if (addComment != null) {
            addComment.setOnClickListener(v -> {
                String commentText = commentAddText.getText().toString().trim();
                if (!commentText.isEmpty()) {
                    String username = (MainActivity.currentUser != null) ? MainActivity.currentUser.getUsername() : "Anon";
                    currentVideo.getComments().add(new Video.Comment("User", username, commentText));
                    recycleAdapter.notifyDataSetChanged();
                    commentAddText.getText().clear();
                }
            });
        }

        if (likeButton != null) {
            likeButton.setOnClickListener(v -> {
                boolean isFound = false;
                String currentUserUsername = (MainActivity.currentUser != null) ? MainActivity.currentUser.getUsername() : "Anon";
                for (String user : currentVideo.getWhoLikedList()) {
                    if (user.equals(currentUserUsername)) {
                        likeButton.setImageDrawable(getResources().getDrawable(R.drawable.like));
                        likeText.setText(R.string.like);
                        isFound = true;
                        currentVideo.getWhoLikedList().remove(user);
                        break;
                    }
                }
                if (!isFound) {
                    currentVideo.getWhoLikedList().add(currentUserUsername);
                    likeButton.setImageDrawable(getResources().getDrawable(R.drawable.likebuttonpressed));
                    likeText.setText(R.string.liked);
                }
            });
        }

        if (shareButton != null) {
            shareButton.setOnClickListener(v -> {
                String textToShare = currentVideo.getUrl();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
                String title = getResources().getString(R.string.share_via);
                Intent chooser = Intent.createChooser(shareIntent, title);
                if (shareIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(chooser);
                }
            });
        }

        if (deleteVideoButton != null) {
            deleteVideoButton.setOnClickListener(v -> {
                showDeleteConfirmationDialog();
            });
        }

        if (editVideoButton != null) {
            editVideoButton.setOnClickListener(v -> {
                if (!isEditMode) {
                    enterEditMode();
                }
            });
        }
        if (editVideoButton != null) {
            editVideoButton.setOnClickListener(v -> {
                Intent intent = new Intent(VideoActivity.this, EditVideo.class);
                intent.putExtra("videoID", currentVideo.getVidID());
                startActivity(intent);
            });
        }

        if (saveButton != null) {
            saveButton.setOnClickListener(v -> {
                saveChanges();
                exitEditMode();
            });
        }
    }

    private void videoPageDarkMode() {
        if (MainActivity.isDarkMode) {
            if (titleTextView != null)
                titleTextView.setTextColor(Color.WHITE);
            if (descriptionTextView != null)
                descriptionTextView.setTextColor(Color.WHITE);
            if (descriptionTextView != null)
                descriptionTextView.setTextColor(Color.WHITE);
            if (publisherTextView != null)
                publisherTextView.setTextColor(Color.WHITE);
            if (dateTextView != null)
                dateTextView.setTextColor(Color.WHITE);
            if (viewsTextView != null)
                viewsTextView.setTextColor(Color.WHITE);
            if (CommentSectionTitle != null)
                CommentSectionTitle.setTextColor(Color.WHITE);
            if (likeText != null)
                likeText.setTextColor(Color.WHITE);
            if (editTitleEditText != null)
                editTitleEditText.setTextColor(Color.WHITE);
            if (editDescriptionEditText != null)
                editDescriptionEditText.setTextColor(Color.WHITE);
            if (commentAddText != null) {
                commentAddText.setTextColor(Color.WHITE);
                commentAddText.setHintTextColor(Color.WHITE);
            }
            if (vidScreenLayout != null)
                vidScreenLayout.setBackgroundColor(Color.DKGRAY);
        } else {
            if (titleTextView != null)
                titleTextView.setTextColor(Color.BLACK);
            if (descriptionTextView != null)
                descriptionTextView.setTextColor(Color.BLACK);
            if (descriptionTextView != null)
                descriptionTextView.setTextColor(Color.BLACK);
            if (publisherTextView != null)
                publisherTextView.setTextColor(Color.BLACK);
            if (dateTextView != null)
                dateTextView.setTextColor(Color.BLACK);
            if (viewsTextView != null)
                viewsTextView.setTextColor(Color.BLACK);
            if (CommentSectionTitle != null)
                CommentSectionTitle.setTextColor(Color.BLACK);
            if (likeText != null)
                likeText.setTextColor(Color.BLACK);
            if (editTitleEditText != null)
                editTitleEditText.setTextColor(Color.BLACK);
            if (editDescriptionEditText != null)
                editDescriptionEditText.setTextColor(Color.BLACK);
            if (commentAddText != null) {
                commentAddText.setTextColor(Color.BLACK);
                commentAddText.setHintTextColor(Color.BLACK);
            }
            if (vidScreenLayout != null)
                vidScreenLayout.setBackgroundColor(Color.WHITE);
        }
    }

    private String formatNum(int num) {
        if (num < 1000) {
            return String.valueOf(num);
        } else if (num >= 1000 && num < 10000) {
            return String.format("%.1fk", num / 1000.0);
        } else if (num >= 10000 && num < 1000000) {
            return String.format("%dk", num / 1000);
        } else if (num >= 1000000 && num < 10000000) {
            return String.format("%.1fM", num / 1000000.0);
        } else if (num >= 10000000 && num < 1000000000) {
            return String.format("%dM", num / 1000000);
        } else if (num >= 1000000000) {
            return String.format("%.1fB", num / 1000000000.0);
        }
        return String.valueOf(num);
    }
}
