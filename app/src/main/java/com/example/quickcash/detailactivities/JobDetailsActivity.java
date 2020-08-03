package com.example.quickcash.detailactivities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.Arrays;
import java.util.List;

import static com.example.quickcash.adapters.JobsAdapter.getRelativeTimeAgo;

/**
 *  JobDetailsActivity
 *
 *  This class handles viewing details of jobs. The Activity changes based off if the user is click-
 *  ing on their own job or if they are viewing other job posts and want to submit a request.
 */
public class JobDetailsActivity extends BaseJobDetailsActivity implements OnMapReadyCallback {

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
    private GoogleMap map;

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

        getJobUserJDA().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(JobDetailsActivity.this, ProfileActivity.class);
                i.putExtra(User.class.getSimpleName(), Parcels.wrap(job.getUser()));
                JobDetailsActivity.this.startActivity(i);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_demo);
        mapFragment.getMapAsync(this);


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

                Notification notification = new Notification();
                notification.setSender(ParseUser.getCurrentUser());
                notification.setRecipient(job.getUser());
                String messenge = currentUser.getUsername() + " " + getString(R.string.notif_req)
                        + " " + job.getName();
                notification.setMessage(messenge);
                notification.setJob(job);
                notification.setRequest(request);
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
        job.removeAll(Job.KEY_JOB_REQUESTS, Arrays.asList(request));
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

    /**
     * This function gets the job's location coordinate points and maps it into the small Google Map.
     * If there is no geopoint, it maps to a default
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        ParseGeoPoint geoPoint = job.getParseGeoPoint(Job.KEY_JOB_LOCATION);
        LatLng myPlace;
        if(geoPoint != null){
            myPlace = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
        } else{
            myPlace = new LatLng(getResources().getFloat(R.dimen.map_lat_default), getResources().getFloat(R.dimen.map_lon_default));
        }
        map.addMarker(new MarkerOptions().position(myPlace).title(job.getName()));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPlace, getResources().getFloat(R.dimen.map_zoom)));
    }

}