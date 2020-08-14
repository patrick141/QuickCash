package com.example.quickcash.detailactivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.quickcash.R;
import com.example.quickcash.databinding.ActivityNotificationDetailsBinding;
import com.example.quickcash.databinding.ItemJobBinding;
import com.example.quickcash.databinding.ItemRequestBinding;
import com.example.quickcash.models.Job;
import com.example.quickcash.models.Notification;
import com.example.quickcash.models.Request;
import com.example.quickcash.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import static com.example.quickcash.adapters.JobsAdapter.dateDisplay;
import static com.example.quickcash.adapters.JobsAdapter.timeNeed;

public class NotificationDetailsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView tvDes;
    private Job job;
    private Request request;
    private Notification notification;
    private ActivityNotificationDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = binding.toolbarNda.toolbarHome;
        setSupportActionBar(toolbar);

        notification = Parcels.unwrap(getIntent().getParcelableExtra(Notification.class.getSimpleName()));
        notification.setHasRead(true);
        notification.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!= null){
                    e.printStackTrace();
                }
            }
        });
        request = notification.getRequest();
        job = notification.getJob();
        tvDes = findViewById(R.id.notif_messenge);

        tvDes.setText(notification.getMessage());

        setProfileContent(notification.getSender());
        ItemJobBinding itemJobBinding = binding.referJob;
        if (job != null) {
            setJobContent(job);
        } else{
            View view = itemJobBinding.getRoot();
            view.setVisibility(View.GONE);
        }
        ItemRequestBinding itemRequestBinding = binding.referRequest;
        if(request != null){
            setRequestContent(request);
        } else{
            View view = itemRequestBinding.getRoot();
            view.setVisibility(View.GONE);
        }
        ImageView ivProfilePic = binding.profileNda.ivProfliePic;
        ParseFile userImage = null;
        try {
            userImage = (ParseFile) notification.getSender().fetchIfNeeded().get(User.KEY_USER_IMAGE);
            if(userImage == null){
                Glide.with(this).load(R.drawable.logo).transform(new CircleCrop()).placeholder(R.drawable.logo).error(R.drawable.logo).into(ivProfilePic);
            } else {
                Glide.with(this).load(userImage.getUrl()).transform(new CircleCrop()).placeholder(R.drawable.logo).error(R.drawable.logo).into(ivProfilePic);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setProfileContent(ParseUser user){
        com.example.quickcash.databinding.ItemProfileBinding ipBinding = binding.profileNda;
        ipBinding.tvUsername.setText(user.getUsername());
        ipBinding.tvPhone.setText(user.getString(User.KEY_USER_PHONE));
        ipBinding.tvUserSince.setText(getString(R.string.user_since) + " " + timeNeed(user.getCreatedAt()));
        ipBinding.tvEmail.setVisibility(View.INVISIBLE);
        ipBinding.rbUserRating.setRating((float) user.getDouble(User.KEY_USER_RATING));
    }

    private void setJobContent(Job job){
        ItemJobBinding ijBinding = binding.referJob;
        ijBinding.jobRequestName.setText(job.getName());
        try {
            ijBinding.jobUsername.setText(job.getUser().fetchIfNeeded().getUsername());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ijBinding.jobAddress.setText(job.getAddress());
        ijBinding.jobPrice.setText("$" + job.getPrice());
        ijBinding.jobDate.setText(dateDisplay(job.getJobDate()));

        View view = ijBinding.clJob;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Job job = notification.getJob();
                Intent i = generateIntent();
                i.putExtra(Job.class.getSimpleName(), Parcels.wrap(job));
                startActivity(i);
            }
        });
    }

    private void setRequestContent(Request request){
        ItemRequestBinding irBinding = binding.referRequest;
    }

    public Intent generateIntent(){
        ParseUser user = ParseUser.getCurrentUser();
        Intent intent;
        String comment = notification.getMessage();
        if(comment.contains("approved")){
            intent = new Intent(this, ToDoJobDetailsActivity.class);
        } else if(comment.contains("has sent a request") || comment.contains("finished")){
            intent = new Intent(this, MyJobsDetailsActivity.class);
        } else {
            intent = null;
        }
        return intent;
    }
}