package com.akshay.know_your_government_shdh;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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

    //move to news
    private ColorDrawable mBackground;
    private int backgroundColor;
    private Drawable deleteDrawable;
    private int intrinsicWidth;
    private int intrinsicHeight;
    private Paint mClearPaint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupComponents();
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                setUpLocation();
                convertLatLon();

                if (!currentLatLon.equals(""))
                {
                    if(networkChecker())
                    {
                        new OfficialLoader(MainActivity.this).execute(geoCodedLatLon);
                    }
                    else
                    {
                        noNetworkDialog(getString(R.string.networkErrorMsg1));
                    }
                }
                else
                {
                    LocationDialog(getString(R.string.locationErrorMsg1), 0);
                }
                swiper.setRefreshing(false);
            }
        });

        officialAdapter = new OfficialAdapter(officialArrayList,this);
        rv.setAdapter(officialAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        setUpLocation();
        convertLatLon();

        if (!currentLatLon.equals(""))
        {
            if(networkChecker())
            {
                new OfficialLoader(this).execute(geoCodedLatLon);
            }
            else
            {
                noNetworkDialog(getString(R.string.networkErrorMsg1));
            }
        }
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


        mBackground = new ColorDrawable();
        backgroundColor = getResources().getColor(R.color.red);
        deleteDrawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_delete);
        intrinsicWidth = deleteDrawable.getIntrinsicWidth();
        intrinsicHeight = deleteDrawable.getIntrinsicHeight();
        mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rv);
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
            //builder.setIcon(R.drawable.location_error);
            builder.setIcon(R.drawable.location_error);
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

        builder.setIcon(R.drawable.network_error);
        builder.setTitle(R.string.networkErrorTitle);
        builder.setMessage(message);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void updateOfficialData(ArrayList<Official> tempList)
    {
        officialArrayList.clear();
        if(tempList.size()!=0)
        {
            officialArrayList.addAll(tempList);
            no_location.setVisibility(View.GONE);
        }
        else
        {
            location.setText(getText(R.string.no_locs));
            no_location.setVisibility(View.VISIBLE);
        }
        officialAdapter.notifyDataSetChanged();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_LOCATION_REQUEST_CODE_ID)
        {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0] == PERMISSION_GRANTED)
            {
                detectLocation();
                if(networkChecker())
                {
                    LocationDialog("",1);
                    setUpLocation();
                    convertLatLon();
                    new OfficialLoader(this).execute(geoCodedLatLon);
                }
                else
                {
                    noNetworkDialog(getString(R.string.networkErrorMsg1));
                }
                return;
            }
        }

        LocationDialog(getString(R.string.locationErrorMsg1),0);
        location.setText(R.string.no_perm);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.opt_menu,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.search:
                if(networkChecker())
                {
                    search();
                }
                else
                    noNetworkDialog(getString(R.string.networkErrorMsg2));
                break;
            case R.id.about:
                Intent i = new Intent(this, AboutActivity.class);
                startActivity(i);
                break;
            default:
                Toast.makeText(this,"Invalid Option",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void search()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText et = new EditText(this);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(et);
        builder.setIcon(R.drawable.ic_search_accent);

        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id)
            {
                String searchString = et.getText().toString().trim();
                if(!searchString.equals(""))
                {
                    location.setText("");
                    new OfficialLoader(MainActivity.this).execute(searchString);
                }
                else
                {
                    Toast.makeText(MainActivity.this, R.string.nullSearchStringMsg, Toast.LENGTH_SHORT).show();
                    search();
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id)
            {
                Toast.makeText(MainActivity.this, R.string.negativeBtn2, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setMessage(R.string.searchMsg);
        builder.setTitle(R.string.searchTitle);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View view)
    {
        int position = rv.getChildAdapterPosition(view);
        Official temp = officialArrayList.get(position);

        Intent i = new Intent(this,OfficialActivity.class);
        i.putExtra("location", location.getText());
        i.putExtra("official",temp);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(0, ItemTouchHelper.LEFT);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            View itemView = viewHolder.itemView;
            int itemHeight = itemView.getHeight();

            boolean isCancelled = dX == 0 && !isCurrentlyActive;

            if (isCancelled) {
                clearCanvas(c, itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                return;
            }

            mBackground.setColor(backgroundColor);
            mBackground.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            mBackground.draw(c);

            int deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
            int deleteIconMargin = (itemHeight - intrinsicHeight) / 2;
            int deleteIconLeft = itemView.getRight() - deleteIconMargin - intrinsicWidth;
            int deleteIconRight = itemView.getRight() - deleteIconMargin;
            int deleteIconBottom = deleteIconTop + intrinsicHeight;


            deleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
            deleteDrawable.draw(c);

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);


        }

        private void clearCanvas(Canvas c, Float left, Float top, Float right, Float bottom) {
            c.drawRect(left, top, right, bottom, mClearPaint);

        }

        @Override
        public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
            return 0.7f;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            int position = rv.getChildAdapterPosition(viewHolder.itemView);
            officialArrayList.remove(position);
            officialAdapter.notifyDataSetChanged();
        }
    };

}
