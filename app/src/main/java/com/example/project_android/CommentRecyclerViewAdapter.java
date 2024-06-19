package com.example.project_android;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.ViewHolder> {

    private List<Video.Comment> comments;
    private RecyclerView recyclerView;

    private LayoutInflater inflater;
    private Context context;

    public CommentRecyclerViewAdapter(Context context, List<Video.Comment> comments, RecyclerView recyclerView) {
        this.context = context;
        this.comments = comments;
        this.recyclerView = recyclerView;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Video.Comment comment = comments.get(position);

        holder.publisherTextView.setText(comment.getPublisher());
        holder.commentContentTextView.setText(comment.getText());
        if (MainActivity.isDarkMode) {
            holder.publisherTextView.setTextColor(Color.WHITE);
            holder.commentContentTextView.setTextColor(Color.WHITE);
            holder.editCommentEditText.setTextColor(Color.WHITE);
        }
        else {
            holder.publisherTextView.setTextColor(Color.BLACK);
            holder.commentContentTextView.setTextColor(Color.BLACK);
            holder.editCommentEditText.setTextColor(Color.BLACK);

        }
        // Reset the profile image to a default image or clear it before setting a new one
        holder.profileImageView.setImageResource(R.drawable.ic_def_user);

        String uploader = comment.getPublisher();
        Bitmap profilePic = null;
        if (MainActivity.userDataList != null) {
            for (UserData user : MainActivity.userDataList) {
                if (user.getUsername().equals(uploader)) {
                    profilePic = user.getImage();
                    break;
                }
            }
        }

        if (profilePic != null) {
            holder.profileImageView.setImageBitmap(profilePic);
        } else {
            holder.profileImageView.setImageResource(R.drawable.ic_def_user);
        }

        holder.editCommentButton.setOnClickListener(v -> {
            holder.commentContentTextView.setVisibility(View.GONE);
            holder.editCommentEditText.setVisibility(View.VISIBLE);
            holder.saveCommentButton.setVisibility(View.VISIBLE);
            holder.editCommentButton.setVisibility(View.GONE);
            holder.editCommentEditText.setText(comment.getText());
        });

        holder.saveCommentButton.setOnClickListener(v -> {
            String newCommentText = holder.editCommentEditText.getText().toString().trim();
            if (!newCommentText.isEmpty()) {
                comment.setText(newCommentText);
                notifyItemChanged(position);
                holder.editCommentEditText.setVisibility(View.GONE);
                holder.saveCommentButton.setVisibility(View.GONE);
                holder.commentContentTextView.setVisibility(View.VISIBLE);
                holder.editCommentButton.setVisibility(View.VISIBLE);
            }
        });

        holder.deleteCommentButton.setOnClickListener(v -> {
            comments.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, comments.size());
            //RecyclerViewUtils.setRecyclerViewHeightBasedOnItems(recyclerView);
        });

        if (MainActivity.currentUser == null) {
            holder.deleteCommentButton.setVisibility(View.GONE);
            holder.editCommentButton.setVisibility(View.GONE);
        } else {
            holder.deleteCommentButton.setVisibility(View.VISIBLE);
            holder.editCommentButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView publisherTextView;
        TextView commentContentTextView;
        EditText editCommentEditText;
        Button editCommentButton;
        Button saveCommentButton;
        Button deleteCommentButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.commentProfilePic);
            publisherTextView = itemView.findViewById(R.id.publisherTextView);
            commentContentTextView = itemView.findViewById(R.id.commentContentTextView);
            editCommentEditText = itemView.findViewById(R.id.editCommentEditText);
            editCommentButton = itemView.findViewById(R.id.EditCommentButton);
            saveCommentButton = itemView.findViewById(R.id.SaveCommentButton);
            deleteCommentButton = itemView.findViewById(R.id.DeleteCommentButton);
        }
    }
}