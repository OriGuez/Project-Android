package com.example.project_android.adapters;

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
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_android.MainActivity;
import com.example.project_android.R;
import com.example.project_android.model.Comment;
import com.example.project_android.viewModel.CommentsViewModel;

import java.util.List;

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.ViewHolder> {
    private CommentsViewModel commentsViewModel;
    private List<Comment> comments;
    private RecyclerView recyclerView;
    private LayoutInflater inflater;
    private Context context;

    public CommentRecyclerViewAdapter(Context context, List<Comment> comments, RecyclerView recyclerView) {
        this.context = context;
        this.comments = comments;
        this.recyclerView = recyclerView;
        this.inflater = LayoutInflater.from(context);

        if (context instanceof ViewModelStoreOwner) {
            ViewModelStoreOwner owner = (ViewModelStoreOwner) context;
            this.commentsViewModel = new ViewModelProvider(owner).get(CommentsViewModel.class);
        } else {
            throw new IllegalArgumentException("Context must implement ViewModelStoreOwner");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(view, this, commentsViewModel, comments, recyclerView, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void updateCommentsList(List<Comment> newCommentsList) {
        comments.clear();
        comments.addAll(newCommentsList);
        notifyDataSetChanged();
    }

    public void addComment(Comment newComment) {
        comments.add(newComment);
        notifyItemInserted(comments.size() - 1);
        recyclerView.scrollToPosition(comments.size() - 1);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView publisherTextView;
        TextView commentContentTextView;
        EditText editCommentEditText;
        Button editCommentButton;
        Button saveCommentButton;
        Button deleteCommentButton;

        private CommentRecyclerViewAdapter adapter;
        private CommentsViewModel commentsViewModel;
        private List<Comment> comments;
        private RecyclerView recyclerView;
        private Context context;

        public ViewHolder(@NonNull View itemView, CommentRecyclerViewAdapter adapter, CommentsViewModel commentsViewModel, List<Comment> comments, RecyclerView recyclerView, Context context) {
            super(itemView);
            this.adapter = adapter;
            this.commentsViewModel = commentsViewModel;
            this.comments = comments;
            this.recyclerView = recyclerView;
            this.context = context;

            profileImageView = itemView.findViewById(R.id.commentProfilePic);
            publisherTextView = itemView.findViewById(R.id.publisherTextView);
            commentContentTextView = itemView.findViewById(R.id.commentContentTextView);
            editCommentEditText = itemView.findViewById(R.id.editCommentEditText);
            editCommentButton = itemView.findViewById(R.id.EditCommentButton);
            saveCommentButton = itemView.findViewById(R.id.SaveCommentButton);
            deleteCommentButton = itemView.findViewById(R.id.DeleteCommentButton);

            editCommentButton.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Comment comment = comments.get(position);
                    commentContentTextView.setVisibility(View.GONE);
                    editCommentEditText.setVisibility(View.VISIBLE);
                    saveCommentButton.setVisibility(View.VISIBLE);
                    editCommentButton.setVisibility(View.GONE);
                    editCommentEditText.setText(comment.getContent());
                }
            });

            saveCommentButton.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Comment comment = comments.get(position);
                    String newCommentText = editCommentEditText.getText().toString().trim();
                    if (!newCommentText.isEmpty()) {
                        commentsViewModel.update(comment.getId(),new Comment(newCommentText,comment.getUserId(),comment.getVideoId())).observe((LifecycleOwner) context, resp -> {
                            if (resp.isSuccessful()) {
                                comment.setContent(newCommentText);
                                adapter.notifyItemChanged(position);
                                editCommentEditText.setVisibility(View.GONE);
                                saveCommentButton.setVisibility(View.GONE);
                                commentContentTextView.setVisibility(View.VISIBLE);
                                editCommentButton.setVisibility(View.VISIBLE);
                            }
                        });

                    }
                }
            });

            deleteCommentButton.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    commentsViewModel.delete(comments.get(position).getId()).observe((LifecycleOwner) context, resp -> {
                        if (resp.isSuccessful()) {
                            comments.remove(position);
                            adapter.notifyItemRemoved(position);
                            adapter.notifyItemRangeChanged(position, comments.size());
                        }
                    });
                }
            });
        }

        public void bind(Comment comment) {
            publisherTextView.setText(comment.getUserId());
            commentContentTextView.setText(comment.getContent());
            if (MainActivity.isDarkMode) {
                publisherTextView.setTextColor(Color.WHITE);
                commentContentTextView.setTextColor(Color.WHITE);
                editCommentEditText.setTextColor(Color.WHITE);
            } else {
                publisherTextView.setTextColor(Color.BLACK);
                commentContentTextView.setTextColor(Color.BLACK);
                editCommentEditText.setTextColor(Color.BLACK);
            }
            profileImageView.setImageResource(R.drawable.ic_def_user);

            String uploaderID = comment.getUserId();
            Bitmap profilePic = null;

            if (profilePic != null) {
                profileImageView.setImageBitmap(profilePic);
            } else {
                profileImageView.setImageResource(R.drawable.ic_def_user);
            }

            if (MainActivity.currentUser == null || !MainActivity.currentUser.getId().equals(uploaderID)) {
                deleteCommentButton.setVisibility(View.GONE);
                editCommentButton.setVisibility(View.GONE);
            } else {
                deleteCommentButton.setVisibility(View.VISIBLE);
                editCommentButton.setVisibility(View.VISIBLE);
            }
        }
    }
}
