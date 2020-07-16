package com.example.quickcash.fragments;

import android.util.Log;

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


    public MyJobsFragment() {
        // Required empty public constructor
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
                getJobsAdapter().notifyDataSetChanged();
                getJobsAdapter().clear();
                getJobsAdapter().addAll(jobs);
                getSwipeContainer().setRefreshing(false);
                getJobsAdapter().notifyDataSetChanged();
            }
        });
    }
}