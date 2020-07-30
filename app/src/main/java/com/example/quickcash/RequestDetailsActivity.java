package com.example.quickcash;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.quickcash.databinding.ActivityRequestDetailsBinding;
import com.example.quickcash.models.Job;
import com.example.quickcash.models.Request;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;

import static com.example.quickcash.adapters.RequestsAdapter.userAssigned;

/**
 * RequestDetailsActivity
 *
 * This class handles how the user reacts to job requests sent by other users.
 */


public class RequestDetailsActivity extends AppCompatActivity {

    private static final String TAG = "RequestDetailsActivity";
    private Request request;

    private ImageView ivRequestor;
    private TextView tvRequestor;
    private TextView tvRequestorComment;
    private Button btnAcceptUser;
    private Button btnDenyUser;
    private Button btnContactUser;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRequestDetailsBinding binding = ActivityRequestDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        toolbar = (Toolbar) findViewById(R.id.toolbar_Home);
        setSupportActionBar(toolbar);

        request = (Request) Parcels.unwrap(getIntent().getParcelableExtra(Request.class.getSimpleName()));
        ivRequestor = binding.ivRequestorPP;
        tvRequestor = binding.tvRequestorName;
        tvRequestorComment = binding.tvRequestComment;
        btnAcceptUser = binding.btnRequestApprove;
        btnDenyUser = binding.btnRequestDeny;
        btnContactUser = binding.btnContact;

        ParseFile userImage = null;
        try {
            userImage = (ParseFile) request.getUser().fetchIfNeeded().getParseFile("profilePic");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(userImage == null){
            Glide.with(this).load(R.drawable.logo).into(ivRequestor);
        }
        else{
            Glide.with(this).load(userImage.getUrl()).into(ivRequestor);
        }

        tvRequestor.setText(request.getUser().getUsername());
        tvRequestorComment.setText(request.getComment());

        if(userAssigned(request.getUser(), request)){
            tvRequestor.setBackground(getDrawable(R.drawable.assigned_user));
        }
        btnAcceptUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Job job = (Job) request.getJob();
                job.setAssignedUser(request.getUser());
                job.setIsTaken(true);
                job.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e!= null){
                            Log.e(TAG, "Error giving " + request.getUser().getUsername() + " this job.", e);
                        }
                        Log.i(TAG, request.getUser().getUsername() + " has this job!");
                        finish();
                    }
                });
            }
        });
        btnDenyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playDeleteAD();
            }
        });

    }

    /**
     * Similar to the sign out Alert Dialog, this gives the user a confirmation
     */
    private void playDeleteAD() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.RDA_deny))
                .setMessage(getResources().getString(R.string.rda_deny_con) + " " + request.getUser().getUsername() + "'s request?")
                .setIcon(R.drawable.logo)

                .setPositiveButton(getResources().getString(R.string.rda_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        removeThisRequest();
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
     * This method deletes the Request in this job's array of request. Then, it deletes the request from the Parse DB.
     */
    private void removeThisRequest() {
        Job job = (Job) request.getJob();
        ArrayList<Request> requests = job.getRequests();
        requests.remove(request);
        job.setJobRequests(requests);
        job.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e== null){
                    Log.i(TAG, "Removed this from array");
                }else{
                    Log.e(TAG, "Need to try again");
                }
            }
        });

        request.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG, "Error deleting this request", e);
                }
                Log.i(TAG, "Going to delete this request");
                Intent i = new Intent();
                setResult(RESULT_OK, i);
                finish();
            }
        });
    }
}