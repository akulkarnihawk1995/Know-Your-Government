package com.akshay.know_your_government_shdh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

public class PhotoDetailActivity extends AppCompatActivity
{
    private ConstraintLayout cs;
    private TextView location, title, name, party;
    private ImageView dp, partyLogo;
    private static final String TAG = "PhotoDetailActivity";
    private Official temp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        setupComponents();
        setUpLocation();
        fillData();
    }

    void setupComponents()
    {
        cs = findViewById(R.id.constrainedLayout);
        location = findViewById(R.id.location);
        title = findViewById(R.id.title);
        name = findViewById(R.id.name);
        party = findViewById(R.id.party);
        dp = findViewById(R.id.dp);
        partyLogo = findViewById(R.id.partyLogo);
    }

    void setUpLocation()
    {
        if(getIntent().hasExtra("location"))
            location.setText(getIntent().getStringExtra("location"));
        else
            location.setText("");
    }

    void fillData()
    {
        if(getIntent().hasExtra("official"))
        {
            temp = (Official) getIntent().getSerializableExtra("official");
            assert temp != null;
            title.setText(temp.getTitle());
            name.setText(temp.getName());
            party.setText(temp.getParty());

            if(temp.getParty().trim().toLowerCase().contains("democratic"))
                setUpDemocraticTheme();
            else if(temp.getParty().trim().toLowerCase().contains("republican"))
                setUpRepublicanTheme();
            else
                setUpNonPartisanTheme();

            loadProfilePicture(temp.getPhotoURL().trim());
        }

    }

    void setUpDemocraticTheme()
    {
        location.setBackgroundResource(R.color.dark_blue);
        partyLogo.setImageResource(R.drawable.dem_logo);
        cs.setBackgroundResource(R.color.blue);
        getWindow().setNavigationBarColor(getColor(R.color.blue));
    }

    void setUpRepublicanTheme()
    {
        location.setBackgroundResource(R.color.dark_red);
        partyLogo.setImageResource(R.drawable.rep_logo);
        cs.setBackgroundResource(R.color.red);
        getWindow().setNavigationBarColor(getColor(R.color.red));
    }
    void setUpNonPartisanTheme()
    {
        location.setBackgroundResource(R.color.extra_dark_grey);
        partyLogo.setImageResource(R.drawable.non_logo);
        cs.setBackgroundResource(R.color.dark_grey);
        getWindow().setNavigationBarColor(getColor(R.color.dark_grey));
    }

    void loadProfilePicture(String URL)
    {
        Log.d(TAG, "bp: loadProfilePicture: URL: " + URL);

        Glide.with(this)
                .load(URL)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.brokenimage)
                .into(dp);
    }

    public void partyLogoClicked(View v)
    {
        String dem_URL = "https://democrats.org";
        String rep_URL = "https://www.gop.com";

        if(temp.getParty().toLowerCase().trim().contains("democratic"))
        {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(dem_URL));
            startActivity(i);
        }
        else if(temp.getParty().toLowerCase().trim().contains("republican"))
        {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(rep_URL));
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}
