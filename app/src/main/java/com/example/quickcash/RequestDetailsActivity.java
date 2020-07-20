package com.example.quickcash;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.quickcash.databinding.ActivityRequestDetailsBinding;
import com.example.quickcash.models.Job;
import com.example.quickcash.models.Request;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import org.parceler.Parcels;

public class RequestDetailsActivity extends AppCompatActivity {

    private static final String TAG = "RequestDetailsActivity";
    private Request request;

    private ImageView ivRequestor;
    private TextView tvRequestor;
    private TextView tvRequestorComment;
    private Button btnAcceptUser;
    private Button btnDenyUser;
    private Button btnContactUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRequestDetailsBinding binding = ActivityRequestDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        request = (Request) Parcels.unwrap(getIntent().getParcelableExtra("REQUEST"));
        ivRequestor = binding.ivRequestorPP;
        tvRequestor = binding.tvRequestorName;
        tvRequestorComment = binding.tvRequestComment;
        btnAcceptUser = binding.btnRequestApprove;
        btnDenyUser = binding.btnRequestDeny;
        btnContactUser = binding.btnContact;

        ParseFile userImage = (ParseFile) request.getUser().get("profilePic");
        if(userImage == null){
            Glide.with(this).load(R.drawable.logo).into(ivRequestor);
        }
        else{
            Glide.with(this).load(userImage.getUrl()).into(ivRequestor);
        }

        tvRequestor.setText(request.getUser().getUsername());
        tvRequestorComment.setText(request.getComment());

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
                request.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null){
                            Log.e(TAG, "Error deleting this request", e);
                        }
                        Log.i(TAG, "Going to delete this request");
                        finish();
                    }
                });
            }
        });
    }
}