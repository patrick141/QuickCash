package com.example.quickcash.detailactivities;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import com.example.quickcash.R;
import com.example.quickcash.models.Job;
import com.google.android.gms.maps.OnMapReadyCallback;

import org.parceler.Parcels;

public class ToDoJobDetailsActivity extends BaseJobDetailsActivity implements OnMapReadyCallback {
    private Toolbar toolbar;
    private Job job;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_job_details);
        job = (Job) Parcels.unwrap(getIntent().getParcelableExtra("JOB"));
        runToolbar();
        findtheViews();
        setJobContents(job);
    }
}