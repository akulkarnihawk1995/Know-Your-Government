package com.akshay.know_your_government_shdh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "NewsActivity";
    ArrayList<Article> articleArrayList = new ArrayList<>();
    ArticleAdapter articleAdapter;

    TextView nn_msg1, nn_msg2, topHeadLines;
    Button tryAgain;
    SwipeRefreshLayout swiper;
    RecyclerView rv;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        setUpComponents();

        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {

                rv.setAdapter(articleAdapter);
                rv.setLayoutManager(new LinearLayoutManager(NewsActivity.this));
                if(networkChecker())
                {
                    nn_msg1.setVisibility(View.GONE);
                    nn_msg2.setVisibility(View.GONE);
                    tryAgain.setVisibility(View.GONE);
                    new NewsLoader(NewsActivity.this).execute(token);
                    Toast.makeText(NewsActivity.this, "Most Recent Headlines loaded", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    nn_msg1.setVisibility(View.VISIBLE);
                    nn_msg2.setVisibility(View.VISIBLE);
                    tryAgain.setVisibility(View.VISIBLE);
                }
                swiper.setRefreshing(false);
            }
        });

        articleAdapter = new ArticleAdapter(articleArrayList,this);
        rv.setAdapter(articleAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        rv.setAdapter(articleAdapter);
        rv.setLayoutManager(new LinearLayoutManager(NewsActivity.this));
        if(networkChecker())
        {
            nn_msg1.setVisibility(View.GONE);
            nn_msg2.setVisibility(View.GONE);
            tryAgain.setVisibility(View.GONE);
            new NewsLoader(NewsActivity.this).execute(token);
            Toast.makeText(NewsActivity.this, "Most Recent Headlines loaded", Toast.LENGTH_SHORT).show();
        }
        else
        {
            nn_msg1.setVisibility(View.VISIBLE);
            nn_msg2.setVisibility(View.VISIBLE);
            tryAgain.setVisibility(View.VISIBLE);
        }
    }

    private void setUpComponents()
    {
        swiper = findViewById(R.id.swiper);
        nn_msg1 = findViewById(R.id.nonetworkIcon);
        nn_msg2 = findViewById(R.id.nonetworkMsg);
        tryAgain = findViewById(R.id.tryAgain);
        rv = findViewById(R.id.recycler);
        token = getIntent().getStringExtra("token");
    }

    public boolean networkChecker()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm == null)
            return false;
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public void updateArticles(ArrayList<Article> articles)
    {
        articleArrayList.clear();
        if(articles.size()!=0)
        {
            articleArrayList.addAll(articles);
        }
        articleAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view)
    {
        int position = rv.getChildAdapterPosition(view);
        Article temp = articleArrayList.get(position);

        if(temp.getUrl() != null)
        {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(temp.getUrl()));
            startActivity(i);
        }

    }
}
