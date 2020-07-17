package com.example.quickcash.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.quickcash.R;
import com.example.quickcash.models.Job;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
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

    private ImageView ivProfilePic;
    private TextView tvUsername;
    private TextView tvUserSince;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivProfilePic = view.findViewById(R.id.iv_profliePic);
        tvUsername = view.findViewById(R.id.tv_Username);
        tvUserSince = view.findViewById(R.id.tv_UserSince);

        ParseUser user = ParseUser.getCurrentUser();
        tvUsername.setText(user.getUsername());
        tvUserSince.setText("User since " + user.getCreatedAt().toString());
        ParseFile userImage = (ParseFile) user.get("profilePic");
        if(userImage == null){
            Glide.with(getContext()).load(R.drawable.logo).into(ivProfilePic);
        }
        else{
            Glide.with(getContext()).load(userImage.getUrl()).into(ivProfilePic);
        }
    }

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