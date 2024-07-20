package com.example.project_android.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import androidx.appcompat.widget.AppCompatImageButton;
import android.widget.Button;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_android.EditVideo;
import com.example.project_android.MainActivity;
import com.example.project_android.MyApplication;
import com.example.project_android.R;
import com.example.project_android.VideoActivity;
import com.example.project_android.model.UserData;
import com.example.project_android.model.Video;

import java.io.InputStream;
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
            filteredList.addAll(videoListFull); // Display all videos if query is empty
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

        // Set text color based on dark mode
        int textColor = MainActivity.isDarkMode ? Color.WHITE : Color.BLACK;
        holder.publisherTextView.setTextColor(textColor);
        holder.titleTextView.setTextColor(textColor);
        holder.uploadDateTextView.setTextColor(textColor);


        // Set profile picture if available
        if (holder.profilePic != null && MainActivity.userDataList != null) {
            for (UserData user : MainActivity.userDataList) {
                if (user.getUsername().equals(video.getPublisher())) {
                    holder.profilePic.setImageBitmap(user.getImage());
                    break;
                }
            }
        }

        // Load thumbnail from resources or set default logo
        //String thumbURL = MyApplication.getContext().getString(R.string.BaseUrl);
        String baseUrl = MyApplication.getContext().getString(R.string.BaseUrl);
        String thumbPath = video.getThumbnail();
        if (thumbPath != null)
            thumbPath = thumbPath.substring(1);
        String thumbURL = baseUrl + thumbPath;

        if (thumbURL != null && !thumbURL.isEmpty()) {
            new LoadImageTask(holder.thumbnailImageView).execute(thumbURL);
        } else {
            Bitmap defaultThumbnail = video.getThumbnailPicture();
            if (defaultThumbnail != null) {
                holder.thumbnailImageView.setImageBitmap(defaultThumbnail);
            } else {
                holder.thumbnailImageView.setImageResource(R.drawable.logo);
            }


        }
////        int thumbnailResourceId = getThumbnailResourceId(video.getThumbnail());
//        int thumbnailResourceId = 0;
//        if (thumbnailResourceId != 0) {
//            holder.thumbnailImageView.setImageResource(thumbnailResourceId);
//        } else {
//            Bitmap defaultThumbnail = video.getThumbnailPicture();
//            if (defaultThumbnail != null) {
//                holder.thumbnailImageView.setImageBitmap(defaultThumbnail);
//            } else {
//                holder.thumbnailImageView.setImageResource(R.drawable.logo);
//            }
//        }

        // Set click listener to open video details activity
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
        Button editButton;


        ImageView profilePic;

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

    // Helper method to get resource ID of thumbnail
    private int getThumbnailResourceId(String thumbnailName) {
        String resourceName = "thumbnail" + thumbnailName.trim();
        Resources resources = context.getResources();
        return resources.getIdentifier(resourceName, "raw", context.getPackageName());
    }

    // Method to filter adapter's dataset based on query
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


    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public LoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

}