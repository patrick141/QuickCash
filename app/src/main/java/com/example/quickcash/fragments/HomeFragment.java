package com.example.quickcash.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;
import com.example.quickcash.adapters.JobsAdapter;
import com.example.quickcash.adapters.RequestsAdapter;
import com.example.quickcash.models.Job;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";

    private RecyclerView rvJobs;
    private JobsAdapter jobsAdapter;
    private RequestsAdapter requestsAdapter;
    private List<Job> allJobs;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvJobs = view.findViewById(R.id.rvJobs);

        allJobs = new ArrayList<>();
        jobsAdapter = new JobsAdapter(getContext(), allJobs);

        rvJobs.setAdapter(jobsAdapter);
        rvJobs.setLayoutManager(new LinearLayoutManager(getContext()));
        queryJobs();
    }

    protected void queryJobs(){
        ParseQuery<Job> query = ParseQuery.getQuery(Job.class);
        query.include(Job.KEY_JOB_USER);
        query.setLimit(20);
        query.addDescendingOrder(Job.KEY_CREATED_AT);
        query.whereNotEqualTo(Job.KEY_JOB_USER, ParseUser.getCurrentUser());
        query.whereNotEqualTo(Job.KEY_JOB_ISTAKEN, true);
        query.findInBackground(new FindCallback<Job>() {
            @Override
            public void done(List<Job> jobs, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issues with getting jobs", e);
                    return;
                }
                for(Job job: jobs){
                    Log.i(TAG, "Job: " + job.getName() + ", username" + job.getUser().getUsername());
                }

                allJobs.addAll(jobs);
                jobsAdapter.notifyDataSetChanged();
            }
        });
    }

    public RecyclerView getRvJobs() {
        return rvJobs;
    }

    public JobsAdapter getJobsAdapter() {
        return jobsAdapter;
    }

    public List<Job> getAllJobs() {
        return allJobs;
    }

}