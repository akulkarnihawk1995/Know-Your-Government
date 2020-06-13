package com.akshay.know_your_government_shdh;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ArticleViewHolder extends RecyclerView.ViewHolder
{
    ImageButton share;
    TextView title, publishedAt;
    ImageView image;
    ArticleViewHolder(@NonNull View itemView)
    {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        publishedAt = itemView.findViewById(R.id.publishedAt);
        image = itemView.findViewById(R.id.image);
        share = itemView.findViewById(R.id.share);
    }
}
