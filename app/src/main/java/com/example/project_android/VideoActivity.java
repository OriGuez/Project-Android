package com.example.project_android;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.AssetManager;
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

import androidx.appcompat.app.AppCompatActivity;
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
import com.example.project_android.utils.ImageLoader;
import com.example.project_android.viewModel.CommentsViewModel;
import com.example.project_android.viewModel.UsersViewModel;
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
    private UsersViewModel usersViewModel;
    private UserData uploader = null;
    private String videoID;

    private CommentRecyclerViewAdapter recycleAdapter;
    private RecyclerView commentsRecycleView;
    private VideoAdapter videoAdapter;
    private VideoView videoView;
    private TextView titleTextView;
    private TextView CommentSectionTitle;
    private TextView dateTextView;
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
    AssetManager assetManager;
    ImageView profileImageView;

    private static final String TAG = "VideoActivity";
    //DELETE IT LATER
    private int pp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assetManager = getAssets();
        vidViewModel = new ViewModelProvider(this).get(VideosViewModel.class);
        commentsViewModel = new ViewModelProvider(this).get(CommentsViewModel.class);
        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        // Retrieve the ID from the intent's extras
        videoID = getIntent().getStringExtra("videoID");
        if (currentVideo == null)
            currentVideo = MainActivity.videoList.get(0);
        Log.d(TAG, "Received videoID: " + videoID);

        vidViewModel.get(videoID).observe(this, video -> {
            // Update the UI with the new video list
            if (video != null) {
                currentVideo = video;

                usersViewModel.get(currentVideo.getPublisher()).observe(this, user -> {
                    if (user != null) {
                        uploader = user;
                        updateVideoDetails();
                    } else {
                        Log.d(TAG, "no user");
                    }
                });
            } else {
                currentVideo = MainActivity.videoList.get(0);
                //currentVideo = null;
                Log.e("ac", "Video list is null");
            }
        });

        commentsViewModel.get(videoID).observe(this, comments -> {
            if (comments != null) {
                recycleAdapter.updateCommentsList(comments);
            }
        });

        // Observe LiveData from ViewModel (videos for the recycler below)
        vidViewModel.get().observe(this, videos -> {
            // Update the UI with the new video list
            if (videos != null) {
                videoAdapter.updateVideoList(videos);
            } else {
                Log.e("ac", "Video list is null");
            }
        });

        setContentView(R.layout.activity_video);
        InitializeUiComponents();

        if (profileImageView != null && MainActivity.userDataList != null) {
            for (UserData user : MainActivity.userDataList) {
                if (user.getUsername().equals(currentVideo.getPublisher())) {
                    profileImageView.setImageBitmap(user.getImage());
                    profileImageView.setOnClickListener(v -> {
                        Intent intent = new Intent(VideoActivity.this, UserPageActivity.class);
                        intent.putExtra("UserID", user.getId());
                        startActivity(intent);
                    });
                    break;
                    //comment
                }
            }
        }
        videoPageDarkMode();

        updateVideoDetails();

        if (commentsRecycleView != null) {
            recycleAdapter = new CommentRecyclerViewAdapter(this, vidCommentList, commentsRecycleView);
            commentsRecycleView.setLayoutManager(new LinearLayoutManager(this));
            commentsRecycleView.setAdapter(recycleAdapter);
            //commentsRecycleView.post(() -> RecyclerViewUtils.setRecyclerViewHeightBasedOnItems(commentsRecycleView));
        }

        if (videoRecyclerView != null) {
            videoRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
            videoAdapter = new VideoAdapter(this, MainActivity.videoList, "Video");
            videoRecyclerView.setAdapter(videoAdapter);
        }
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
        // Set video details
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
            } else
                dateTextView.setText("");
        }
        if (publisherTextView != null) {
            if (uploader != null)
                publisherTextView.setText(uploader.getChannelName());
            else
                publisherTextView.setText("");
        }
        if (profileImageView != null) {
            if (uploader != null) {
                String baseUrl = MyApplication.getContext().getString(R.string.BaseUrl);
                String profilePicPath = uploader.getImageURI();
                if (profilePicPath != null)
                    profilePicPath = profilePicPath.substring(1);
                String profileImageUrl = baseUrl + profilePicPath;
                ImageLoader.loadImage(profileImageUrl, profileImageView);
            }

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

    private void enterEditMode() {
        if (editTitleEditText == null || editDescriptionEditText == null || saveButton == null ||
                titleTextView == null || descriptionTextView == null || editVideoButton == null)
            return;

        // Show edit fields and save button
        editTitleEditText.setVisibility(View.VISIBLE);
        editDescriptionEditText.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.VISIBLE);
        // Hide non-editable fields
        titleTextView.setVisibility(View.GONE);
        descriptionTextView.setVisibility(View.GONE);
        // Populate edit fields with current video details
        editTitleEditText.setText(currentVideo.getTitle());
        editDescriptionEditText.setText(currentVideo.getDescription());
        // Change edit button text to "Cancel"
        editVideoButton.setVisibility(View.GONE);
        // Set edit mode flag
        isEditMode = true;
    }

    private void exitEditMode() {
        if (editTitleEditText == null || editDescriptionEditText == null || saveButton == null ||
                titleTextView == null || descriptionTextView == null || editVideoButton == null)
            return;

        // Hide edit fields and save button
        editTitleEditText.setVisibility(View.GONE);
        editDescriptionEditText.setVisibility(View.GONE);
        saveButton.setVisibility(View.GONE);
        // Show non-editable fields
        titleTextView.setVisibility(View.VISIBLE);
        descriptionTextView.setVisibility(View.VISIBLE);
        // Change edit button text back to "Edit"
        editVideoButton.setVisibility(View.VISIBLE);
        // Reset edit mode flag
        isEditMode = false;
    }

    private void saveChanges() {
        if (editTitleEditText == null || editDescriptionEditText == null) return;

        // Save changes to currentVideo object
        currentVideo.setTitle(editTitleEditText.getText().toString());
        currentVideo.setDescription(editDescriptionEditText.getText().toString());
        // Update UI with new details
        if (titleTextView != null) {
            titleTextView.setText(currentVideo.getTitle());
        }
        if (descriptionTextView != null) {
            descriptionTextView.setText(currentVideo.getDescription());
        }
    }

    private void InitializeUiComponents() {
        // Initialize views
        videoView = findViewById(R.id.videoView);
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        dateTextView = findViewById(R.id.dateTextView);
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

        profileImageView.setOnClickListener(v -> {
            if (uploader != null) {
                Intent intent = new Intent(VideoActivity.this, UserPageActivity.class);
                intent.putExtra("userID", uploader.getId());
                startActivity(intent);
                finish();
            }
        });


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

        // Check if the view exists before interacting with it
        if (editTitleEditText != null) {
            editTitleEditText.setVisibility(View.GONE);
        }
        if (editDescriptionEditText != null) {
            editDescriptionEditText.setVisibility(View.GONE);
        }
        if (saveButton != null) {
            saveButton.setVisibility(View.GONE);
        }

//        if (MainActivity.currentUser == null) {
        if (pp == 1) {

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
                    Comment newComment = new Comment(commentText,videoID,MainActivity.currentUser.getId());

                    commentsViewModel.add(videoID,newComment).observe(this,resp->{
                        if (resp != null && resp.isSuccessful()){
                            recycleAdapter.addComment(newComment);
                        }

                    });
//
//
//                    if (MainActivity.currentUser != null) {
//                        currentVideo.getComments().add(new Video.Comment("User", MainActivity.currentUser.getUsername(), commentText));
//                    } else {
//                        currentVideo.getComments().add(new Video.Comment("User", "Anon", commentText));
//                    }
//                    recycleAdapter.notifyDataSetChanged();
                    //RecyclerViewUtils.setRecyclerViewHeightBasedOnItems(commentsRecycleView);
                    commentAddText.getText().clear();
                }
            });
        }

        if (likeButton != null) {
            likeButton.setOnClickListener(v -> {
                boolean isFound = false;
                for (String user : currentVideo.getWhoLikedList()) {
                    if (user.equals(MainActivity.currentUser.getUsername())) {
                        likeButton.setImageDrawable(getResources().getDrawable(R.drawable.like));
                        likeText.setText(R.string.like);
                        isFound = true;
                        currentVideo.getWhoLikedList().remove(user);
                        break;
                    }
                }
                if (!isFound) {
                    currentVideo.getWhoLikedList().add(MainActivity.currentUser.getUsername());
                    likeButton.setImageDrawable(getResources().getDrawable(R.drawable.likebuttonpressed));
                    likeText.setText(R.string.liked);
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
}