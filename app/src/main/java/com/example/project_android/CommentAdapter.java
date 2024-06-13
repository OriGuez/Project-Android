package com.example.project_android;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
public class CommentAdapter extends ArrayAdapter<Video.Comment>{

    private LayoutInflater inflater;
    public CommentAdapter(Context context, List<Video.Comment> comments) {
        super(context, 0, comments);
        inflater = LayoutInflater.from(context);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.comment_item, parent, false);
        }

        Video.Comment comment = getItem(position);

        ImageView profileImageView = convertView.findViewById(R.id.commentProfilePic);
        String uploader=comment.getPublisher();
        Bitmap profilePic=null;
        if (MainActivity.userDataList!=null)
        {
            for (UserData user : MainActivity.userDataList) {
                if (user.getUsername().equals(uploader)) {
                    profilePic = user.getImage();
                }
            }
        }


        if (MainActivity.currentUser !=null)
            profileImageView.setImageBitmap(profilePic);
        TextView publisherTextView = convertView.findViewById(R.id.publisherTextView);
        TextView commentContentTextView = convertView.findViewById(R.id.commentContentTextView);

        // Set publisher name and comment content
        publisherTextView.setText(comment.getPublisher());
        commentContentTextView.setText(comment.getText());

        return convertView;
    }

}
