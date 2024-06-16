package com.example.project_android;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<Video> videoList;
    private Context context;

    public VideoAdapter(Context context, List<Video> videoList) {
        this.context = context;
        this.videoList = videoList;
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

        // Load thumbnail from resources
        int thumbnailResourceId = getThumbnailResourceId(video.getThumbnailUrl());
        holder.thumbnailImageView.setImageResource(thumbnailResourceId);

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, VideoActivity.class);
            intent.putExtra("videoID", video.getVidID());
            context.startActivity(intent);
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

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            publisherTextView = itemView.findViewById(R.id.publisherTextView);
        }
    }

    // Helper method to get resource ID of thumbnail
    private int getThumbnailResourceId(String thumbnailName) {
        String resourceName = "thumbnail" + thumbnailName;  // Construct the resource name
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier(resourceName, "raw", context.getPackageName());

        if (resourceId == 0) {
            // Handle case where the resource is not found
            // You might want to set a default image or log an error
//            resourceId = R.drawable.default_thumbnail; // Example default resource
        }

        return resourceId;
    }
}
