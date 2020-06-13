package com.akshay.know_your_government_shdh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }
    public void apiClicked(View v)
    {
        String dem_URL = "https://developers.google.com/civic-information/";
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(dem_URL));
        startActivity(i);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
