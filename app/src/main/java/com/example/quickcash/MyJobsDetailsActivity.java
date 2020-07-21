package com.example.quickcash;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quickcash.adapters.RequestsAdapter;
import com.example.quickcash.models.Job;
import com.example.quickcash.models.Request;
import com.google.android.gms.maps.GoogleMap;
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

public class MyJobsDetailsActivity extends AppCompatActivity {
    public static final String TAG = "MyJobsDetailsActivity";
    private Job job;

    private TextView jobNameJDA;
    private TextView jobDateJDA;
    private TextView jobDateCreatedJDA;
    private TextView jobDescriptionJDA;
    private TextView jobUserJDA;
    private TextView jobPriceJDA;
    private TextView jobAddressJDA;
    private ImageView jobImageJDA;
    private Toolbar toolbar;
    private GoogleMap map;

    private RequestsAdapter requestsAdapter;
    private RecyclerView rvRequests;
    private List<Request> requests;

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
}