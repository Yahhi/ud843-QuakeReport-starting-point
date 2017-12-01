package com.example.android.quakereport;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SignalStrength;
import android.widget.TextView;
import java.text.DecimalFormat;

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
private double noValueToken = 1;
private int noValueToken2 = 1;
private GoogleMap mMap;
private double longitude = 1;
private double latitude  = 1;
private double distance = 1;
private String marker = "";
    SignalStrength signalstrength;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //context = this.getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detailsToolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Getting all data passed by intent
        Intent details = getIntent();
        String mag = details.getStringExtra("Magnitude");
        String locationMain = details.getStringExtra("Location");
        String locationOff = details.getStringExtra("LocationOff");
        String date = details.getStringExtra("Date");
        String time = details.getStringExtra("Time");
        String felt = details.getStringExtra("Felt");
        longitude = details.getDoubleExtra("Longitude", noValueToken);
        latitude = details.getDoubleExtra("Latitude", noValueToken);
        distance = details.getDoubleExtra("Distance", noValueToken);
        int depth = details.getIntExtra("Depth", noValueToken2);
        marker = locationOff + locationMain;
        //converting values to strings
        DecimalFormat formatter = new DecimalFormat("0.00000");
        String strLongitude = formatter.format(longitude);
        String strLatitude = formatter.format(latitude);
        String strDistance = formatter.format(distance);
        String strDepth = String.valueOf(depth);
        strDistance = strDistance + " km";
        //setting views
        TextView magnitudeView = (TextView) findViewById(R.id.magnitudeView);
        magnitudeView.setText(mag);
        TextView locationOffView = (TextView) findViewById(R.id.locationOffView);
        locationOffView.setText(locationOff);
        TextView locationMView = (TextView) findViewById(R.id.locationMView);
        locationMView.setText(locationMain);
        TextView dateView = (TextView) findViewById(R.id.dateView);
        dateView.setText(date);
        TextView timeView = (TextView) findViewById(R.id.timeView);
        timeView.setText(time);
        TextView longitudeView = (TextView) findViewById(R.id.longitudeView);
        longitudeView.setText(strLongitude);
        TextView latitudeView = (TextView) findViewById(R.id.latitudeView);
        latitudeView.setText(strLatitude);
        TextView depthView = (TextView) findViewById(R.id.depthView);
        depthView.setText(strDepth);
        TextView farawayView = (TextView) findViewById(R.id.farawayView);
        farawayView.setText(strDistance);
        TextView feltView = (TextView) findViewById(R.id.feltView);
        feltView.setText(felt);
}
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker and move the camera.
        LatLng sydney = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title(marker));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
