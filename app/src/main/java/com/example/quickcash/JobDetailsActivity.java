package com.example.quickcash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.quickcash.databinding.ActivityJobDetailsBinding;
import com.example.quickcash.models.Job;
import com.example.quickcash.models.Request;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.List;

import static com.example.quickcash.adapters.JobsAdapter.getRelativeTimeAgo;
import static com.example.quickcash.adapters.JobsAdapter.timeNeed;

/**
 *  JobDetailsActivity
 *
 *  This class handles viewing details of jobs. The Activity changes based off if the user is click-
 *  ing on their own job or if they are viewing other job posts and want to submit a request.
 */
public class JobDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

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
    private GoogleMap map;

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
        jobNameJDA = findViewById(R.id.jda_job_name);
        jobDateJDA = findViewById(R.id.jda_job_date);
        jobDateCreatedJDA = findViewById(R.id.jda_job_created_date);
        jobUserJDA = findViewById(R.id.jda_job_user);
        jobPriceJDA = findViewById(R.id.jda_job_price);
        jobAddressJDA = findViewById(R.id.jda_job_address);
        jobDescriptionJDA = findViewById(R.id.jda_job_description);
        jobImageJDA = findViewById(R.id.jda_job_image);

        llJobRequest = binding.llJdaRequest;
        etRequestJDA = binding.jdaEtRequest;
        btnSubmitRequest = binding.jdaButtonRequest;
        sentReq = binding.sentView;
        sentReq.setVisibility(View.GONE);

            /**
             *  Assuming the user is clicking on another user's job. They will go to the default
             *  layout where they can submit a job request.
             */
        llJobRequest.setVisibility(View.VISIBLE);

        /**
         * If the user has already submitted a job, to prevent the user from sending multiple jobs
         * They are given an alternative layout that shows that they have already submitted a job request.
         */
        if(job.getRequests() != null){
            if(submittedRequest(job.getRequests())){
                llJobRequest.setVisibility(View.GONE);
                sentReq.setVisibility(View.VISIBLE);
            }
        }

        jobNameJDA.setText(job.getName());
        jobDateJDA.setText(timeNeed(job.getJobDate()));
        jobDescriptionJDA.setText(job.getDescription());
        jobDateCreatedJDA.setText(" " + getRelativeTimeAgo(job.getCreatedAt().toString()));
        ParseFile jobImage = job.getImage();
        if(jobImage == null){
            Glide.with(this).load(R.drawable.logo).into(jobImageJDA);
        } else{
            Glide.with(this).load(job.getImage().getUrl()).into(jobImageJDA);
        }
        jobUserJDA.setText(job.getUser().getUsername());
        jobPriceJDA.setText("$" + String.format("%.2f", job.getPrice()));
        jobAddressJDA.setText(job.getAddress());


        jobUserJDA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(JobDetailsActivity.this, ProfileActivity.class);
                i.putExtra("PROFILE", Parcels.wrap(job.getUser()));
                JobDetailsActivity.this.startActivity(i);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_demo);
        mapFragment.getMapAsync(this);


        /**
         *  User can submit request by filling out the editText and clicking on the submit button.
         */
        btnSubmitRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = etRequestJDA.getText().toString();
                ParseUser currentUser = ParseUser.getCurrentUser();
                /**
                 * The user cannot submit a request if they do not fill out the editText.
                 */
                if(comment.isEmpty()){
                    Toast.makeText(JobDetailsActivity.this, "Please fill in request comment", Toast.LENGTH_SHORT).show();
                    return;
                }
                final Request request = new Request();
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
                Toast.makeText(JobDetailsActivity.this, "Request has been submitted", Toast.LENGTH_SHORT).show();
                Intent i = new Intent();
                i.putExtra("MINE", 21);
                setResult(RESULT_OK, i);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },2000);
            }
        });
    }

    /**
     * This function checks to see if the current user has already submitted a request for the job. This iterates through the array
     * and returns true if it finds the matching user id.
     * @param requests
     * @return
     */
    public boolean submittedRequest(List<Request> requests){
        for (Request request: requests){
            if(request.getUser().hasSameId(ParseUser.getCurrentUser())){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng myPlace = new LatLng(35.258599, -80.836403);
        map.addMarker(new MarkerOptions().position(myPlace).title("My Location"));
        map.moveCamera(CameraUpdateFactory.newLatLng(myPlace));
    }
}