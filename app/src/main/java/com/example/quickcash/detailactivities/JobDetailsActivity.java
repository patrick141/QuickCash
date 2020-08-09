package com.example.quickcash.detailactivities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.quickcash.ProfileActivity;
import com.example.quickcash.R;
import com.example.quickcash.databinding.ActivityJobDetailsBinding;
import com.example.quickcash.models.Job;
import com.example.quickcash.models.Notification;
import com.example.quickcash.models.Request;
import com.example.quickcash.models.User;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static com.example.quickcash.adapters.JobsAdapter.getRelativeTimeAgo;

/**
 *  JobDetailsActivity
 *
 *  This class handles viewing details of jobs. The Activity changes based off if the user is click-
 *  ing on their own job or if they are viewing other job posts and want to submit a request.
 */
public class JobDetailsActivity extends BaseJobDetailsActivity{

    public static final String TAG = "JobDetailsActivity";
    public static final int PAUSE = 2000;

    private Job job;
    private LinearLayout llJobRequest;
    private EditText etRequestJDA;
    private Button btnSubmitRequest;
    private ConstraintLayout clRequest;
    private ImageView ivReqPic;
    private TextView tvReqName;
    private TextView tvReqComment;
    private TextView tvReqCA;
    private TextView sentReq;
    private Request request;
    private Button btnEditReq;
    private Button btnDelReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityJobDetailsBinding binding = ActivityJobDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        runToolbar();
        job = (Job) Parcels.unwrap(getIntent().getParcelableExtra(Job.class.getSimpleName()));
        /**
         * The follow methods get our
         */
        findtheViews();
        setJobContents(job);

        llJobRequest = binding.llJdaRequest;
        etRequestJDA = binding.jdaEtRequest;
        btnSubmitRequest = binding.jdaButtonRequest;
        sentReq = binding.sentView;


        sentReq.setVisibility(View.GONE);
            /**
             *  Assuming the user is clicking on another user's job. They will go to the default
             *  layout where they can submit a job request.
             */
        llJobRequest.setVisibility(View.VISIBLE);

        clRequest = findViewById(R.id.cl_my_request);
        btnEditReq = binding.btnEditReq;
        btnDelReq = binding.btnDeleteMyReq;

        ivReqPic = findViewById(R.id.iv_request_User);
        tvReqName = findViewById(R.id.request_user);
        tvReqComment = findViewById(R.id.request_comment);
        tvReqCA = findViewById(R.id.request_createdAt);
        clRequest.setVisibility(View.GONE);

        /**
         * If the user has already submitted a job, to prevent the user from sending multiple jobs
         * They are given an alternative layout that shows that they have already submitted a job request.
         */

        if(job.getRequests() != null){
            if(submittedRequest(job.getRequests())){
                llJobRequest.setVisibility(View.GONE);
                sentReq.setVisibility(View.VISIBLE);
            }
        }

        jobUserJDA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(JobDetailsActivity.this, ProfileActivity.class);
                i.putExtra(User.class.getSimpleName(), Parcels.wrap(job.getUser()));
                JobDetailsActivity.this.startActivity(i);
            }
        });

        /**
         *  User can submit request by filling out the editText and clicking on the submit button.
         */
        btnSubmitRequest.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                String comment = etRequestJDA.getText().toString();
                ParseUser currentUser = ParseUser.getCurrentUser();
                /**
                 * The user cannot submit a request if they do not fill out the editText.
                 */
                if(comment.isEmpty()){
                    Toast.makeText(JobDetailsActivity.this, getString(R.string.JDA_fill), Toast.LENGTH_SHORT).show();
                    return;
                }
                final Request request = new Request();
                request.setUser(currentUser);
                request.setComment(comment);
                request.setJob(job);
                request.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e !=null){
                            Log.e(TAG, getString(R.string.JDA_request_failure), e);
                            return;
                        }
                        Log.i(TAG, getString(R.string.JDA_request_sucess));
                        etRequestJDA.setText("");
                        llJobRequest.setVisibility(View.GONE);
                        sentReq.setVisibility(View.VISIBLE);
                    }
                });
                Notification notification = Notification.generateMyNotification(Notification.SEND_REQUEST, job, request, JobDetailsActivity.this);
                notification.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null){
                            Log.e(TAG, "" + e);
                        }
                        Log.i(TAG, getString(R.string.notif_suc));
                    }
                });


                job.addJobRequest(request);
                job.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e!= null){
                            Log.e(TAG, getString(R.string.JDA_job_request_arr_error), e);
                        }
                        Log.i(TAG, getString(R.string.JDA_job_request_arr));
                    }
                });
                Toast.makeText(JobDetailsActivity.this, getString(R.string.JDA_request_toast), Toast.LENGTH_SHORT).show();
                Intent i = new Intent();
                setResult(RESULT_OK, i);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },PAUSE);
            }
        });
        btnEditReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playEditAD();
            }
        });

        btnDelReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playDeleteAD();
            }
        });
    }

    private void playEditAD() {
        ParseUser user = ParseUser.getCurrentUser();
        LayoutInflater inflator = LayoutInflater.from(JobDetailsActivity.this);
        final View view = inflator.inflate(R.layout.edit_request, null);
        TextView tvName = view.findViewById(R.id.tv_myU);
        EditText etNewComment = view.findViewById(R.id.et_request);
        ImageView ivMyImage = view.findViewById(R.id.iv_er);

        ParseFile image = (ParseFile) user.getParseFile(User.KEY_USER_IMAGE);
        if(image == null){
            Glide.with(this).load(R.drawable.logo).into(ivMyImage);
        } else{
            Glide.with(this).load(image.getUrl()).into(ivMyImage);
        }

        tvName.setText(user.getUsername());
        etNewComment.setText(tvReqComment.getText().toString());

        AlertDialog dialog = new AlertDialog.Builder(JobDetailsActivity.this)
                .setTitle(getString(R.string.JDA_edit_req))
                .setView(view)
                .setPositiveButton(getString(R.string.JDA_edit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newComment = etNewComment.getText().toString();
                        if(newComment.isEmpty()){
                            Toast.makeText(JobDetailsActivity.this, getString(R.string.JDA_fill), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        saveRequest(newComment);
                    }
                })
                .setNegativeButton(getString(R.string.JDA_edit_cancel), null).create();
        dialog.show();
    }

    /**
     * This methods takes what we typed into our edit request and
     * @param newComment
     */
    private void saveRequest(String newComment) {
        request.setComment(newComment);
        request.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    e.printStackTrace();
                }
                Log.i(TAG, getString(R.string.JDA_edit_req_success) + " " + newComment);
                tvReqComment.setText(newComment);
            }
        });

    }

    private void playDeleteAD() {
        AlertDialog logOutDialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.JDA_my_req_del))
                .setMessage(getResources().getString(R.string.JDA_my_req_del_info))
                .setIcon(R.drawable.logo)
                .setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        deleteMyRequest();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.return_now), null)
                .create();
        logOutDialog.show();
    }

    private void deleteMyRequest() {
        ArrayList<Request> requests = job.getRequests();
        requests.remove(request);
        job.setJobRequests(requests);
        job.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!= null){
                    e.printStackTrace();
                }
                Log.i(TAG, getString(R.string.JDA_del_req_arr));
            }
        });
        request.deleteInBackground(new DeleteCallback() {
            @SuppressLint("ResourceType")
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    e.printStackTrace();
                }
                Log.i(TAG, getString(R.string.JDA_del_req));
                sentReq.setText(getString(R.string.JDA_del_req_conf));
                clRequest.setVisibility(View.GONE);
                request = null;

                Intent i = new Intent();
                setResult(RESULT_OK, i);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },PAUSE);
            }
        });
    }

    /**
     * This function checks to see if the current user has already submitted a request for the job. This iterates through the array
     * and returns true if it finds the matching user id.
     * * This function also displays the request information, if a request has been found.
     * @param requests
     * @return
     */
    public boolean submittedRequest(List<Request> requests){
        for (Request request: requests){
            if(request.getUser().hasSameId(ParseUser.getCurrentUser())){
                this.request = request;
                populateRequest();
                return true;
            }
        } return false;
    }

    /**
     * If a user has already submitted a request, the request's info view is populated on
     * item_request layout.
     */
    private void populateRequest() {
        clRequest.setVisibility(View.VISIBLE);
        ParseUser user = ParseUser.getCurrentUser();
        ParseFile image = (ParseFile) user.get(User.KEY_USER_IMAGE);
        if(image != null) {
            Glide.with(this).load(image).placeholder(R.drawable.logo).into(ivReqPic);
        } else {
            Glide.with(this).load(R.drawable.logo).placeholder(R.drawable.logo).into(ivReqPic);
        }
        tvReqName.setText(user.getUsername());
        tvReqComment.setText(request.getComment());
        tvReqCA.setText(getRelativeTimeAgo(request.getCreatedAt().toString()));
    }
}