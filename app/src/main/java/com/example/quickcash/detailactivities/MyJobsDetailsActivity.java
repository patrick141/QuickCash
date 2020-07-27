package com.example.quickcash.detailactivities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.quickcash.R;
import com.example.quickcash.adapters.RequestsAdapter;
import com.example.quickcash.models.Job;
import com.example.quickcash.models.Request;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class MyJobsDetailsActivity extends BaseJobDetailsActivity implements OnMapReadyCallback {
    public static final String TAG = "MyJobsDetailsActivity";
    private Job job;
    private TextView numReqs;
    private SwipeRefreshLayout swipeContainerRequests;
    private GoogleMap map;

    private RequestsAdapter requestsAdapter;
    private RecyclerView rvRequests;
    private List<Request> requests;

    public static final int REQUEST_CODE_MYDA_RDA = 190;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_jobs_details);
        job = (Job) Parcels.unwrap(getIntent().getParcelableExtra("JOB"));
        runToolbar();
        findtheViews();
        setJobContents(job);
        rvRequests = findViewById(R.id.rv_View_Requests);
        numReqs = findViewById(R.id.request_count);
        swipeContainerRequests = findViewById(R.id.swipe_container_requests);

        swipeContainerRequests.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainerRequests.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "Fetching more requests");
                queryRequests();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_demo);
        mapFragment.getMapAsync(this);

        requests = new ArrayList<>();
        requestsAdapter = new RequestsAdapter(this, requests);
        rvRequests.setAdapter(requestsAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvRequests.setLayoutManager(linearLayoutManager);
        rvRequests.setItemAnimator(new SlideInUpAnimator());
        numReqs.setText(getRequestCount() + " Requests");
        queryRequests();
    }

    protected void queryRequests() {
        ParseQuery<Request> query = ParseQuery.getQuery(Request.class);
        query.include(Request.KEY_REQUEST_USER);
        query.setLimit(20);
        query.addDescendingOrder(Request.KEY_CREATED_AT);
        query.whereEqualTo(Request.KEY_REQUEST_JOB, job);
        query.findInBackground(new FindCallback<Request>() {
            @Override
            public void done(List<Request> requests, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issues with getting requests", e);
                    return;
                }
                for (Request request : requests) {
                    Log.i(TAG, "Request User: " + request.getUser().getUsername() + " Comment: " + request.getComment() + " Post: " + request.getJob());
                }
                requestsAdapter.clear();
                requestsAdapter.addAll(requests);
                swipeContainerRequests.setRefreshing(false);
                requestsAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng myPlace = new LatLng(35.258599, -80.836403);
        map.addMarker(new MarkerOptions().position(myPlace).title("My Location"));
        map.moveCamera(CameraUpdateFactory.newLatLng(myPlace));
    }

    public int getRequestCount(){
        ParseQuery<Request> query = ParseQuery.getQuery(Request.class);
        query.whereEqualTo(Request.KEY_REQUEST_JOB, job);
        try {
            return query.count();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_MYDA_RDA && resultCode == RESULT_OK){
            requestsAdapter.clear();
            queryRequests();
        }
    }
}