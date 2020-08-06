package com.example.quickcash.detailactivities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.quickcash.R;
import com.example.quickcash.databinding.ActivityToDoJobDetailsBinding;
import com.example.quickcash.models.Job;
import com.example.quickcash.models.Notification;
import com.example.quickcash.models.Payment;
import com.example.quickcash.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

public class ToDoJobDetailsActivity extends BaseJobDetailsActivity{
    private Job job;
    private ConstraintLayout clButtons;
    private Button btnDone;
    private Button btnLeave;
    private Button btnPayRequest;
    private Payment jobPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityToDoJobDetailsBinding binding = ActivityToDoJobDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        job = (Job) Parcels.unwrap(getIntent().getParcelableExtra(Job.class.getSimpleName()));
        runToolbar();
        findtheViews();
        setJobContents(job);
        btnDone = binding.todoDone;
        btnLeave = binding.todoLeave;
        btnPayRequest = binding.btnRequestPay;
        clButtons = binding.clTodoViews;

        clButtons.setVisibility(View.VISIBLE);
        btnPayRequest.setVisibility(View.GONE);
        if(job.isFinished()){
            clButtons.setVisibility(View.GONE);
            btnPayRequest.setVisibility(View.VISIBLE);
        }
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

        jobPayment = job.getPayment();

        btnPayRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(jobPayment == null){
                    createPayment();
                }else{
                    Toast.makeText(ToDoJobDetailsActivity.this, getString(R.string.todo_request_pay_reminder), Toast.LENGTH_SHORT).show();
                }
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

        Notification notification = Notification.generateMyNotification(Notification.JOB_DONE, job, null, ToDoJobDetailsActivity.this);
        notification.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    e.printStackTrace();
                }
                Log.i(TAG, getString(R.string.notif_suc));
            }
        });

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

        jobstatusJDA.setText(getString(R.string.MJDA_status));
        clButtons.setVisibility(View.GONE);
        btnPayRequest.setVisibility(View.VISIBLE);
    }

    private void leaveThisJob() {
        job.remove(Job.KEY_JOB_ASSIGNED_USER);
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

    private void createPayment() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        Payment payment = new Payment();
        payment.setRecipient(currentUser);
        payment.setBuyer(job.getUser());
        payment.setAmount(job.getPrice());
        payment.setJob(job);
        payment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!= null){
                    e.printStackTrace();
                }
                Log.i(TAG, getString(R.string.tododa_request_pay_success));
                job.setPayment(payment);
                job.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null){
                            e.printStackTrace();
                        }
                    }
                });

                Notification notification = new Notification();
                notification.setSender(currentUser);
                notification.setRecipient(job.getUser());
                notification.setMessage(currentUser.getUsername() +getString(R.string.notif_pay) + " " + job.getName());
                notification.setJob(job);
                notification.setPayment(payment);
                notification.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null){
                            e.printStackTrace();
                        }
                        Log.i(TAG,getString(R.string.notif_suc));
                    }
                });
                Toast.makeText(ToDoJobDetailsActivity.this, getString(R.string.payment_req_sent), Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 2000);
            }
        });
    }


}