package com.example.quickcash.fragments;

import android.util.Log;

import com.example.quickcash.models.Job;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * ProfileFragment
 *
 * This is the ProfileFragment. In here, the user can see job
 * postings that they have submitted and view requests.
 *
 * @author Patrick Amaro Rivera
 */


public class ProfileFragment extends HomeFragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }*/

    @Override
    protected void queryJobs() {
        ParseQuery<Job> query = ParseQuery.getQuery(Job.class);
        query.include(Job.KEY_JOB_USER);
        query.whereEqualTo(Job.KEY_JOB_USER, ParseUser.getCurrentUser());
        query.setLimit(20);
        query.addDescendingOrder(Job.KEY_CREATED_AT);
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