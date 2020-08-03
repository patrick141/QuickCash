package com.example.quickcash.detailactivities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.quickcash.R;
import com.example.quickcash.adapters.RequestsAdapter;
import com.example.quickcash.databinding.ActivityMyJobsDetailsBinding;
import com.example.quickcash.models.Job;
import com.example.quickcash.models.Request;
import com.example.quickcash.models.User;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * MyJobsDetailsActivity
 *
 * This activity shows the user's own jobs and its details.
 */

public class MyJobsDetailsActivity extends BaseJobDetailsActivity{
    public static final int REQUEST_CODE_MYDA_RDA = 190;
    public static final String TAG = "MyJobsDetailsActivity";

    private Job job;
    private TextView numReqs;
    private SwipeRefreshLayout swipeContainerRequests;
    private Button btnDeleteJob;

    private RequestsAdapter requestsAdapter;
    private RecyclerView rvRequests;
    private List<Request> requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMyJobsDetailsBinding binding = ActivityMyJobsDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        job = Parcels.unwrap(getIntent().getParcelableExtra(Job.class.getSimpleName()));
        runToolbar();
        findtheViews();
        setJobContents(job);
        rvRequests = binding.rvViewRequests;
        numReqs = binding.requestCount;
        btnDeleteJob = binding.btnDeleteJob;
        swipeContainerRequests = binding.swipeContainerRequests;

        swipeContainerRequests.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainerRequests.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "Fetching more requests");
                queryRequests();
            }
        });

        /**
         * If a job is finished, there is no need for the user to be able to delete the job
         */
        if(job.isFinished()){
            btnDeleteJob.setVisibility(View.GONE);
        }

        requests = new ArrayList<>();
        requestsAdapter = new RequestsAdapter(this, requests);
        rvRequests.setAdapter(requestsAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvRequests.setLayoutManager(linearLayoutManager);
        rvRequests.setItemAnimator(new SlideInUpAnimator());
        numReqs.setText(getString(R.string.requestCount_1) + " " + job.getJobRequestCount() + " " + getString(R.string.requestCount_2));
        queryRequests();

        btnDeleteJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playDeleteJobAlertDialog();
            }
        });
    }

    /**
     * This method plays an alert dialog to get another confirmation for a user to
     * delete a job.
     */
    private void playDeleteJobAlertDialog() {
        AlertDialog deleteJobDialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.MJDA_delete))
                .setMessage(getResources().getString(R.string.MJDA_delete_info))
                .setIcon(R.drawable.logo)

                .setPositiveButton(getResources().getString(R.string.MJDA_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        deleteJob(job);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.MJDA_no), null)
                .create();
        deleteJobDialog.show();
    }

    /**
     * This function queries requests that only point to this job.
     */
    protected void queryRequests() {
        ParseQuery<Request> query = ParseQuery.getQuery(Request.class);
        query.include(Request.KEY_REQUEST_USER);
        query.setLimit(20);
        query.addDescendingOrder(Request.KEY_CREATED_AT);
        query.whereEqualTo(Request.KEY_REQUEST_JOB, job);
        query.findInBackground(new FindCallback<Request>() {
            @Override
            public void done(List<Request> requests, ParseException e) {
                if (e != null) {
                    Log.e(TAG, getString(R.string.requestQError), e);
                    return;
                }
                Log.i(TAG, getString(R.string.requestQSuccess));
                for (Request request : requests) {
                    Log.i(TAG, request.getUser().getUsername() + " " + request.getComment());
                }
                requestsAdapter.clear();
                requestsAdapter.addAll(requests);
                swipeContainerRequests.setRefreshing(false);
                requestsAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * This method deletes the job and also deletes all requests made to it.
     * It first goes through all of its requests that have been saved and delete it.
     * Then, it removes the job from the user's myJobs array.
     * Finally, it deletes the job object from the Parse DB.
     * @param job
     */
    public void deleteJob(Job job){
        ParseUser user = ParseUser.getCurrentUser();
        List<Request> requests = job.getRequests();
        if(requests != null) {
            for (final Request request : requests) {
                request.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, getString(R.string.MJDA_delete_reqs_failure) + " " + request.getComment());
                        }
                        Log.i(TAG, getString(R.string.MJDA_delete_reqs_success));
                    }
                });
            }
        }
        user.removeAll(User.KEY_USER_JOBS, Arrays.asList(job));
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    Log.e(TAG, getString(R.string.MJDA_delete_job_array_failure));
                }
                Log.i(TAG, getString(R.string.MJDA_delete_job_array_sucess));
            }
        });
        job.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG, getString(R.string.MJDA_delete_job_error));
                }
                Log.i(TAG, getString(R.string.MJDA_delete_job_success));
                Toast.makeText(MyJobsDetailsActivity.this, getString(R.string.jobDelConfirm), Toast.LENGTH_SHORT).show();
            }
        });
        setResult(RESULT_OK);
        finish();
    }

    /**
     * If user has deleted a request, this function updates the query and count.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_MYDA_RDA && resultCode == RESULT_OK){
            numReqs.setText(getString(R.string.requestCount_1) + " " + job.getJobRequestCount() + " " + getString(R.string.requestCount_2));
            requestsAdapter.clear();
            queryRequests();
        }
    }
}