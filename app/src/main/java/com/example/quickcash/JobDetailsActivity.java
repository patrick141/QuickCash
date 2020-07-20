package com.example.quickcash;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quickcash.databinding.ActivityJobDetailsBinding;
import com.example.quickcash.models.Job;
import com.example.quickcash.models.Request;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import static com.example.quickcash.adapters.JobsAdapter.getRelativeTimeAgo;

public class JobDetailsActivity extends AppCompatActivity {

    public static final String TAG = "JobDetailsActivity";
    private Job job;
    private TextView jobNameJDA;
    private TextView jobDateJDA;
    private TextView jobDateCreatedJDA;
    private TextView jobUserJDA;
    private TextView jobPriceJDA;
    private TextView jobAddressJDA;
    private ImageView jobImageJDA;
    private LinearLayout llJobRequest;
    private EditText etRequestJDA;
    private Button btnSubmitRequest;
    private TextView sentReq;
    private RecyclerView rvRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJobDetailsBinding binding = ActivityJobDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
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
        jobImageJDA = binding.jdaJobImage;

        llJobRequest = binding.llJdaRequest;
        etRequestJDA = binding.jdaEtRequest;
        btnSubmitRequest = binding.jdaButtonRequest;
        sentReq = binding.sentView;
        rvRequests = binding.rvViewRequests;

        sentReq.setVisibility(View.GONE);
        if(job.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
            llJobRequest.setVisibility(View.GONE);
            rvRequests.setVisibility(View.VISIBLE);
        }
        else{
            llJobRequest.setVisibility(View.VISIBLE);
            rvRequests.setVisibility(View.GONE);
        }

        jobNameJDA.setText(job.getName());
        jobDateJDA.setText(job.getJobDate().toString());
        jobDateCreatedJDA.setText(getRelativeTimeAgo(job.getCreatedAt().toString()));
        ParseFile jobImage = job.getImage();
        if(jobImage == null){
            Glide.with(this).load(R.drawable.logo).into(jobImageJDA);
        }
        else{
            Glide.with(this).load(job.getImage().getUrl()).into(jobImageJDA);
        }
        jobUserJDA.setText(job.getUser().getUsername());
        jobPriceJDA.setText("$" + job.getPrice().toString());
        jobAddressJDA.setText(job.getAddress());

        jobUserJDA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(JobDetailsActivity.this, ProfileActivity.class);
                i.putExtra("PROFILE", Parcels.wrap(job));
                JobDetailsActivity.this.startActivity(i);
            }
        });

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
            }
        });
    }
}