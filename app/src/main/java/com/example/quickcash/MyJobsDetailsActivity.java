package com.example.quickcash;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.adapters.RequestsAdapter;
import com.example.quickcash.models.Job;

public class MyJobsDetailsActivity extends AppCompatActivity {
    private Job job;

    private RequestsAdapter requestsAdapter;
    private RecyclerView rvRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_jobs_details);
    }
}