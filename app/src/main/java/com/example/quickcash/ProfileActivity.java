package com.example.quickcash;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.quickcash.models.User;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import static com.example.quickcash.adapters.JobsAdapter.timeNeed;

public class ProfileActivity extends AppCompatActivity {
    private ParseUser user;
    private Toolbar toolbar;
    private ImageView ivProfilePic;
    private TextView tvUsername;
    private TextView tvDescription;
    private TextView tvPhoneNumber;
    private RatingBar rbUserRating;


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

        ParseUser user = Parcels.unwrap(getIntent().getParcelableExtra("PROFILE"));

        tvUsername.setText(user.getUsername());
        String description = user.getString(User.KEY_USER_DESCRIPTION);
        if(description != null){
            tvDescription.setText(description);
        } else{
            tvDescription.setText("User since " + timeNeed(user.getCreatedAt()));
        }
        String phone = user.getString(User.KEY_USER_PHONE);
        if(phone != null){
            tvPhoneNumber.setText(phone);
        }
        ParseFile userImage = (ParseFile) user.get(User.KEY_USER_IMAGE);
        if(userImage == null){
            Glide.with(this).load(R.drawable.logo).into(ivProfilePic);
        } else{
            Glide.with(this).load(userImage.getUrl()).into(ivProfilePic);
        }

        rbUserRating.setRating((float) user.getDouble(User.KEY_USER_RATING));

    }

}