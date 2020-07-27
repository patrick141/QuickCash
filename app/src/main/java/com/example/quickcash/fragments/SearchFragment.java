package com.example.quickcash.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quickcash.R;
import com.example.quickcash.models.Job;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
        searchView.setQueryHint(" Search here ");
        /**
         * This handles what is being searched in the search view.
         */
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String myText) {
                querySearchJobs(myText);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });
    }

    /**
     * This method types the enteredText in the searchView and
     * looks up job postings name that match the text.
     * @param enteredText
     */
    protected void querySearchJobs(String enteredText){
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
                getJobsAdapter().clear();
                getJobsAdapter().addAll(jobs);
                getSwipeContainer().setRefreshing(false);
                getJobsAdapter().notifyDataSetChanged();
            }
        });
    }
}