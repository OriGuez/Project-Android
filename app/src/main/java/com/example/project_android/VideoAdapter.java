package com.example.project_android;

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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private String source;
    private List<Video> videoList;
    private Context context;

    public VideoAdapter(Context context, List<Video> videoList, String source) {
        this.context = context;
        this.videoList = videoList;
        this.source = source;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = videoList.get(position);
        holder.titleTextView.setText(video.getTitle());
        holder.publisherTextView.setText(video.getPublisher());
        if (MainActivity.isDarkMode) {
            holder.publisherTextView.setTextColor(Color.WHITE);
            holder.titleTextView.setTextColor(Color.WHITE);
        } else {
            holder.publisherTextView.setTextColor(Color.BLACK);
            holder.titleTextView.setTextColor(Color.BLACK);
        }
        if (holder.profilePic != null && MainActivity.userDataList != null) {
            for (UserData user : MainActivity.userDataList) {
                if (user.getUsername().equals(video.getPublisher())) {
                    holder.profilePic.setImageBitmap(user.getImage());
                    break;
                }
            }
        }

        // Load thumbnail from resources
        int thumbnailResourceId = getThumbnailResourceId(video.getThumbnailUrl());
        if (thumbnailResourceId == 0) {
            Bitmap pic = video.getThumbnailPicture();
            if (pic != null)
                holder.thumbnailImageView.setImageBitmap(pic);
            else
                holder.thumbnailImageView.setImageResource(R.drawable.logo);
        } else {
            holder.thumbnailImageView.setImageResource(thumbnailResourceId);
        }
        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, VideoActivity.class);
            intent.putExtra("videoID", video.getVidID());
            context.startActivity(intent);
            if (source.equals("Video")) {
                ((Activity) context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnailImageView;
        TextView titleTextView;
        TextView publisherTextView;
        ImageView profilePic;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            publisherTextView = itemView.findViewById(R.id.publisherTextView);
            profilePic = itemView.findViewById(R.id.publisherPicInList);
        }
    }

    // Helper method to get resource ID of thumbnail
    private int getThumbnailResourceId(String thumbnailName) {
        // Ensure the resource name is correctly prefixed
        String resourceName = "thumbnail" + thumbnailName.trim();  // Ensure no leading/trailing spaces
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier(resourceName, "raw", context.getPackageName());

//        if (resourceId == 0) {
//            resourceId = R.drawable.logo; // Example default resource
//        }

        return resourceId;
    }
}
