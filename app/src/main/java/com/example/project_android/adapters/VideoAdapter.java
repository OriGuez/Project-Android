package com.example.project_android.adapters;

import static android.graphics.Color.BLACK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_android.EditVideo;
import com.example.project_android.UserPageActivity;
import com.example.project_android.utils.ImageLoader;
import com.example.project_android.MainActivity;
import com.example.project_android.MyApplication;
import com.example.project_android.R;
import com.example.project_android.VideoActivity;
import com.example.project_android.model.UserData;
import com.example.project_android.model.Video;
import com.example.project_android.viewModel.UsersViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.ArrayList;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private UsersViewModel usersViewModel;
    private String source;
    private List<Video> videoList;
    private List<Video> videoListFull;
    private Context context;
    private boolean hideOwnerButtons;



    public VideoAdapter(Context context, List<Video> videoList, String source,boolean hideOwnerButtons) {
        this.context = context;
        this.videoList = videoList;
        this.videoListFull = new ArrayList<>(videoList);
        this.source = source;
        this.hideOwnerButtons = hideOwnerButtons;

        if (context != null)
            usersViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(UsersViewModel.class);
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        return new VideoViewHolder(view);
    }

    public void updateVideoList(List<Video> newVideoList) {
            this.videoList = newVideoList;
            notifyDataSetChanged();
    }


    public void clearList(){
        this.videoList.clear();
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = videoList.get(position);
        Date createdAt = video.getCreatedAt();

        if (createdAt != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = formatter.format(createdAt);
            holder.uploadDateTextView.setText(formattedDate);
        } else {
            holder.uploadDateTextView.setText(R.string.na_date);
        }
        String publisherID = video.getPublisher();
        usersViewModel.get(publisherID).observe((LifecycleOwner) context, user ->{
            if (user != null){
                holder.publisherTextView.setText(user.getChannelName());
                //UserData videoPublisher = user;
                String baseUrl = MyApplication.getContext().getString(R.string.BaseUrl);
                String profilePicPath = user.getImageURI();
                if (profilePicPath != null)
                    profilePicPath = profilePicPath.substring(1);
                String profileImageUrl = baseUrl + profilePicPath;
                ImageLoader.loadImage(profileImageUrl, holder.profilePic);
            }
        });
        if (hideOwnerButtons) {
            holder.publisherTextView.setVisibility(View.GONE);
            holder.profilePic.setVisibility(View.GONE);


        } else {
            holder.publisherTextView.setVisibility(View.VISIBLE);
            holder.profilePic.setVisibility(View.VISIBLE);
        }

        holder.titleTextView.setText(video.getTitle());
        //holder.publisherTextView.setText(video.getPublisher());
        int viewsCount = Integer.parseInt(String.valueOf(video.getViews()));
        String viewsText = formatNum(viewsCount) + " •" ;
        holder.viewsTextView.setText(viewsText);
        // Set text color based on dark mode
        int textColor = MainActivity.isDarkMode ? Color.WHITE : BLACK;
        holder.publisherTextView.setTextColor(textColor);
        holder.titleTextView.setTextColor(textColor);
        holder.uploadDateTextView.setTextColor(textColor);
        holder.viewsTextView.setTextColor(textColor);

        // Load thumbnail from resources or set default logo
        //String thumbURL = MyApplication.getContext().getString(R.string.BaseUrl);
        String baseUrl = MyApplication.getContext().getString(R.string.BaseUrl);
        String thumbPath = video.getThumbnail();
        if (thumbPath != null)
            thumbPath = thumbPath.substring(1);
        String thumbURL = baseUrl + thumbPath;

        if (thumbURL != null && !thumbURL.isEmpty()) {
            ImageLoader.loadImage(thumbURL, holder.thumbnailImageView);
        } else {
            Bitmap defaultThumbnail = video.getThumbnailPicture();
            if (defaultThumbnail != null) {
                holder.thumbnailImageView.setImageBitmap(defaultThumbnail);
            } else {
                holder.thumbnailImageView.setImageResource(R.drawable.logo);
            }
        }

        // Set click listener to open video details activity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VideoActivity.class);
            intent.putExtra("videoID", video.getVidID());
            context.startActivity(intent);
            if (source.equals("Video")) {
                ((Activity) context).finish();
            }

        });
        // Set click listener to open user page activity from profile picture
        holder.profilePic.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserPageActivity.class);
            intent.putExtra("userID", video.getPublisher());
            context.startActivity(intent);
        });

        if ("User".equals(source)) {
            holder.publisherTextView.setVisibility(View.GONE);
            holder.profilePic.setVisibility(View.GONE);
            holder.editButton.setVisibility(View.VISIBLE);
            holder.uploadDateTextView.setTextColor(BLACK);
            holder.titleTextView.setTextColor(BLACK);
            holder.viewsTextView.setTextColor(BLACK);
            holder.editButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, EditVideo.class);
                intent.putExtra("videoID", video.getVidID());
                context.startActivity(intent);


            });
        } else {
            holder.editButton.setVisibility(View.GONE);
            if(hideOwnerButtons)
            {
                holder.uploadDateTextView.setTextColor(BLACK);
                holder.titleTextView.setTextColor(BLACK);
                holder.viewsTextView.setTextColor(BLACK);
            }
        }

    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnailImageView;
        TextView titleTextView;
        TextView publisherTextView;
        TextView uploadDateTextView;
        TextView viewsTextView;
        ImageButton editButton;
        ImageView profilePic;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            publisherTextView = itemView.findViewById(R.id.publisherTextView);
            uploadDateTextView = itemView.findViewById(R.id.uploadDateTextView);
            viewsTextView = itemView.findViewById(R.id.viewsTextView);
            profilePic = itemView.findViewById(R.id.publisherPicInList);
            editButton = itemView.findViewById(R.id.editVidButton);
        }
    }

    private String formatNum(int num) {
        String viewsWord = MyApplication.getContext().getString(R.string.views);
        if (num < 1000) {
            return num + " " + viewsWord;
        } else if (num >= 1000 && num < 10000) {
            return String.format("%.1fk " + viewsWord, num / 1000.0);
        } else if (num >= 10000 && num < 1000000) {
            return (num / 1000) + "k " + viewsWord;
        } else if (num >= 1000000 && num < 10000000) {
            return String.format("%.1fM " + viewsWord, num / 1000000.0);
        } else if (num >= 10000000 && num < 1000000000) {
            return (num / 1000000) + "M " + viewsWord;
        } else if (num >= 1000000000) {
            return String.format("%.1fB " + viewsWord, num / 1000000000.0);
        }
        return num + " views";
    }
}