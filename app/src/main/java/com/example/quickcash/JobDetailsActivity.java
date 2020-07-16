package com.example.quickcash;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.quickcash.databinding.ActivityJobDetailsBinding;
import com.example.quickcash.models.Job;
import com.parse.ParseFile;

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

    private EditText etRequestJDA;
    private Button btnSubmitRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJobDetailsBinding binding = ActivityJobDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        job = (Job) Parcels.unwrap(getIntent().getParcelableExtra("JOB"));

        jobNameJDA = binding.jdaJobName;
        jobDateJDA = binding.jdaJobDate;
        jobDateCreatedJDA = binding.jdaJobCreatedDate;
        jobUserJDA = binding.jdaJobUser;
        jobPriceJDA = binding.jdaJobPrice;
        jobAddressJDA = binding.jdaJobAddress;
        jobImageJDA = binding.jdaJobImage;

        etRequestJDA = binding.jdaEtRequest;
        btnSubmitRequest = binding.jdaButtonRequest;

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

    }
}