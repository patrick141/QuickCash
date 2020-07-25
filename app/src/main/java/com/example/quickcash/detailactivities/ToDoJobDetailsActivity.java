package com.example.quickcash.detailactivities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.example.quickcash.R;
import com.example.quickcash.databinding.ActivityToDoJobDetailsBinding;
import com.example.quickcash.models.Job;
import com.example.quickcash.models.User;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

public class ToDoJobDetailsActivity extends BaseJobDetailsActivity implements OnMapReadyCallback {
    private Job job;
    private Button btnDone;
    private Button btnLeave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityToDoJobDetailsBinding binding = ActivityToDoJobDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        job = (Job) Parcels.unwrap(getIntent().getParcelableExtra("JOB"));
        runToolbar();
        findtheViews();
        setJobContents(job);
        btnDone = binding.todoDone;
        btnLeave = binding.todoLeave;

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishedThisJob();
                finish();
            }
        });
        btnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playLeaveAD();
            }
        });

    }

    /**
     * Alert Dialog warning the user if they can no longer do the job they
     * have been assigned for.
     */
    private void playLeaveAD(){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.tododa_leave))
                .setMessage(getResources().getString(R.string.tododa_leave_text) + " " + job.getUser().getUsername() + "'s job?")
                .setIcon(R.drawable.logo)

                .setPositiveButton(getResources().getString(R.string.rda_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        leaveThisJob();
                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.rda_no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    /**
     * This is what happens when a user submits that they finished a job.
     * 1. The job's isFinished is set to true.
     * 2. The job is in the User's finishedJobs array.
     * 3. The user's rating (UR) is updated.
     */
    private void finishedThisJob() {
        job.setIsFinished(true);
        job.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG, "error saying Finished");
                }
                Log.i(TAG, " You have completed this job");
            }
        });
        ParseUser user = ParseUser.getCurrentUser();

        user.add(User.KEY_USER_COMPLETED_JOBS, job);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Failure on saving job in completed array");
                }
                Log.i(TAG, "added job in completed array");
            }
        });
    }

    private void leaveThisJob() {
        job.setAssignedUser(null);
        job.setIsTaken(false);
        job.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG, "Error trying to leave this job");
                }
                Log.i(TAG, " Leaving this job from " + job.getUser().getUsername());
            }
        });
    }

}