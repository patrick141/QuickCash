package com.example.quickcash.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quickcash.JobDetailsActivity;
import com.example.quickcash.R;
import com.example.quickcash.models.Job;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.List;

import static com.example.quickcash.adapters.JobsAdapter.getRelativeTimeAgo;

/**
 * This class will be used to modify the Jobs RV in our profile activity
 */

public class MyAssignedJobsAdapter extends RecyclerView.Adapter<MyAssignedJobsAdapter.ViewHolder> {
    public static final String TAG = "MyAssignedJobsAdapter";
    private final Context context;
    private final List<Job> jobs;

    public MyAssignedJobsAdapter(Context context, List<Job> jobs) {
        this.context = context;
        this.jobs = jobs;
    }

    @NonNull
    @Override
    public MyAssignedJobsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_job, parent, false);
        return new MyAssignedJobsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAssignedJobsAdapter.ViewHolder holder, int position) {
        Job job = jobs.get(position);
        holder.bind(job);
    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    public void addAll(List<Job> jobs) {
        this.jobs.addAll(jobs);
        notifyDataSetChanged();
    }

    public void clear() {
        jobs.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView jobName;
        private TextView jobRequestorName;
        private ImageView jobPicture;
        private TextView jobDate;
        private TextView jobDatePosted;
        private TextView jobAddress;
        private TextView jobDescription;
        private TextView jobPrice;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            jobName = itemView.findViewById(R.id.job_request_name);
            jobDate = itemView.findViewById(R.id.job_date);
            jobRequestorName = itemView.findViewById(R.id.job_username);
            jobPicture = itemView.findViewById(R.id.job_picture);
            jobDatePosted = itemView.findViewById(R.id.job_date_posted);
            jobAddress = itemView.findViewById(R.id.job_address);
            jobPrice = itemView.findViewById(R.id.job_price);
            itemView.setOnClickListener(this);
        }

        public void bind(final Job job) {
            jobName.setText(job.getName());
            jobDate.setText(job.getJobDate().toString());
            jobRequestorName.setText(job.getUser().getUsername());
            ParseFile image = job.getImage();
            if (image != null) {
                Log.i(TAG, "This post has " + image.getUrl());
                Glide.with(context).load(job.getImage().getUrl()).placeholder(R.drawable.ic_launcher_background).into(jobPicture);
            } else {
                Glide.with(context).load(R.drawable.logo).placeholder(R.drawable.ic_launcher_background).into(jobPicture);
            }
            jobDatePosted.setText(" " + getRelativeTimeAgo(job.getCreatedAt().toString()));
            jobAddress.setText(job.getAddress());
            jobPrice.setText("$" + String.format("%.2f", job.getPrice()));
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                Job job = jobs.get(position);
                Log.d("Load job details for ", job.getName());
                Intent i = new Intent(context, JobDetailsActivity.class);
                Toast.makeText(context, job.getName(), Toast.LENGTH_SHORT).show();
                i.putExtra("JOB", Parcels.wrap(job));
                context.startActivity(i);
            }
        }
    }
}
