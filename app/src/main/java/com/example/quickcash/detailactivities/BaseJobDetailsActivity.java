package com.example.quickcash.detailactivities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.quickcash.R;
import com.example.quickcash.models.Job;
import com.example.quickcash.models.User;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

import static com.example.quickcash.adapters.JobsAdapter.getRelativeTimeAgo;
import static com.example.quickcash.adapters.JobsAdapter.timeNeed;

/**
 * BaseJobDetailsActivity class
 *
 * This class will hold out sample item_job_detail class. This is for code sharing purposes.
 */

public class BaseJobDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String TAG = "BaseJobDetailsActivity";
    private Toolbar toolbar;
    private TextView jobNameJDA;
    private TextView jobDateJDA;
    private TextView jobDateCreatedJDA;
    private TextView jobDescriptionJDA;
    private TextView jobUserJDA;
    private TextView jobPriceJDA;
    private TextView jobAddressJDA;
    private ImageView jobImageJDA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * This method sets our toolbar.
     */
    public void runToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar_Home);
        setSupportActionBar(toolbar);
    }

    public void findtheViews(){
        jobNameJDA = findViewById(R.id.jda_job_name);
        jobDateJDA = findViewById(R.id.jda_job_date);
        jobDateCreatedJDA = findViewById(R.id.jda_job_created_date);
        jobUserJDA = findViewById(R.id.jda_job_user);
        jobPriceJDA = findViewById(R.id.jda_job_price);
        jobAddressJDA = findViewById(R.id.jda_job_address);
        jobDescriptionJDA = findViewById(R.id.jda_job_description);
        jobImageJDA = findViewById(R.id.jda_job_image);
    }

    public void setJobContents(Job job){
        if(job!= null) {
            jobNameJDA.setText(job.getName());
            jobDateJDA.setText(timeNeed(job.getJobDate()));
            jobDescriptionJDA.setText(job.getDescription());
            jobDateCreatedJDA.setText(" " + getRelativeTimeAgo(job.getCreatedAt().toString()));
            ParseFile jobImage = job.getImage();
            if (jobImage == null) {
                Glide.with(this).load(R.drawable.logo).into(jobImageJDA);
            } else {
                Glide.with(this).load(job.getImage().getUrl()).into(jobImageJDA);
            }
            jobUserJDA.setText(job.getUser().getUsername());
            jobPriceJDA.setText("$" + String.format("%.2f", job.getPrice()));
            jobAddressJDA.setText(job.getAddress());
        } else {
            Log.e(TAG, "Did not get Job");
        }
    }

    public TextView getJobNameJDA() {
        return jobNameJDA;
    }

    public void setJobNameJDA(TextView jobNameJDA) {
        this.jobNameJDA = jobNameJDA;
    }

    public TextView getJobDateJDA() {
        return jobDateJDA;
    }

    public void setJobDateJDA(TextView jobDateJDA) {
        this.jobDateJDA = jobDateJDA;
    }

    public TextView getJobDateCreatedJDA() {
        return jobDateCreatedJDA;
    }

    public void setJobDateCreatedJDA(TextView jobDateCreatedJDA) {
        this.jobDateCreatedJDA = jobDateCreatedJDA;
    }

    public TextView getJobDescriptionJDA() {
        return jobDescriptionJDA;
    }

    public void setJobDescriptionJDA(TextView jobDescriptionJDA) {
        this.jobDescriptionJDA = jobDescriptionJDA;
    }

    public TextView getJobUserJDA() {
        return jobUserJDA;
    }

    public void setJobUserJDA(TextView jobUserJDA) {
        this.jobUserJDA = jobUserJDA;
    }

    public TextView getJobPriceJDA() {
        return jobPriceJDA;
    }

    public void setJobPriceJDA(TextView jobPriceJDA) {
        this.jobPriceJDA = jobPriceJDA;
    }

    public TextView getJobAddressJDA() {
        return jobAddressJDA;
    }

    public void setJobAddressJDA(TextView jobAddressJDA) {
        this.jobAddressJDA = jobAddressJDA;
    }

    public ImageView getJobImageJDA() {
        return jobImageJDA;
    }

    public void setJobImageJDA(ImageView jobImageJDA) {
        this.jobImageJDA = jobImageJDA;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    /**
     * This is a function that calculates a user Rating based off a user finishing an assigned job.
     * @return
     */
    public double getUpdatedUR(){
        ParseUser user = ParseUser.getCurrentUser();
        int num1 = 0;
        ArrayList<Job> jobs = (ArrayList<Job>) user.get(User.KEY_USER_JOBS);
        for(Job job: jobs){
            if(job.isFinished()) num1 += 1;
        }
        num1 = (jobs != null) ? num1:0;
        double d1 = (jobs!=null) ? (double) num1 / jobs.size() : 0;
        d1 = d1 * 2.5 + 1;

        ArrayList<Job> myJobs = null;
        try {
            myJobs = (ArrayList<Job>) user.fetchIfNeeded().get(User.KEY_USER_COMPLETED_JOBS);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int num2 = myJobs != null ? myJobs.size():0;

        ParseQuery<Job> query = ParseQuery.getQuery(Job.class);
        query.whereEqualTo(Job.KEY_JOB_ASSIGNED_USER, user);
        int num3 = 0;
        try {
            num3 = query.count();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        double d2 = (double) num2 / num3;
        d2 = d2 * 2.5 + 1;

        double finalRating = d1 + d2;
        return (finalRating > 5) ? 5.00: finalRating;
    }

    public void updateUR(){
        ParseUser user = ParseUser.getCurrentUser();
        user.put(User.KEY_USER_RATING, getUpdatedUR());
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Log.i(TAG, " Updated UR");
                } else {
                    Log.e(TAG, "Error changing UR");
                }
            }
        });
    }
}