package com.example.quickcash.detailactivities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import com.parse.ParseFile;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

import static com.example.quickcash.adapters.JobsAdapter.getRelativeTimeAgo;
import static com.example.quickcash.adapters.JobsAdapter.timeNeed;

public class MyJobsDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String TAG = "MyJobsDetailsActivity";
    private Job job;

    private TextView jobNameJDA;
    private TextView jobDateJDA;
    private TextView jobDateCreatedJDA;
    private TextView jobDescriptionJDA;
    private TextView jobUserJDA;
    private TextView jobPriceJDA;
    private TextView jobAddressJDA;
    private TextView numReqs;
    private ImageView jobImageJDA;
    private Toolbar toolbar;
    private GoogleMap map;

    private RequestsAdapter requestsAdapter;
    private RecyclerView rvRequests;
    private List<Request> requests;

    public static final int REQUEST_CODE_MYDA_RDA = 190;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_jobs_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar_Home);
        setSupportActionBar(toolbar);
        job = (Job) Parcels.unwrap(getIntent().getParcelableExtra("JOB"));

        jobNameJDA = findViewById(R.id.jda_job_name);
        jobDateJDA = findViewById(R.id.jda_job_date);
        jobDateCreatedJDA = findViewById(R.id.jda_job_created_date);
        jobUserJDA = findViewById(R.id.jda_job_user);
        jobPriceJDA = findViewById(R.id.jda_job_price);
        jobAddressJDA = findViewById(R.id.jda_job_address);
        jobDescriptionJDA = findViewById(R.id.jda_job_description);
        jobImageJDA = findViewById(R.id.jda_job_image);
        rvRequests = findViewById(R.id.rv_View_Requests);
        numReqs = findViewById(R.id.request_count);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_demo);
        mapFragment.getMapAsync(this);

        requests = new ArrayList<>();
        requestsAdapter = new RequestsAdapter(this, requests);
        rvRequests.setAdapter(requestsAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvRequests.setLayoutManager(linearLayoutManager);
        rvRequests.setItemAnimator(new SlideInUpAnimator());


        jobNameJDA.setText(job.getName());
        jobDateJDA.setText(timeNeed(job.getJobDate()));
        jobDescriptionJDA.setText(job.getDescription());
        jobDateCreatedJDA.setText(" " + getRelativeTimeAgo(job.getCreatedAt().toString()));
        ParseFile jobImage = job.getImage();
        if(jobImage == null){
            Glide.with(this).load(R.drawable.logo).into(jobImageJDA);
        }
        else{
            Glide.with(this).load(job.getImage().getUrl()).into(jobImageJDA);
        }
        jobUserJDA.setText(job.getUser().getUsername());
        jobPriceJDA.setText("$" + String.format("%.2f", job.getPrice()));
        jobAddressJDA.setText(job.getAddress());
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
                requestsAdapter.addAll(requests);
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