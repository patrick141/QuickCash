package com.example.quickcash.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.quickcash.R;
import com.example.quickcash.models.Job;
import com.example.quickcash.models.User;
import com.google.android.material.tabs.TabLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.quickcash.ProfileActivity.DEFAULT_RM1;
import static com.example.quickcash.adapters.JobsAdapter.timeNeed;
import static com.example.quickcash.detailactivities.MyJobsDetailsActivity.REQUEST_CODE_MYDA_RDA;
import static com.example.quickcash.models.User.KEY_USER_DESCRIPTION;

/**
 * ProfileFragment
 *
 * This is the ProfileFragment. In here, the user can see job
 * postings that they have submitted and view requests.
 *
 * @author Patrick Amaro Rivera
 */


public class ProfileFragment extends HomeFragment {

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public static final String TAG = "ProfileFragment";

    private ImageView ivProfilePic;
    private TextView tvUsername;
    private TextView tvUserSince;
    private TextView tvPhone;
    private TextView tvEmail;
    private RatingBar rbUserRating;
    private TabLayout jobTabLayout;
    private File photoFile;
    private String photoFileName = "photo.jpg";

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivProfilePic = view.findViewById(R.id.iv_profliePic);
        tvUsername = view.findViewById(R.id.tv_Username);
        tvUserSince = view.findViewById(R.id.tv_UserSince);
        tvPhone = view.findViewById(R.id.tv_phone);
        tvEmail = view.findViewById(R.id.tv_email);
        rbUserRating = view.findViewById(R.id.rb_user_rating);
        jobTabLayout = view.findViewById(R.id.myjobs_view_switcher);

        ParseUser user = ParseUser.getCurrentUser();
        tvUsername.setText(user.getUsername());
        if(user.getString(KEY_USER_DESCRIPTION) == null){
            tvUserSince.setText(DEFAULT_RM1 + timeNeed(user.getCreatedAt()));
        } else{
            tvUserSince.setText(user.getString(KEY_USER_DESCRIPTION));
        }
        ParseFile userImage = (ParseFile) user.get(User.KEY_USER_IMAGE);
        if(userImage == null){
            Glide.with(getContext()).load(R.drawable.logo).transform(new CircleCrop()).placeholder(R.drawable.logo).error(R.drawable.logo).into(ivProfilePic);
        } else {
            Glide.with(getContext()).load(userImage.getUrl()).transform(new CircleCrop()).placeholder(R.drawable.logo).error(R.drawable.logo).into(ivProfilePic);
        }
        rbUserRating.setRating((float) user.getDouble(User.KEY_USER_RATING));
        tvPhone.setText(user.getString(User.KEY_USER_PHONE));
        tvEmail.setText(user.getEmail());
        queryJobs();

        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchMyCamera();
            }
        });

        /**
         * This method checks to see which tab has been opened.
         *
         * 0 -- Pending Jobs
         * 1 -- Completed Jobs
         *
         * It also resets the swipe refresher based on whatever the user clicks.
         */
        jobTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch(position){
                    case 0:{
                        queryJobs();
                        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                queryJobs();
                            }
                        });
                        break;
                    }
                    case 1:{
                        queryFinishedJobs();
                        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                queryFinishedJobs();
                            }
                        });
                        break;
                    }
                    default:{
                        return;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                return;
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                return;
            }
        });
    }

    private void launchMyCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider.QuickCash", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private File getPhotoFileUri(String photoFileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + photoFileName);

        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivProfilePic.setImageBitmap(takenImage);
                ParseUser user = ParseUser.getCurrentUser();
                user.put(User.KEY_USER_IMAGE, new ParseFile(photoFile));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null){
                            Log.e(TAG, "Error on saving photo");
                        }
                        Log.i(TAG, "Saving photo");
                    }
                });
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        } else if( requestCode ==  REQUEST_CODE_MYDA_RDA && resultCode == RESULT_OK){
            jobsAdapter.clear();
            queryJobs();
        }
    }


    @Override
    protected void queryJobs() {
        ParseQuery<Job> query = ParseQuery.getQuery(Job.class);
        query.include(Job.KEY_JOB_USER);
        query.whereEqualTo(Job.KEY_JOB_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(Job.KEY_JOB_ISFINISHED, false);
        query.setLimit(20);
        query.addDescendingOrder(Job.KEY_CREATED_AT);
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
                jobsAdapter.clear();
                jobsAdapter.addAll(jobs);
                swipeContainer.setRefreshing(false);
                jobsAdapter.notifyDataSetChanged();
            }
        });
    }

    protected void queryFinishedJobs(){
        ParseQuery<Job> query = ParseQuery.getQuery(Job.class);
        query.include(Job.KEY_JOB_USER);
        query.whereEqualTo(Job.KEY_JOB_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(Job.KEY_JOB_ISFINISHED, true);
        query.setLimit(20);
        query.addDescendingOrder(Job.KEY_CREATED_AT);
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
                jobsAdapter.clear();
                jobsAdapter.addAll(jobs);
                swipeContainer.setRefreshing(false);
                jobsAdapter.notifyDataSetChanged();
            }
        });
    }

}