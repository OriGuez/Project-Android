package com.example.project_android;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class CommentAdapter extends ArrayAdapter<Video.Comment> {
    private List<Video.Comment> comments;

    private LayoutInflater inflater;

    public CommentAdapter(Context context, List<Video.Comment> comments) {
        super(context, 0, comments);
        this.comments = comments;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.comment_item, parent, false);
        }

        Video.Comment comment = getItem(position);

        ImageView profileImageView = convertView.findViewById(R.id.commentProfilePic);
        assert comment != null;
        String uploader = comment.getPublisher();
        Bitmap profilePic = null;
        if (MainActivity.userDataList != null) {
            for (UserData user : MainActivity.userDataList) {
                if (user.getUsername().equals(uploader)) {
                    profilePic = user.getImage();
                }
            }
        }


        if (profilePic != null)
            profileImageView.setImageBitmap(profilePic);

        TextView publisherTextView = convertView.findViewById(R.id.publisherTextView);
        TextView commentContentTextView = convertView.findViewById(R.id.commentContentTextView);
        Button deleteCommentButton = convertView.findViewById(R.id.DeleteCommentButton);
        EditText editCommentEditText = convertView.findViewById(R.id.editCommentEditText);
        Button editCommentButton = convertView.findViewById(R.id.EditCommentButton);
        Button saveCommentButton = convertView.findViewById(R.id.SaveCommentButton);

        publisherTextView.setText(comment.getPublisher());
        commentContentTextView.setText(comment.getText());
        editCommentButton.setOnClickListener(v -> {
            commentContentTextView.setVisibility(View.GONE);
            editCommentEditText.setVisibility(View.VISIBLE);
            saveCommentButton.setVisibility(View.VISIBLE);
            editCommentButton.setVisibility(View.GONE);
            editCommentEditText.setText(comment.getText());
        });
        saveCommentButton.setOnClickListener(v -> {
            String newCommentText = editCommentEditText.getText().toString().trim();
            if (!newCommentText.isEmpty()) {
                comment.setText(newCommentText);
                notifyDataSetChanged();
                editCommentEditText.setVisibility(View.GONE);
                saveCommentButton.setVisibility(View.GONE);
                commentContentTextView.setVisibility(View.VISIBLE);
                editCommentButton.setVisibility(View.VISIBLE);

            }
        });
        deleteCommentButton.setOnClickListener(v -> {
            // Remove the comment from the list
            comments.remove(position);
            notifyDataSetChanged();
        });
        if (MainActivity.currentUser == null) {
            deleteCommentButton.setVisibility(View.GONE);
            editCommentButton.setVisibility(View.GONE);
        }

        // Set publisher name and comment content

        return convertView;
    }

}
