package com.example.quickcash;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.quickcash.adapters.MapInfoAdapter;
import com.example.quickcash.fragments.HomeFragment;
import com.example.quickcash.models.Job;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final int FIND_LOC_CODE = 139;
    public static final int zoomLength = 10;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private Toolbar toolbar;
    private HashMap<Marker, Job> markers;
    private MapInfoAdapter mapInfoAdapter;

    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        markers = new HashMap<>();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();
        toolbar = findViewById(R.id.toolbar_Home);
        mapInfoAdapter = new MapInfoAdapter(this, markers);
        setSupportActionBar(toolbar);
    }

    /**
     * This method gets the user's location (if permission granted).
     */
    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FIND_LOC_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLocation = location;
                    mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_search);
                    mapFragment.getMapAsync(MapActivity.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if(currentLocation != null) {
            LatLng myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            MarkerOptions myMarker = new MarkerOptions().position(myLocation).title(getString(R.string.my_location)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            map.addMarker(myMarker);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoomLength));
        }
        ParseQuery<Job> query = ParseQuery.getQuery(Job.class);
        query.include(Job.KEY_JOB_USER);
        query.setLimit(HomeFragment.maxJobsView);
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
        map.setInfoWindowAdapter(mapInfoAdapter);
        map.setOnInfoWindowClickListener(mapInfoAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MapInfoAdapter.GM_REQUEST_CODE &&  resultCode == RESULT_OK){
            Intent i = new Intent();
            setResult(RESULT_OK, i);
            finish();
        }
    }

    /**
     * This method ensures if asked, user can grant us the permission to get their location so we can
     * plot it into the MapActivity.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case FIND_LOC_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fetchLastLocation();
                }
                break;
        }
    }
}