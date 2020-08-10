package com.example.quickcash.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quickcash.R;
import com.example.quickcash.models.Job;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.quickcash.adapters.JobsAdapter.JOB_FINISHED_CODE;

/**
 * JobTasksFragment
 *
 * This is the JobTasksFragment. This is where a user can see
 * jobs that they have been approved to do.
 *
 * @author Patrick Amaro Rivera
 */


public class JobTasksFragment extends HomeFragment {
    public static final String TAG = "JobTasksFragment";
    private TextView tvInfo;


    public JobTasksFragment() {
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
                jobsAdapter.clear();
                jobsAdapter.addAll(jobs);
                swipeContainer.setRefreshing(false);
                jobsAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == JOB_FINISHED_CODE && resultCode == RESULT_OK){
            jobsAdapter.clear();
            queryJobs();
        }
    }
}