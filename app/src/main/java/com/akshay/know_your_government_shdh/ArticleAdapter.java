package com.akshay.know_your_government_shdh;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleViewHolder>
{
    private static final String TAG = "ArticleAdapter";
    private List<Article> articleList;
    private NewsActivity newsActivity;

    public ArticleAdapter(List<Article> articleList, NewsActivity newsActivity)
    {
        this.articleList = articleList;
        this.newsActivity = newsActivity;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_vh, parent, false);
        itemView.setOnClickListener(newsActivity);
        return new ArticleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        final Article temp = articleList.get(position);
        holder.title.setText(temp.getTitle());
        holder.publishedAt.setText(temp.getPublishedAt());
        ImageView picture = holder.image;
        ImageButton share = holder.share;

        if(isNull(temp.getUrlToImage()))
            picture.setBackgroundResource(R.drawable.placeholder);
        else
        {
            Glide.with(newsActivity)
                    .load(temp.getUrlToImage())
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .into(picture);
        }

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(!isNull(temp.getUrl()))
                {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = "Checkout this new story from News Gateway:\n" + temp.getUrl();
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Know Your Government");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    newsActivity.startActivity(sharingIntent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    private boolean isNull(String data)
    {
        return data == null || data.equals("null");
    }
}
