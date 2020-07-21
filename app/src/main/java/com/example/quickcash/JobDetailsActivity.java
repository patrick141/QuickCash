package com.example.quickcash;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quickcash.adapters.RequestsAdapter;
import com.example.quickcash.databinding.ActivityJobDetailsBinding;
import com.example.quickcash.models.Job;
import com.example.quickcash.models.Request;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static com.example.quickcash.adapters.JobsAdapter.getRelativeTimeAgo;
import static com.example.quickcash.adapters.JobsAdapter.timeNeed;

/**
 *  JobDetailsActivity
 *
 *  This class handles viewing details of jobs. The Activity changes based off if the user is click-
 *  ing on their own job or if they are viewing other job posts and want to submit a request.
 */
public class JobDetailsActivity extends AppCompatActivity {

    public static final String TAG = "JobDetailsActivity";
    private Job job;
    private TextView jobNameJDA;
    private TextView jobDateJDA;
    private TextView jobDateCreatedJDA;
    private TextView jobDescriptionJDA;
    private TextView jobUserJDA;
    private TextView jobPriceJDA;
    private TextView jobAddressJDA;
    private ImageView jobImageJDA;
    private LinearLayout llJobRequest;
    private EditText etRequestJDA;
    private Button btnSubmitRequest;
    private TextView sentReq;
    private Toolbar toolbar;

    private RecyclerView rvRequests;
    private RequestsAdapter requestsAdapter;
    private List<Request> requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJobDetailsBinding binding = ActivityJobDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        toolbar = (Toolbar) findViewById(R.id.toolbar_Home);
        setSupportActionBar(toolbar);
        job = (Job) Parcels.unwrap(getIntent().getParcelableExtra("JOB"));

        /**This sets our activity_job_details layout based off the job information.
         * We are also adding a request option.
         */
        jobNameJDA = binding.jdaJobName;
        jobDateJDA = binding.jdaJobDate;
        jobDateCreatedJDA = binding.jdaJobCreatedDate;
        jobUserJDA = binding.jdaJobUser;
        jobPriceJDA = binding.jdaJobPrice;
        jobAddressJDA = binding.jdaJobAddress;
        jobDescriptionJDA = binding.jdaJobDescription;
        jobImageJDA = binding.jdaJobImage;

        llJobRequest = binding.llJdaRequest;
        etRequestJDA = binding.jdaEtRequest;
        btnSubmitRequest = binding.jdaButtonRequest;
        sentReq = binding.sentView;
        rvRequests = binding.rvViewRequests;

        requests = new ArrayList<>();
        requestsAdapter = new RequestsAdapter(JobDetailsActivity.this, requests);
        sentReq.setVisibility(View.GONE);

        /**
         * This function checks to see if a user job he viewing is his job postings.
         * If it is, a different layout appears showing the requests of that job.
         * Only the job creator can view the requests and approve or deny.
         */
        if(job.getUser().hasSameId(ParseUser.getCurrentUser())){
            llJobRequest.setVisibility(View.GONE);
            rvRequests.setVisibility(View.VISIBLE);
            rvRequests.setAdapter(requestsAdapter);
            rvRequests.setLayoutManager(new LinearLayoutManager(JobDetailsActivity.this));
            queryRequests();
        }
        else{
            /**
             *  Assuming the user is clicking on another user's job. They will go to the default
             *  layout where they can submit a job request.
             */
            llJobRequest.setVisibility(View.VISIBLE);
            rvRequests.setVisibility(View.GONE);

            /**
             * If the user has already submitted a job, to prevent the user from sending multiple jobs
             * They are given an alternative layout that shows that they have already submitted a job request.
             */
            if(job.getRequests() != null){
                if(checkME(job.getRequests(), ParseUser.getCurrentUser())){
                    llJobRequest.setVisibility(View.GONE);
                    sentReq.setVisibility(View.VISIBLE);
                }
            }

        }

        jobNameJDA.setText(job.getName());
        jobDateJDA.setText(timeNeed(job.getJobDate()));
        jobDescriptionJDA.setText(job.getDescription());
        jobDateCreatedJDA.setText(" " + getRelativeTimeAgo(job.getCreatedAt().toString()));
        ParseFile jobImage = job.getImage();
        if(jobImage == null){
            Glide.with(this).load(R.drawable.logo).into(jobImageJDA);
        }
        else{
            Glide.with(this).load(job.getImage().getUrl()).into(jobImageJDA);
        }
        jobUserJDA.setText(job.getUser().getUsername());
        jobPriceJDA.setText("$" + String.format("%.2f", job.getPrice()));
        jobAddressJDA.setText(job.getAddress());

        /*
        jobUserJDA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(JobDetailsActivity.this, ProfileActivity.class);
                i.putExtra("PROFILE", Parcels.wrap(job));
                JobDetailsActivity.this.startActivity(i);
            }
        });*/


        btnSubmitRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = etRequestJDA.getText().toString();
                final Request request = new Request();
                ParseUser currentUser = ParseUser.getCurrentUser();
                request.setUser(currentUser);
                request.setComment(comment);
                request.setJob(job);
                request.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e !=null){
                            Log.e(TAG, "Error while creating job request", e);
                            return;
                        }
                        Log.i(TAG, "Request was successful");
                        etRequestJDA.setText("");
                        llJobRequest.setVisibility(View.GONE);
                        sentReq.setVisibility(View.VISIBLE);
                    }
                });
                job.addJobRequest(request);
                job.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e!= null){
                            Log.e(TAG, "Error while adding job request", e);
                        }
                        Log.i(TAG, "It's now in DB");
                    }
                });
                finish();
            }
        });
    }

    /**
     * This method queries our requests into our rvRequests. If finds Requests that point to the job.
     * It shows up to 20 results.
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
                if(e != null){
                    Log.e(TAG, "Issues with getting requests", e);
                    return;
                }
                for(Request request: requests){
                    Log.i(TAG, "Request User: " + request.getUser().getUsername() + " Comment: " + request.getComment() + " Post: " + request.getJob());
                }
                requestsAdapter.addAll(requests);
                requestsAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * This function checks to see if a user have already submitted a request for the job. If a request matches the user id,
     * it returns true or false if user's id is not there, meaning they haven't sent a request yet.
     * @param requests
     * @param user
     * @return
     */
    public boolean checkME(List<Request> requests, ParseUser user){
        List<ParseUser> users = new ArrayList<>();
        for (Request request: requests){
            users.add(request.getUser());
        }
        for(ParseUser parseUser: users){
            if(parseUser.hasSameId(user)){
                return true;
            }
        }
        return false;
    }
}