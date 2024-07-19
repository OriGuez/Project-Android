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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private String source;
    private List<Video> videoList;
    private List<Video> videoListFull;
    private Context context;

    public VideoAdapter(Context context, List<Video> videoList, String source) {
        this.context = context;
        this.videoList = videoList;
        this.videoListFull = new ArrayList<>(videoList);
        this.source = source;
    }

    public void filterList(String query) {
        List<Video> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            filteredList.addAll(videoListFull);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Video video : videoListFull) {
                if (video.getTitle().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(video);
                }
            }
        }
        videoList.clear();
        videoList.addAll(filteredList);
        notifyDataSetChanged();
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

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = videoList.get(position);
        holder.titleTextView.setText(video.getTitle());
        holder.publisherTextView.setText(video.getPublisher());
        holder.uploadDateTextView.setText(video.getUpload_date());

        int textColor = MainActivity.isDarkMode ? Color.WHITE : Color.BLACK;
        holder.publisherTextView.setTextColor(textColor);
        holder.uploadDateTextView.setTextColor(textColor);
        holder.titleTextView.setTextColor(textColor);

        if (holder.profilePic != null && MainActivity.userDataList != null) {
            for (UserData user : MainActivity.userDataList) {
                if (user.getUsername().equals(video.getPublisher())) {
                    holder.profilePic.setImageBitmap(user.getImage());
                    break;
                }
            }
        }

        int thumbnailResourceId = getThumbnailResourceId(video.getThumbnailUrl());
        if (thumbnailResourceId != 0) {
            holder.thumbnailImageView.setImageResource(thumbnailResourceId);
        } else {
            Bitmap defaultThumbnail = video.getThumbnailPicture();
            if (defaultThumbnail != null) {
                holder.thumbnailImageView.setImageBitmap(defaultThumbnail);
            } else {
                holder.thumbnailImageView.setImageResource(R.drawable.logo);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VideoActivity.class);
            intent.putExtra("videoID", video.getVidID());
            context.startActivity(intent);
            if (source.equals("Video")) {
                ((Activity) context).finish();
            }
        });

        if ("User".equals(source)) {
            holder.editButton.setVisibility(View.VISIBLE);
            holder.editButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, EditVideo.class);
                intent.putExtra("videoID", video.getVidID());
                context.startActivity(intent);
            });
        } else {
            holder.editButton.setVisibility(View.GONE);
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
        ImageView profilePic;
        Button editButton;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            publisherTextView = itemView.findViewById(R.id.publisherTextView);
            uploadDateTextView = itemView.findViewById(R.id.uploadDateTextView);
            profilePic = itemView.findViewById(R.id.publisherPicInList);
            editButton = itemView.findViewById(R.id.editVidButton);
        }
    }

    private int getThumbnailResourceId(String thumbnailName) {
        String resourceName = "thumbnail" + thumbnailName.trim();
        Resources resources = context.getResources();
        return resources.getIdentifier(resourceName, "raw", context.getPackageName());
    }

    public void filter(String query) {
        videoList.clear();
        if (query.isEmpty()) {
            videoList.addAll(videoListFull);
        } else {
            query = query.toLowerCase().trim();
            for (Video video : videoListFull) {
                if (video.getTitle().toLowerCase().contains(query)) {
                    videoList.add(video);
                }
            }
        }
        notifyDataSetChanged();
    }
}