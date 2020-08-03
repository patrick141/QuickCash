package com.example.quickcash.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quickcash.R;
import com.example.quickcash.models.Job;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
/**
 * SearchFragment
 *
 * This is the Search Fragment. In here, the user can search for
 * specific job postings.
 *
 * @author Patrick Amaro Rivera
 */


public class SearchFragment extends HomeFragment {

    public static final String TAG = "SearchFragment";
    private SearchView searchView;
    private RadioGroup searchFilter;
    private List<Job> jobsSearch;
    private String textSearch;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchView = view.findViewById(R.id.search_bar);
        searchFilter = view.findViewById(R.id.rg_filter);
        jobsSearch = new ArrayList<>();
        getSwipeContainer().setOnRefreshListener(null);

        searchView.setQueryHint(" Search here ");
        searchFilter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.filter_location:
                        break;
                    case R.id.filter_amount:
                        Collections.sort(jobsSearch, new Comparator<Job>() {
                            @Override
                            public int compare(Job j1, Job j2) {
                                if(j1.getPrice() > j2.getPrice()){
                                    return -1;
                                } else if(j1.getPrice() < j2.getPrice()){
                                    return 1;
                                } else{
                                    return 0;
                                }
                            }
                        });
                        jobsAdapter.clear();
                        jobsAdapter.addAll(jobsSearch);
                        break;
                    case R.id.filter_popularity:
                        Collections.sort(jobsSearch, new Comparator<Job>() {
                            @Override
                            public int compare(Job j1, Job j2) {
                                return j2.getJobRequestCount() - j1.getJobRequestCount();
                            }
                        });
                        jobsAdapter.clear();
                        jobsAdapter.addAll(jobsSearch);
                        break;
                }
            }
        });

        /**
         * This handles what is being searched in the search view.
         */
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String myText) {
                textSearch = myText;
                querySearchJobs(textSearch);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });
    }

    @Override
    protected void queryJobs(){
        ParseQuery<Job> query = ParseQuery.getQuery(Job.class);
        query.include(Job.KEY_JOB_USER);
        query.setLimit(20);
        query.addAscendingOrder(Job.KEY_JOB_DATE);
        query.whereNotEqualTo(Job.KEY_JOB_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(Job.KEY_JOB_ISTAKEN, false);
        query.whereGreaterThan(Job.KEY_JOB_DATE, new Date());
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
                jobsSearch.clear();
                jobsSearch.addAll(jobs);
                jobsAdapter.clear();
                jobsAdapter.addAll(jobs);
                swipeContainer.setRefreshing(false);
                jobsAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * This method types the enteredText in the searchView and
     * looks up job postings name that match the text.
     * @param enteredText
     */
    protected void querySearchJobs(String enteredText){
        if(enteredText == null){
            return;
        }
        ParseQuery<Job> query = ParseQuery.getQuery(Job.class);
        query.include(Job.KEY_JOB_USER);
        query.setLimit(5);
        query.addDescendingOrder(Job.KEY_CREATED_AT);
        query.whereNotEqualTo(Job.KEY_JOB_USER, ParseUser.getCurrentUser());
        query.whereNotEqualTo(Job.KEY_JOB_ISTAKEN, true);
        query.whereFullText(Job.KEY_JOB_NAME, enteredText);
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
                jobsSearch.clear();
                jobsSearch.addAll(jobs);
                jobsAdapter.clear();
                jobsAdapter.addAll(jobs);
                swipeContainer.setRefreshing(false);
                jobsAdapter.notifyDataSetChanged();
            }
        });
    }
}