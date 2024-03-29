package com.example.quickcash.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.quickcash.R;
import com.example.quickcash.adapters.JobsAdapter;
import com.example.quickcash.databinding.FragmentHomeBinding;
import com.example.quickcash.models.Job;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

import static android.app.Activity.RESULT_OK;
import static com.example.quickcash.adapters.JobsAdapter.JOB_SEND_REQUEST_CODE;

/**
 * HomeFragment
 *
 * This is the Home Fragment. In here, the user can see job
 * postings from other users.
 *
 * @author Patrick Amaro Rivera
 */

public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";
    public static final int maxJobsView = 100;
    protected RecyclerView rvJobs;
    protected JobsAdapter jobsAdapter;
    protected List<Job> allJobs;
    protected SwipeRefreshLayout swipeContainer;
    protected FragmentHomeBinding binding;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvJobs = view.findViewById(R.id.rv_Jobs);
        allJobs = new ArrayList<>();

        jobsAdapter = new JobsAdapter(getContext(), allJobs, this);
        rvJobs.setAdapter(jobsAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvJobs.setLayoutManager(linearLayoutManager);
        rvJobs.setItemAnimator(new SlideInUpAnimator());

        swipeContainer = view.findViewById(R.id.swipe_Container);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "fetching more data");
                queryJobs();
            }
        });
        queryJobs();
    }

    protected void queryJobs(){
        ParseQuery<Job> query = ParseQuery.getQuery(Job.class);
        query.include(Job.KEY_JOB_USER);
        query.setLimit(maxJobsView);
        query.addAscendingOrder(Job.KEY_JOB_DATE);
        query.whereNotEqualTo(Job.KEY_JOB_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(Job.KEY_JOB_ISTAKEN, false);
        query.whereGreaterThan(Job.KEY_JOB_DATE, new Date());
        query.findInBackground((objects, e) -> {
            if(e == null){
                for (ParseObject result : objects) {
                    Log.d("Object found ",result.getObjectId());
                }
            }else{
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == JOB_SEND_REQUEST_CODE && resultCode == RESULT_OK){
            jobsAdapter.clear();
            queryJobs();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}