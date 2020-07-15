package com.example.quickcash.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quickcash.JobDetailsActivity;
import com.example.quickcash.R;
import com.example.quickcash.models.Job;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

//This class will handle making our job requests into a recycler view.
public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.ViewHolder> {

    public static final String TAG = "JobsAdapter";
    Context context;
    List<Job> jobs;
    public JobsAdapter(Context context, List<Job> jobs){
        this.context = context;
        this.jobs= jobs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_job, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Job job = jobs.get(position);
        Log.e(TAG, "isTaken: " + job.isTaken());
        holder.bind(job);
    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    public void addAll(List<Job> jobs){
        jobs.addAll(jobs);
        notifyDataSetChanged();
    }

    public void clear() {
        jobs.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView JobName;
        private TextView JobRequestorName;
        private ImageView JobPicture;
        private LinearLayout llUserDetails;
        private TextView JobDatePosted;
        private TextView JobAddress;
        private TextView JobDescription;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            JobName = itemView.findViewById(R.id.JobRequestName);
            JobRequestorName = itemView.findViewById(R.id.JobUserName);
            JobPicture = itemView.findViewById(R.id.JobPic);
            llUserDetails = itemView.findViewById(R.id.LLrequestUserInfo);
            JobDatePosted = itemView.findViewById(R.id.JobDatePosted);
            JobDescription = itemView.findViewById(R.id.JobDescription);
            JobAddress = itemView.findViewById(R.id.JobAddress);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Job job = jobs.get(position);
            Log.d("Load job details for ", job.getName());
            Intent i = new Intent(context, JobDetailsActivity.class);
            i.putExtra("REQUEST", Parcels.wrap(job));
            context.startActivity(i);
        }

        public void bind(Job request) {
            JobName.setText(request.getName());
            JobRequestorName.setText(request.getUser().getUsername());
            ParseFile image = request.getImage();
            if(image != null){
                Log.i(TAG, "This post has " + image);
                Glide.with(context).load(request.getImage().getUrl()).placeholder(R.drawable.ic_launcher_background).into(JobPicture);
            }
            JobDatePosted.setText(" " + getRelativeTimeAgo(request.getCreatedAt().toString()));
            JobDescription.setText(request.getDescription());
            JobAddress.setText(request.getAddress());
        }
    }

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String instaFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(instaFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String[] check = relativeDate.split(" ");
        return check[0] + check[1].charAt(0);
    }


}
