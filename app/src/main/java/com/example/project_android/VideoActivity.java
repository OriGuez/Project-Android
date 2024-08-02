package com.example.project_android;

import androidx.activity.OnBackPressedCallback;
import java.text.NumberFormat;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;
import java.text.SimpleDateFormat;
import java.util.Date;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
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
import com.example.project_android.utils.LoadingDialogUtility;
import com.example.project_android.viewModel.CommentsViewModel;
import com.example.project_android.viewModel.UsersViewModel;
import com.example.project_android.viewModel.VideosViewModel;
import java.util.ArrayList;
import java.util.List;

public class VideoActivity extends AppCompatActivity {
    private LoadingDialogUtility loadingDialogUtility;
    private ProgressBar progressBar;
    private TextView loadingText;
    MediaController mediaController = null;
    private boolean isLiked = false;
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
    private TextView viewsTextView;
    private TextView descriptionTextView;
    private TextView publisherTextView;
    private Button addComment;
    private EditText commentAddText;
    private ImageButton likeButton;
    private TextView likeText;
    private ImageButton shareButton;
    private EditText editTitleEditText;
    private EditText editDescriptionEditText;
    private Button saveButton;
    private boolean isEditMode = false;
    private RecyclerView videoRecyclerView;
    private Video currentVideo;
    private LinearLayout vidScreenLayout;
    private CardView cardView;
    ImageView profileImageView;

    private static final String TAG = "VideoActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        vidViewModel = new ViewModelProvider(this).get(VideosViewModel.class);
        commentsViewModel = new ViewModelProvider(this).get(CommentsViewModel.class);
        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        loadingDialogUtility = new LoadingDialogUtility(this);
        // Retrieve the ID from the intent's extras
        videoID = getIntent().getStringExtra("videoID");
        Log.d(TAG, "Received videoID: " + videoID);
        vidViewModel.get(videoID).observe(this, video -> {
            // Update the UI with the new video list
            if (video != null) {
                loadingDialogUtility.hide();
                currentVideo = video;
                List<String> likedList = currentVideo.getWhoLikedList();
                if (likedList != null && MainActivity.currentUser != null && likedList.contains(MainActivity.currentUser.getId()))
                    isLiked = true;
                updateVideoDetails();
                usersViewModel.get(currentVideo.getPublisher()).observe(this, user -> {
                    if (user != null) {
                        uploader = user;
                        updateVideoDetails();
                    } else {
                        Log.d(TAG, "no user");
                    }
                });
            } else {
                loadingDialogUtility.hide();
                Log.e("ac", "Video in page is null");
            }
        });
        commentsViewModel.get(videoID).observe(this, comments -> {
            if (comments != null) {
                recycleAdapter.updateCommentsList(comments);
            }
        });
        // Observe LiveData from ViewModel (videos for the recycler below)
        vidViewModel.get().observe(this, videos -> {
            if (videos != null) {
                MainActivity.videoList = videos;
                videoAdapter.updateVideoList(videos);
            } else {
                Log.e("ac", "Video list is null");
            }
        });
        InitializeUiComponents();
        videoPageDarkMode();
        updateVideoDetails();
        if (commentsRecycleView != null) {
            recycleAdapter = new CommentRecyclerViewAdapter(this, vidCommentList, commentsRecycleView);
            commentsRecycleView.setLayoutManager(new LinearLayoutManager(this));
            commentsRecycleView.setAdapter(recycleAdapter);
        }
        if (videoRecyclerView != null) {
            videoRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
            videoAdapter = new VideoAdapter(this, MainActivity.videoList, "Video", false);
            videoRecyclerView.setAdapter(videoAdapter);
        }
    }
    private void updateLikeButtonState() {
        if (likeButton == null || likeText == null) return;
        if (isLiked) {
            likeButton.setImageDrawable(getResources().getDrawable(R.drawable.likebuttonpressed));
            likeText.setText(R.string.liked);
            return;
        }
        likeButton.setImageDrawable(getResources().getDrawable(R.drawable.like));
        likeText.setText(R.string.like);
    }
    private void updateVideoDetails() {
        if (currentVideo == null)
        {
            // Show loading indicator
            if (loadingDialogUtility != null)
                loadingDialogUtility.show();
            return;
        }
        if (titleTextView != null) {
            titleTextView.setText(currentVideo.getTitle());
        }
        if (descriptionTextView != null) {
            descriptionTextView.setText(currentVideo.getDescription());
        }
        if (dateTextView != null) {
            Date d = currentVideo.getCreatedAt();
            if (d != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = formatter.format(d);
                dateTextView.setText(formattedDate);
            } else {
                dateTextView.setText("");
            }
        }
        if (viewsTextView != null) {
            NumberFormat numberFormat = NumberFormat.getInstance();
            String views = numberFormat.format(currentVideo.getViews()) + " " + MyApplication.getContext().getString(R.string.views) + "   •";
            viewsTextView.setText(views);
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
        updateLikeButtonState();
        String baseUrl = MyApplication.getContext().getString(R.string.BaseUrl);
        String path = currentVideo.getUrl();
        if (path != null)
            path = path.substring(1);
        String vidPath = baseUrl + path;

        videoView.setVideoPath(vidPath);
        mediaController = new MediaController(this) {
            @Override
            public void show(int timeout) {
                super.show(1500);
            }
        };
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.start();
    }

    private void InitializeUiComponents() {
        progressBar = findViewById(R.id.progressBar);
        loadingText = findViewById(R.id.loadingText);
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
        editTitleEditText = findViewById(R.id.editTitleEditText);
        editDescriptionEditText = findViewById(R.id.editDescriptionEditText);
        saveButton = findViewById(R.id.saveEditVidButton);
        videoRecyclerView = findViewById(R.id.recyclerView);
        commentsRecycleView = findViewById(R.id.commentsRecyclerView);
        profileImageView = findViewById(R.id.publisherProfilePic);
        CommentSectionTitle = findViewById(R.id.commentsTitleTextView);
        vidScreenLayout = findViewById(R.id.vidLO);
        cardView = findViewById(R.id.commentsCardView);
        if (commentsRecycleView != null) {
            commentsRecycleView.setNestedScrollingEnabled(false);
        }
        if (videoRecyclerView != null) {
            videoRecyclerView.setNestedScrollingEnabled(false);
        }
        if (profileImageView != null) {
            profileImageView.setOnClickListener(v -> {
                if (uploader != null) {
                    Intent intent = new Intent(VideoActivity.this, UserPageActivity.class);
                    intent.putExtra("userID", uploader.getId());
                    startActivity(intent);
                    finish();
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
        }
        if (addComment != null) {
            addComment.setOnClickListener(v -> {
                String commentText = commentAddText.getText().toString().trim();
                if (!commentText.isEmpty()) {
                    Comment newComment = new Comment(commentText, MainActivity.currentUser.getId(), videoID);
                    commentsViewModel.add(videoID, newComment).observe(this, resp -> {
                        if (resp != null && resp.isSuccessful()) {
                            newComment.setId(resp.get_id());
                            recycleAdapter.addComment(newComment);
                        }
                    });
                    commentAddText.getText().clear();
                }
            });
        }
        if (likeButton != null) {
            likeButton.setOnClickListener(v -> {
                //if not liked we want to like
                if (!isLiked) {
                    vidViewModel.like(MainActivity.currentUser.getId(), currentVideo.getVidID()).observe(this, resp -> {
                        if (resp != null && resp.isSuccessful()) {
                            //video liked successfully
                            currentVideo.getWhoLikedList().add(MainActivity.currentUser.getId());
                            isLiked = true;
                            updateLikeButtonState();
                        }
                    });
                }
                //else we want to unlike
                else {
                    vidViewModel.unlike(MainActivity.currentUser.getId(), currentVideo.getVidID()).observe(this, resp -> {
                        if (resp != null && resp.isSuccessful()) {
                            //video unliked successfully
                            currentVideo.getWhoLikedList().remove(MainActivity.currentUser.getId());
                            isLiked = false;
                            updateLikeButtonState();
                        }
                    });
                }
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
                cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.backgroundColorDark));
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
            if (vidScreenLayout != null) {
                vidScreenLayout.setBackgroundColor(Color.WHITE);
            }
            cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white));
        }
    }
    private String formatNum(int num) {
        if (num < 1000) {
            return num + " " ;
        } else if (num >= 1000 && num < 10000) {
            return String.format("%.1fk " , num / 1000.0);
        } else if (num >= 10000 && num < 1000000) {
            return (num / 1000) + "k ";
        } else if (num >= 1000000 && num < 10000000) {
            return String.format("%.1fM ", num / 1000000.0);
        } else if (num >= 10000000 && num < 1000000000) {
            return (num / 1000000) + "M ";
        } else if (num >= 1000000000) {
            return String.format("%.1fB " , num / 1000000000.0);
        }
        return num + " views";
    }
