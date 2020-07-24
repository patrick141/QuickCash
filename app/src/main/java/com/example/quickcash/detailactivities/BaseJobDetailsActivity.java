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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.parse.ParseFile;

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
}