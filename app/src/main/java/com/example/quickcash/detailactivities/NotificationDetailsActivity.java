package com.example.quickcash.detailactivities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.quickcash.databinding.ActivityNotificationDetailsBinding;
import com.example.quickcash.databinding.ItemJobBinding;
import com.example.quickcash.databinding.ItemRequestBinding;
import com.example.quickcash.models.Job;
import com.example.quickcash.models.Notification;
import com.example.quickcash.models.Request;
import com.example.quickcash.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.parceler.Parcels;

import static com.example.quickcash.adapters.JobsAdapter.dateDisplay;

public class NotificationDetailsActivity extends AppCompatActivity {
    private Toolbar toolbar;
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
        request = notification.getRequest();
        job = notification.getJob();

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
    }

    private void setProfileContent(ParseUser user){
        com.example.quickcash.databinding.ItemProfileBinding ipBinding = binding.profileNda;
        ipBinding.tvUsername.setText(user.getUsername());
        ipBinding.tvPhone.setText(user.getString(User.KEY_USER_PHONE));
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
    }

    private void setRequestContent(Request request){
        ItemRequestBinding irBinding = binding.referRequest;
    }
}