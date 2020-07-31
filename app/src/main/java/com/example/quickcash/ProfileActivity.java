package com.example.quickcash;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.quickcash.adapters.JobsAdapter;
import com.example.quickcash.models.Job;
import com.example.quickcash.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static com.example.quickcash.adapters.JobsAdapter.timeNeed;

public class ProfileActivity extends AppCompatActivity {
    public static final String TAG = "ProfileActivity";
    public static final String DEFAULT_RM1 = "User since ";
    public static final String PHONE_URI = "tel:";

    private ParseUser user;
    private Toolbar toolbar;
    private ImageView ivProfilePic;
    private TextView tvUsername;
    private TextView tvDescription;
    private TextView tvPhoneNumber;
    private RatingBar rbUserRating;

    private RecyclerView rvProfile;
    private JobsAdapter pJobsAdapter;
    private List<Job> profileJobs;
    private SwipeRefreshLayout swipeContainter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar_Home);
        setSupportActionBar(toolbar);
        ivProfilePic = findViewById(R.id.iv_profliePic);
        tvUsername = findViewById(R.id.tv_Username);
        tvDescription = findViewById(R.id.tv_UserSince);
        tvPhoneNumber = findViewById(R.id.tv_phone);
        rbUserRating = findViewById(R.id.rb_user_rating);

        final ParseUser user = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));

        tvUsername.setText(user.getUsername());
        String description = user.getString(User.KEY_USER_DESCRIPTION);
        if(description != null){
            tvDescription.setText(description);
        } else{
            tvDescription.setText(DEFAULT_RM1 + timeNeed(user.getCreatedAt()));
        }
        String phone = user.getString(User.KEY_USER_PHONE);
        if(phone != null){
            tvPhoneNumber.setText(phone);
        } else{
            tvPhoneNumber.setText("7047047040");
        }
        ParseFile userImage = (ParseFile) user.get(User.KEY_USER_IMAGE);
        if(userImage == null){
            Glide.with(this).load(R.drawable.logo).into(ivProfilePic);
        } else{
            Glide.with(this).load(userImage.getUrl()).into(ivProfilePic);
        }

        rbUserRating.setRating((float) user.getDouble(User.KEY_USER_RATING));

        rvProfile = findViewById(R.id.rv_Jobs);
        profileJobs = new ArrayList<>();
        pJobsAdapter = new JobsAdapter(this, profileJobs);
        rvProfile.setLayoutManager(new LinearLayoutManager(this));
        rvProfile.setAdapter(pJobsAdapter);
        swipeContainter = findViewById(R.id.swipe_Container);

        swipeContainter.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryProfileJobs(user);
            }
        });
        queryProfileJobs(user);
    }

    /**
     * This method takes the user and queries jobs that have been created by that user that
     * still haven't been assigned to anyone yet.
     * @param user
     */
    private void queryProfileJobs(ParseUser user) {
        ParseQuery<Job> query = ParseQuery.getQuery(Job.class);
        query.include(Job.KEY_JOB_USER);
        query.setLimit(20);
        query.addDescendingOrder(Job.KEY_CREATED_AT);
        query.whereEqualTo(Job.KEY_JOB_USER, user);
        query.whereEqualTo(Job.KEY_JOB_ISTAKEN, false);
        query.findInBackground(new FindCallback<Job>() {
            @Override
            public void done(List<Job> jobs, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issues with getting jobs", e);
                    return;
                }
                for(Job job: jobs){
                    Log.i(TAG, "Job: " + job.getName() + ", username" + job.getUser().getUsername());
                }
                pJobsAdapter.clear();
                pJobsAdapter.addAll(jobs);
                swipeContainter.setRefreshing(false);
                pJobsAdapter.notifyDataSetChanged();
            }
        });
    }

}