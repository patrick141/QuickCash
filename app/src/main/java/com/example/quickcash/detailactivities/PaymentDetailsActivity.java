package com.example.quickcash.detailactivities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.quickcash.R;
import com.example.quickcash.databinding.ActivityPaymentDetailsBinding;
import com.example.quickcash.databinding.ItemProfileBinding;
import com.example.quickcash.models.Job;
import com.example.quickcash.models.Payment;
import com.example.quickcash.models.User;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class PaymentDetailsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Payment payment;
    private Button btnPay;
    private TextView tvPrice;
    private TextView tvName;
    private LinearLayout llPayUser;
    private PaymentsClient paymentsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityPaymentDetailsBinding binding = ActivityPaymentDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        payment = (Payment) Parcels.unwrap(getIntent().getParcelableExtra(Payment.class.getSimpleName()));

        toolbar = (Toolbar) findViewById(R.id.toolbar_Home);
        setSupportActionBar(toolbar);
        btnPay = binding.btnPayUser;
        tvPrice = binding.pdaJobPrice;
        tvName = binding.pdaJobName;
        llPayUser = binding.llEnterInfo;
        llPayUser.setVisibility(View.VISIBLE);

        if(payment.getRecipient().hasSameId(ParseUser.getCurrentUser())){
            llPayUser.setVisibility(View.GONE);
            btnPay.setVisibility(View.GONE);
        }
        setProfileContents(binding, payment);

        Job job = (Job)     payment.getJob();
        try {
            tvName.setText(job.getUser().fetchIfNeeded().getUsername() + ": " + job.getName());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvPrice.setText("$" + String.format("%.2f", payment.getAmount()));

        Wallet.WalletOptions walletOptions = new Wallet.WalletOptions.Builder()
                .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                .build();
        paymentsClient = Wallet.getPaymentsClient(this, walletOptions);

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Implement Google Pay
            }
        });
    }

    /**
     * This method takes our binding class and populates the ItemProfileBinding with
     * the user's profile view.
     * @param binding
     * @param payment
     */
    private void setProfileContents(ActivityPaymentDetailsBinding binding, Payment payment) {
        ItemProfileBinding ipBinding = binding.pdaProfile;
        ImageView ivPic = ipBinding.ivProfliePic;
        TextView tvName = ipBinding.tvUsername;
        TextView tvDes = ipBinding.tvUserSince;
        TextView tvPhone = ipBinding.tvPhone;
        TextView tvEmail = ipBinding.tvEmail;
        RatingBar rbUser = ipBinding.rbUserRating;

        ParseUser user = payment.getRecipient();
        tvName.setText(user.getUsername());
        tvDes.setText(user.getString(User.KEY_USER_DESCRIPTION));
        tvPhone.setText(user.getString(User.KEY_USER_PHONE));
        tvEmail.setText(user.getEmail());
        rbUser.setRating((float) user.getDouble(User.KEY_USER_RATING));
        ParseFile image = (ParseFile) user.getParseFile(User.KEY_USER_IMAGE);
        if(image != null){
            Glide.with(this).load(image.getUrl()).transform(new CircleCrop()).into(ivPic);
        } else{
            Glide.with(this).load(R.drawable.logo).transform(new CircleCrop()).into(ivPic);
        }
    }

}