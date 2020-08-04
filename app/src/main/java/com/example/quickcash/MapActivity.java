package com.example.quickcash;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.quickcash.adapters.MapInfoAdapter;
import com.example.quickcash.models.Job;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final int zoomLength = 5;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private Toolbar toolbar;
    private HashMap<Marker, Job> markers;
    private MapInfoAdapter mapInfoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        markers = new HashMap<>();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_search);
        mapFragment.getMapAsync(this);
        toolbar = findViewById(R.id.toolbar_Home);
        mapInfoAdapter = new MapInfoAdapter(this, markers);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        ParseQuery<Job> query = ParseQuery.getQuery(Job.class);
        query.include(Job.KEY_JOB_USER);
        query.setLimit(20);
        query.whereNotEqualTo(Job.KEY_JOB_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(Job.KEY_JOB_ISTAKEN, false);
        query.whereGreaterThan(Job.KEY_JOB_DATE, new Date());
        try {
            List<Job> jobs = query.find();
            for(Job job: jobs){
                ParseGeoPoint geoPoint = job.getParseGeoPoint(Job.KEY_JOB_LOCATION);
                LatLng myPlace;
                if(geoPoint != null){
                    myPlace = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                } else{
                    myPlace = new LatLng(getResources().getFloat(R.dimen.map_lat_default), getResources().getFloat(R.dimen.map_lon_default));
                }
                Marker marker = map.addMarker(new MarkerOptions().position(myPlace));
                map.setInfoWindowAdapter(null);
                markers.put(marker, job);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(getResources().getFloat(R.dimen.map_lat_default),getResources().getFloat(R.dimen.map_lon_default)),zoomLength));
        map.setInfoWindowAdapter(mapInfoAdapter);
    }
}