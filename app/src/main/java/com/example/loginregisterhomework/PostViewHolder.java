package com.example.loginregisterhomework;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class PostViewHolder extends RecyclerView.ViewHolder {
    ImageView postImageView;
    TextView postTitleTextView;


    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        postImageView = itemView.findViewById(R.id.postImageView);
        postTitleTextView = itemView.findViewById(R.id.postEditText);

    }

    public void bind(Post post) {

        Picasso.get().load(post.getImageUrl()).into(postImageView);

        postTitleTextView.setText(post.getText());

    }
}
