package com.example.quickcash.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quickcash.R;
import com.example.quickcash.adapters.MyAssignedJobsAdapter;
import com.example.quickcash.models.Job;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
/**
 * MyJobsFragment
 *
 * This is the MyJobsFragment. This is where a user can see
 * jobs that they have been approved to do.
 *
 * @author Patrick Amaro Rivera
 */


public class MyJobsFragment extends HomeFragment {
    private MyAssignedJobsAdapter myAdapter;
    private TextView tvInfo;


    public MyJobsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_jobs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvInfo = view.findViewById(R.id.MTFtext);
        myAdapter = new MyAssignedJobsAdapter(getContext(), getAllJobs());
        getRvJobs().setAdapter(myAdapter);
    }

    /**.
     * This query job posts whenever the current user has been assigned to
     * do a job.
     */
    @Override
    protected void queryJobs() {
        ParseQuery<Job> query = ParseQuery.getQuery(Job.class);
        query.include(Job.KEY_JOB_USER);
        query.whereEqualTo(Job.KEY_JOB_ASSIGNED_USER, ParseUser.getCurrentUser());
        query.setLimit(20);
        query.addDescendingOrder(Job.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Job>() {
            @Override
            public void done(List<Job> jobs, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issues with getting my jobs", e);
                    return;
                }
                for(Job job: jobs){
                    Log.i(TAG, "Job: " + job.getName() + ", username" + job.getUser().getUsername());
                }
                getAllJobs().addAll(jobs);
                myAdapter.notifyDataSetChanged();
                myAdapter.clear();
                myAdapter.addAll(jobs);
                getSwipeContainer().setRefreshing(false);
                myAdapter.notifyDataSetChanged();
            }
        });
    }
}