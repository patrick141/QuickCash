package com.example.quickcash.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quickcash.R;
import com.example.quickcash.detailactivities.JobDetailsActivity;
import com.example.quickcash.detailactivities.MyJobsDetailsActivity;
import com.example.quickcash.detailactivities.ToDoJobDetailsActivity;
import com.example.quickcash.fragments.HomeFragment;
import com.example.quickcash.fragments.ProfileFragment;
import com.example.quickcash.fragments.SearchFragment;
import com.example.quickcash.models.Job;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.example.quickcash.detailactivities.MyJobsDetailsActivity.REQUEST_CODE_MYDA_RDA;

/**
 * JobsAdapter Class
 *
 * This class will handle putting our job postings into a recycler view.
 */
public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.ViewHolder> {

    //This static int variables are used to create our relative time ago string
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    public static final int JOB_SEND_REQUEST_CODE = 1001;

    public static final String TAG = "JobsAdapter";

    private final Context context;
    private final List<Job> jobs;
    private Fragment fragment;

    public JobsAdapter(Context context, List<Job> jobs) {
        this.context = context;
        this.jobs = jobs;
    }

    public JobsAdapter(Context context, List<Job> jobs, Fragment fragment){
        this.context = context;
        this.jobs = jobs;
        this.fragment = fragment;
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

    /**
     * The ViewHolder class handles making our job views.
     */

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private CardView myJobCard;
        private TextView jobName;
        private TextView jobRequestorName;
        private ImageView jobPicture;
        private TextView jobDate;
        private TextView jobDatePosted;
        private TextView jobAddress;
        private TextView jobPrice;

        public static final int CCHEIGHT = 30;
        public static final int CCWIDTH = 30;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            jobName = itemView.findViewById(R.id.job_request_name);
            jobDate = itemView.findViewById(R.id.job_date);
            jobRequestorName = itemView.findViewById(R.id.job_username);
            jobPicture = itemView.findViewById(R.id.job_picture);
            jobDatePosted = itemView.findViewById(R.id.job_date_posted);
            jobAddress = itemView.findViewById(R.id.job_address);
            jobPrice = itemView.findViewById(R.id.job_price);
            myJobCard = itemView.findViewById(R.id.cv_job);
            itemView.setOnClickListener(this);
        }

        public void bind(final Job job) {
            jobName.setText(job.getName());
            jobDate.setText(dateDisplay(job.getJobDate()));
            jobRequestorName.setText(job.getUser().getUsername());
            ParseFile image = job.getImage();
            if(image !=  null){
                Glide.with(context).load(image.getUrl()).transform(new RoundedCornersTransformation(CCHEIGHT,CCWIDTH)).placeholder(R.drawable.logo).error(R.drawable.logo).into(jobPicture);
            } else{
                Glide.with(context).load(R.drawable.logo).transform(new RoundedCornersTransformation(CCHEIGHT,CCWIDTH)).placeholder(R.drawable.logo).error(R.drawable.logo).into(jobPicture);
            }
            jobDatePosted.setText(" " + getRelativeTimeAgo(job.getCreatedAt().toString()));
            jobAddress.setText(job.getAddress());
            jobPrice.setText("$" + String.format("%.2f", job.getPrice()));
        }

        /**
         * When user clicks on a job, they are sent to the JobDetailsActivity class.
         * @param view
         */
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                Job job = jobs.get(position);
                Log.d(context.getResources().getString(R.string.jobdetailOnClick), job.getName());

                Pair<View, String> p1 = Pair.create((View) jobPicture, context.getResources().getString(R.string.tr_job_image));
                Pair<View, String> p2 = Pair.create((View) jobName, context.getResources().getString(R.string.tr_job_name));
                Pair<View, String> p3 = Pair.create((View) jobPrice, context.getResources().getString(R.string.tr_job_price));
                Pair<View, String> p4 = Pair.create((View) jobRequestorName, context.getResources().getString(R.string.tr_job_creator));

                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) context, p1, p2, p3, p4);
                Intent intent = generateIntent(context, fragment, job);
                startActivityFor(context, intent, fragment, options);
            }
        }
    }

    /**
     * Since we are navigating between several different types of job details activities, we only care about
     * the fragment that the jobsAdapter has initialized. This method helps create our
     * Intent we want to use for navigation between views.
     *
     * Clicking job in HomeFragment -> JobDetailsActivity
     * Clicking job in ProfileFragment -> MyJobsDetailsActivity
     *
     * This function uses a switch statement that checks to see the fragment and creates
     * the intent based from there. It also makes sure to wrap the job so that these
     * other details classes can use that to display the job information.
     * @param context
     * @param fragment
     * @param job
     * @return
     */
    private Intent generateIntent(Context context, Fragment fragment, Job job){
        Intent intent = null;
        if(fragment != null){
            switch (fragment.getClass().getSimpleName()) {
                case HomeFragment.TAG:
                    intent = new Intent(context, JobDetailsActivity.class);
                    break;
                case SearchFragment.TAG:
                    intent = new Intent(context, JobDetailsActivity.class);
                    break;
                case ProfileFragment.TAG:
                    intent = new Intent(context, MyJobsDetailsActivity.class);
                    break;
                default:
                    intent = new Intent(context, ToDoJobDetailsActivity.class);
                    break;
            }
        } else {
            intent = new Intent(context, JobDetailsActivity.class);
        }
        intent.putExtra(Job.class.getSimpleName(), Parcels.wrap(job));
        return intent;
    }

    /**
     * This method determines how the new activity is going to be started. Since, we have
     * several different request codes, we only want to fist check to see if the adapter is
     * in a profile fragment and have that fragment start the activity (for request approve
     * and deny). The remaining fragments (or if it's null, the context) can start their
     * other activities with the options to bundle.
     * @param context
     * @param intent
     * @param fragment
     * @param options
     */
    private void startActivityFor(Context context, Intent intent, Fragment fragment, ActivityOptionsCompat options){
        if(fragment instanceof ProfileFragment){
            fragment.startActivityForResult(intent, REQUEST_CODE_MYDA_RDA);
        } else {
            if(fragment != null){
                fragment.startActivityForResult(intent, JOB_SEND_REQUEST_CODE, options.toBundle());
            } else{
                ((Activity) context).startActivityForResult(intent, JOB_SEND_REQUEST_CODE, options.toBundle());
            }
        }
    }

    /**
     * Converts a Date object's string and converts it into a timestamp from the time from that date.
     * Ex: "Thu Jul 16 08:30:00 EDT 2020" -> "3d" if today was Jul 19, 3 days past Jul 16.
     * Note: this function has been modified to display a relative date for job posts that have been
     * at least a week ago.
     **/
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String format = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(format, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            long time = sf.parse(rawJsonDate).getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + "m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + "h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + "d";
            }
        } catch (ParseException e) {
            Log.i(TAG, "getRelativeTimeAgo failed");
            e.printStackTrace();
        }
        return "";
    }
    /**
     * This methods converts the date class to a String. This is for the job's requested date and time.
     * @param date
     * @return
     */
    public static String timeNeed(Date date){
        return new SimpleDateFormat("MMM dd, YYYY hh:mm a zzz").format(date);
    }

    /**
     * This function converts the date to a String but just returns the date, month and year
     * @param date
     * @return
     */
    public static String dateDisplay(Date date){
        return new SimpleDateFormat("MM/dd/YY").format(date);
    }

    /**
     * Getters and setters
     */
    public Context getContext() {
        return context;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

}
