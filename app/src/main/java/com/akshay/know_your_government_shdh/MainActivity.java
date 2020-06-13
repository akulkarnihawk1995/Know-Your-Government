package com.akshay.know_your_government_shdh;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout swiper;
    private RecyclerView rv;
    private TextView location, no_location;
    private ArrayList<Official> officialArrayList = new ArrayList<>();
    private OfficialAdapter officialAdapter;
    private static final String TAG = "MainActivity";

    private static int MY_LOCATION_REQUEST_CODE_ID = 329;
    private LocationManager locationManager;
    private Criteria criteria;

    private String currentLatLon, geoCodedLatLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public boolean networkChecker()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm == null)
            return false;
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void setupComponents()
    {
        swiper = findViewById(R.id.swiper);
        rv = findViewById(R.id.recycler);
        location = findViewById(R.id.location);
        no_location = findViewById(R.id.location404);
        currentLatLon = "";
        geoCodedLatLon = "";
    }

    public void setUpLocation()
    {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();

        // GPS
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE_ID);
        }
        else
        {
            detectLocation();
        }
    }

    public void detectLocation()
    {
        String bestProvider = locationManager.getBestProvider(criteria, true);
        assert bestProvider != null;
        @SuppressLint("MissingPermission") Location currentLocation = locationManager.getLastKnownLocation(bestProvider);
        if (currentLocation != null)
        {
            currentLatLon = String.format(Locale.getDefault(),  "%.4f, %.4f", currentLocation.getLatitude(), currentLocation.getLongitude());
            location.setText(currentLatLon);
        }
        else
        {
            location.setText(R.string.no_locs);
        }
    }

    public void convertLatLon()
    {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try
        {
            List<Address> addresses;

            String loc = currentLatLon;
            if (!loc.trim().isEmpty())
            {
                String[] latLon = loc.split(",");
                double lat = Double.parseDouble(latLon[0]);
                double lon = Double.parseDouble(latLon[1]);

                addresses = geocoder.getFromLocation(lat, lon, 1);

                if(!addresses.get(0).getPostalCode().equals(""))
                {
                    geoCodedLatLon = addresses.get(0).getPostalCode();
                }
                else if(!addresses.get(0).getLocality().equals(""))
                {
                    geoCodedLatLon = addresses.get(0).getLocality();
                }
                Log.d(TAG, "bp: convertLatLon: addresses: " + addresses.get(0).getPostalCode());
                Toast.makeText(this, "Location Detected: " + addresses.get(0).getLocality(), Toast.LENGTH_SHORT).show();
            }

        }
        catch (IOException e)
        {
            Log.d(TAG, "EXCEPTION | convertLatLon: bp: " + e);
        }

    }

    private void LocationDialog(String message, int dismissFlag)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.create();

        if (dismissFlag == 0)
        {
            builder.setIcon(R.drawable.ic_location_error);
            builder.setTitle(R.string.locationErrorTitle);
            builder.setMessage(message);

            dialog = builder.create();
            dialog.show();
        }
        else if (dismissFlag == 1)
            dialog.dismiss();
    }

    public void noNetworkDialog(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.ic_network_error);
        builder.setTitle(R.string.networkErrorTitle);
        builder.setMessage(message);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
